package com.example.bogdan.sortspeed.screens;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bogdan.sortspeed.R;
import com.example.bogdan.sortspeed.data.Player;
import com.example.bogdan.sortspeed.utilities.InternetConnectionStatus;
import com.example.bogdan.sortspeed.utilities.LoaderUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Class that display the main activity with different
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Player>> {

    private final int LOAD_PLAYER_TASK_ID = 1;

    @BindView(R.id.score_board_txt)TextView hiScoreBoardView;
    @BindView(R.id.errorMsgViewID) TextView errorMsgView;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        loadHiScoreBoard();

    }

    /**
     * Method that download and displays the high score board if there is Internet connectivity.
     * Otherwise it displays an error message informing that there isn't an Internet connection.
     */
    private void loadHiScoreBoard() {
        InternetConnectionStatus internetConnection = new InternetConnectionStatus(this);
        if(internetConnection.isConnectedToInternet()){
            getSupportLoaderManager().initLoader(LOAD_PLAYER_TASK_ID, null, this);
        }
        else{
            progressBar.setVisibility(View.INVISIBLE);
            errorMsgView.setVisibility(View.VISIBLE);
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

    //Implementation of the Loader methods that download the high score board from the server
    //and returns a list of players that appears into the high score board.
    @NonNull
    @Override
    public Loader<List<Player>> onCreateLoader(int id, @Nullable Bundle args) {
        return new LoaderUtils(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Player>> loader, List<Player> data) {
        progressBar.setVisibility(View.INVISIBLE);
        displayPlayerList(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Player>> loader) {

    }

    /**
     * Method that format the player list into a string and displays it into the hiscore view.
     * @param playerList List of the players that appear into the high score board.
     */
    private void displayPlayerList(List<Player> playerList){
        if (playerList.size()>0) {
            String res = "";
            for(Player player: playerList){
                res += player.formatPlayersText() + "\n";
            }
            hiScoreBoardView.setVisibility(View.VISIBLE);
            hiScoreBoardView.setText(res);
        }
        else{
            //TODO : SHow an server error had occurred.
        }
    }
}
