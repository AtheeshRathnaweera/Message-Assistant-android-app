package com.atheeshproperty.messageassistantfinal;

import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AddNewMessage extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private EditText title_text, contact_number,
            message_one, message_two, message_three,
            message_four;
    private TextView time_text;
    private ImageButton time_picker;

    private Button cancel_button, save_button;

    private String time;

    private RadioGroup timeGroup;
    private RadioButton selectedRadioButton;

    private CheckBox whatsapp, TextMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_new_message);

        title_text = findViewById(R.id.message_title);
        contact_number = findViewById(R.id.message_contact);
        message_one = findViewById(R.id.message_body_one);
        message_two = findViewById(R.id.message_body_two);
        message_three = findViewById(R.id.message_body_three);
        message_four = findViewById(R.id.message_body_four);

        time_text = findViewById(R.id.message_time);

        time_picker = findViewById(R.id.message_time_picker);

        cancel_button = findViewById(R.id.cancel);
        save_button = findViewById(R.id.save);

        timeGroup = findViewById(R.id.timeRadioGroup);
        selectedRadioButton = findViewById(R.id.onceButton);

        selectedRadioButton.setChecked(true);

        whatsapp = findViewById(R.id.message_type_whatsapp);
        TextMessage = findViewById(R.id.message_type_text);

        time_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        saveData();


    }

    private void saveData(){

        save_button.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(validatingTheForm()){
                    Toast.makeText(AddNewMessage.this,"Ready to save",Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(AddNewMessage.this,"Please fill the fields properly!.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Boolean validatingTheForm(){

        String title = title_text.getText().toString();
        String contactNum = contact_number.getText().toString();
        String messageOne = message_one.getText().toString();
        String messageTwo = message_two.getText().toString();
        String messageThree = message_three.getText().toString();
        String messageFour = message_four.getText().toString();

       // int selectedId = timeGroup.getCheckedRadioButtonId();
        //selectedRadioButton = findViewById(selectedId);

        //String timebuttontext = (String) selectedRadioButton.getText();//get selected time button text

        if(title.trim().length() != 0 && isPhoneNumberValid(contactNum) && time != null &&
                validMessageBody(messageOne,messageTwo,messageThree,messageFour) && validateMediaInput() ){

            Log.e("Full validation","OK.");
            return true;

        }else{
            Log.e("Full validation","Error.");
            return false;
        }
    }

    private Boolean validMessageBody(String first, String second,String third, String fourth){


        if(first.trim().length() != 0 || second.trim().length() != 0 || third.trim().length() != 0 || fourth.trim().length() != 0){

            Log.e("Message body","message body validate is OK.");
            return true;

        }else{
            Log.e("Message body","message body validate is failed.");
            return false;
        }

    }

    private Boolean validateMediaInput(){

        if(whatsapp.isChecked() || TextMessage.isChecked()){
            Log.e("MediaType","Media type validate is OK.");
            return true;

        }else{
            Log.e("Media type","error.");
            return false;
        }
    }

    public boolean isPhoneNumberValid(String phoneNumber)
    {
        //NOTE: This should probably be a member variable.
        Boolean res = PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber);

        if(res){
            Log.e("Phone number"," OK.");

        }else{
            Log.e("Phone number","error");
        }

        return res;
    }



    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(0, 0, 0,hourOfDay,minute,0);
        String timeFormat = new SimpleDateFormat("hh : mm").format(calendar.getTime());

        SimpleDateFormat fullTimeFormatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        time = fullTimeFormatter.format(calendar.getTime());

        String time;
        if(hourOfDay > 12){

            time = "PM";
        }else{
            time = "AM";

        }

        time_text.setText(timeFormat+" "+time);
    }
}
