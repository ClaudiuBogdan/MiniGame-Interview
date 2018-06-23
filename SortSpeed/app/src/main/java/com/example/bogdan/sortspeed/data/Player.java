package com.example.bogdan.sortspeed.data;

public class Player {
    private final String name;
    private String value;

    public Player(String name, String value){
        this.name = name;
        this.value = value;
    }

    public String toString(){
        int seconds = ((int)Double.parseDouble(value))%60;
        int minutes = ((int)Double.parseDouble(value))/60;
        return String.format("%2d:%02d - %s",minutes,seconds, name);
    }

    public String formatPlayersText(){
        int maxLen = 10;
        int initialLen = 10;
        String playerString = this.name.length()<maxLen ?
                this.toString() :
                this.toString().substring(0,initialLen + maxLen) + "...";
        return playerString;
    }


    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
