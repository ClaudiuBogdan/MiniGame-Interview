package com.example.bogdan.sortspeed.screens;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TextView;

import com.example.bogdan.sortspeed.R;
import com.example.bogdan.sortspeed.utilities.NetworkUtils;

import java.io.IOException;

public class HiscoreScreen extends AppCompatActivity {
TextView playerScore;
EditText enterNameView;
ViewGroup rootView;
int totalTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiscore_screen);
        totalTime = getIntent().getIntExtra("time", -1);
        int minutes = totalTime/60;
        int seconds = totalTime%60;
        playerScore = new TextView(this);
        playerScore.setText(String.format("%2d:%02d", minutes, seconds));
        enterNameView = findViewById(R.id.editTextID);


        rootView = findViewById(R.id.rootHiscoreScreen);
        rootView.addView(playerScore);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //rootView.addView(enterNameView, 1, params );
        Log.v("HiscoreScreen", "Hello World! HiscoreScreen");
        enterNameView.setText("Press");
        enterNameView.setMaxLines(1);
        enterNameView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        enterNameView.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        enterNameView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        enterNameView.setOnEditorActionListener( new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Identifier of the action. This will be either the identifier you supplied,
                // or EditorInfo.IME_NULL if being called due to the enter key being pressed.
                Log.v("HiscoreScreen", "Name:" + enterNameView.getText().toString());
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        Intent intent = new Intent(HiscoreScreen.this, MainActivity.class);
                        startActivity(intent);
                    return true;
                }
                // Return true if you have consumed the action, else false.
                return false;
            }
        });

        //postNameToServer();

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
