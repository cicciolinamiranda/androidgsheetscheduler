package com.google.scheduler.enums;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


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

        DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
        this.startTime = formatter.parseDateTime(startTime);
        this.endTime = formatter.parseDateTime(endTime);

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
