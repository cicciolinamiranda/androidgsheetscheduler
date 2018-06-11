package com.google.scheduler.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.scheduler.R;
import com.google.scheduler.enums.ShiftRange;
import com.google.scheduler.interfaces.MainInterface;
import com.google.scheduler.presenter.MainPresenter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.google.scheduler.constants.AppConstants.PH_TIMEZONE;
import static com.google.scheduler.constants.AppConstants.REQUEST_PERMISSIONS;

public class MainActivity extends BaseAuthActivity implements MainInterface {


    private ListView main_list;
    private MainListAdapter adapter;
    private Spinner spinner;
    private MainPresenter mainPresenter;
    private ProgressBar loader_bar;
    private RelativeLayout emptyListMsgLayout;
    private List<String> lobList;
    private ImageButton btn_sort;
    private boolean isAscending;
    private List<DataModel> employees = new ArrayList<>();

    private boolean isUserNotPermittedAlreadyCalled;
    private AlertDialog tagAsAbsentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainPresenter = new MainPresenter(this, this);
        mainPresenter.getLobList();
        main_list = findViewById(R.id.main_list);

        View listHeaderView = getLayoutInflater().inflate(R.layout.header_main_list,null);
        main_list.addHeaderView(listHeaderView, null, false);
        main_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {

                final int newPosition = position - main_list.getHeaderViewsCount();

                if(!employees.isEmpty() && employees.get(newPosition) != null) {
                    AlertDialog.Builder selectEmployeeDialog  = new AlertDialog.Builder(
                            MainActivity.this);

                    selectEmployeeDialog.setTitle(employees.get(newPosition).getName());
                    selectEmployeeDialog.setNeutralButton(getString(R.string.alert_dialog_msg_employee_contact_details),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {

                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://moma.corp.google.com/person/"+employees.get(newPosition).getName()));
                                    startActivity(browserIntent);

                                }
                            });


                    selectEmployeeDialog.setPositiveButton(getString(R.string.alert_dialog_msg_tag_employee_to_absent),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {

                                    showTagAsAbsentDialogBox(employees.get(newPosition));

                                }
                            });

                    selectEmployeeDialog.show();


                }


            }
        });
        emptyListMsgLayout = findViewById(R.id.rl_empty_list_row);
        emptyListMsgLayout.setVisibility(View.GONE);
        main_list.setVisibility(View.GONE);
        btn_sort = findViewById(R.id.btn_sort);

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(lobList != null && !lobList.isEmpty() && getShiftRange() != null) {
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

        btn_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAscending = !isAscending;
                setListToAdapter();
            }
        });

    }

    private void showTagAsAbsentDialogBox(final DataModel dataModel) {
        tagAsAbsentDialog = new AlertDialog.Builder(MainActivity.this)
                .setCancelable(false)
                .setMessage(String.format(MainActivity.this.getString(R.string.label_tag_employee_to_absent), dataModel.getName()))
                .setPositiveButton(MainActivity.this.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        loader_bar.setVisibility(View.VISIBLE);
                        mainPresenter.tagEmployeeAsAbsent(dataModel);
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

    private void refreshData() {

        main_list.setVisibility(View.GONE);
        loader_bar.setVisibility(View.VISIBLE);
        mainPresenter.getLobList();
    }

    private ShiftRange getShiftRange () {
        DateTime currentDateTime = new DateTime(Calendar.getInstance().getTime()).withZone(DateTimeZone.forID(PH_TIMEZONE));
        Log.d("START", currentDateTime.toString());

        Log.d(MainActivity.class.getName(), currentDateTime.toString());

        Log.d(MainActivity.class.getName(), ShiftRange.TWELVEAM_TO_TWELVEPM.getStartTime().toString());
        Log.d(MainActivity.class.getName(), ShiftRange.TWELVEAM_TO_TWELVEPM.getEndTime().toString());
        if((currentDateTime.isEqual(ShiftRange.TWELVEAM_TO_TWELVEPM.getStartTime()) || currentDateTime.isAfter(ShiftRange.TWELVEAM_TO_TWELVEPM.getStartTime())) &&
                (currentDateTime.isEqual(ShiftRange.TWELVEAM_TO_TWELVEPM.getEndTime()) || currentDateTime.isBefore(ShiftRange.TWELVEAM_TO_TWELVEPM.getEndTime()))) {
            return ShiftRange.TWELVEAM_TO_TWELVEPM;
        }

        else if((currentDateTime.isEqual(ShiftRange.TWELVEPM_TO_TWELVEAM.getStartTime()) || currentDateTime.isAfter(ShiftRange.TWELVEPM_TO_TWELVEAM.getStartTime())) &&
                (currentDateTime.isEqual(ShiftRange.TWELVEPM_TO_TWELVEAM.getEndTime()) || currentDateTime.isBefore(ShiftRange.TWELVEPM_TO_TWELVEAM.getEndTime()))) {
            return ShiftRange.TWELVEPM_TO_TWELVEAM;
        }

        return null;
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
        this.employees = employees;
        this.isAscending = true;
        setListToAdapter();


    }

    private void setListToAdapter() {
        emptyListMsgLayout.setVisibility(View.GONE);
        if(employees != null && !employees.isEmpty()) {

            if(this.isAscending) {
                Collections.sort(employees, new Comparator<DataModel>() {
                    @Override
                    public int compare(DataModel dataModel, DataModel dataModel2) {
                        return dataModel.getTime().compareTo(dataModel2.getTime());
                    }
                });
            } else {
                Collections.sort(employees, new Comparator<DataModel>() {
                    @Override
                    public int compare(DataModel dataModel, DataModel dataModel2) {
                        return dataModel2.getTime().compareTo(dataModel.getTime());
                    }
                });
            }

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

        if(tagAsAbsentDialog != null) {
            tagAsAbsentDialog.dismiss();
        }

        loader_bar.setVisibility(View.GONE);

    }

    @Override
    public void userNotPermitted(final String message) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String messageAlert = message;
                    isUserNotPermittedAlreadyCalled = true;

                    if(!isUserNotPermittedAlreadyCalled && message.equalsIgnoreCase("The caller does not have permission")) {
                        messageAlert = String.format(getString(R.string.error_user_unauthorized_in_gsheet), getString(R.string.spreadsheet_id));
                        logout();
                    }

                    else if( message.contentEquals("You are trying to edit a protected cell or object. Please contact the spreadsheet owner to remove protection if you need to edit.")){
                        loader_bar.setVisibility(View.GONE);
                    }
                    else {
                        loader_bar.setVisibility(View.GONE);
                        emptyListMsgLayout.setVisibility(View.VISIBLE);
                    }


                    Toast.makeText(MainActivity.this, messageAlert, Toast.LENGTH_SHORT).show();

                }
            });
    }

    @Override
    public void tagOrUnTagEmployeeAsAbsentResponse(boolean isSuccessful, DataModel dataModel) {

        if(isSuccessful) {
            refreshData();
        } else {
            Toast.makeText(MainActivity.this, String.format(getString(R.string.unable_to_update_to_absent), dataModel.getName()), Toast.LENGTH_SHORT).show();

        }
    }
}
