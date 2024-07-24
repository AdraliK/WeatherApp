package com.example.weatherapp;

import static java.util.Map.entry;

import java.util.Map;

public class Translater {

    private static Map<String, String> map = Map.ofEntries(
            entry("01", "Ясно"),
            entry("02", "Переменная обл."),
            entry("03", "Рассеянные обл."),
            entry("04", "Разорванные обл."),
            entry("09", "Ливень"),
            entry("10", "Дождь"),
            entry("11", "Гроза"),
            entry("13", "Снег"),
            entry("50", "Туман")
    );

    public static String translate(String value){
        return map.get(value);
    }
}
