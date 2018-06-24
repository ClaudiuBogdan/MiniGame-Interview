package com.example.bogdan.sortspeed.screens;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bogdan.sortspeed.R;
import com.example.bogdan.sortspeed.data.Player;
import com.example.bogdan.sortspeed.utilities.InternetConnectionStatus;
import com.example.bogdan.sortspeed.utilities.LoaderUtils;
import com.example.bogdan.sortspeed.utilities.ScorePullService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HiscoresActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Player>>{

    private static final String TAG = HiscoresActivity.class.getName();
    @BindView(R.id.firstPlayers) TextView firstPlayerScoreView;
    @BindView(R.id.lastPlayer) TextView lastPlayerScoreView;
    @BindView(R.id.timePlayerText) TextView timePlayerView;
    @BindView(R.id.enterNameEdTxt) EditText enterNameView;
    @BindView(R.id.errorMsgViewID) TextView errorMsgView;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private int totalTime;
    private static final int LOAD_PLAYER_TASK_ID = 1;
    private static final String NAME_TAG = "name";
    private static final String VALUE_TAG = "value";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiscores);
        ButterKnife.bind(this);

        totalTime = getIntent().getIntExtra("time", -1);
        loadHiScoreBoard();

    }

    /**
     * Method that displays the high score board and the view to enter player's name if there is an
     * Internet connectivity.
     */
    private void loadHiScoreBoard() {
        InternetConnectionStatus internetConnection = new InternetConnectionStatus(this);
        if(internetConnection.isConnectedToInternet()){
            setPlayerTime();
            setEnterNameView();
            getSupportLoaderManager().initLoader(LOAD_PLAYER_TASK_ID,  null, this);
        }
        else{
            progressBar.setVisibility(View.INVISIBLE);
            errorMsgView.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Method that displays the time view of the player.
     */
    private void setPlayerTime() {
        int minutes = totalTime/60;
        int seconds = totalTime%60;
        timePlayerView.setText(String.format("%2d:%02d - ",minutes,seconds));
    }

    /**
     * Method that attach a listener to the EditText where the player will insert his/her nickname.
     */
    private void setEnterNameView() {
        enterNameView.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    //Start service to post user score
                    String playerName = enterNameView.getText().toString();
                    Intent mPostScoreIntent = new Intent(HiscoresActivity.this, ScorePullService.class);
                    mPostScoreIntent.putExtra(NAME_TAG, playerName);
                    mPostScoreIntent.putExtra(VALUE_TAG, totalTime);
                    startService(mPostScoreIntent);
                    //Start MainActivity
                    Intent intent = new Intent(HiscoresActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0); //Disable transition between activities.
                    return true;
                }
                // Return true if you have consumed the action, else false.
                return false;
            }
        });
    }

    /**
     * Method that displays the high score view.
     * @param playersList The list of the players that appears in the high score board.
     */
    private void displayScoreViews(List<Player> playersList) {
            String firstPlayersText = "";
            String lastPlayersText = "";
            boolean isLastPosition = true;
            String playersScoreText = firstPlayersText;
            for(Player player: playersList){
                if(Double.parseDouble(player.getValue()) > totalTime){
                    isLastPosition = false;
                }
                if(isLastPosition){
                    firstPlayersText += player.formatPlayersText() + "\n";
                }
                else{
                    lastPlayersText += player.formatPlayersText() + "\n";
                }
            }
            timePlayerView.setVisibility(View.VISIBLE);
            enterNameView.setVisibility(View.VISIBLE);
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

    @NonNull
    @Override
    public Loader<List<Player>> onCreateLoader(int id, @Nullable Bundle args) {
        return new LoaderUtils(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Player>> loader, List<Player> data) {
        progressBar.setVisibility(View.INVISIBLE);
        displayScoreViews(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Player>> loader) {

    }
}