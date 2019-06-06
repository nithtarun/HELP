package com.example.help;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // For contact Information
    private final static  String EXTRA_MESSAGE = "MESSAGE";
    private ListView obj;
    DBHelper mydb;

    // For msg sending
    Button btnShowLocation;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;

    // GPSTracker class
    GPSTracker gps;

    //for sms purpose
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    String phoneNo;
    String message;
    FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mydb = new DBHelper(this);

        final int x = mydb.numberOfRows();
        floatingActionButton = findViewById(R.id.add_post_btn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),DisplayContact.class);
                startActivity(intent);
            }
        });

        final Geocoder geocoder;
        final List<Address>[] addresses = new List[]{null};
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will
                //execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnShowLocation = (Button) findViewById(R.id.help);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    try {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.SEND_SMS)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.SEND_SMS},
                                    MY_PERMISSIONS_REQUEST_SEND_SMS);

                            // If any permission above not allowed by user, this condition will
                            //execute every time, else your else part will work
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    /*
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            android.Manifest.permission.SEND_SMS)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                                android.Manifest.permission.SEND_SMS)) {
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{android.Manifest.permission.SEND_SMS},
                                    MY_PERMISSIONS_REQUEST_SEND_SMS);
                        }
                    }*/

                    // create class object
                    gps = new GPSTracker(MainActivity.this);

                    // check if GPS enabled
                    if (gps.canGetLocation()) {

                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();

                        try {
                            addresses[0] = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String address = addresses[0].get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        String city = addresses[0].get(0).getLocality();
                        String state = addresses[0].get(0).getAdminArea();
                        String country = addresses[0].get(0).getCountryName();
                        String postalCode = addresses[0].get(0).getPostalCode();
                        String knownName = addresses[0].get(0).getFeatureName(); // Only if available else return NULL

                        // \n is for new line
                        //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
                        //      + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                        message = "Save me my location is : " + knownName + ", " + state + ", " + postalCode;

                        SQLiteDatabase db;
                        db=openOrCreateDatabase("MyDBName.db", Context.MODE_PRIVATE, null);
                        Cursor c=db.rawQuery("SELECT * FROM contacts", null);
                        if(c.moveToFirst()==false){
                            Toast.makeText(getApplicationContext(),"No Contacts added yet",Toast.LENGTH_LONG).show();
                        }
                        else {
                            while (c.moveToNext()) {
                                String target_ph_number = c.getString(c.getColumnIndex("phone"));
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(target_ph_number, null, message, null, null);

                            }
                            Toast.makeText(getApplicationContext(), "SMS sent.",
                                    Toast.LENGTH_LONG).show();

                        }
                        db.close();

                        //Toast.makeText(getApplicationContext(),"Your location is : "
                        //      +address+" address "
                        //    +city+" city "
                        //  +state+" state "
                        //       +country+" country "
                        //     +postalCode+" postal code "
                        //   +knownName+" knownName ",Toast.LENGTH_LONG).show();

                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gps.showSettingsAlert();
                    }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()) {
            case R.id.item1:
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", 0);

                Intent intent = new Intent(getApplicationContext(),DisplayContact.class);
                intent.putExtras(dataBundle);

                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }
}

