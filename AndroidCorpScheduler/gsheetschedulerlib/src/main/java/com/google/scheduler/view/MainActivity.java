package com.google.scheduler.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.scheduler.R;
import com.google.scheduler.enums.ShiftRange;
import com.google.scheduler.interfaces.MainInterface;
import com.google.scheduler.presenter.MainPresenter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.google.scheduler.constants.AppConstants.PH_TIMEZONE;
import static com.google.scheduler.constants.AppConstants.REQUEST_PERMISSIONS;

public class MainActivity extends BaseAuthActivity implements MainInterface {


    private ListView main_list;
    private MainListAdapter adapter;
    private Spinner spinner;
    private MainPresenter mainPresenter;
    private TextView tvShiftRange;
    private ProgressBar loader_bar;
    private RelativeLayout emptyListMsgLayout;
    private List<String> lobList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainPresenter = new MainPresenter(this, this);
        mainPresenter.getLobList();
        tvShiftRange = findViewById(R.id.tv_shift_range);
        main_list = findViewById(R.id.main_list);
        emptyListMsgLayout = findViewById(R.id.rl_empty_list_row);
        main_list.setVisibility(View.GONE);

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(lobList != null && !lobList.isEmpty() && getShiftRange() != null && !getShiftRange().isEmpty()) {
                    main_list.setVisibility(View.GONE);
                    loader_bar.setVisibility(View.VISIBLE);
                    mainPresenter.getTodaysActiveEmployees(lobList.get(position), getShiftRange());
                } else {
                    loader_bar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        loader_bar = findViewById(R.id.loading_progress);

        setTimeRangeText();

    }

    private void refreshData() {

        main_list.setVisibility(View.GONE);
        loader_bar.setVisibility(View.VISIBLE);
        setTimeRangeText();
        mainPresenter.getLobList();
    }

    private void setTimeRangeText() {

        if(getShiftRange() != null && !getShiftRange().isEmpty()) {
            StringBuilder range = new StringBuilder();
            for(ShiftRange shiftRange: getShiftRange()) {
                range.append(shiftRange.getLabel());
                range.append("\n");
            }

            tvShiftRange.setText(range.toString());

        }

    }

    private List<ShiftRange> getShiftRange () {
        DateTime currentDateTime = new DateTime(Calendar.getInstance().getTime());
        List<ShiftRange> results = new ArrayList<>();
        Log.d("START", currentDateTime.toString());

        if((currentDateTime.isEqual(ShiftRange.SIXAM_TO_THREEPM.getStartTime()) || currentDateTime.isAfter(ShiftRange.SIXAM_TO_THREEPM.getStartTime())) &&
                (currentDateTime.isEqual(ShiftRange.SIXAM_TO_THREEPM.getEndTime()) || currentDateTime.isBefore(ShiftRange.SIXAM_TO_THREEPM.getEndTime()))) {
            results.add(ShiftRange.SIXAM_TO_THREEPM);
        }

        else if((currentDateTime.isEqual(ShiftRange.TWOPM_TO_ELEVENPM.getStartTime()) || currentDateTime.isAfter(ShiftRange.TWOPM_TO_ELEVENPM.getStartTime())) &&
                (currentDateTime.isEqual(ShiftRange.TWOPM_TO_ELEVENPM.getEndTime()) || currentDateTime.isBefore(ShiftRange.TWOPM_TO_ELEVENPM.getEndTime()))) {
            results.add(ShiftRange.SIXAM_TO_THREEPM);
        }

        else if((currentDateTime.isEqual(ShiftRange.TENPM_TO_SEVENAM.getStartTime()) || currentDateTime.isAfter(ShiftRange.TENPM_TO_SEVENAM.getStartTime())) &&
                (currentDateTime.isEqual(ShiftRange.TENPM_TO_SEVENAM.getEndTime()) || currentDateTime.isBefore(ShiftRange.TENPM_TO_SEVENAM.getEndTime()))) {
            results.add(ShiftRange.SIXAM_TO_THREEPM);
        }

        return results;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            boolean callForRecheckPermissions = false;
            if (!callForRecheckPermissions) {
                refreshData();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PERMISSIONS && resultCode == RESULT_OK) {
            refreshData();
        }
    }

    @Override
    public void getEmployees(List<DataModel> employees) {

        loader_bar.setVisibility(View.GONE);

        Log.d(MainActivity.class.getName(), employees.toString());
        emptyListMsgLayout.setVisibility(View.GONE);

        if(employees != null && !employees.isEmpty()) {
            adapter = new MainListAdapter(employees, MainActivity.this);
            main_list.setAdapter(adapter);
            main_list.setVisibility(View.VISIBLE);
        } else {
            emptyListMsgLayout.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        refreshMenu.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        int id = item.getItemId();

        if (id == R.id.menu_main_refresh) {
            refreshData();
        }
        return true;
    }



    @Override
    public void getLobsResponse(final List<String> lobList) {
        Log.d(MainActivity.class.getName(), lobList.toString());
        this.lobList = lobList;

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                this,R.layout.spinner_text,lobList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_text);
        spinner.setAdapter(spinnerArrayAdapter);

    }

    @Override
    public void requestForAuthorization(Intent intent) {
        startActivityForResult(intent, REQUEST_PERMISSIONS);
    }

    @Override
    public void userNotPermitted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, getString(R.string.error_user_unauthorized_in_gsheet)+ getString(R.string.spreadsheet_id), Toast.LENGTH_SHORT).show();
                logout();
            }
        });
    }
}
