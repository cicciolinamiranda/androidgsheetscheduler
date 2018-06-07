package com.google.scheduler.enums;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import static com.google.scheduler.constants.AppConstants.PH_TIMEZONE;


/**
 * Created by cicciolina on 6/7/18.
 */

public enum ShiftRange {

    TWELVEAM_TO_TWELVEPM("00:00", "12:00","12am to 12pm(MNL TIME)"),
    TWELVEPM_TO_TWELVEAM("12:01", "23:59","12pm to 12am(MNL TIME)");
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

            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();

            this.startTime = new DateTime(sdf2.parse(formattedDate+" "+startTime)).withZone(DateTimeZone.forTimeZone(tz));
            this.endTime = new DateTime(sdf2.parse(formattedDate+" "+endTime)).withZone(DateTimeZone.forTimeZone(tz));
            Log.d("START2", this.startTime.toString());
            Log.d(ShiftRange.class.getName(), this.startTime.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        this.label = label;
    }

    public DateTime getStartTime() {
        return this.startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public String getLabel() {
        return label;
    }
}
