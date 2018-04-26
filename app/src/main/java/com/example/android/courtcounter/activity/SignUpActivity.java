package com.example.android.courtcounter.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.courtcounter.utils.Constants;
import com.example.android.courtcounter.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    Button signUpButton;
    String email;
    String name;
    String password;
    EditText emailEditTextView;
    EditText passwordEditTextView;
    private FirebaseAuth mAuth;
    String TAG = "email";
    EditText nameEditTextView;
    ProgressDialog progress;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    Date date = new Date();
    String created_time = formatter.format(date);





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        initializeProgressDialogue();
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        initializeView();

    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

    }

    private void initializeView() {
        emailEditTextView = findViewById(R.id.email);
        passwordEditTextView = findViewById(R.id.password);
        signUpButton = findViewById(R.id.btn_signup);
        TextView logInLink = findViewById(R.id.link_login);
        String textToHighlight = logInLink.getText().toString();

        Spannable WordtoSpan = new SpannableString(textToHighlight);
        WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 18,23 , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logInLink.setText(WordtoSpan);
        nameEditTextView = findViewById(R.id.input_name);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {
                if(isConnectedToInternet()) {

                    if (validateLogInDetails()) {
                        progress.show();
                        signUpUser(email, password);

                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Sorry,no internet connectivity", 1).show();
                }


            }
        });
        logInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTologInActivity();
            }
        });
    }

    public void initializeProgressDialogue(){
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
    }

    private void initializeFireStone(){



        DocumentReference userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(email);

// Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("email_id", email);
        user.put("user_name", name);
        user.put("created_date_time", created_time);
        userDocumentRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Document has been saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Document Was not saved", e);
            }
        });
    }
    private boolean validateLogInDetails() {
        email = emailEditTextView.getText().toString().trim();
        password = passwordEditTextView.getText().toString();
        name = nameEditTextView.getText().toString();


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


    private void signUpUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("AuthFail ", e.getMessage());
                        progress.dismiss();

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.e("AuthSuccess", authResult.getUser().getUid());
                        FirebaseUser user = mAuth.getCurrentUser();
                        progress.dismiss();
                        initializeFireStone();
                        moveToTeamNameActivity();
                    }
                })
        ;
    }

    private void moveToTeamNameActivity() {
        Intent moveToTeamNameActivity = new Intent(this, TeamNameActivity.class);
        moveToTeamNameActivity.putExtra(Constants.INTENT_KEY_EMAIL, email);
        moveToTeamNameActivity.putExtra(Constants.INTENT_KEY_PASSWORD, password);
        moveToTeamNameActivity.putExtra(Constants.INTENT_USER_NAME,name);
        startActivity(moveToTeamNameActivity);
        finish();
    }


    private void moveTologInActivity() {
        Intent moveTologInActivity = new Intent(this, LogInActivity.class);
        startActivity(moveTologInActivity);
        finish();

    }

    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
    }
}
