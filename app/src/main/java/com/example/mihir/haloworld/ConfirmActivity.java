package com.example.mihir.haloworld;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ConfirmActivity extends AppCompatActivity {

    final String host = "71.62.99.75";
    final int portNumber = 1337;
    public String hurt;

    public static double latitude;
    public static double longitude;

    public Intent i1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        hurt = getIntent().getExtras().getString("Q3");
        String bad = "";
        if (hurt.equals("111"))
            bad = "0";
        else if (hurt.equals("110"))
            bad = "-1";  //nothing you can do
        else if (hurt.equals("10"))
            bad = "1";
        else if (hurt.equals("01"))
            bad = "2";
        else if (hurt.equals("011"))
            bad = "3";
        else    //010
            bad = "4";
        Log.e("ANISH", "4");


        i1 =  new Intent(this, MyServiceUser.class);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();             // set criteria for location provider:
        criteria.setAccuracy(Criteria.ACCURACY_FINE);   // fine accuracy
        criteria.setCostAllowed(false);                 // no monetary cost

        String bestProvider = locationManager.getBestProvider(criteria, false);
        Log.e("b", bestProvider);
        Location location = locationManager.getLastKnownLocation(bestProvider);

        LatLng myPosition= new LatLng(location.getLatitude(), location.getLongitude());
        if(myPosition ==null) {
            myPosition = new LatLng(38.818487, -77.168534);
        }

        Socket confirm = null;
        try {
            confirm = new Socket(host, portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter out = null;
        try {
            out = new PrintWriter(confirm.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.println("report," + LoginActivity.phonenumber + "," + myPosition.longitude + ":" + myPosition.latitude + "," + bad);
        try {
            confirm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset(View view) {
        Intent i = new Intent(this, ReportActivity.class);
        startActivity(i);
        finish();
        stopService(i1);
    }
}