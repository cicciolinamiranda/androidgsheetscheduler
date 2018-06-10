package com.google.scheduler.rest;

import android.content.Context;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.scheduler.enums.ShiftRange;
import com.google.scheduler.util.Util;
import com.google.scheduler.view.DataModel;
import com.google.scheduler.view.MainActivity;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.scheduler.constants.AppConstants.PH_TIMEZONE;

/**
 * Created by cicciolina on 6/6/18.
 */

public class RestGetTodaysActiveEmployeeInGSheet extends BaseGSheetAsyncTask {

    private RestGetTodaysActiveEmployeeInGSheet.Listener listener;
    private final static String DATE_HEADER_RANGE = "F1:ZZ1";
    private final static String WHOLE_WITHOUT_HEADERS_DATA_RANGE = "A2:";
    private String lob;
    private ShiftRange shiftRange;
    private final Pattern P = Pattern.compile(".*([01]?[0-9]|2[0-3]):[0-5][0-9].*");
    private String todaysColumnLetter;
    private String yesterdaysColumnLetter;

    public RestGetTodaysActiveEmployeeInGSheet(GoogleAccountCredential googleAccountCredential,
                                               Context context,
                                               RestGetTodaysActiveEmployeeInGSheet.Listener listener,
                                               String spreadsheetId,
                                               String tabSheetName,
                                               String lob,
                                               ShiftRange shiftRange) {
        super(googleAccountCredential, context, listener, spreadsheetId, tabSheetName);
        this.listener = listener;
        this.lob = lob;
        this.shiftRange = shiftRange;
    }

    @Override
    public List<List<Object>>  getDataFromApi(Object... params) throws IOException {
        List<List<Object>> results = new ArrayList<>();

        String dateHeaderRange = tabSheetName+"!"+DATE_HEADER_RANGE;

        ValueRange dateSheetRange = mService.spreadsheets().values()
                .get(spreadsheetId, dateHeaderRange)
                .execute();

        if(dateSheetRange != null && dateSheetRange.getValues() != null &&
                !dateSheetRange.getValues().isEmpty()) {


            Log.d(RestGetTodaysActiveEmployeeInGSheet.class.getName(), dateSheetRange.getValues().toString());

            todaysColumnLetter = getTodaysDateColumn(dateSheetRange.getValues());

            yesterdaysColumnLetter = getYesterdaysDateColumn(dateSheetRange.getValues());

            if(todaysColumnLetter != null) {
                String todaysTimeDataRange = tabSheetName + "!" + todaysColumnLetter + "2:" + todaysColumnLetter;
                ValueRange todaysTimeSheetRange = mService.spreadsheets().values()
                        .get(spreadsheetId, todaysTimeDataRange)
                        .execute();

                ValueRange yesterdaysTimeSheetRange = null;

                if(yesterdaysColumnLetter != null) {
                    String yesterdaysTimeDataRange = tabSheetName + "!" + yesterdaysColumnLetter + "2:" + yesterdaysColumnLetter;


                    yesterdaysTimeSheetRange = mService.spreadsheets().values()
                            .get(spreadsheetId, yesterdaysTimeDataRange)
                            .execute();
                }

                if (todaysTimeSheetRange != null && todaysTimeSheetRange.getValues() != null &&
                        !todaysTimeSheetRange.getValues().isEmpty()) {

                    List<Integer> indexThatNeedsToBeRetrieved = getListOfRowNumThatIsOnDuty(yesterdaysTimeSheetRange, todaysTimeSheetRange);

                    String employeeDataRange = tabSheetName + "!" + WHOLE_WITHOUT_HEADERS_DATA_RANGE + todaysColumnLetter;
                    ValueRange wholeDataRange = mService.spreadsheets().values()
                            .get(spreadsheetId, employeeDataRange)
                            .execute();

                    if (wholeDataRange != null && wholeDataRange.getValues() != null &&
                            !wholeDataRange.getValues().isEmpty()) {

                        for (Integer rowData : indexThatNeedsToBeRetrieved) {
                            results.add(wholeDataRange.getValues().get(rowData));
                        }
                    }
                }

            }

        }
        return results;
    }


    @Override
    protected void onPostExecute(Object output) {
        listener.result(translateToEmployeeNames((List<List<Object>>)output));
    }

    private String getTodaysDateColumn(List<List<Object>> values){

        String column = null;

        DateTime currentDateTime = new DateTime(Calendar.getInstance().getTime()).withZone(DateTimeZone.forID(PH_TIMEZONE));
        Log.d(RestGetTodaysActiveEmployeeInGSheet.class.getName(), "getYesterdaysDateColumn currentDateTime: "+currentDateTime.toString());

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone(PH_TIMEZONE));

        String todaysDateStr = sdf.format(currentDateTime.toDate());
        for(List<Object> employeesArr: values){

                for(int i=0; i < employeesArr.size(); i++) {

                    if(todaysDateStr.equalsIgnoreCase((String)employeesArr.get(i))) {
                        column = Util.getInstance().fromColumnNumberToColumnLetter(i);
                        break;
                    }
                }


        }

        return column;
    }

    private String getYesterdaysDateColumn(List<List<Object>> values){

        String column = null;
        DateTime currentDateTime = new DateTime(Calendar.getInstance().getTime()).minusDays(1).withZone(DateTimeZone.forID(PH_TIMEZONE));
        Log.d(RestGetTodaysActiveEmployeeInGSheet.class.getName(), "getYesterdaysDateColumn currentDateTime: "+currentDateTime.toString());

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone(PH_TIMEZONE));

        String yesterdaysDateStr = sdf.format(currentDateTime.toDate());

        for(List<Object> employeesArr: values){

            for(int i=0; i < employeesArr.size(); i++) {

                if(yesterdaysDateStr.equalsIgnoreCase((String)employeesArr.get(i))) {
                    column = Util.getInstance().fromColumnNumberToColumnLetter(i);
                    break;
                }
            }


        }

        return column;
    }

    private List<Integer> getListOfRowNumThatIsOnDuty(ValueRange yesterdaysValueRange, ValueRange todaysValueRange){
        List<Integer> listOfRowsThatIsOnDuty = new ArrayList<>();

        if(todaysValueRange != null) {
            List<List<Object>> todaysValues = todaysValueRange.getValues();

            for (int i = 0; i < todaysValues.size(); i++) {
                List<Object> timestampArr = todaysValues.get(i);
                if (!timestampArr.isEmpty()) {
                    Matcher m = P.matcher((String) timestampArr.get(0));

                    if (m.matches()) {
                        listOfRowsThatIsOnDuty.add(i);
                    }
                }

            }
        }

        if(yesterdaysValueRange != null) {
            List<List<Object>> yesterdaysValues = yesterdaysValueRange.getValues();
            for (int i = 0; i < yesterdaysValues.size(); i++) {
                List<Object> timestampArr = yesterdaysValues.get(i);
                if (!timestampArr.isEmpty()) {
                    Matcher m = P.matcher((String) timestampArr.get(0));

                    if (m.matches()) {

                        if (!listOfRowsThatIsOnDuty.contains(i)) {
                            listOfRowsThatIsOnDuty.add(i);
                        }
                    }
                }

            }
        }

        return listOfRowsThatIsOnDuty;
    }

    private ArrayList<DataModel> translateToEmployeeNames(List<List<Object>> values){
        ArrayList<DataModel>  employeeList = new ArrayList<>();
        Set<String> employeeNames = new HashSet<>();
        for(List<Object> employeeArr: values){

                if(!employeeArr.isEmpty() && employeeArr.size() >= 4 &&
                        ((String)employeeArr.get(1)).equalsIgnoreCase(lob)
                        &&
                        isShiftIsBetweenRangeToday(((String)employeeArr.get(employeeArr.size()-1))) &&
                        !employeeNames.contains(employeeArr.get(4)) &&
                        getShiftIsBetweenRangeToday((String)employeeArr.get(employeeArr.size()-1)) != null
                        ) {
                    employeeNames.add((String)employeeArr.get(4));

                    employeeList.add(new DataModel(((String)employeeArr.get(4)),((String)employeeArr.get(1)),((String)employeeArr.get(3)), getShiftIsBetweenRangeToday((String)employeeArr.get(employeeArr.size()-1)), todaysColumnLetter));
                }

            if(!employeeArr.isEmpty() && employeeArr.size() >= 4 &&
                    ((String)employeeArr.get(1)).equalsIgnoreCase(lob)
                    &&
                    isShiftIsBetweenRangeYesterday(((String)employeeArr.get(employeeArr.size()-2))) &&
                    !employeeNames.contains(employeeArr.get(4)) &&
                    getShiftIsBetweenRangeYesterday((String)employeeArr.get(employeeArr.size()-2)) != null
                    ) {

                employeeNames.add((String)employeeArr.get(4));
                employeeList.add(new DataModel(((String)employeeArr.get(4)),((String)employeeArr.get(1)),((String)employeeArr.get(3)), getShiftIsBetweenRangeYesterday((String)employeeArr.get(employeeArr.size()-2)), yesterdaysColumnLetter));
            }

        }

        return employeeList;
    }

    private boolean isShiftIsBetweenRangeToday(String todaysStartTime) {

        Matcher m = P.matcher(todaysStartTime);

        if (m.matches()) {
            DateTime currentDateTime = new DateTime(Calendar.getInstance().getTime()).withZone(DateTimeZone.forID(PH_TIMEZONE));

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone(PH_TIMEZONE));

            try {
                String formattedTodaysDate = sdf.format(currentDateTime.toDate());

                Log.d(ShiftRange.class.getName(), "Format todays date: " + formattedTodaysDate);

                DateFormat sdf2 = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                sdf2.setTimeZone(TimeZone.getTimeZone(PH_TIMEZONE));

                Log.d(ShiftRange.class.getName(), sdf2.parse(formattedTodaysDate + " " + todaysStartTime).toString());

                DateTime startShiftDateTimeStart = new DateTime(sdf2.parse(formattedTodaysDate + " " + todaysStartTime)).withZone(DateTimeZone.forTimeZone(sdf2.getTimeZone()));
                DateTime endShiftDateTimeStart = new DateTime(sdf2.parse(formattedTodaysDate + " " + todaysStartTime)).plusHours(9).withZone(DateTimeZone.forTimeZone(sdf2.getTimeZone()));

                Log.d(MainActivity.class.getName(), startShiftDateTimeStart.toString());

                if ((startShiftDateTimeStart.isEqual(shiftRange.getStartTime()) || startShiftDateTimeStart.isAfter(shiftRange.getStartTime())) &&
                        (startShiftDateTimeStart.isEqual(shiftRange.getEndTime()) || startShiftDateTimeStart.isBefore(shiftRange.getEndTime()))) {
                    return true;
                }

                if ((endShiftDateTimeStart.isEqual(shiftRange.getStartTime()) || endShiftDateTimeStart.isAfter(shiftRange.getStartTime())) &&
                        (endShiftDateTimeStart.isEqual(shiftRange.getEndTime()) || endShiftDateTimeStart.isBefore(shiftRange.getEndTime()))) {
                    return true;
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private DateTime getShiftIsBetweenRangeToday(String todaysStartTime) {

        Matcher m = P.matcher(todaysStartTime);

        if (m.matches()) {
            DateTime currentDateTime = new DateTime(Calendar.getInstance().getTime()).withZone(DateTimeZone.forID(PH_TIMEZONE));

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone(PH_TIMEZONE));

            try {
                String formattedTodaysDate = sdf.format(currentDateTime.toDate());

                Log.d(ShiftRange.class.getName(), "Format todays date: " + formattedTodaysDate);

                DateFormat sdf2 = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                sdf2.setTimeZone(TimeZone.getTimeZone(PH_TIMEZONE));

                Log.d(ShiftRange.class.getName(), sdf2.parse(formattedTodaysDate + " " + todaysStartTime).toString());

                DateTime shiftDateTimeStart = new DateTime(sdf2.parse(formattedTodaysDate + " " + todaysStartTime)).withZone(DateTimeZone.forTimeZone(sdf2.getTimeZone()));

                return shiftDateTimeStart;


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    private boolean isShiftIsBetweenRangeYesterday(String yesterdaysStartTime) {

        Matcher m = P.matcher(yesterdaysStartTime);

        if (m.matches()) {

            DateTime yesterdaysDateTime = new DateTime(Calendar.getInstance().getTime()).minusDays(1).withZone(DateTimeZone.forID(PH_TIMEZONE));

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone(PH_TIMEZONE));

            try {
                String formattedYesterdaysDate = sdf.format(yesterdaysDateTime.toDate());

                Log.d(ShiftRange.class.getName(), "Format yesterdays date: " + formattedYesterdaysDate);

                DateFormat sdf2 = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                sdf2.setTimeZone(TimeZone.getTimeZone(PH_TIMEZONE));

                Log.d(ShiftRange.class.getName(), sdf2.parse(formattedYesterdaysDate + " " + yesterdaysStartTime).toString());

                DateTime shiftDateTimeEnd = new DateTime(sdf2.parse(formattedYesterdaysDate + " " + yesterdaysStartTime)).plusHours(9).withZone(DateTimeZone.forTimeZone(sdf2.getTimeZone()));


                if ((shiftDateTimeEnd.isEqual(shiftRange.getStartTime()) || shiftDateTimeEnd.isAfter(shiftRange.getStartTime())) &&
                        (shiftDateTimeEnd.isEqual(shiftRange.getEndTime()) || shiftDateTimeEnd.isBefore(shiftRange.getEndTime()))) {
                    return true;
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private DateTime getShiftIsBetweenRangeYesterday(String yesterdaysStartTime) {

        Matcher m = P.matcher(yesterdaysStartTime);

        if (m.matches()) {

            DateTime yesterdaysDateTime = new DateTime(Calendar.getInstance().getTime()).minusDays(1).withZone(DateTimeZone.forID(PH_TIMEZONE));

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone(PH_TIMEZONE));

            try {
                String formattedYesterdaysDate = sdf.format(yesterdaysDateTime.toDate());

                Log.d(ShiftRange.class.getName(), "Format yesterdays date: " + formattedYesterdaysDate);

                DateFormat sdf2 = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                sdf2.setTimeZone(TimeZone.getTimeZone(PH_TIMEZONE));

                Log.d(ShiftRange.class.getName(), sdf2.parse(formattedYesterdaysDate + " " + yesterdaysStartTime).toString());

                DateTime shiftDateTimeEnd = new DateTime(sdf2.parse(formattedYesterdaysDate + " " + yesterdaysStartTime)).withZone(DateTimeZone.forTimeZone(sdf2.getTimeZone()));

                return shiftDateTimeEnd;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;

    }

    public interface Listener extends BaseGSheetListener {
        void result(ArrayList<DataModel> employees);
    }
}
