package com.example.bogdan.sortspeed.data;

import com.google.gson.annotations.SerializedName;

public class Player {
    private final String name;
    private final String value;

    public Player(String name, String value){
        this.name = name;
        this.value = value;
    }

    public String toString(){
        int seconds = ((int)Double.parseDouble(value))%60;
        int minutes = ((int)Double.parseDouble(value))/60;
        return String.format("%2d:%02d - %s",seconds,minutes, name);
    }


    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
