package com.google.scheduler.rest;

import android.content.Context;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.scheduler.enums.ShiftRange;
import com.google.scheduler.util.Util;
import com.google.scheduler.view.DataModel;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

            String dateColumn = getTodaysDateColumn(dateSheetRange.getValues());
            String timeDataRange = tabSheetName+"!"+dateColumn+"2:"+dateColumn;

            ValueRange timeSheetRange = mService.spreadsheets().values()
                    .get(spreadsheetId, timeDataRange)
                    .execute();

            if(timeSheetRange != null && timeSheetRange.getValues() != null &&
                    !timeSheetRange.getValues().isEmpty()) {

                List<Integer> indexThatNeedsToBeRetrieved = getListOfRowNumThatIsOnDuty(timeSheetRange.getValues());

                String employeeDataRange = tabSheetName+"!"+WHOLE_WITHOUT_HEADERS_DATA_RANGE+dateColumn;
                ValueRange wholeDataRange = mService.spreadsheets().values()
                        .get(spreadsheetId,  employeeDataRange)
                        .execute();

                if(wholeDataRange != null && wholeDataRange.getValues() != null &&
                        !wholeDataRange.getValues().isEmpty()) {

                    for(Integer rowData: indexThatNeedsToBeRetrieved) {
                        results.add(wholeDataRange.getValues().get(rowData));
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

        String column = "F";

        String todaysDateStr = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
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

    private List<Integer> getListOfRowNumThatIsOnDuty(List<List<Object>> values){

        List<Integer> listOfRowsThatIsOnDuty = new ArrayList<>();

        Pattern p = Pattern.compile(".*([01]?[0-9]|2[0-3]):[0-5][0-9].*");

        for(int i=0; i < values.size(); i++) {
            List<Object> timestampArr =  values.get(i);
            if(!timestampArr.isEmpty()) {
                Matcher m = p.matcher((String) timestampArr.get(0));

                if (m.matches()) {
                    listOfRowsThatIsOnDuty.add(i);
                }
            }

        }

        return listOfRowsThatIsOnDuty;
    }

    private ArrayList<DataModel> translateToEmployeeNames(List<List<Object>> values){
        ArrayList<DataModel>  employeeList = new ArrayList<>();

        for(List<Object> employeeArr: values){

                if(!employeeArr.isEmpty() && employeeArr.size() >= 4 &&
                        ((String)employeeArr.get(1)).equalsIgnoreCase(lob)
                        &&
                        isShiftIsBetweenRange(((String)employeeArr.get(employeeArr.size()-1)))
                        ) {
                    employeeList.add(new DataModel(((String)employeeArr.get(4)),((String)employeeArr.get(1)),((String)employeeArr.get(3)),((String)employeeArr.get(employeeArr.size()-1))));
                }

        }

        return employeeList;
    }

    private boolean isShiftIsBetweenRange(String time) {

        String [] timeSplits = time.split(":");

        if(timeSplits.length >= 2) {
            Calendar shiftCal = Calendar.getInstance();
            shiftCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeSplits[0]));
            shiftCal.set(Calendar.MINUTE, 0);
            shiftCal.set(Calendar.SECOND, 0);

            DateTime shiftDateTime = new DateTime(shiftCal).withZone(DateTimeZone.forID(PH_TIMEZONE));

            if ((shiftDateTime.isEqual(shiftRange.getStartTime()) || shiftDateTime.isAfter(shiftRange.getStartTime())) &&
                    (shiftDateTime.isEqual(shiftRange.getEndTime()) || shiftDateTime.isBefore(shiftRange.getEndTime()))) {
                return true;
            }
        }

        return false;
    }



    public interface Listener extends BaseGSheetListener {
        void result(ArrayList<DataModel> employees);
    }
}
