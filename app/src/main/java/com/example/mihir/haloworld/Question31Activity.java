package com.example.mihir.haloworld;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Question31Activity extends AppCompatActivity {

    private String q2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question31);
        q2 = getIntent().getExtras().getString("Q2");
    }
    public void nextY(View view) {
        Intent i = new Intent(Question31Activity.this, ConfirmActivity.class);
        i.putExtra("Q3", q2 + "1");
        startActivity(i);
        finish();
    }

    public void nextN(View view) {
        Intent i = new Intent(Question31Activity.this, ConfirmActivity.class);
        i.putExtra("Q3", q2+"0");
        startActivity(i);
        finish();
    }
}
