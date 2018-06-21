package com.example.bogdan.sortspeed.utilities;

import java.io.IOException;

import okhttp3.*;

import static com.example.bogdan.sortspeed.TestMain.JSON;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {
    private OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = " https://development.m75.ro/test_mts/public/highscore/";
    private static final String MAX_PLAYERS = "10";

    // code request code here
    public String doGetRequest() throws IOException {
        String url = BASE_URL + MAX_PLAYERS;
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    public String doPostRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}