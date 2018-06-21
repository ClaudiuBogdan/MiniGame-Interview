package com.example.bogdan.sortspeed.screens;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.example.bogdan.sortspeed.R;
import com.example.bogdan.sortspeed.data.Player;
import com.example.bogdan.sortspeed.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class that display the main activity with different
 */
public class MainActivity extends AppCompatActivity {

    private TextView showMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showMessage = findViewById(R.id.showMsg);

        new OkHTTPTask().execute();
    }

    public class OkHTTPTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String results = null;
            try {
                NetworkUtils networkUtils = new NetworkUtils();
                results = networkUtils.doGetRequest();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected void onPostExecute(String responseString) {
            if (responseString != null && !responseString.equals("")) {
                ArrayList<Player> playersList = processJSONData(responseString);
                String res = "";
                for(Player player: playersList){
                    int maxLen = 10;
                    int initialLen = 6;
                    String playerString = player.getName().length()<maxLen ?
                            player.toString() :
                            player.toString().substring(0,initialLen + maxLen) + "...";

                    res += playerString + "\n";
                }
                Log.v("HTTP" ,res);
                showMessage.setText(res);
            }
        }

        /**
         * Method that parse the JSON data into array of players.
         * @param stringJson
         * @return
         */
        private ArrayList<Player> processJSONData(String stringJson){
            ArrayList<Player> listOfPlayers = new ArrayList<>();
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

    /**
     * Method that handles any click on the screen and starts GameActivity.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

}
