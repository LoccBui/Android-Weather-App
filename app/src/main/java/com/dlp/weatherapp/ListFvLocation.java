package com.dlp.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dlp.weatherapp.adapter.CityAdapter;

import java.util.ArrayList;

public class ListFvLocation extends AppCompatActivity {

    ImageButton ibtnBack, ibtnSearch;
    EditText etxtSearch;
    ListView lvFavourite;
    public static CityAdapter cityAdapter = null;
    static ArrayList<String> listCity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_fv_location);

        AddControls();
        AddEvents();
    }

    private void AddControls() {

        ibtnBack = findViewById(R.id.ibtnBack);
        ibtnSearch = findViewById(R.id.ibtnSearch);
        etxtSearch = findViewById(R.id.etxtSearch);
        lvFavourite = findViewById(R.id.lvFavourite);
        listCity = MainActivity.listCity.size() > 0 ? MainActivity.listCity : new ArrayList<String>();
        cityAdapter = new CityAdapter(this, R.layout.favourite_location, listCity);
        lvFavourite.setAdapter(cityAdapter);


    }

    private void AddEvents() {
        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent(ListFvLocation.this, MainActivity.class);
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        });

        ibtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etxtSearch.getText().toString())){
                    Toast.makeText(ListFvLocation.this, "Form is empty!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent resultIntent = new Intent(ListFvLocation.this, MainActivity.class);
                    resultIntent.putExtra("data", etxtSearch.getText().toString());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });


    }

    public static void Remove(int index){
        listCity.remove(index);
        cityAdapter.notifyDataSetChanged();
    }
}