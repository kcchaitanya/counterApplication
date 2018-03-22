package com.example.android.courtcounter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import org.json.JSONException;
import java.util.Random;
import static com.example.android.courtcounter.Constants.INTENT_KEY_TEAM_NAME_A;
import static com.example.android.courtcounter.Constants.INTENT_KEY_TEAM_NAME_B;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private int scoreTeamA = 0;
    private int scoreTeamB = 0;
    private FirebaseAnalytics mFirebaseAnalytics;
    private MixpanelAPI mixpanel = null;
    private Bundle mBundle;


    {
        mBundle = new Bundle();
    }

    private final String MIXPANEL_TOKEN;
    private final int TEAM_A = 0;
    private final int TEAM_B = 1;

    String teamOneName;
    String teamTwoName;
    Button completeGameButton;
    Random rand = new Random();
    private final String rand_int1 = String.valueOf(rand.nextInt(1000));

    public MainActivity() {
        MIXPANEL_TOKEN = "5d864f9b235075e4c5e24c992c9b2196";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            initializeMixPanel();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initializeFirebaseAnalytics();
        initializeViews();

    }

    private void initializeFirebaseAnalytics() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, mBundle);
    }

    private void initializeMixPanel() throws JSONException {
        mixpanel =
                MixpanelAPI.getInstance(this, MIXPANEL_TOKEN);
        mixpanel.identify(rand_int1);
        mixpanel.getPeople().identify(rand_int1);
        mixpanel.getPeople().append("name", "Hyderabad");

    }


    public void initializeViews() {
        teamOneName = getIntent().getStringExtra(INTENT_KEY_TEAM_NAME_A);
        teamTwoName = getIntent().getStringExtra(INTENT_KEY_TEAM_NAME_B);
        ((TextView) findViewById(R.id.team_a_name)).setText(teamOneName);
        ((TextView) findViewById(R.id.team_b_name)).setText(teamTwoName);
        completeGameButton = findViewById(R.id.button_complete_game);
        Button signOutButton = findViewById(R.id.button_signOut);
        signOutButton.setOnClickListener(this);

    }

    public void updateScore(View buttonView) {
        switch (buttonView.getId()) {
            case R.id.button_add_1_for_team_a: {
                handleNewScore(1, TEAM_A);
                break;
            }
            case R.id.button_add_2_for_team_a: {
                handleNewScore(2, TEAM_A);
                break;
            }
            case R.id.button_add_3_for_team_a: {
                handleNewScore(3, TEAM_A);
                break;
            }
            case R.id.button_add_1_for_team_b: {
                handleNewScore(1, TEAM_B);
                break;
            }
            case R.id.button_add_2_for_team_b: {
                handleNewScore(2, TEAM_B);
                break;
            }
            case R.id.button_add_3_for_team_b: {
                handleNewScore(3, TEAM_B);
                break;
            }
        }
    }

    private void handleNewScore(int score, int team) {
        updateScoreBoard(getUpdateScore(score, team), team);
        mixPanelTrack(mixPanelMessageBuilder(score, team));
        mixpanel.timeEvent("Updated Score");

        int visibility = (scoreTeamA == 0 && scoreTeamB == 0) ? View.GONE : View.VISIBLE;
        completeGameButton.setVisibility(visibility);
    }

    private int getUpdateScore(int score, int team) throws IllegalArgumentException {
        switch (team) {
            case TEAM_A: {
                scoreTeamA += score;
                return scoreTeamA;
            }
            case TEAM_B: {
                scoreTeamB += score;
                return scoreTeamB;
            }
            default: {
                throw new IllegalArgumentException("Team not supported");
            }
        }
    }

    private void updateScoreBoard(int score, int team) throws IllegalArgumentException {
        switch (team) {
            case TEAM_A: {
                displayForTeamA(score);
                break;
            }
            case TEAM_B: {
                displayForTeamB(score);
                break;
            }
            default: {
                throw new IllegalArgumentException("Team not supported");
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private String mixPanelMessageBuilder(int score, int team) {
        return String.format("%d Point for Team %s", score, team == TEAM_A ? "A" : "B");
    }

    public void displayForTeamA(int scoreTeamA) {
        TextView scoreView = findViewById(R.id.team_a_score);
        scoreView.setText(String.valueOf(scoreTeamA));
        mixPanelTrack(teamOneName);
        mixpanel.timeEvent(teamOneName);
        mFirebaseAnalytics.logEvent("TeamA", mBundle);
    }

    public void displayForTeamB(int scoreTeamB) {
        TextView scoreView = findViewById(R.id.team_b_score);
        scoreView.setText(String.valueOf(scoreTeamB));
        mixPanelTrack(teamTwoName);
        mixpanel.timeEvent(teamTwoName);

    }

    private void mixPanelTrack(String message) {
        mixpanel.track(message);
    }

    public void resetScore(View view) {
        scoreTeamB = 0;
        scoreTeamA = 0;
        handleNewScore(scoreTeamA, TEAM_A);
        handleNewScore(scoreTeamB, TEAM_B);
    }

    public void completeGame(View view) {

        if(scoreTeamA > scoreTeamB){
            showToast(String.format("%s won the Game", teamOneName));
        }

        if (scoreTeamA < scoreTeamB){
            showToast(String.format("%s won the Game", teamTwoName));
        }

        if(scoreTeamA == scoreTeamB){
            showToast(String.format("Game Draw"));
        }
    }


    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        moveToLogInActivity();
    }

    private void moveToLogInActivity() {
        Intent moveToLogInActivity = new Intent(this, LogInActivity.class);

        startActivity(moveToLogInActivity);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.button_signOut: {
                signOut();

            }
        }
    }
}


