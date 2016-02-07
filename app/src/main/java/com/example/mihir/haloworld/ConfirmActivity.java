package com.example.mihir.haloworld;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ConfirmActivity extends AppCompatActivity {

    final String host = "71.62.99.75";
    final int portNumber = 1337;
    public String hurt;

    public static double latitude;
    public static double longitude;

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


        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 100,5, locationListener);
        if(longitude != 0d && latitude != 0d) {
            locationManager.removeUpdates(locationListener);
        }

        Socket confirm = null;
        try {
            confirm = new Socket(host, portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("ANISH", "7");

        PrintWriter out = null;
        try {
            out = new PrintWriter(confirm.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("e", "report," + LoginActivity.phonenumber + "," + longitude + ":" + latitude + "," + bad);
        out.println("report," + LoginActivity.phonenumber + "," + longitude + ":" + latitude + "," + bad);
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
    }
}
class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location loc) {
        ConfirmActivity.latitude = loc.getLatitude();
        ConfirmActivity.longitude = loc.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}