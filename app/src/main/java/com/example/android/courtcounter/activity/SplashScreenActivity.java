package com.example.android.courtcounter.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.android.courtcounter.R;
import com.example.android.courtcounter.activity.LogInActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                moveToLogInActivity();
            }
        }, 2000);

    }

    private void moveToLogInActivity() {
        Intent moveToLogInActivity = new Intent(this, LogInActivity.class);
        startActivity(moveToLogInActivity);
        finish();
    }
}
