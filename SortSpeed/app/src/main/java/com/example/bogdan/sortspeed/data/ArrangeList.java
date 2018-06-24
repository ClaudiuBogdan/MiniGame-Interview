package com.example.bogdan.sortspeed.data;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bogdan.sortspeed.R;
import com.example.bogdan.sortspeed.screens.MainActivity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class that represents the list of numbers to be sorted.
 */
public class ArrangeList implements View.OnTouchListener{
    private Context mContext;
    private ViewGroup rootView;
    private SortedListListener mListener;
    private TextView auxiliaryNumberView; //Number view that act as a shadow of the view selected.
    private ImageView helperLine; //Line that act as helper to insert a view between other views.
    private ArrayList<View> numberViewList; // Holds the views that will be render into the screen
    private ArrayList<Integer> numberIntList; //Holds the numbers' order
    private int numOfViews; //Amount of elements to display.
    private int initialX; //Horizontal coordinate of the first numberView
    private int initialY; //Vertical position of all the numberViews.
    private int marginBetweenNumberViews; //Separation between views
    private int numberViewWidth; //The width dimension of each numberView.
    private int animationDuration; //Time duration for view position change.
    private int globalLastIndex; //Last index of the view since it was clicked.
    private int dX, dY; //Coordinates of the view touch


    public ArrangeList(Context context, SortedListListener listener, ViewGroup rootView,
                       ImageView helperLine, int numOfViews, int animationDuration) {
        this.mContext = context;
        this.mListener = listener;
        this.rootView = rootView;
        this.helperLine = helperLine;
        this.numOfViews = numOfViews;
        this.animationDuration = animationDuration;

        initializeVariables();
        createNumericArray();
        createNumericViews();
        bindNumbersToViews();
        initializeAuxiliaryNumericView();
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int horizontalPosition = (int) view.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                globalLastIndex = numberViewList.indexOf(view);
                showAuxiliaryNumberView(globalLastIndex, ((TextView) view).getText().toString());
                view.bringToFront();
                changeViewColor(view, Color.YELLOW);
                dX = (int) (view.getX() - event.getRawX());
                dY = (int) (view.getY() - event.getRawY());
                break;

            case MotionEvent.ACTION_MOVE:
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
                pushLateralViews(globalLastIndex, newViewIndex);
                changeViewIndex(globalLastIndex, newViewIndex);

                view.animate()
                        .x(positionX)
                        .y(initialY)
                        .setDuration(animationDuration)
                        .start();
                if(this.isSorted()){
                    mListener.handleListSorted();
                }
                break;
            default:
                return false;
        }
        return true;
    }

    private void initializeVariables() {
        numberViewList = new ArrayList<>();
        numberIntList = new ArrayList<>();

        //Calculate the width of the view in relation with the screen
        double marginBetweenBorders = 2; //The distance between numberViews and screenBorders measured in view width
        double totalMarginBetweenViews = 2; //uno view unit. Set the margin between views;
        numberViewWidth = (int) (Resources.getSystem().getDisplayMetrics().widthPixels/(numOfViews + marginBetweenBorders + totalMarginBetweenViews) );
        marginBetweenNumberViews = (int) ((numberViewWidth*totalMarginBetweenViews)/(numOfViews-1)); // % of the view width.
        initialY = (int) (Resources.getSystem().getDisplayMetrics().heightPixels/2.5);
        initialX =  numberViewWidth;
    }

    /**
     * Method that creates and renders the numberViews into the screen and saves them into an array.
     */
    private void createNumericViews() {
        for(int i=0; i<numOfViews; i++){
            TextView numberView = (TextView) createNumberView();
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
     * Method that creates a random array of numbers.
     */
    private void createNumericArray() {
        for(int i=1; i<=numOfViews; i++){
            numberIntList.add(i);
        }
        Collections.shuffle(numberIntList);
    }

    /**
     * Method that set the value of the number array into the view array.
     */
    private void bindNumbersToViews(){
        //Set the text to the numberViews
        for(int i=0;  i<numberIntList.size(); i++){
            ((TextView)(numberViewList.get(i))).setText(numberIntList.get(i).toString());
        }
    }

    /**
     * Method that creates and initializes an auxiliary view that represents the view selected by the player.
     */
    private void initializeAuxiliaryNumericView() {
        auxiliaryNumberView = (TextView) createNumberView();
        auxiliaryNumberView.setVisibility(View.INVISIBLE);
        auxiliaryNumberView.setY(initialY);
        changeViewColor(auxiliaryNumberView, Color.GRAY);
        rootView.addView(auxiliaryNumberView);
    }

    /**
     * Method that set the auxiliary view number visible and places it into the proper position.
     * @param index The index of the selected number that will determine the aux view position.
     * @param number The value of the selected number that will by displayed into the aux view.
     */
    private void showAuxiliaryNumberView(int index, String number){
        int position = calculateViewPosition(index);
        auxiliaryNumberView.setText(number);
        auxiliaryNumberView.setX(position);
        auxiliaryNumberView.setVisibility(View.VISIBLE);
    }

    /**
     * Method that creates and initializes the views that represents the numbers to be sorted.
     * @return  The views with the default configuration;
     */
    private View createNumberView() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(numberViewWidth, numberViewWidth);
        TextView viewItem = new TextView(mContext);
        viewItem.setBackground(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.numer_frame, null));
        viewItem.setGravity(Gravity.CENTER);
        viewItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24f);
        viewItem.setLayoutParams(layoutParams);
        changeViewColor(viewItem, Color.WHITE);
        return viewItem;
    }

    /**
     * Method that calculates the corresponding array index based on the position of the view.
     * @param position The position of the view to which the index will be calculated.
     * @return Index of the view into the array based on the position of the view.
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
     * The index of the view into the array will be different if the view will be
     * inserted to the right than to the left of its last position.
     */
    private int calculateIndexOfViewFromLastIndex(int position){
        int index = calculateIndexOfView(position);
        if(index > globalLastIndex){
            index = calculateIndexOfView(position + (int) (numberViewWidth * 0.5) + 2*marginBetweenNumberViews);
            int interiorArrayWidth = (numOfViews - 1)* (numberViewWidth + marginBetweenNumberViews);
            int firsViewPositionWidth = initialX + numberViewWidth;
            int lastPositionIndex = firsViewPositionWidth +  interiorArrayWidth - numberViewWidth;
            index = position > lastPositionIndex ? numOfViews - 1 : index - 1;
        }
        else if(index < globalLastIndex){
            index = calculateIndexOfView(position + (int) (numberViewWidth * 0.5) + 2*marginBetweenNumberViews);
        }
        return index;
    }

    /**
     * Method that calculate the position X of the view based on an array index.
     * @param index The array index that represent the position of the view into the array.
     * @return The position in pixels.
     */
    private int calculateViewPosition(int index){
        int position =  (numberViewWidth + marginBetweenNumberViews)* index + initialX;
        return  position;
    }

    /**
     * Method that change the array index of the view that has been moved tho a new screen position.
     * @param lastIndex The index that the view had before being moved to a new position.
     * @param newIndex The index that the view will have after being moved.
     */
    private void changeViewIndex(int lastIndex, int newIndex){
        View view = numberViewList.get(lastIndex);
        numberViewList.remove(lastIndex);
        numberViewList.add(newIndex, view);
        Integer number = numberIntList.get(lastIndex);
        numberIntList.remove(lastIndex);
        numberIntList.add(newIndex,number);
    }

    /**
     * Method that animates the change of a view position into the array.
     * @param lastIndex The index that the view had before being moved to a new position.
     * @param newIndex The index that the view will have after being moved.
     */
    private void pushLateralViews(int lastIndex, int newIndex){
        int directionIncrement = lastIndex - newIndex > 0 ? 1 : -1;
        int indexOfView = newIndex;
        while (indexOfView != lastIndex) {
            View view = numberViewList.get(indexOfView);
            indexOfView += directionIncrement;
            int positionX = calculateViewPosition(indexOfView);
            view.animate()
                    .x(positionX)
                    .y(initialY)
                    .setDuration(animationDuration)
                    .start();
        }
    }


    /**
     * Method that show an aux line to insert the number.
     */
    private void showHelperLine(int position) {
        int index = calculateIndexOfViewFromLastIndex(position);
        if(index != globalLastIndex){
            index = index > globalLastIndex ? index + 1 : index;
            int newPosition = calculateViewPosition(index) -
                    ( marginBetweenNumberViews + helperLine.getWidth()) / 2;
            helperLine.setVisibility(View.VISIBLE);
            helperLine.setX(newPosition);
            helperLine.setY(initialY);
        }
        else {
            helperLine.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Method that changes the color of a view.
     * @param view The view to which the color will be changed.
     * @param color The color to be changed to.
     */
    private void changeViewColor(View view, int  color) {
        int strokeWidth = (int) (numberViewWidth*0.05); // Percentage of view width
        GradientDrawable drawable = (GradientDrawable)view.getBackground();
        drawable.setStroke(strokeWidth, color); // set stroke width and stroke color
        ((TextView) view).setTextColor(color);
    }

    /**
     * Method that checks if the array is sorted.
     * @return True if the array is sorted.
     */
    private boolean isSorted(){
        boolean arrayIsSorted = true;
        int lastNum  = 0;
        for(Integer num : numberIntList){
            if(num < lastNum){
                arrayIsSorted = false;
            }
            lastNum = num;
        }
        return arrayIsSorted;
    }


    public void setNumberIntList(ArrayList<Integer> numberIntList) {
        this.numberIntList = numberIntList;
        bindNumbersToViews();
    }

    public ArrayList<Integer> getNumberIntList() {
        return numberIntList;
    }
}
