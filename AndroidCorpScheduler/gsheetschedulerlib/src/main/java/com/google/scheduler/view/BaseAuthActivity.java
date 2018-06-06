package com.google.scheduler.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.scheduler.MainApplication;
import com.google.scheduler.R;
import com.google.scheduler.presenter.BaseAuthPresenter;
import com.google.scheduler.util.Util;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static com.google.scheduler.constants.AppConstants.REQUEST_AUTHORIZATION;
import static com.google.scheduler.constants.AppConstants.REQUEST_GOOGLE_PLAY_SERVICES;

/**
 * Created by cicciolina on 6/6/18.
 */

public class BaseAuthActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        EasyPermissions.PermissionCallbacks {

    protected BaseAuthPresenter baseWithAuthPresenter;
    protected MenuItem refreshMenu;
    protected MenuItem logoutMenu;
    protected List<String> tabSheetNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_white_24dp);


        tabSheetNames = new ArrayList<String>() {{
            add(getString(R.string.sheet_name));


        }};

        baseWithAuthPresenter = new BaseAuthPresenter(this, this, this, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (baseWithAuthPresenter != null)
            baseWithAuthPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (baseWithAuthPresenter != null) baseWithAuthPresenter.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (baseWithAuthPresenter != null) {
            baseWithAuthPresenter.onDestroy();
        }
    }

    /**
     * Sign-out from google
     */
    public void signOutFromGplus() {
        if (baseWithAuthPresenter != null) baseWithAuthPresenter.signOut();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu_base_auth, menu);
        refreshMenu = menu.findItem(R.id.menu_main_refresh);
        refreshMenu.setVisible(false);
        logoutMenu = menu.findItem(R.id.menu_main_logout);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        Intent intent;

        if (id == android.R.id.home) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.menu_main_logout) {

            logoutDialog();
        }
        return true;
    }

    protected void logoutDialog() {

        new AlertDialog.Builder(BaseAuthActivity.this)
                .setCancelable(false)
                .setMessage(BaseAuthActivity.this.getString(R.string.label_wanna_logout))
                .setPositiveButton(BaseAuthActivity.this.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void logout() {
        signOutFromGplus();
        ((MainApplication)getApplicationContext()).setmCredential(null);


        Intent logout = new Intent(this, MainActivity.class);
        startActivity(logout);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (baseWithAuthPresenter != null) baseWithAuthPresenter.verifyToken();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (baseWithAuthPresenter != null) baseWithAuthPresenter.onStart();
    }



    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(baseWithAuthPresenter != null) {
            baseWithAuthPresenter.initGoogleCredential();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //TODO: cicci: what to do
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Util.getInstance().showSnackBarToast(this,
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    startActivity(getIntent());
                    finish();
                }
                break;
            case REQUEST_AUTHORIZATION:
                logout();
                break;
        }
    }

}
