package com.example.weatherapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonParse {

    private String response;

    public JsonParse(String response) {

        this.response = response;

    }


    public Double getTemp(WeatherAPI.TempPrefix tempPrefix) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            double temperature = jsonObject.getJSONObject("main").getDouble(tempPrefix.getValue());
            Log.d("JSON", String.valueOf(temperature));

            return temperature;
        } catch (JSONException e) {
            Log.e("JSON Parsing", "Error parsing JSON: " + e.getMessage());
            return -1.0;
        }
    }

    public String getWeatherStatus() {
        String description = getIcon();
        description = description.substring(0, description.length() - 1);
        return Translater.translate(description);
    }

    public String getIcon() {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String icon = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
            Log.d("JSON", icon);

            return icon;
        } catch (JSONException e) {
            Log.e("JSON Parsing", "Error parsing JSON: " + e.getMessage());
            return "error";
        }
    }

    public Temp[] getTempWeek() {
        try {
            JSONObject jsonObject = new JSONObject(response);

            Temp[] tempWeek = new Temp[4];
            double tempMax = -1000;
            double tempMin = 1000;

            for (int i = 1; i < 5; i++) {
                for (int j = i * 5; j < i * 5 + 7; j++) {
                    double temp = jsonObject.getJSONArray("list").getJSONObject(j).getJSONObject("main").getDouble("temp");
                    if (tempMax < temp) {
                        tempMax = temp;
                        tempWeek[i - 1] = new Temp(tempMax, tempMin);
                        continue;
                    }
                    if (tempMin > temp) {
                        tempMin = temp;
                        tempWeek[i - 1] = new Temp(tempMax, tempMin);
                    }
                }
                tempMax = -1000;
                tempMin = 1000;
            }

            return tempWeek;

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public Weather[] getWeatherWeek() {
        try {
            JSONObject jsonObject = new JSONObject(response);

            Weather[] weatherWeek = new Weather[4];
            int max = 0;
            String icon, description;

            for (int i = 1; i < 5; i++) {
                for (int j = i * 5; j < i * 5 + 7; j++) {
                    icon = jsonObject.getJSONArray("list").getJSONObject(j).getJSONArray("weather").getJSONObject(0).getString("icon");
                    description = icon.substring(0, icon.length() - 1);

                    if (max < Integer.parseInt(icon.replaceAll("[^0-9]", ""))) {
                        max = Integer.parseInt(icon.replaceAll("[^0-9]", ""));
                        weatherWeek[i - 1] = new Weather(description, icon);
                    }
                }
                Log.d("JSON", max + " " + i);
                max = 0;
            }

            return weatherWeek;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
