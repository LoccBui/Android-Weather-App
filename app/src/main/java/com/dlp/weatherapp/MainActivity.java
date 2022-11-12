package com.dlp.weatherapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.dlp.weatherapp.adapter.ViewAdapter;
import com.dlp.weatherapp.models.City;
import com.dlp.weatherapp.sql.CityDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ImageButton ibtnMenu;
    ViewPager viewPager;
    ArrayList<City> listModelCity;
    public static ArrayList<String> listCity;
    ViewAdapter viewAdapter;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    static double latitude;
    static double longitude;

    static String DB_NAME = "Weather.db";
    private String DB_PATH = "/databases/";
    static SQLiteDatabase database = null;
    private boolean RECEIVE_LOCATION = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AddControls();
        AddEvents();
        CheckPermission();
        viewPager.setAdapter(viewAdapter);
        UpdateListWeather(Cls_Main.APPAPI);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case (2):
                if (resultCode == Activity.RESULT_OK){
                    RECEIVE_LOCATION = false;
                    String returnValue = data.getStringExtra("data");
                    Search(returnValue, Cls_Main.APPAPI);
                } else if (resultCode == Activity.RESULT_CANCELED){
                    if (listCity.size() == 0){
                        RECEIVE_LOCATION = true;
                        listModelCity.clear();
                        viewAdapter.notifyDataSetChanged();
                        GetCurrentLocation();
                    }
                    else {
                        listModelCity.clear();
                        UpdateListWeather(Cls_Main.APPAPI);
                        viewAdapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
                Toast.makeText(MainActivity.this, "Default", Toast.LENGTH_SHORT).show();
        }
    }

    private void Search(String data, String apiKey) {
        City city = new City();

        city.setCurrent(false);

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + data + "&units=metric&appid=" + apiKey;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    listModelCity.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    city.setCityName(jsonObject.getString("name"));
                    String day = jsonObject.getString("dt");
                    long lday = Long.valueOf(day);
                    Date date = new Date(lday * 1000L);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM-dd HH-mm-ss");
                    String day1 = simpleDateFormat.format(date);
                    city.setDay(date.toString());

                    JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                    city.setStatus(jsonObjectWeather.getString("main"));
                    city.setImg(jsonObjectWeather.getString("icon"));

                    JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                    String temperature = jsonObjectMain.getString("temp");
                    Double a = Double.valueOf(temperature);
                    String Temp = String.valueOf(a.intValue());
                    city.setTemperature(Temp + "°C");
                    city.setHumidity(jsonObjectMain.getString("humidity") + "%");

                    JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                    city.setWind(jsonObjectWind.getString("speed") + "m/s");

                    JSONObject jsonObjectClouds = jsonObject.getJSONObject("clouds");
                    city.setCloud(jsonObjectClouds.getString("all"));

                    JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");
                    city.setCountryName(jsonObjectSys.getString("country"));

                    city.setFavourite(CheckIsFavourite(city.getCityName().toString()));

                    listModelCity.add(city);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                viewAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(stringRequest);
    }

    private void AddControls() {

        ibtnMenu = findViewById(R.id.ibtnMenu);

        listModelCity = new ArrayList<>();
        viewAdapter = new ViewAdapter(this, listModelCity);
        LoadDatabaseFromAssets();
        InitData();

        viewPager = findViewById(R.id.viewPager);
    }

    private void AddEvents() {
        ibtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, ListFvLocation.class);
                startActivityForResult(myIntent, 2);
            }
        });


    }

    private void InitData() {
        database = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        Cursor cursor = database.query("City", null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            listCity.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
    }

    private void LoadDatabaseFromAssets() {
        File dbFile = getDatabasePath(DB_NAME);
        listCity = new CityDAO(MainActivity.this).GetAll();
        if (listCity.size() == 0) {
            copyDatabase();
        } else {
            dbFile.delete();
            copyDatabase();
        }
    }

    private void copyDatabase() {
        try {
            InputStream myInput = getAssets().open(DB_NAME);
            String outFileName = getApplicationInfo().dataDir + DB_PATH + DB_NAME;
            File f = new File(getApplicationInfo().dataDir + DB_PATH);
            if (!f.exists()) {
                f.mkdir();
            }
            OutputStream myOutPut = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = myInput.read(buffer)) > 0) {
                myOutPut.write(buffer, 0, len);
            }
            myOutPut.flush();
            myInput.close();
            myOutPut.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Copy Error", e.toString());
        }
    }


    private void LoadWeatherFromCoordinate(String apiKey, double latitude, double longitude) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=metric&appid=" + apiKey;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (RECEIVE_LOCATION){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (listModelCity.size() == 0) {
                            City city = new City();
                            city.setCurrent(true);
                            city.setCityName(jsonObject.getString("name"));
                            String day = jsonObject.getString("dt");
                            long lday = Long.valueOf(day);
                            Date date = new Date(lday * 1000L);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM-dd HH-mm-ss");
                            String day1 = simpleDateFormat.format(date);
                            city.setDay(date.toString());

                            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            city.setStatus(jsonObjectWeather.getString("main"));
                            city.setImg(jsonObjectWeather.getString("icon"));

                            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                            String temperature = jsonObjectMain.getString("temp");
                            Double a = Double.valueOf(temperature);
                            String Temp = String.valueOf(a.intValue());
                            city.setTemperature(Temp + "°C");
                            city.setHumidity(jsonObjectMain.getString("humidity") + "%");

                            JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                            city.setWind(jsonObjectWind.getString("speed") + "m/s");

                            JSONObject jsonObjectClouds = jsonObject.getJSONObject("clouds");
                            city.setCloud(jsonObjectClouds.getString("all"));

                            JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");
                            city.setCountryName(jsonObjectSys.getString("country"));

                            city.setFavourite(CheckIsFavourite(city.getCityName()));

                            listModelCity.add(city);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    viewAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(stringRequest);
    }

    private void CheckPermission() {
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            GetCurrentLocation();
        }
    }

    private void GetCurrentLocation() {
        try {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            LocationServices.getFusedLocationProviderClient(MainActivity.this)
                    .requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                    .removeLocationUpdates(this);
                            if (locationResult != null && locationResult.getLocations().size() > 0) {
                                int latestLocationIndex = locationResult.getLocations().size() - 1;
                                latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                                longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                                LoadWeatherFromCoordinate(Cls_Main.APPAPI, latitude, longitude);
                            }
                        }
                    }, Looper.getMainLooper());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GetCurrentLocation();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void UpdateListWeather(String apiKey) {
        if (listCity.size() > 0) {
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            for (String city : listCity) {
                String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + apiKey;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            City city = new City();
                            city.setCityName(jsonObject.getString("name"));
                            String day = jsonObject.getString("dt");
                            long lday = Long.valueOf(day);
                            Date date = new Date(lday * 1000L);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM-dd HH-mm-ss");
                            String day1 = simpleDateFormat.format(date);
                            city.setDay(date.toString());

                            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            city.setStatus(jsonObjectWeather.getString("main"));
                            city.setImg(jsonObjectWeather.getString("icon"));

                            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                            String temperature = jsonObjectMain.getString("temp");
                            Double a = Double.valueOf(temperature);
                            String Temp = String.valueOf(a.intValue());
                            city.setTemperature(Temp + "°C");
                            city.setHumidity(jsonObjectMain.getString("humidity") + "%");

                            JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                            city.setWind(jsonObjectWind.getString("speed") + "m/s");

                            JSONObject jsonObjectClouds = jsonObject.getJSONObject("clouds");
                            city.setCloud(jsonObjectClouds.getString("all"));

                            JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");
                            city.setCountryName(jsonObjectSys.getString("country"));

                            city.setFavourite(CheckIsFavourite(city.getCityName().toString()));

                            listModelCity.add(city);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        viewAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                requestQueue.add(stringRequest);
            }
        }
    }

    private boolean CheckIsFavourite(String cityName){
        if (listCity.size() >0){
            for (int i = 0; i<listCity.size(); i++){
                if (TextUtils.equals(listCity.get(i).toString(), cityName)){
                    return true;
                }
            }
        }
        return false;
    }
}