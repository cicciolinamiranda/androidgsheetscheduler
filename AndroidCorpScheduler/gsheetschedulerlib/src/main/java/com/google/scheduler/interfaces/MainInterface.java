package com.google.scheduler.interfaces;

import android.content.Intent;

import java.util.List;

/**
 * Created by cicciolina on 6/6/18.
 */

public interface MainInterface {

    void getEmployees(List<String> employees);
    void getLobsResponse(List<String> lobList);
    void requestForAuthorization(Intent intent);
    void userNotPermitted();

}
