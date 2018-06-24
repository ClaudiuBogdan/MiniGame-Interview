package com.example.bogdan.sortspeed.screens;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bogdan.sortspeed.R;
import com.example.bogdan.sortspeed.data.ArrangeList;
import com.example.bogdan.sortspeed.data.SortedListListener;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GameActivity extends AppCompatActivity implements SortedListListener {

    private final String TIME_TAG = "time";
    private final String NUMBER_ARRAY_TAG = "array";
    private long startTime; // Initial time of the chronometer
    private int animationDuration; //Duration of number view animation
    private ArrangeList numbersList; //The list of numbers to be sorted.

    @BindView(R.id.root) ViewGroup rootView;
    @BindView(R.id.timer_game_screen) TextView timeView;
    @BindView(R.id.helperLineId) ImageView helperLine;
    //View that represent the shadow of the number selected.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        initializeVariables();

        if(savedInstanceState != null){
            startTime = savedInstanceState.getLong(TIME_TAG);
            numbersList.setNumberIntList(savedInstanceState.getIntegerArrayList(NUMBER_ARRAY_TAG));
        }
        else {
            startTime = SystemClock.elapsedRealtime();
        }

        updateChronometerView();
    }

    /**
     * Method that initializes the number array variables.
     */
    private void initializeVariables() {

        int numOfViews = 9; //Number of views that the player will have to order.
        animationDuration = 700; // Time in milliseconds for the number view animation.
        numbersList = new ArrangeList(this, this, rootView, helperLine, numOfViews, animationDuration);
    }

    /**
     * Method that update the chronometer view with the current time.
     */
    private void updateChronometerView() {
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView
                                timeView.setText(String.format("- %d seconds -", getTime()));
                            }
                        });
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        thread.start();
    }


    /**
     * Method that calculates the total amount of time the player spent to sort the numbers.
     * @return
     */
    private int getTime(){
        long endTime = SystemClock.elapsedRealtime();
        int totalTime = (int) (endTime - startTime) / 1000;
        return totalTime;
    }

    /**
     * Method that waits for the view animation to end and starts the HiscoreActivity.
     */
    private void startHiscoreActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int totalTime = getTime();
                Intent intent = new Intent(GameActivity.this, HiscoresActivity.class);
                intent.putExtra("time", totalTime);
                startActivity(intent);
                finish();
            }
        }, animationDuration);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(TIME_TAG, startTime);
        outState.putIntegerArrayList(NUMBER_ARRAY_TAG, numbersList.getNumberIntList());
        super.onSaveInstanceState(outState);
    }

    /**
     * Method that start the HiscoreActivity when the number view array is sorted.
     */
    @Override
    public void handleListSorted() {
        startHiscoreActivity();
    }
}
