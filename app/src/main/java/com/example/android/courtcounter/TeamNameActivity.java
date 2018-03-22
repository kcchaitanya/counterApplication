package com.example.android.courtcounter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class TeamNameActivity extends AppCompatActivity {

    String teamOneName;
    String teamTwoName;
    EditText teamOneNameEditTextView;
    EditText teamTwoNameEditTextView;
    Button nextButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_name);
        initializeView();
    }

    private void initializeView(){
        teamOneNameEditTextView = findViewById(R.id.team_a_name);
        teamTwoNameEditTextView = findViewById(R.id.team_b_name);
        nextButton = findViewById(R.id.next);

        Spinner spinner = (Spinner) findViewById(R.id.select_sport);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.select_sport, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(teamNamesAreValid()){

                moveToMainActivity();}
            }
        });

    }

    private boolean teamNamesAreValid() {
       teamOneName =  teamOneNameEditTextView.getText().toString();
        teamTwoName = teamTwoNameEditTextView.getText().toString();
        if (!isNameValid(teamOneName)){
            teamOneNameEditTextView.setError("Please Enter Valid Team Name for Team A");

        }
        if (!isNameValid(teamTwoName)){
            teamTwoNameEditTextView.setError("Please Enter Valid Team Name for Team B");
        }
        return isNameValid(teamOneName) && isNameValid(teamTwoName);

    }

    private boolean isNameValid(String name){
        return name.length() > 0;
    }

    private void moveToMainActivity() {
        Intent moveToMainActivity = new Intent(this, MainActivity.class);
        moveToMainActivity.putExtra(Constants.INTENT_KEY_TEAM_NAME_A, teamOneName);
        moveToMainActivity.putExtra(Constants.INTENT_KEY_TEAM_NAME_B, teamTwoName);

        startActivity(moveToMainActivity);

    }
}
