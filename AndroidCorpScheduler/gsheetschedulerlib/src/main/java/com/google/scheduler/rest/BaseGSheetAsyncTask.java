package com.google.scheduler.rest;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.scheduler.R;

import java.io.IOException;

/**
 * Created by cicciolina on 6/6/18.
 */

public abstract class BaseGSheetAsyncTask extends AsyncTask<Object, Void, Object> {

    protected Context context;
    protected String spreadsheetId;
    protected String tabSheetName;
    protected BaseGSheetListener listener;
    protected com.google.api.services.sheets.v4.Sheets mService = null;
    protected HttpTransport transport;
    protected JsonFactory jsonFactory;

    public BaseGSheetAsyncTask(GoogleAccountCredential googleAccountCredential,
                                Context context,
                               BaseGSheetListener listener,
                               String spreadsheetId,
                               String tabSheetName) {
        this.context = context;
        this.spreadsheetId = spreadsheetId;
        this.tabSheetName = tabSheetName;
        this.listener = listener;
        transport = AndroidHttp.newCompatibleTransport();
        jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                transport, jsonFactory, googleAccountCredential)
                .setApplicationName(context.getString(R.string.app_name))
                .build();
    }

    @Override
    protected Object doInBackground(Object... params) {
        try {
            return getDataFromApi(params);
        } catch (UserRecoverableAuthIOException e) {
            listener.requestForAuthorization(e.getIntent());
            e.printStackTrace();
            cancel(true);
            return null;
        }catch (GoogleJsonResponseException e) {
            if(e.getStatusCode() == 403 || e.getStatusCode() == 400) {
                listener.userNotPermitted();
            }
            e.printStackTrace();
            cancel(true);
            return null;
        }
        catch (IllegalArgumentException e) {
            listener.userNotPermitted();
            e.printStackTrace();
            cancel(true);
            return null;
        }
        catch (Exception e) {
            listener.userNotPermitted();
            e.printStackTrace();
            cancel(true);
            return null;
        }
    }

    public abstract Object getDataFromApi(Object... params) throws IOException;

    public interface BaseGSheetListener {
        void requestForAuthorization(Intent intent);
        void userNotPermitted();
    }
}
