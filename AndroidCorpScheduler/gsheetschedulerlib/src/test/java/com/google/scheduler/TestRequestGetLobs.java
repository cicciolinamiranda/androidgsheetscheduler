package com.google.scheduler;

import android.content.Intent;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.scheduler.rest.RestGetLobInGSheet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLooper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.scheduler.constants.AppConstants.SCOPES;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by cicciolina on 6/6/18.
 */

    @Config(application = com.google.scheduler.MainApplication.class, manifest=Config.NONE, sdk = 26,
        constants = BuildConfig.class, packageName = "com.google.scheduler")
@RunWith(RobolectricTestRunner.class)
public class TestRequestGetLobs {

    private List result = new ArrayList();

    @Before
    public void setUp() throws Exception {
        Robolectric.getBackgroundThreadScheduler().pause();
        Robolectric.getForegroundThreadScheduler().pause();
    }

    @Test
    public void testGetLobs() {

        ((MainApplication)RuntimeEnvironment.application.getApplicationContext()).setmCredential(GoogleAccountCredential.usingOAuth2(
                RuntimeEnvironment.application.getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff()));

        ((MainApplication)RuntimeEnvironment.application.getApplicationContext()).getmCredential().setSelectedAccountName("nbutest.mnl@gmail.com");
        RestGetLobInGSheet restGetLobInGSheet = new RestGetLobInGSheet(
                ((MainApplication)RuntimeEnvironment.application.getApplicationContext()).getmCredential(),
                RuntimeEnvironment.application.getApplicationContext(),
                new RestGetLobInGSheet.Listener() {
                    @Override
                    public void result(ArrayList<String> lobList) {
                        TestRequestGetLobs.this.result = lobList;
                    }

                    @Override
                    public void requestForAuthorization(Intent intent) {

                    }
                }, "9AX4vx_mbm8AshMwhfFS5pavCvHnQD9LM",
                "Schedule Dashboard");

        restGetLobInGSheet.execute();

        ShadowApplication.runBackgroundTasks();

        ShadowLooper.runUiThreadTasks();

        assertNotEquals(0, result.size());
    }

}
