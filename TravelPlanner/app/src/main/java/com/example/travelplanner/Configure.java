package com.example.travelplanner;

public class Configure {

    public static String formatTourName(String name){
        if (name.length() > 18){
            name = name.substring(0, 17);
            name += "...";
        }
        return name;
    }
}
