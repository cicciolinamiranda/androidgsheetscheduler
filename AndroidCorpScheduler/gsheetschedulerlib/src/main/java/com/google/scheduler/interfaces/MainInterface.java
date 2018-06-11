package com.google.scheduler.interfaces;

import android.content.Intent;

import com.google.scheduler.view.DataModel;

import java.util.List;

/**
 * Created by cicciolina on 6/6/18.
 */

public interface MainInterface {

    void getEmployees(List<DataModel> employees);
    void getLobsResponse(List<String> lobList);
    void requestForAuthorization(Intent intent);
    void userNotPermitted(String message);
    void tagOrUnTagEmployeeAsAbsentResponse(boolean isSuccessful, DataModel dataModel);

}
