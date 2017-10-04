package com.homearound.www.homearound;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, SplashAuthActivity.class);
        //  Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }
}
