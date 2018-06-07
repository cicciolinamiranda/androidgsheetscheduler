package com.google.scheduler.enums;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

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

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

        String formattedDate = sdf.format(new Date());

        String SHIFT_DATE_FORMAT = "MM-dd-yyyy HH:mm";
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd-yyyy HH:mm");

        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM-dd-yyyy HH:mm");
        this.startTime = formatter.parseDateTime(formattedDate+" "+startTime).withZone(DateTimeZone.forID(PH_TIMEZONE));
        this.endTime = formatter.parseDateTime(formattedDate+" "+endTime).withZone(DateTimeZone.forID(PH_TIMEZONE));

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
}
