package com.example.bogdan.sortspeed.utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.example.bogdan.sortspeed.data.Player;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LoaderUtils extends AsyncTaskLoader<List<Player>> {
    private List<Player> playerList;
    public LoaderUtils(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if(playerList != null){
            deliverResult(playerList);
        }
        else {
            forceLoad();
        }
    }

    @Nullable
    @Override
    public List<Player> loadInBackground() {
        String jsonStringResponse = null;
        try {
            NetworkUtils networkUtils = new NetworkUtils();
            jsonStringResponse = networkUtils.doGetRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Player> playersFromJson = processJSONData(jsonStringResponse);
        return playersFromJson;
    }


    @Override
    public void deliverResult(@Nullable List<Player> data) {
        playerList = data;
        super.deliverResult(data);
    }


    /**
     * Method that parse the JSON data into array of players.
     * @param stringJson
     * @return The list of players and their time.
     */
    private List<Player> processJSONData(String stringJson){
        List<Player> listOfPlayers = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(stringJson);
            //Getting Json array node
            JSONArray result = jsonObject.getJSONArray("result");

            //Looping through all players
            for(int i=0; i<result.length(); i++){
                JSONObject playerJson = result.getJSONObject(i);
                String name = playerJson.getString("name");
                String value = playerJson.getString("value");
                Player player = new Player(name, value);
                listOfPlayers.add(player);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listOfPlayers;
    }
}
