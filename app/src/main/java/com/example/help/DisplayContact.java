package com.example.help;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayContact extends Activity {

    DBHelper mydb;
    TextView name ;
    TextView phone;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contact);

        mydb = new DBHelper(this);

        name = findViewById(R.id.name);
        phone =  findViewById(R.id.phone);
        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeInDB();
            }
        });
    }
        public void storeInDB() {
            Toast.makeText(getApplicationContext(), "save started",Toast.LENGTH_SHORT).show();
            String str_name=name.getText().toString();
            String str_number=phone.getText().toString();

            if(mydb.insertContact(str_name,str_number)){
                Toast.makeText(getApplicationContext(), "done",
                        Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(getApplicationContext(), "not done",
                        Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
}