package com.example.mihir.haloworld;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Question32Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question32);
    }
    public void nextY(View view) {
        Intent i = new Intent(Question32Activity.this, ConfirmActivity.class);
        startActivity(i);
        finish();
    }

    public void nextN(View view) {
        Intent i = new Intent(Question32Activity.this, ConfirmActivity.class);
        startActivity(i);
        finish();
    }
}
