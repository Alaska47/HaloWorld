package com.example.mihir.haloworld;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.Manifest;

public class StartUpActivity extends AppCompatActivity {

    public static SharedPreferences prefs = null;

    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        prefs = getSharedPreferences("com.example.mihir.haloworld", MODE_PRIVATE);

        if (prefs.getBoolean("firstrun", true)) {
            Intent login = new Intent(this, LoginActivity.class);
            prefs.edit().putBoolean("firstrun", false).commit();
            startActivity(login);
        } else {
            Intent report = new Intent(this, ReportActivity.class);
            startActivity(report);
        }
        finish();

    }


}
