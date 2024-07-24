package com.example.weatherapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;


import com.google.android.gms.location.LocationCallback;


/**
 * Implementation of App Widget functionality.
 */
public class MainWidget extends AppWidgetProvider {

    private RemoteViews newViews;
    private Context newContext;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_widget);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        newContext = context;
        newViews = new RemoteViews(context.getPackageName(), R.layout.main_widget);
        Log.d("UPDATE", "updating...");

        getLocation(context, appWidgetManager, appWidgetIds);

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private void update(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, newViews);
        }
    }

    private void getLocation(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        MyLocation myLocation = new MyLocation(context);
        Log.d("UPDATE", "getLocation");
        myLocation.getLocation(new MyLocation.Callback<LocationCallback>() {

            @Override
            public void onSuccess(double latitude, double longitude) {
                Log.d("Loc", latitude + " " + longitude);

                myLocation.setLocation(latitude, longitude);
                Log.d("UPDATE", "GOTO getWeatherNOW");

                getWeatherNow(myLocation, context, appWidgetManager, appWidgetIds);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle the error here
            }
        });
    }

    private void getWeatherNow(MyLocation location, Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        String request = "https://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&appid=" + WeatherAPI.API_KEY + "&units=metric";
        WeatherAPI weatherAPI = new WeatherAPI();
        Log.d("UPDATE", "getWeatherNow");
        weatherAPI.getWeatherResponse(request, new WeatherAPI.OnWeatherResponseListener() {
            @Override
            public void onWeatherResponse(String response) {
                Log.d("WeatherAPI", "Response: " + response);

                JsonParse jp = new JsonParse(response);
                Log.d("UPDATE", "GOTO update");

                updateWeatherNowUi(jp);
                update(context, appWidgetManager, appWidgetIds);
                Log.d("UPDATE", "updating is sucsess");
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
        String tempNow = " " + Math.round(jp.getTemp(WeatherAPI.TempPrefix.TEMP_NOW)) + "°";

        newViews.setTextViewText(R.id.textTemp, tempNow);
    }

    private void setWeatherDescriptionNowText(JsonParse jp) {
        newViews.setTextViewText(R.id.textDescription, jp.getWeatherStatus());
    }

    private void setWeatherNowIcon(JsonParse jp) {
        String icon = "ic_" + jp.getIcon();
        int resourceId = newContext.getResources().getIdentifier(icon, "drawable", newContext.getPackageName());
        newViews.setImageViewResource(R.id.weatherIcon, resourceId);
    }

}