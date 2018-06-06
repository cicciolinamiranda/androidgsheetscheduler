package com.google.scheduler.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import static com.google.scheduler.constants.AppConstants.REQUEST_PERMISSIONS;

/**
 * Created by cicciolina on 6/6/18.
 */

public class Util {

    private static Util instance;

    private Util() {}

    public static Util getInstance() {

        if (instance == null) {
            instance = new Util();
        }
        return instance;
    }

    /**
     * Checks if GPS is turned on. If not prompts user to turn it on.
     *
     * @param context - either the Activity or the context passed by the receiver.
     */
    public boolean checkGPS(final Context context) {
        boolean enabled = false;

        if (context !=null) {
            LocationManager mlocManager = (LocationManager) context.getSystemService(
                    Context.LOCATION_SERVICE);
            enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!enabled) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Location needed")
                        .setMessage("App needs your current location. Enable GPS?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        });

                builder.create().show();
            }
        }

        return enabled;
    }

    public void requestPermission(Activity activity){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_NETWORK_STATE
                        ,Manifest.permission.CHANGE_NETWORK_STATE
                        , Manifest.permission.ACCESS_NETWORK_STATE
                        , Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.GET_ACCOUNTS
/*                        , Manifest.permission.READ_PHONE_STATE
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE*/

                }, REQUEST_PERMISSIONS);

    }

    public void showSnackBarToast(Context context, String message){
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            // Do something for lollipop and above versions
            Snackbar.make(((Activity)context).getWindow().getDecorView().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        } else{
            // do something for phones running an SDK before lollipop
            Toast.makeText(context,message,Toast.LENGTH_LONG).show();
        }
    }
}
