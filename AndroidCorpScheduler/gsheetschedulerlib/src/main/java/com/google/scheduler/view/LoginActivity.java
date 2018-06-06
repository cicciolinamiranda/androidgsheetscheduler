package com.google.scheduler.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.scheduler.MainApplication;
import com.google.scheduler.R;
import com.google.scheduler.enums.NetworkTypes;
import com.google.scheduler.util.Util;

import static com.google.scheduler.constants.AppConstants.RC_GET_TOKEN;

public class LoginActivity extends BaseAuthActivity implements View.OnClickListener {

    protected Button btnSignIn;
    protected LinearLayout mainLayoutActivityLogin;
    protected ActionBar actionBar;
    protected TextView titleText;
    private View mProgressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnSignIn = findViewById(R.id.btn_sign_in);
        mainLayoutActivityLogin = findViewById(R.id.main_layout_activity_login);
        mainLayoutActivityLogin.setVisibility(View.GONE);
        mProgressView = findViewById(R.id.login_loading_layout);

        // Button click listeners
        btnSignIn.setOnClickListener(this);
        actionBar = this.getSupportActionBar();
        actionBar.hide();
        titleText = findViewById(R.id.title_text);
        titleText.setVisibility(View.VISIBLE);
        titleText.setText(getString(R.string.app_name)+" "+getString(R.string.app_version));

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mProgressView != null) mProgressView.setVisibility(View.VISIBLE);

        if (baseWithAuthPresenter != null && baseWithAuthPresenter.getmGoogleApiClient() != null) {
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(baseWithAuthPresenter.getmGoogleApiClient());
            if (opr.isDone()) {
                // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                // and the GoogleSignInResult will be available instantly.
                Log.d(BaseAuthActivity.class.getName(), "Got cached sign-in");
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                // If the user has not previously signed in on this device or the sign-in has expired,
                // this asynchronous branch will attempt to sign in the user silently.  Cross-device
                // single sign-on will occur in this branch.
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GET_TOKEN) {
            // [START get_id_token]
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(LoginActivity.class.getName(), "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());

            if (result.isSuccess()) {
                String idToken = result.getSignInAccount().getIdToken();
                ((MainApplication)getApplicationContext()).setEmail(result.getSignInAccount().getEmail());

                Log.d(LoginActivity.class.getName(), "onActivityResult:ID TOKEN:" + idToken);
                ((MainApplication)getApplicationContext()).setoAuthIdToken(idToken);
            }
            // [END get_id_token]

            handleSignInResult(result);
        }
    }

    protected void handleSignInResult(GoogleSignInResult result) {



        if (result.isSuccess()) {

            String email = result.getSignInAccount().getEmail();
            if (email != null && !email.isEmpty()) {

                String idToken = result.getSignInAccount().getIdToken();

                Log.d(LoginActivity.class.getName(), "ID TOKEN:" + idToken);

                ((MainApplication)getApplicationContext()).setEmail(result.getSignInAccount().getEmail());

                updateUI(true);

            }
        } else {
            updateUI(false);
        }
    }

    public void updateUI(boolean isSuccess) {
        mProgressView.setVisibility(View.GONE);
        if (isSuccess && baseWithAuthPresenter != null) {

            Intent intent;
            intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if(!isSuccess && Util.getInstance().getNetwork(LoginActivity.this) == NetworkTypes.NO_CONNECTION ) {

            Toast.makeText(LoginActivity.this, getString(R.string.no_network_avail), Toast.LENGTH_SHORT).show();
            signOutUIUpdate();
        }
        else {
            signOutUIUpdate();
        }
    }

    public void signOutUIUpdate(){
        mainLayoutActivityLogin.setVisibility(View.VISIBLE);
        btnSignIn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_sign_in) {// Signin button clicked
            onBtnSignInClick();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //nothing to do
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return false;
    }

    public void onBtnSignInClick() {
        if(baseWithAuthPresenter != null) {
            mProgressView.setVisibility(View.VISIBLE);
            baseWithAuthPresenter.signInWithGplus();
        }
    }
}
