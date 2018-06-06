package com.google.scheduler;

import android.app.Application;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

/**
 * Created by cicciolina on 6/6/18.
 */

public class MainApplication extends Application {

    private static String email;
    private static GoogleAccountCredential mCredential;
    private static String oAuthIdToken;

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        MainApplication.email = email;
    }

    public static GoogleAccountCredential getmCredential() {
        return mCredential;
    }

    public static void setmCredential(GoogleAccountCredential mCredential) {
        MainApplication.mCredential = mCredential;
    }

    public static String getoAuthIdToken() {
        return oAuthIdToken;
    }

    public static void setoAuthIdToken(String oAuthIdToken) {
        MainApplication.oAuthIdToken = oAuthIdToken;
    }
}
