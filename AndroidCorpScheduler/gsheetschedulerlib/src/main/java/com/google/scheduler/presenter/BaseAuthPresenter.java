package com.google.scheduler.presenter;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.scheduler.MainApplication;
import com.google.scheduler.R;
import com.google.scheduler.util.Util;
import com.google.scheduler.view.LoginActivity;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import pub.devrel.easypermissions.EasyPermissions;

import static android.support.v4.content.PermissionChecker.PERMISSION_DENIED;
import static com.google.scheduler.constants.AppConstants.RC_GET_TOKEN;
import static com.google.scheduler.constants.AppConstants.REQUEST_GOOGLE_PLAY_SERVICES;
import static com.google.scheduler.constants.AppConstants.REQUEST_PERMISSIONS;
import static com.google.scheduler.constants.AppConstants.REQUEST_PERMISSION_GET_ACCOUNTS;
import static com.google.scheduler.constants.AppConstants.SCOPES;

/**
 * Created by cicciolina on 6/6/18.
 */

public class BaseAuthPresenter {

    protected Context mContext;
    protected Activity mActivity;
    protected GoogleApiClient mGoogleApiClient;
    protected static final HttpTransport transport = new NetHttpTransport();
    protected static final JsonFactory jsonFactory = new JacksonFactory();
    protected GoogleAccountCredential mCredential;

    public BaseAuthPresenter(Context mContext,
                                             Activity mActivity,
                                             GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener,
                                             GoogleApiClient.ConnectionCallbacks connectionCallbacks) {
        this.mContext = mContext;
        this.mActivity = mActivity;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mContext.getString(R.string.server_client_id))
//                .requestScopes(Drive.SCOPE_APPFOLDER, Drive.SCOPE_FILE)
                .requestEmail()
                .build();

        Util.getInstance().checkGPS(mContext);
        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage((FragmentActivity) mActivity /* FragmentActivity */, onConnectionFailedListener /* OnConnectionFailedListener */)
                .addConnectionCallbacks(connectionCallbacks)
//                .addApi(Drive.API)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        checkPermissions();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        validateServerClientID();
    }

    /**
     * Validates that there is a reasonable server client ID in strings.xml, this is only needed
     * to make sure users of this sample follow the README.
     */
    public void validateServerClientID() {
        String serverClientId = mContext.getString(R.string.server_client_id);
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            StringBuilder msg = new StringBuilder(mActivity.getResources().getString(R.string.error_msg_invalid_server_client_id));
            msg.append(suffix);

//            String message = "Invalid server client ID in strings.xml, must end with " + suffix;

            Log.w(BaseAuthPresenter.class.getName(), msg.toString());
            Util.getInstance().showSnackBarToast(mContext, msg.toString());
        }
    }

    public void checkPermissions() {

        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.CHANGE_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            Util.getInstance().requestPermission(mActivity);


        } else if (!EasyPermissions.hasPermissions(
                mContext, Manifest.permission.GET_ACCOUNTS)) {

            EasyPermissions.requestPermissions(
                    mActivity,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
        else {
            initGoogleCredential();
        }
    }


    /**
     *
     * This is for GSheet API rest calls
     *
     * **/
    public void initGoogleCredential() {
        mCredential = GoogleAccountCredential.usingOAuth2(
                mContext.getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        if(((MainApplication)mContext.getApplicationContext()).getEmail() != null &&
                !((MainApplication)mContext.getApplicationContext()).getEmail().isEmpty()) {
            setGoogleCredentialAccount(((MainApplication)mContext.getApplicationContext()).getEmail());
        }

        if(!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        }
    }


    public void verifyToken() {

        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() &&
                ((MainApplication)mContext.getApplicationContext()).getoAuthIdToken() != null &&
                ((MainApplication)mContext.getApplicationContext()).getoAuthIdToken().isEmpty()) {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    .setAudience(Collections.singletonList(mContext.getString(R.string.server_client_id)))
                    .build();

            GoogleIdToken idToken = null;

            try {
                idToken = verifier.verify(((MainApplication)mContext.getApplicationContext()).getoAuthIdToken());
                if (idToken != null) {
                    GoogleIdToken.Payload payload = idToken.getPayload();

                    //Get token expiration date
                    Log.d(BaseAuthPresenter.class.getName(), "token code exp:" + payload.getExpirationTimeSeconds());

                    // validate timestamps
                    long timestampNow = (new Date()).getTime();
                    long timestampIssued = payload.getIssuedAtTimeSeconds() * 1000;
                    long timestampExpired = payload.getExpirationTimeSeconds() * 1000;
                    if (timestampIssued > timestampNow || timestampExpired < timestampNow) {
                        //token is expired.signout to get new token
                        Util.getInstance().showSnackBarToast(mContext, mContext.getString(R.string.session_is_expired));
                        signOut();
                    }

                } else {
                    Util.getInstance().showSnackBarToast(mContext, mContext.getString(R.string.session_is_expired));
                    signOut();
                }

            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public void onStart() {
        if(mGoogleApiClient != null) mGoogleApiClient.connect();
    }

    public void onDestroy() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    public void signOut() {

        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Log.d(BaseAuthPresenter.class.getName(), "signOut:onResult:" + status);
                            Intent mainIntent = new Intent(mContext, LoginActivity.class);
                            mActivity.startActivityForResult(mainIntent, 0);
                            mActivity.finish();
                        }
                    });
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean callForRecheckPermissions = false;
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_DENIED) {
                    callForRecheckPermissions = true;
                }
            }
            if (callForRecheckPermissions) {
                checkPermissions();
            } else {
                initGoogleCredential();
            }
        }
    }
    public void setGoogleCredentialAccount(String email) {
        if(isGooglePlayServicesAvailable() && mCredential != null && email != null && !email.isEmpty()) {
            mCredential.setSelectedAccountName(email);
            ((MainApplication)mContext.getApplicationContext()).setmCredential(mCredential);
        } else {
            acquireGooglePlayServices();
        }
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mContext);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    public void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mContext);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                mActivity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void signInWithGplus() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        if(mGoogleApiClient != null) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            mActivity.startActivityForResult(signInIntent, RC_GET_TOKEN);
        }else {
            Util.getInstance().showSnackBarToast(mContext, mContext.getResources().getString(R.string.error_sign_in));
        }
    }
}
