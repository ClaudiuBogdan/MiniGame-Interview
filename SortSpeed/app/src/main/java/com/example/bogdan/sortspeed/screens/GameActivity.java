package com.example.bogdan.sortspeed.screens;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener {

    private ArrayList<View> numberViewList; // Holds the views that will be render into the screen
    private ArrayList<Integer> numberIntList; //Holds the numbers' order
    private long startTime; // Initial time of the chronometer

    private ViewGroup rootView;
    private TextView timeView;
    private ImageView helperLine;
    private TextView auxiliaryNumberView;

    private int numOfViews; //Amount of elements to display.
    private int initialX; //Horizontal coordinate of the first numberView
    private int initialY; //Vertical position of all the numberViews.
    private int marginBetweenNumberViews; //Separation between views
    private int numberViewWidth; //The width dimension of each numberView.
    private int lastIndex; //Last index of the view since it was clicked.

    private int dX;
    private int dY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        timeView = findViewById(R.id.timer_game_screen);
        rootView = findViewById(R.id.root);
        helperLine = findViewById(R.id.helperLineId);

        startTime = SystemClock.elapsedRealtime();
        numberViewList = new ArrayList<>();
        numberIntList = new ArrayList<>();
        numOfViews = 9;
        createNumericViews();
        createNumericArray();
        initializeAuxiliaryNumericView();
        updateChronometerView();

        showArrayelements(); //TODO delete
    }

    private void initializeAuxiliaryNumericView() {
        auxiliaryNumberView = (TextView) initializeNumberView();
        auxiliaryNumberView.setVisibility(View.INVISIBLE);
        auxiliaryNumberView.setY(initialY);
        changeViewColor(auxiliaryNumberView, Color.GRAY);
        rootView.addView(auxiliaryNumberView);
    }

    private void showAuxiliaryNumberView(int index, String number){
        int position = calculateViewPosition(index);
        auxiliaryNumberView.setText(number);
        auxiliaryNumberView.setX(position);
        auxiliaryNumberView.setVisibility(View.VISIBLE);
    }

    /**
     * Method that create and random sorted array of numbers and fetch the numbers into the views
     */
    private void createNumericArray() {
        for(int i=1; i<=numOfViews; i++){
            numberIntList.add(i);
        }
        Collections.shuffle(numberIntList);
        //Set the text to the numberViews
        for(int i=0;  i<numberIntList.size(); i++){
            ((TextView)(numberViewList.get(i))).setText(numberIntList.get(i).toString());
        }
    }

    /**
     * Method that update the chronometer with the current time.
     */
    private void updateChronometerView() {
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView
                                timeView.setText(String.format("- %d seconds -", getTime()));
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        thread.start();
    }

    /**
     * Method that creates and renders the numberViews into the screen and saves them into an array.
     */
    private void createNumericViews() {
        //Calculate the width of the view in relation with the screen
        double marginBetweenBorders = 2; //The distance between numberViews and screenBorders measured in view width
        double totalMarginBetweenViews = 2; //uno view unit. Set the margin between views;
        numberViewWidth = (int) (Resources.getSystem().getDisplayMetrics().widthPixels/(numOfViews + marginBetweenBorders + totalMarginBetweenViews) );

        initialY = (int) (Resources.getSystem().getDisplayMetrics().heightPixels/3.0);
        marginBetweenNumberViews = (int) ((numberViewWidth*totalMarginBetweenViews)/(numOfViews-1)); // % of the view width.
        initialX =  numberViewWidth;
        for(int i=0; i<numOfViews; i++){
            TextView numberView = (TextView) initializeNumberView();
            numberView.setOnTouchListener(this);
            numberView.animate()
                    .x(calculateViewPosition(i))
                    .y(initialY)
                    .setDuration(0);
            rootView.addView(numberView);
            numberViewList.add(numberView);
        }
    }

    /**
     * Method that create a TextView and add it to the array.
     * @return  TextView with the default configuration;
     */
    private View initializeNumberView() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(numberViewWidth, numberViewWidth);
        TextView viewItem = new TextView(this);
        viewItem.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.numer_frame, null));
        viewItem.setGravity(Gravity.CENTER);
        viewItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 28f);
        viewItem.setLayoutParams(layoutParams);
        changeViewColor(viewItem, Color.WHITE);
        return viewItem;
    }

    /**
     * Method that calculate the index into the array based on the position of the view.
     * @param position The position of the view.
     * @return Index of the view based on the position.
     */
    private int calculateIndexOfView(int position){
        int index;
        int interiorArrayWidth = (numOfViews - 2)* (numberViewWidth + marginBetweenNumberViews);
        int firsViewPositionWidth = initialX + numberViewWidth;
        if(position < firsViewPositionWidth){
            index = 0;
        }
        else if(position > firsViewPositionWidth +  interiorArrayWidth){
            index = numOfViews - 1;
        }
        else{
            int relativePosition = position - firsViewPositionWidth;
            index = 1 + relativePosition/(marginBetweenNumberViews + numberViewWidth); //Integer division
        }
        return index;
    }

    /**
     * Method that calculate the index of the view based on the last index of the view.
     */
    private int calculateIndexOfViewFromLastIndex(int position){
        int index = calculateIndexOfView(position);
        if(index > lastIndex){
            index = calculateIndexOfView(position + (int) (numberViewWidth * 0.5) + 2*marginBetweenNumberViews);
            int interiorArrayWidth = (numOfViews - 1)* (numberViewWidth + marginBetweenNumberViews);
            int firsViewPositionWidth = initialX + numberViewWidth;
            int lastPositionIndex = firsViewPositionWidth +  interiorArrayWidth - numberViewWidth;
            index = position > lastPositionIndex ? numOfViews - 1 : index - 1;
        }
        else if(index < lastIndex){
            index = calculateIndexOfView(position + (int) (numberViewWidth * 0.5) + 2*marginBetweenNumberViews);
        }
        return index;
    }

    /**
     * Method that calculate the position X of the view based on a given index.
     * @param index The index of the view into the view array.
     * @return The position in pixels.
     */
    private int calculateViewPosition(int index){
        int position =  (numberViewWidth + marginBetweenNumberViews)* index + initialX;
        return  position;
    }

    private void setNewViewPosition(int lastIndex, int newIndex){
        showArrayelements();
        View view = numberViewList.get(lastIndex);
        numberViewList.remove(lastIndex);
        numberViewList.add(newIndex, view);
        Log.v("GameScreen", "New Array elements order:" + ((TextView) view).getText());
        showArrayelements();
    }

    private void moveViewsToRight(int lastIndex, int newIndex){
        int directionIncrement = lastIndex - newIndex > 0 ? 1 : -1;
        int positionOfViews = newIndex;
        while (positionOfViews != lastIndex) {
            View view = numberViewList.get(positionOfViews);
            int positionX = calculateViewPosition((int) view.getX());
            positionX += directionIncrement * numberViewWidth;
            view.animate()
                    .x(positionX)
                    .y(initialY)
                    .setDuration(500)
                    .start();
            positionOfViews += directionIncrement;
        }


    }

    private boolean checkOrder(){
        boolean isOrderd = true;
        int lastNum  = 0;
        for(View view: numberViewList){
            String numString = ((TextView) view).getText().toString();
            int num = Integer.parseInt(numString);
            if(num < lastNum){
                isOrderd = false;
            }
            lastNum = num;
        }
        return isOrderd;
    }

    private int getTime(){
        long endTime = SystemClock.elapsedRealtime();
        int totalTime = (int) (endTime - startTime) / 1000;
        return totalTime;
    }

    private void showArrayelements(){
        String msg = "";
        for(View view: numberViewList){
            msg += ((TextView) view).getText() + " ";
        }
        Log.v("GameScreen", "Array elements: " + msg );
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int horizontalPosition = (int) view.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastIndex = calculateIndexOfView(horizontalPosition);
                showAuxiliaryNumberView(lastIndex, ((TextView) view).getText().toString());
                view.bringToFront();
                changeViewColor(view, Color.YELLOW);
                dX = (int) (view.getX() - event.getRawX());
                dY = (int) (view.getY() - event.getRawY());
                break;

            case MotionEvent.ACTION_MOVE:
                //Log.d("GameScreen", "Index: " + calculateIndexOfView((int) view.getX()));
                showHelperLine(horizontalPosition);
                view.animate()
                        .x(event.getRawX() + dX)
                        .y(event.getRawY() + dY)
                        .setDuration(0)
                        .start();
                break;

            case MotionEvent.ACTION_UP:
                auxiliaryNumberView.setVisibility(View.INVISIBLE);
                helperLine.setVisibility(View.INVISIBLE);
                changeViewColor(view, Color.WHITE);
                int newViewIndex = calculateIndexOfViewFromLastIndex(horizontalPosition);
                int positionX = calculateViewPosition(newViewIndex);

                //moveViewsToRight(lastIndex, newViewIndex);
                //setNewViewPosition(lastIndex, newViewIndex);

                view.animate()
                        .x(positionX)
                        .y(initialY)
                        .setDuration(500)
                        .start();
                if(checkOrder()){
                    int totalTime = getTime();
                    Log.v("GameScreen", "Congratulations! Time = " + totalTime);
                    Intent intent = new Intent(this, HiscoreScreen.class);
                    intent.putExtra("time", totalTime);
                    startActivity(intent);

                }
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Method that show a guide to insert the number.
     */
    private void showHelperLine(int position) {
        int index = calculateIndexOfViewFromLastIndex(position);
        if(index != lastIndex){
            index = index > lastIndex ? index + 1 : index;
            int newPosition = calculateViewPosition(index) -
                    ( marginBetweenNumberViews + helperLine.getWidth()) / 2;
            helperLine.setVisibility(View.VISIBLE);
            helperLine.setX(newPosition);
        }
        else {
            helperLine.setVisibility(View.INVISIBLE);
        }
    }

    private void changeViewColor(View view, int  color) {
        int strokeWidth = (int) (numberViewWidth*0.05); // Percentage of view width
        GradientDrawable drawable = (GradientDrawable)view.getBackground();
        drawable.setStroke(strokeWidth, color); // set stroke width and stroke color
        ((TextView) view).setTextColor(color);
    }
}
