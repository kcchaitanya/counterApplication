package com.example.android.courtcounter.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.courtcounter.model.HistoryDetails;
import com.example.android.courtcounter.utils.Constants;
import com.example.android.courtcounter.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.android.courtcounter.utils.Constants.INTENT_DOCUMENT_ID;
import static com.example.android.courtcounter.utils.Constants.INTENT_KEY_TEAM_NAME_A;
import static com.example.android.courtcounter.utils.Constants.INTENT_KEY_TEAM_NAME_B;
import static com.example.android.courtcounter.utils.Constants.INTENT_KEY_SELECTED_SPORT;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    //private final String MIXPANEL_TOKEN;
    private final int TEAM_A = 0;
    private final int TEAM_B = 1;
    String email;
    String selectedSport;
    String TAG = "Scores";
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    String game_completed_time = formatter.format(date);
    String documentId;
    ProgressDialog progress;
    String teamOneName;
    String teamTwoName;
    Button completeGameButton;
    Button historyButton;
    Random rand = new Random();
    private final String rand_int1 = String.valueOf(rand.nextInt(1000));
    private int scoreTeamA = 0;
    private int scoreTeamB = 0;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Bundle mBundle;
    private FirebaseAuth mAuth;

    {
        mBundle = new Bundle();
    }

    public MainActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = this;
        activity.setTitle("SCORE CARD");
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        initializeViews();

        initializeProgressDialog();
        try {
            initializeMixPanel();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initializeFirebaseAnalytics();
        handleNewScore(0, 0);
        documentId = getIntent().getStringExtra(INTENT_DOCUMENT_ID);
        selectedSport = getIntent().getStringExtra(INTENT_KEY_SELECTED_SPORT);

    }

    private void initializeProgressDialog() {
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
    }

    private void initializeFirebaseAnalytics() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mBundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, mBundle);
    }

    private void initializeMixPanel() throws JSONException {

       // mixpanel.identify(rand_int1);
        //mixpanel.getPeople().identify(rand_int1);
        //mixpanel.getPeople().append("name", "Hyderabad");

    }


    public void initializeViews() {
        teamOneName = getIntent().getStringExtra(INTENT_KEY_TEAM_NAME_A);
        teamTwoName = getIntent().getStringExtra(INTENT_KEY_TEAM_NAME_B);
        email = mAuth.getCurrentUser().getEmail();
        ((TextView) findViewById(R.id.team_a_name)).setText(teamOneName);
        ((TextView) findViewById(R.id.team_b_name)).setText(teamTwoName);
        completeGameButton = findViewById(R.id.button_complete_game);
        historyButton = findViewById(R.id.button_history);
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
        //mixPanelTrack(mixPanelMessageBuilder(score, team));
       // mixpanel.timeEvent("Updated Score");

        int visibility = (scoreTeamA == 0 && scoreTeamB == 0) ? View.GONE : View.VISIBLE;
        completeGameButton.setVisibility(visibility);
        historyButton.setVisibility(visibility);
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
      //  mixPanelTrack(teamOneName);
      //  mixpanel.timeEvent(teamOneName);
        mFirebaseAnalytics.logEvent("TeamA", mBundle);
    }

    public void displayForTeamB(int scoreTeamB) {
        TextView scoreView = findViewById(R.id.team_b_score);
        scoreView.setText(String.valueOf(scoreTeamB));
      //  mixPanelTrack(teamTwoName);
      //  mixpanel.timeEvent(teamTwoName);

    }

 //   private void mixPanelTrack(String message) {
      //  mixpanel.track(message);
    //}


    public void resetScore(View view) {
        scoreTeamB = 0;
        scoreTeamA = 0;
        handleNewScore(scoreTeamA, TEAM_A);
        handleNewScore(scoreTeamB, TEAM_B);
    }

    public void completeGame(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Game Result");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Toast.makeText(getApplicationContext(), "Game Completed", Toast.LENGTH_LONG).show();
                    }
                });

        if (scoreTeamA > scoreTeamB) {
            alertDialogBuilder.setMessage(String.format("Congratulations!! %s won the Game", teamOneName));
            alertDialogBuilder.show();
            initializeFireStone();

        }

        if (scoreTeamA < scoreTeamB) {
            alertDialogBuilder.setMessage(String.format("Congratulations!! %s won the Game", teamTwoName));
            alertDialogBuilder.show();
            initializeFireStone();

        }

        if (scoreTeamA == scoreTeamB) {
            alertDialogBuilder.setMessage("Game Draw");
            alertDialogBuilder.show();
            initializeFireStone();
        }
    }

    public String result() {
        if (scoreTeamA > scoreTeamB) {
                return teamOneName;
        }

        if (scoreTeamA < scoreTeamB) {
            return teamTwoName;
        }
        if (scoreTeamA == scoreTeamB) {
            return "Match Draw";
        }

        return "Match not completed";
    }
    private void signOut() {
        progress.show();
        FirebaseAuth.getInstance().signOut();
        moveToLogInActivity();
        progress.dismiss();
    }

    private void moveToLogInActivity() {
        Intent moveToLogInActivity = new Intent(this, LogInActivity.class);
        startActivity(moveToLogInActivity);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_signOut: {
                signOut();
            }
        }
    }


    public void displayHistory(View view) {
        switch (view.getId()) {
            case R.id.button_history: {
                moveToHistoryActivity();

            }
        }
    }


    private void initializeFireStone() {
        DocumentReference userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(email).collection(selectedSport).document();
        HistoryDetails details = new HistoryDetails();
        details.setDateTime(game_completed_time);
        details.setSportsSelected(selectedSport);
        details.setTeamOneName(teamOneName);
        details.setTeamTwoName(teamTwoName);
        details.setTeamOneScore(scoreTeamA);
        details.setTeamTwoScore(scoreTeamB);
        details.setMatchWinner(result());


        Map<String, Object> user = new HashMap<>();

        userDocumentRef.set(details);

        userDocumentRef.set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void moveToHistoryActivity() {
        Intent moveToHistoryActivity = new Intent(this, HistoryDetailsActivity.class);
        moveToHistoryActivity.putExtra(Constants.INTENT_KEY_SELECTED_SPORT, selectedSport);

        startActivity(moveToHistoryActivity);
    }
}




