package com.example.mihir.haloworld;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Question31Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question31);
    }
    public void nextY(View view) {
        Intent i = new Intent(Question31Activity.this, ConfirmActivity.class);
        startActivity(i);
        finish();
    }

    public void nextN(View view) {
        Intent i = new Intent(Question31Activity.this, ConfirmActivity.class);
        startActivity(i);
        finish();
    }
}
