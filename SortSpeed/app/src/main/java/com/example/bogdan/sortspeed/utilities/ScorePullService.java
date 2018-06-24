package com.example.bogdan.sortspeed.utilities;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.bogdan.sortspeed.screens.HiscoresActivity;

import java.io.IOException;

/**
 * Service that post the data to the server in a background thread.
 */
public class ScorePullService extends IntentService {

    private static final String SERVICE_TAG = "ScorePullService";
    private static final String NAME_TAG = "name";
    private static final String VALUE_TAG = "value";

    public ScorePullService(){
        super(SERVICE_TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String playerName = intent.getStringExtra(NAME_TAG);
        double value = intent.getIntExtra(VALUE_TAG, Integer.MAX_VALUE);
        if(value>0){
            NetworkUtils networkUtils = new NetworkUtils();
            try {
                String msg = networkUtils.doPostRequest(playerName, String.valueOf(value));
                Log.v(SERVICE_TAG, msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
