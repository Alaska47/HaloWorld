package com.example.mihir.haloworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class Question1Activity extends AppCompatActivity {
    String FILENAME = "hello_file";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question1);
    }
    public void nextY(View view) {
        Intent i = new Intent(Question1Activity.this, Question2Activity.class);
        i.putExtra("Q1", "1");
        startActivity(i);
        finish();

    }

    public void nextN(View view) {
        Intent i = new Intent(Question1Activity.this, Question2Activity.class);
        i.putExtra("Q1", "0");
        startActivity(i);
        finish();
    }
}
