package com.example.mihir.haloworld;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        if(q1.equals("1")) {
            Intent i = new Intent(Question2Activity.this, Question31Activity.class);
            i.putExtra("Q2", "11");
            startActivity(i);
            finish();
        }
        else {
            Intent i = new Intent(Question2Activity.this, ConfirmActivity.class);
            i.putExtra("Q3", "01");
            startActivity(i);
            finish();
        }
    }

    public void nextN(View view) {
        if(q1.equals("1")) {
            Intent i = new Intent(Question2Activity.this, ConfirmActivity.class);
            i.putExtra("Q3", "10");
            startActivity(i);
            finish();
        }
        else {
            Intent i = new Intent(Question2Activity.this, Question32Activity.class);
            i.putExtra("Q2", "00");
            startActivity(i);
            finish();
        }
    }
}
