package com.google.scheduler.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.scheduler.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<DataModel> dataModels;
    private ListView main_list;
    private MainListAdapter adapter;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_list = findViewById(R.id.main_list);
        dataModels= new ArrayList<>();

        dataModels.add(new DataModel("Kevin Fugaban", "Developer", "Corp","22:00"));
        dataModels.add(new DataModel("Cicciolina Magdangal", "Developer", "Corp","22:00"));
        dataModels.add(new DataModel("Miani Agbayani", "Developer", "Corp","22:00"));

        adapter = new MainListAdapter(dataModels, MainActivity.this);
        main_list.setAdapter(adapter);

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
    }
}
