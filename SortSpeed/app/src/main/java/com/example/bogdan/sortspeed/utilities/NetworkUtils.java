package com.example.bogdan.sortspeed.utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.*;
/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {
    private OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = " https://development.m75.ro/test_mts/public/highscore/";
    private static final String MAX_PLAYERS = "10";
    private static final String POST_URL = "https://development.m75.ro/test_mts/public/highscore/";
    private static final String NAME_TAG = "name";
    private static final String VALUE_TAG = "value";

    /**
     * Method that get data from server.
     * @return String response from the server.
     * @throws IOException
     */
    public String doGetRequest() throws IOException {
        String url = BASE_URL + MAX_PLAYERS;
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * Method that perform POST request to the server.
     * @param name The name of the player .
     * @param value The value of the player's time.
     * @return String response from the server.
     * @throws IOException
     */
    public String doPostRequest(String name, String value) throws IOException {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(NAME_TAG, name)
                .addFormDataPart(VALUE_TAG , value)
                .build();
        Request request = new Request.Builder()
                .url(POST_URL)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}