package com.google.scheduler.rest;

import android.content.Context;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cicciolina on 6/6/18.
 */

public class RestGetLobInGSheet extends BaseGSheetAsyncTask {

    private Listener listener;
    private final static String RANGE = "B2:B";

    public RestGetLobInGSheet(GoogleAccountCredential googleAccountCredential,
                              Context context, Listener listener, String spreadsheetId,
                              String tabSheetName) {
        super(googleAccountCredential, context, listener, spreadsheetId, tabSheetName);
        this.listener = listener;
    }

    @Override
    public List<List<Object>>  getDataFromApi(Object... params) throws IOException {
        List<List<Object>> results = new ArrayList<>();

        String commentRange = tabSheetName+"!"+RANGE;

        ValueRange sheetRange = mService.spreadsheets().values()
                .get(spreadsheetId, commentRange)
                .execute();

        if(sheetRange != null && sheetRange.getValues() != null &&
                !sheetRange.getValues().isEmpty()) {
            results = sheetRange.getValues();
        }
        return results;
    }


    @Override
    protected void onPostExecute(Object output) {
        listener.result(translateToLobStr((List<List<Object>>)output));
    }

    private ArrayList<String> translateToLobStr(List<List<Object>> values){
        ArrayList<String>  lobList = new ArrayList<>();

        for(List<Object> lobArr: values){

            if(!lobList.contains(lobArr.get(0))) {
                lobList.add((String) lobArr.get(0));
            }

        }

        return lobList;
    }



    public interface Listener extends BaseGSheetListener {
        void result(ArrayList<String> lobList);
    }
}
