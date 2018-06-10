package com.google.scheduler.presenter;

import android.content.Context;
import android.content.Intent;

import com.google.scheduler.MainApplication;
import com.google.scheduler.R;
import com.google.scheduler.enums.ShiftRange;
import com.google.scheduler.interfaces.MainInterface;
import com.google.scheduler.rest.RestGetLobInGSheet;
import com.google.scheduler.rest.RestGetTodaysActiveEmployeeInGSheet;
import com.google.scheduler.rest.RestUpdateSchedToAbsentGSheet;
import com.google.scheduler.view.DataModel;

import java.util.ArrayList;

/**
 * Created by cicciolina on 6/6/18.
 */

public class MainPresenter {

    private MainInterface listener;
    private Context context;

    public MainPresenter(Context context, MainInterface listener) {
        this.listener = listener;
        this.context = context;
    }

    public void getTodaysActiveEmployees(String lob, ShiftRange shiftRange) {

        if(shiftRange != null) {
            RestGetTodaysActiveEmployeeInGSheet restGetTodaysActiveEmployeeInGSheet = new RestGetTodaysActiveEmployeeInGSheet(
                    ((MainApplication) this.context.getApplicationContext()).getmCredential(),
                    this.context,
                    new RestGetTodaysActiveEmployeeInGSheet.Listener() {
                        @Override
                        public void result(ArrayList<DataModel> employees) {
                            listener.getEmployees(employees);
                        }

                        @Override
                        public void requestForAuthorization(Intent intent) {
                            listener.requestForAuthorization(intent);
                        }

                        @Override
                        public void userNotPermitted(String message) {
                            listener.userNotPermitted(message);
                        }
                    }, context.getString(R.string.spreadsheet_id),
                    context.getString(R.string.sheet_name), lob.toUpperCase(), shiftRange);

            restGetTodaysActiveEmployeeInGSheet.execute();
        }
    }

    public void getLobList() {

        RestGetLobInGSheet restGetLobInGSheet = new RestGetLobInGSheet(
                ((MainApplication)this.context.getApplicationContext()).getmCredential(),
                this.context,
                new RestGetLobInGSheet.Listener() {
                    @Override
                    public void result(ArrayList<String> lobList) {
                        listener.getLobsResponse(lobList);
                    }

                    @Override
                    public void requestForAuthorization(Intent intent) {
                        listener.requestForAuthorization(intent);
                    }

                    @Override
                    public void userNotPermitted(String message) {
                        listener.userNotPermitted(message);
                    }
                }, context.getString(R.string.spreadsheet_id),
                context.getString(R.string.sheet_name));

        restGetLobInGSheet.execute();
    }

    public void tagEmployeeAsAbsent(final DataModel dataModel) {

        RestUpdateSchedToAbsentGSheet restUpdateSchedToAbsentGSheet = new RestUpdateSchedToAbsentGSheet(
                ((MainApplication)this.context.getApplicationContext()).getmCredential(),
                this.context,
                new RestUpdateSchedToAbsentGSheet.Listener() {
                    @Override
                    public void result(Integer updatedRow) {

                        if(updatedRow != null && updatedRow > 0) {
                            listener.tagEmployeeAsAbsentResponse(true, dataModel);
                        } else {
                            listener.tagEmployeeAsAbsentResponse(false, dataModel);
                        }
                    }

                    @Override
                    public void requestForAuthorization(Intent intent) {
                        listener.requestForAuthorization(intent);
                    }

                    @Override
                    public void userNotPermitted(String message) {
                        listener.userNotPermitted(message);
                    }
                }, context.getString(R.string.spreadsheet_id),
                context.getString(R.string.sheet_name), dataModel);

        restUpdateSchedToAbsentGSheet.execute();
    }

}
