package com.atheeshproperty.messageassistantfinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;

public class SplashScreenActivity extends AppCompatActivity {

    private DatabaseHandler db;

    private final String DBname = "MessageAssistant.db";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startup();

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();

    }

    public void startup() {

        File database = getApplicationContext().getDatabasePath(DBname);

        if (!database.exists()) {
            Log.d("***Database exist****", "Database nor found.");
        } else {
            Log.d("****Database exist****", "Database exist");
        }
        db = new DatabaseHandler(this);

        db.checkExistence();
    }
}
