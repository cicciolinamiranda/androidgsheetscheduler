package com.google.scheduler.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.scheduler.R;
import com.google.scheduler.interfaces.MainInterface;
import com.google.scheduler.presenter.MainPresenter;

import java.util.List;
import java.util.ArrayList;

import static com.google.scheduler.constants.AppConstants.REQUEST_PERMISSIONS;

public class MainActivity extends BaseAuthActivity implements MainInterface {


    private ArrayList<DataModel> dataModels;
    private ListView main_list;
    private MainListAdapter adapter;
    private Spinner spinner;
    private MainPresenter mainPresenter;
    private String lob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainPresenter = new MainPresenter(this, this);
        mainPresenter.getLobList();
        main_list = findViewById(R.id.main_list);
        dataModels= new ArrayList<>();

        String[] arr = new String[]{
                "item 1",
                "item 2",
                "item 3",
                "item 4",
                "item 5"
        };

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_text,arr);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_text);
        spinner.setAdapter(spinnerArrayAdapter);

        dataModels.add(new DataModel("Kevin Fugaban", "Developer", "Corp","22:00"));
        dataModels.add(new DataModel("Cicciolina Magdangal", "Developer", "Corp","22:00"));
        dataModels.add(new DataModel("Miani Agbayani", "Developer", "Corp","22:00"));

        adapter = new MainListAdapter(dataModels, MainActivity.this);
        main_list.setAdapter(adapter);

    }

    private void refreshData() {
        mainPresenter.getLobList();
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
    public void getEmployees(List<String> employees) {
        Log.d(MainActivity.class.getName(), employees.toString());

    }

    @Override
    public void getLobsResponse(List<String> lobList) {
        Log.d(MainActivity.class.getName(), lobList.toString());

        if(lobList != null && !lobList.isEmpty()) {
            lob = lobList.get(0);
            mainPresenter.getTodaysActiveEmployees(lob);
        }
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
