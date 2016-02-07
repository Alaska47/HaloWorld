package com.example.mihir.haloworld;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Question2Activity extends AppCompatActivity {
    private String q1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question2);
        q1 = getIntent().getExtras().getString("Q1");
    }
    public void nextY(View view) {
        if(q1.equals("Y")) {
            Intent i = new Intent(Question2Activity.this, Question31Activity.class);
            startActivity(i);
            finish();
        }
        else {
            Intent i = new Intent(Question2Activity.this, Question32Activity.class);
            startActivity(i);
            finish();
        }
    }

    public void nextN(View view) {
        if(q1.equals("Y")) {
            Intent i = new Intent(Question2Activity.this, ConfirmActivity.class);
            startActivity(i);
            finish();
        }
        else {
            Intent i = new Intent(Question2Activity.this, Question32Activity.class);
            startActivity(i);
            finish();
        }
    }
}
