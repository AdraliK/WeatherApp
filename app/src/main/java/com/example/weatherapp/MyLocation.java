package com.example.weatherapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MyLocation {

    private FusedLocationProviderClient fusedLocationClient;
    private Context main;
    private double latitude, longitude;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public MyLocation(Context main) {
        this.main = main;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(main);

    }

    public void getLocation(Callback<LocationCallback> callback) {
        if (ActivityCompat.checkSelfPermission(main, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(main, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("MyLocation", "Permission denied");
            callback.onFailure(new Exception("Permission denied"));
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d("MyLocation", "Last known location: " + location.getLatitude() + ", " + location.getLongitude());
                    callback.onSuccess(location.getLatitude(), location.getLongitude());
                } else {
                    Log.d("MyLocation", "Last known location is null, requesting new location...");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e("MyLocation", "Failed to get last known location", e);
                callback.onFailure(e);
            }
        });
    }

    public interface Callback<T> {
        void onSuccess(double latitude, double longitude);
        void onFailure(Exception e);
    }

    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
