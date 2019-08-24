package com.example.android.courtcounter.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.courtcounter.utils.Constants;
import com.example.android.courtcounter.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.example.android.courtcounter.utils.Constants.INTENT_USER_NAME;




public class TeamNameActivity extends AppCompatActivity {

    String teamOneName;
    String teamTwoName;
    EditText teamOneNameEditTextView;
    EditText teamTwoNameEditTextView;
    Button nextButton;
    String user_name;
    String email;
    String TAG = "Team Name";
    String selectedSport;
    String documentId;
    ProgressDialog progress;
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = this;
        setContentView(R.layout.activity_team_name);
        mAuth = FirebaseAuth.getInstance();
        initializeView();
        initializeProgressDialogue();
        if (user_name != null) {
            activity.setTitle(user_name);
        } else {
            activity.setTitle("Team Name");
        }
    }


    private void initializeView() {
        teamOneNameEditTextView = findViewById(R.id.team_a_name);
        teamTwoNameEditTextView = findViewById(R.id.team_b_name);
        nextButton = findViewById(R.id.next);
        user_name = getIntent().getStringExtra(INTENT_USER_NAME);
        email =  mAuth.getCurrentUser().getEmail();
        initializeSpinner();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (teamNamesAreValid()) {
                    progress.show();
                    initializeFireStone();
                }
            }
        });

    }

    public void initializeSpinner() {

        final Spinner spinner = findViewById(R.id.select_sport);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.select_sport, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                selectedSport = parent.getItemAtPosition(position).toString();

            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {
                Toast.makeText(getApplicationContext(),"Please select the sport",Toast.LENGTH_LONG).show();

            }
        });
    }

    private void initializeFireStone() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
// Create a new user with a first and last name
        final Map<String, Object> user = new HashMap<>();
        user.put("team_one_name", teamOneName);
        user.put("team_two_name", teamTwoName);
        user.put("sport_Selected", selectedSport);
        db.collection("users").document(email).collection(selectedSport).add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                documentId =  documentReference.getId();
                if (selectedSport.equals("Cricket")) {
                    movetoCricketActivity();
                }
                else {
                    moveToMainActivity();
                }
                progress.dismiss();
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentId);
            }

        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    private void movetoCricketActivity() {

        Intent movetoCricketActivity = new Intent(this, CricketActivity.class);
        movetoCricketActivity.putExtra(Constants.INTENT_KEY_TEAM_NAME_A, teamOneName);
        movetoCricketActivity.putExtra(Constants.INTENT_KEY_TEAM_NAME_B, teamTwoName);
        movetoCricketActivity.putExtra(Constants.INTENT_KEY_SELECTED_SPORT, selectedSport);
        movetoCricketActivity.putExtra(Constants.INTENT_DOCUMENT_ID, documentId);

        startActivity(movetoCricketActivity);
        finish();
    }

    private boolean teamNamesAreValid() {
        teamOneName = teamOneNameEditTextView.getText().toString();
        teamTwoName = teamTwoNameEditTextView.getText().toString();
        if (!isNameValid(teamOneName)) {
            teamOneNameEditTextView.setError("Please Enter Valid Team Name for Team A");

        }
        if (!isNameValid(teamTwoName)) {
            teamTwoNameEditTextView.setError("Please Enter Valid Team Name for Team B");
        }
        return isNameValid(teamOneName) && isNameValid(teamTwoName);

    }

    private boolean isNameValid(String name) {
        return name.length() > 0;
    }

    private void moveToMainActivity() {
        Intent moveToMainActivity = new Intent(this, MainActivity.class);
        moveToMainActivity.putExtra(Constants.INTENT_KEY_TEAM_NAME_A, teamOneName);
        moveToMainActivity.putExtra(Constants.INTENT_KEY_TEAM_NAME_B, teamTwoName);
        moveToMainActivity.putExtra(Constants.INTENT_KEY_SELECTED_SPORT, selectedSport);
        moveToMainActivity.putExtra(Constants.INTENT_DOCUMENT_ID, documentId);

        startActivity(moveToMainActivity);
        finish();
    }
    public void initializeProgressDialogue(){
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
    }
}
