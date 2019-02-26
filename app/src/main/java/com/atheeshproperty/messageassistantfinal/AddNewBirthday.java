package com.atheeshproperty.messageassistantfinal;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TimePicker;

public class AddNewBirthday extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_new_birthday);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }

    @Override
    public void onBackPressed() {
        communicateWithMain();
        super.onBackPressed();
    }

    private void communicateWithMain() {
        Log.e("Communicate with main","Executed.");
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
