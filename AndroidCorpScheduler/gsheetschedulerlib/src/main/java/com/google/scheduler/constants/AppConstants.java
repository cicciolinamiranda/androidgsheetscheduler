package com.google.scheduler.constants;

import com.google.api.services.drive.DriveScopes;

/**
 * Created by cicciolina on 6/6/18.
 */

public class AppConstants {

    public static final int REQUEST_PERMISSIONS = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    public static final int RC_GET_TOKEN = 9002;

    public static final int REQUEST_AUTHORIZATION = 0x96;
    public static final String[] SCOPES = { DriveScopes.DRIVE };

    public static final String PH_TIMEZONE = "Asia/Singapore";

}
