package com.example.android.courtcounter.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.android.courtcounter.utils.Constants;
import com.example.android.courtcounter.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.Manifest;


public class LogInActivity extends AppCompatActivity {
    String email;
    String password;
    EditText emailEditTextView;
    EditText passwordEditTextView;
    Button signInButton;
    private FirebaseAuth mAuth;
    ProgressDialog progress;
    private FusedLocationProviderClient mFusedLocationClient;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        initializeProgressDialog();

        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();

        forceACrash();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            System.out.println("currentUser" + currentUser.toString());
            moveToTeamNameActivity();
            return;

        }

        initializeView();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


    }

    public void forceACrash() {
        // [START crash_force_crash]
        Button crashButton = new Button(this);
        crashButton.setText("Crash!");
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Crashlytics.getInstance().crash(); // Force a crash
            }
        });

        addContentView(crashButton, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        // [END crash_force_crash]
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void initializeProgressDialog() {
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
    }


    public void onStart() {
        super.onStart();

    }


    private void initializeView() {
        emailEditTextView = findViewById(R.id.email);
        passwordEditTextView = findViewById(R.id.password);
        signInButton = findViewById(R.id.signIn);
        TextView signUpButton = findViewById(R.id.signUp);
        String signUpButtonText = signUpButton.getText().toString();
        Spannable WordtoSpan = new SpannableString(signUpButtonText);
        WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 16, 22, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 23, 26, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        signUpButton.setText(WordtoSpan);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View view) {

                if (isConnectedToInternet()) {

                    if (validateLogInDetails()) {
                        progress.show();
                        signInUser(email, password);

                    }
                } else
                    Toast.makeText(getApplicationContext(), "Sorry,no internet connectivity", 1).show();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToSignUpActivity();
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


    public void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progress.dismiss();
                            moveToTeamNameActivity();
                            progress.dismiss();
                        } else {
                            progress.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }


    private void moveToSignUpActivity() {
        Intent moveToSignUpActivity = new Intent(this, SignUpActivity.class);
        startActivity(moveToSignUpActivity);
        finish();
    }

    public boolean isConnectedToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    private boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isLocationPermissionProvided() {
        return isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION) && isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void askForLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Please provide permission", Toast.LENGTH_SHORT).show();
            return;
        } else {
            //move to settings
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    getLocation();
                } else {
                    //permission denied
                }
            }
        }
    }

    private void getLocation() {
        if (!isLocationPermissionProvided()) {
            askForLocationPermission();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.title_location_permission)
                .setMessage(R.string.text_location_permission)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(LogInActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                })
                .create()
                .show();

    }



}
