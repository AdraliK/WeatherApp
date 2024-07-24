package com.example.weatherapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.LocationCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static MainActivity activity;

    private TextView weatherNow_text, stateWeather_text;
    private ImageView imageView;
    private LinearLayout layoutOfDays, linearLayoutIC, layoutOfDays2;

    private static final int REQUEST_FOREGROUND_LOCATION_PERMISSION = 101;
    private static final int REQUEST_BACKGROUND_LOCATION_PERMISSION = 102;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        activity = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestFineLocationPermission();
        }

        getData();

    }

    private void requestFineLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FOREGROUND_LOCATION_PERMISSION);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_FOREGROUND_LOCATION_PERMISSION);
                showPermissionDialog();
            }
        }

    }

    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Требуется разрешение");
        builder.setMessage("Для корректной работы этого приложения, требуется разрешение для местоположения \"В любом случаи\"");
        builder.setPositiveButton("Предоставить разрешение", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Request the permission
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_FOREGROUND_LOCATION_PERMISSION);
            }
        });
        builder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Permission denied, do something else
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_FOREGROUND_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now access background location
            } else {
                // Permission denied, you can't access background location
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData(){
        getLocation();
    }

    private void getLocation(){
        MyLocation myLocation = new MyLocation(this);
        myLocation.getLocation(new MyLocation.Callback<LocationCallback>() {

            @Override
            public void onSuccess(double latitude, double longitude) {
                Log.d("Location", latitude + " " + longitude);

                myLocation.setLocation(latitude, longitude);

                getWeather(myLocation);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle the error here
            }
        });
    }

    private void getWeather(MyLocation location){
        getWeatherNow(location);
        getWeatherWeek(location);
    }

    private void getWeatherNow(MyLocation location){
        String request = "https://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&appid=" + WeatherAPI.API_KEY + "&units=metric";
        WeatherAPI weatherAPI = new WeatherAPI();
        weatherAPI.getWeatherResponse(request, new WeatherAPI.OnWeatherResponseListener() {
            @Override
            public void onWeatherResponse(String response) {
                Log.d("WeatherAPI", "Response: " + response);

                JsonParse jp = new JsonParse(response);

                updateWeatherNowUi(jp);

            }

            @Override
            public void onError(String error) {
                Log.e("WeatherAPI", "Error: " + error);
                // Handle the error here
            }
        });
    }

    private void updateWeatherNowUi(JsonParse jp){
        setWeatherNowText(jp);
        setWeatherDescriptionNowText(jp);
        setWeatherNowIcon(jp);
    }

    private void setWeatherNowText(JsonParse jp) {
        String tempNow = String.format("%.1f", jp.getTemp(WeatherAPI.TempPrefix.TEMP_NOW)) + " °C";
        weatherNow_text = findViewById(R.id.weatherNow_text);
        weatherNow_text.setText(tempNow);
    }

    private void setWeatherDescriptionNowText(JsonParse jp) {
        String tempMax = Math.round(jp.getTemp(WeatherAPI.TempPrefix.TEMP_MAX)) + "°";
        String tempMin = Math.round(jp.getTemp(WeatherAPI.TempPrefix.TEMP_MIN)) + "°";

        stateWeather_text = findViewById(R.id.stateWeather_text);
        stateWeather_text.setText(jp.getWeatherStatus() + " " + tempMax + "/" + tempMin );
    }

    private void setWeatherNowIcon(JsonParse jp) {
        String icon = "ic_" + jp.getIcon();

        imageView = findViewById(R.id.imageView);
        imageView.setImageResource(getResources().getIdentifier(icon, "drawable", getPackageName()));
    }

    private void getWeatherWeek(MyLocation location){
        String request = "https://api.openweathermap.org/data/2.5/forecast?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&appid=" + WeatherAPI.API_KEY + "&units=metric";
        WeatherAPI weatherAPI = new WeatherAPI();
        weatherAPI.getWeatherResponse(request, new WeatherAPI.OnWeatherResponseListener() {
            @Override
            public void onWeatherResponse(String response) {
                Log.d("WeatherAPI", "Response: " + response);

                JsonParse jp = new JsonParse(response);
                setWeatherWeek(jp);

            }

            @Override
            public void onError(String error) {
                Log.e("WeatherAPI", "Error: " + error);
                // Handle the error here
            }
        });
    }

    private void setWeatherWeek(JsonParse jp){
        setWeatherDescriptionAndIconWeek(jp);
        setWeatherTempWeek(jp);
    }

    private void setWeatherDescriptionAndIconWeek(JsonParse jp){
        Weather[] weatherWeek = jp.getWeatherWeek();

        setWeatherDescriptionWeek(weatherWeek);
        setWeatherIconWeek(weatherWeek);
    }

    private void setWeatherDescriptionWeek(Weather[] weatherWeek){
        layoutOfDays = findViewById(R.id.layoutOfDays);
        List<TextView> textViewOfDescriptionDaysList = getObjectList(layoutOfDays, TextView.class);

        Date date;
        Calendar calendar = Calendar.getInstance(); // current date
        SimpleDateFormat formatForDate = new SimpleDateFormat("E");

        for (int i = 0; i < textViewOfDescriptionDaysList.size(); i++){
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            date = calendar.getTime();
            textViewOfDescriptionDaysList.get(i).setText(formatForDate.format(date) + "  " + weatherWeek[i].getDescription());
        }
    }

    private void setWeatherIconWeek(Weather[] weatherWeek){
        linearLayoutIC = findViewById(R.id.linearLayoutIC);
        List<ImageView> iconsForDaysList = getObjectList(linearLayoutIC, ImageView.class);

        for (int i = 0; i < iconsForDaysList.size(); i++){
            iconsForDaysList.get(i).setImageResource(getResources().getIdentifier("ic_" + weatherWeek[i].getIcon(), "drawable", getPackageName()));
        }
    }

    private void setWeatherTempWeek(JsonParse jp){
        layoutOfDays2 = findViewById(R.id.layoutOfDays2);
        Temp[] tempWeek = jp.getTempWeek();

        List<TextView> textViewOfTempDaysList = getObjectList(layoutOfDays2, TextView.class);
        for (int i = 0; i < textViewOfTempDaysList.size(); i++){
            textViewOfTempDaysList.get(i).setText(Math.round(tempWeek[i].getTempMax()) + "°/" + Math.round(tempWeek[i].getTempMin()) + "°");
        }
    }

    private <T> List<T> getObjectList(LinearLayout linearLayout, Class<T> object){
        List<T> objectList = new ArrayList<>();
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View child = linearLayout.getChildAt(i);
            if (object.isInstance(child)) {
                objectList.add(object.cast(child));
            }
        }
        return objectList;
    }

}