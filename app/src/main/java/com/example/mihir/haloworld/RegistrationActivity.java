package com.example.mihir.haloworld;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.gcm.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class RegistrationActivity extends AppCompatActivity {

    GoogleCloudMessaging gcmObj;
    public static String regId = "";

    // Google Project Number
    static final String GOOGLE_PROJ_ID = "574935733370";
    // Message Key
    static final String MSG_KEY = "m";

    final String host = "71.62.99.75";
    final int portNumber = 1337;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public String state;
    public String city;
    public static boolean angel;
    public int range = 2;
    public String quals = ";";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void save1(View v) {
        EditText st = (EditText)findViewById(R.id.state);
        EditText ct = (EditText)findViewById(R.id.city);
        state=st.getText().toString();
        city=ct.getText().toString();
        mViewPager.setCurrentItem(1, true);
    }

    public void save2(View v) {
        CheckBox wc = (CheckBox)findViewById(R.id.woundCare);
        CheckBox h = (CheckBox)findViewById(R.id.heimlich);
        CheckBox rp = (CheckBox)findViewById(R.id.recoveryPosition);
        CheckBox cpr = (CheckBox)findViewById(R.id.cpr);
        CheckBox aed = (CheckBox)findViewById(R.id.aed);
        if(wc.isChecked())
            quals+="1";
        else
            quals+="0";
        if(h.isChecked())
            quals+="1";
        else
            quals+="0";
        if(rp.isChecked())
            quals+="1";
        else
            quals+="0";
        if(cpr.isChecked())
            quals+="1";
        else
            quals+="0";
        if(aed.isChecked())
            quals+="1";
        else
            quals+="0";
        registerInBackground();
        Intent pushIntent = new Intent(this, MyService.class);
        this.startService(pushIntent);

        Toast.makeText(getApplicationContext(), "Saved! Initiating app....", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(RegistrationActivity.this, ReportActivity.class);
        startActivity(i);
        finish();
    }

    public String MD5(String md5) throws UnsupportedEncodingException {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging
                                .getInstance(RegistrationActivity.this);
                    }

                    regId = gcmObj.register(GOOGLE_PROJ_ID);

                    Socket register = new Socket(host, portNumber);
                    PrintWriter out = new PrintWriter(register.getOutputStream(), true);

                    if(angel) {
                        out.println("register,a," + LoginActivity.phonenumber + "," + LoginActivity.name + "," + MD5(LoginActivity.password) + "," + state + " " + city + "," + range + "," + quals + "," + regId + "\n");
                    } else {
                        out.println("register,r," + LoginActivity.phonenumber + "," + LoginActivity.name + "," + MD5(LoginActivity.password) + "," + state + " " + city + "\n");
                    }
                    register.close();
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!regId.equals("")) {
                    // Store RegId created by GCM Server in SharedPref

                } else {
                    Toast.makeText(
                            RegistrationActivity.this,
                            "Reg ID Creation Failed.Either you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time."
                                    + msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        // When Play services not found in device
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // Show Error dialog to install Play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "This device doesn't support Play services. App will not work normally",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } else {}
        return true;
    }

    // When Application is resumed, check for Play services support to make sure
    // app will be running normally
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    public void angelVer(View v) {
        angel=true;
        mViewPager.setCurrentItem(2, true);
    }

    public void plebVer(View v) {
        angel=false;
        registerInBackground();
        Toast.makeText(getApplicationContext(), "Saved! Advancing to alert screen.", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(RegistrationActivity.this, ReportActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_registration, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FirstFragment();
                case 1:
                    return new SecondFragment();
                case 2:
                    return new ThirdFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1"; //Regional Info
                case 1:
                    return "SECTION 2"; //Doctor or no
                case 2:
                    return "SECTION 3"; //Certified in Hel
            }
            return null;
        }
    }
}
