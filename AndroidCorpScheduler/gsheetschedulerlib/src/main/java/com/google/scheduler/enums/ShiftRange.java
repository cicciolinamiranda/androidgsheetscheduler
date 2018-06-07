package com.google.scheduler.enums;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.google.scheduler.constants.AppConstants.PH_TIMEZONE;


/**
 * Created by cicciolina on 6/7/18.
 */

public enum ShiftRange {

    SIXAM_TO_THREEPM("6:00", "15:00","6:00 to 15:00(MNL TIME)"),
    TWOPM_TO_ELEVENPM("14:00", "23:00","14:00 to 23:00(MNL TIME)"),
    TENPM_TO_SEVENAM("22:00", "7:00","22:00-7:00(MNL TIME)")
    ;

    DateTime startTime;
    DateTime endTime;
    String label;

    ShiftRange(String startTime, String endTime, String label) {
        DateTime currentDateTime = new DateTime(Calendar.getInstance().getTime()).withZone(DateTimeZone.forID(PH_TIMEZONE));

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

        try {
            String formattedDate = sdf.format(currentDateTime.toDate());

            Log.d(ShiftRange.class.getName(), "Format date: "+formattedDate);

            DateFormat sdf2 = new SimpleDateFormat("MM-dd-yyyy HH:mm");

            Log.d(ShiftRange.class.getName(), sdf2.parse(formattedDate+" "+startTime).toString());
            Log.d(ShiftRange.class.getName(), sdf2.parse(formattedDate+" "+endTime).toString());

//            Date startTimeDate = shiftTimeZone(sdf2.parse(formattedDate+" "+startTime), TimeZone.getTimeZone("GMT"), TimeZone.getTimeZone("EST"));
//            Date endTimeDate = shiftTimeZone(sdf2.parse(formattedDate+" "+endTime), TimeZone.getTimeZone("GMT"), TimeZone.getTimeZone("EST"));

//            Log.d(ShiftRange.class.getName(), startTimeDate.toString());
//            Log.d(ShiftRange.class.getName(), endTimeDate.toString());

            this.startTime = new DateTime(sdf2.parse(formattedDate+" "+startTime)).withZone(DateTimeZone.forID(PH_TIMEZONE));
            this.endTime = new DateTime(sdf2.parse(formattedDate+" "+endTime)).withZone(DateTimeZone.forID(PH_TIMEZONE));
            Log.d(ShiftRange.class.getName(), this.startTime.toString());
            Log.d(ShiftRange.class.getName(), this.startTime.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }


//        Calendar startCal = Calendar.getInstance();
//        startCal.set(Calendar.HOUR_OF_DAY,startTime);
//        startCal.set(Calendar.MINUTE, 0);
//        startCal.set(Calendar.SECOND, 0);
//
//        Calendar endCal = Calendar.getInstance();
//        endCal.set(Calendar.HOUR_OF_DAY,endTime);
//        endCal.set(Calendar.MINUTE, 0);
//        endCal.set(Calendar.SECOND, 0);

//        this.startTime = new DateTime(startCal).withZone(DateTimeZone.forID(PH_TIMEZONE));
//        this.endTime = new DateTime(endCal).withZone(DateTimeZone.forID(PH_TIMEZONE));

        this.label = label;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public String getLabel() {
        return label;
    }

    private Date shiftTimeZone(Date date, TimeZone sourceTimeZone, TimeZone targetTimeZone) {
        Calendar sourceCalendar = Calendar.getInstance();
        sourceCalendar.setTime(date);
        sourceCalendar.setTimeZone(sourceTimeZone);

        Calendar targetCalendar = Calendar.getInstance();
        for (int field : new int[] {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND}) {
            targetCalendar.set(field, sourceCalendar.get(field));
        }
        targetCalendar.setTimeZone(targetTimeZone);

        Log.d(ShiftRange.class.getName(), targetCalendar.getTimeZone().toString());


        return targetCalendar.getTime();
    }
}
