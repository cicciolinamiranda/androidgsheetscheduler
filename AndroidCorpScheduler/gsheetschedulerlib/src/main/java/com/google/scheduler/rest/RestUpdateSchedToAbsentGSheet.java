package com.google.scheduler.rest;

import android.content.Context;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.scheduler.view.DataModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cicciolina on 6/11/18.
 */

public class RestUpdateSchedToAbsentGSheet extends BaseGSheetAsyncTask {

    private Listener listener;
    private DataModel dataModel;

    private final static String WHOLE_WITHOUT_HEADERS_DATA_RANGE = "A2:";

    public RestUpdateSchedToAbsentGSheet(GoogleAccountCredential googleAccountCredential,
                                         Context context,
                                         Listener listener,
                                         String spreadsheetId,
                                         String tabSheetName,
                                         DataModel dataModel) {
        super(googleAccountCredential, context, listener, spreadsheetId, tabSheetName);
        this.listener = listener;
        this.dataModel = dataModel;
    }
    @Override
    public Integer getDataFromApi(Object... params) throws IOException {

        if(dataModel.getColumnLetter() != null && !dataModel.getColumnLetter().isEmpty()) {

            List<Object> itemList = new ArrayList<>();
            itemList.add("A");

            String employeeDataRange = tabSheetName + "!" + WHOLE_WITHOUT_HEADERS_DATA_RANGE + dataModel.getColumnLetter();

            ValueRange wholeDataRange = mService.spreadsheets().values()
                    .get(spreadsheetId, employeeDataRange)
                    .execute();

            if (wholeDataRange != null && wholeDataRange.getValues() != null &&
                    !wholeDataRange.getValues().isEmpty()) {

                for (int i = 0; i <= wholeDataRange.getValues().size()-1; i++) {
                    List<Object> deviceArr = wholeDataRange.getValues().get(i);

                    if (deviceArr != null && !deviceArr.isEmpty() && deviceArr.size() >=5 &&
                            deviceArr.get(1) != null &&
                            ((String) deviceArr.get(1)).equalsIgnoreCase(dataModel.getTierGroup() + "") &&
                            deviceArr.get(3) != null &&
                            ((String) deviceArr.get(3)).equalsIgnoreCase(dataModel.getRole() + "") &&
                            deviceArr.get(4) != null &&
                            ((String) deviceArr.get(4)).equalsIgnoreCase(dataModel.getName() + "")) {
                        int id = i + 2;
                        String updateRange = tabSheetName + "!" + this.dataModel.getColumnLetter() + id + ":"+this.dataModel.getColumnLetter();
                        List<List<Object>> itemValues = Arrays.asList(itemList);
                        ValueRange body = new ValueRange().setValues(itemValues);

                        UpdateValuesResponse response = mService.spreadsheets().values().update(spreadsheetId, updateRange, body).setValueInputOption("RAW").execute();

                        return response.getUpdatedRows();
                    }
                }
            }

        }

        return 0;
    }

    @Override
    protected void onPostExecute(Object output) {
        listener.result((Integer) output);
    }

    public interface Listener extends BaseGSheetListener {
        void result(Integer updatedRow);
    }
}
