package com.example.android.courtcounter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LogInActivity extends AppCompatActivity {
    String email;
    String password;
    EditText emailEditTextView;
    EditText passwordEditTextView;
    Button signInButton;
    Button signUpButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            System.out.println("currentUser" + currentUser.toString());
            moveToTeamNameActivity();
        }

        initializeView();

    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }


    private void initializeView() {
        emailEditTextView = findViewById(R.id.email);
        passwordEditTextView = findViewById(R.id.password);
        signInButton = findViewById(R.id.signIn);
        signUpButton = findViewById(R.id.signUp);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateLogInDetails()) {
                    signupUser(email, password);
                }

            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateLogInDetails()) {
                    signInUser(email, password);
                }

            }
        });

    }

    private boolean validateLogInDetails() {
        email = emailEditTextView.getText().toString().trim();
        password = passwordEditTextView.getText().toString();


        if (!isEmailValid()) {
            emailEditTextView.setError("Please Enter Valid Email");

        }
        if (!isPasswordValid(password)) {
            passwordEditTextView.setError("Please Enter Valid Password greater than 6 characters");
        }
        return isEmailValid() && isPasswordValid(password);

    }

    private boolean isEmailValid() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        return email.matches(emailPattern);

    }


    private boolean isPasswordValid(String name) {
        return name.length() > 6;
    }

    private void moveToTeamNameActivity() {
        Intent moveToTeamNameActivity = new Intent(this, TeamNameActivity.class);
        moveToTeamNameActivity.putExtra(Constants.INTENT_KEY_EMAIL, email);
        moveToTeamNameActivity.putExtra(Constants.INTENT_KEY_PASSWORD, password);
        startActivity(moveToTeamNameActivity);
        finish();
    }

    private void signupUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // user signup successful
                            //update current user
                            FirebaseUser user = mAuth.getCurrentUser();
                            moveToTeamNameActivity();
                            finish();
                        } else {
                            showToast(String.format("User Already Exists with this Email Address"));


                        }
                    }
                });

    }

    public void signInUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            moveToTeamNameActivity();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(LogInActivity.this, message, Toast.LENGTH_LONG).show();
    }


}
