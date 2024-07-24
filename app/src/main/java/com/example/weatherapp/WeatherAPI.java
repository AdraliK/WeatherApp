package com.example.weatherapp;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherAPI {

    public static String CITY_NAME = "Moscow";
    public static String API_KEY = "80ee904fbc2bd705405e300f40de456c";

    public static enum TempPrefix {
        TEMP_NOW("temp"),
        TEMP_MAX("temp_max"),
        TEMP_MIN("temp_min");

        private final String value;

        TempPrefix(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public interface OnWeatherResponseListener {
        void onWeatherResponse(String response);

        void onError(String error);
    }

    public void getWeatherResponse(String request, OnWeatherResponseListener listener) {
        new WeatherTask(listener).execute(request);
    }

    private static class WeatherTask extends AsyncTask<String, Void, String> {
        private OnWeatherResponseListener listener;

        public WeatherTask(OnWeatherResponseListener listener) {
            this.listener = listener;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                int responseCode = con.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    return response.toString();
                } else {
                    return "Error: " + responseCode;
                }
            } catch (IOException e) {
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (listener != null) {
                if (result.startsWith("Error:")) {
                    listener.onError(result);
                } else {
                    listener.onWeatherResponse(result);
                }
            }
        }
    }
}
