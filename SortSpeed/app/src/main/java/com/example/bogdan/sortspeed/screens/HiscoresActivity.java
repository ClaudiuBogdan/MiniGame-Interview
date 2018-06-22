package com.example.bogdan.sortspeed.screens;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bogdan.sortspeed.R;
import com.example.bogdan.sortspeed.data.Player;
import com.example.bogdan.sortspeed.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class HiscoresActivity extends AppCompatActivity {

    private static final String TAG = HiscoresActivity.class.getName();
    private TextView firstPlayerScoreView;
    private TextView lastPlayerScoreView;
    private TextView timePlayerView;
    private EditText enterNameView;
    private int totalTime;
    private ViewGroup rootLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiscores);
        totalTime = getIntent().getIntExtra("time", -1);
        int minutes = totalTime/60;
        int seconds = totalTime%60;
        firstPlayerScoreView = findViewById(R.id.firstPlayers);
        lastPlayerScoreView = findViewById(R.id.lastPlayer);
        timePlayerView = findViewById(R.id.timePlayerText);
        enterNameView = findViewById(R.id.enterNameEdTxt);

        timePlayerView.setText(String.format("%2d:%02d - ",minutes,seconds));

        enterNameView.setMaxLines(1);
        enterNameView.setHint("Enter name");
        enterNameView.setHintTextColor(Color.GRAY);
        enterNameView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        enterNameView.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        enterNameView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        enterNameView.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                Log.v("HiscoreScreen", "Name:" + enterNameView.getText().toString());
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    Intent intent = new Intent(HiscoresActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                }
                // Return true if you have consumed the action, else false.
                return false;
            }
        });

        new OkHTTPTask().execute();

        //postNameToServer();

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
                String firstPlayersText = "";
                String lastPlayersText = "";
                boolean isLastPosition = true;
                String playersScoreText = firstPlayersText;
                //TODO : check that players are sorted.
                int count = 0;
                for(Player player: playersList){
                    player.setValue(count + "");
                    count++;
                    if(Double.parseDouble(player.getValue()) > totalTime){
                        isLastPosition = false;
                    }
                    if(isLastPosition){
                        firstPlayersText += formatPlayersText(player) + "\n";
                    }
                    else{
                        lastPlayersText += formatPlayersText(player) + "\n";
                    }
                }
                if(firstPlayersText.length() > 0){
                    firstPlayersText = firstPlayersText.substring(0,firstPlayersText.length() -1 );
                }
                if(isLastPosition){
                    firstPlayerScoreView.setVisibility(View.VISIBLE);
                    firstPlayerScoreView.setText(firstPlayersText);
                }
                else{
                    if(firstPlayersText.length() > 0){
                        firstPlayerScoreView.setVisibility(View.VISIBLE);
                        firstPlayerScoreView.setText(firstPlayersText);
                    }
                    lastPlayerScoreView.setVisibility(View.VISIBLE);
                    lastPlayerScoreView.setText(lastPlayersText);
                }
            }
        }

        private String formatPlayersText(Player player){
            int maxLen = 10;
            int initialLen = 6;
            String playerString = player.getName().length()<maxLen ?
                    player.toString() :
                    player.toString().substring(0,initialLen + maxLen) + "...";
            return playerString;
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


    private void postNameToServer() {
        NetworkUtils networkUtils = new NetworkUtils();
        String url = "https://development.m75.ro/test_mts/public/highscore/";
        String json = "";
        try {
            networkUtils.doPostRequest(url, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}