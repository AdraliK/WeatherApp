package com.example.weatherapp;

public class Weather {

    private String description;
    private String icon;

    public Weather(String description, String icon){
        this.description = Translater.translate(description);
        this.icon = icon;
    }

    public String getDescription(){
        return description;
    }

    public String getIcon(){
        return icon;
    }
}
