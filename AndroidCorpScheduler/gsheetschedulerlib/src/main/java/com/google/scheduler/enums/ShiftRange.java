package com.google.scheduler.enums;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Calendar;


/**
 * Created by cicciolina on 6/7/18.
 */

public enum ShiftRange {

    SIXAM_TO_THREEPM(6, 15,"6:00 to 15:00(MNL TIME)"),
    TWOPM_TO_ELEVENPM(14, 23,"14:00 to 23:00(MNL TIME)"),
    TENPM_TO_SEVENAM(22, 7,"22:00-7:00(MNL TIME)")
    ;

    DateTime startTime;
    DateTime endTime;
    String label;

    ShiftRange(int startTime, int endTime, String label) {

        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.HOUR_OF_DAY,startTime);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.HOUR_OF_DAY,endTime);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);

        this.startTime = new DateTime(startCal).withZone(DateTimeZone.forID("Asia/Tokyo"));
        this.endTime = new DateTime(endCal).withZone(DateTimeZone.forID("Asia/Tokyo"));

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
