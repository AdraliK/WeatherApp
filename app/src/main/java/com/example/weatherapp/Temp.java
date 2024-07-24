package com.example.weatherapp;

public class Temp {

    private double tempMax, tempMin;

    public Temp(double tempMax, double tempMin){
        this.tempMax = tempMax;
        this.tempMin = tempMin;
    }

    public double getTempMax(){
        return tempMax;
    }

    public double getTempMin(){
        return tempMin;
    }

}
