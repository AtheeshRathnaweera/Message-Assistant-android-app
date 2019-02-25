package com.atheeshproperty.messageassistantfinal;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

public class UpdateAMesage extends AppCompatActivity  implements TimePickerDialog.OnTimeSetListener{

    private EditText title_text, contact_number,
            message_one, message_two, message_three,
            message_four;
    private TextView time_text;
    private ImageButton time_picker;

    private Button cancel_button, save_button;

    private String setTime;

    private String id,repeat, media;

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

        Intent intent = getIntent();

        Log.e("Update", "Started");

        Log.e("Intent data", " title : " + intent.getExtras().getString("Title"));

        id = intent.getExtras().getString("Id");

        title_text.setText(intent.getExtras().getString("Title"));
        contact_number.setText(intent.getExtras().getString("Number"));
        message_one.setText(intent.getExtras().getString("mOne"));
        message_two.setText(intent.getExtras().getString("mThree"));
        message_three.setText(intent.getExtras().getString("mThree"));
        message_four.setText(intent.getExtras().getString("mFour"));
        time_text.setText(intent.getExtras().getString("time"));
        repeat = intent.getExtras().getString("repeat");
        media = intent.getExtras().getString("media");

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

        save_button.setText("Update");

        if (repeat.equals("Once")) {
            timeGroup.check(R.id.onceButton);
        } else {
            timeGroup.check(R.id.everyDayButton);
        }

        switch (media) {
            case "1":
                whatsapp.setChecked(true);
                break;
            case "2":
                TextMessage.setChecked(true);
                break;
            case "3":
                whatsapp.setChecked(true);
                TextMessage.setChecked(true);
                break;
        }

        updatedata();
    }

    private void updatedata() {

        save_button.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                String title = title_text.getText().toString();
                String contactNum = contact_number.getText().toString();
                String messageOne = message_one.getText().toString();
                String messageTwo = message_two.getText().toString();
                String messageThree = message_three.getText().toString();
                String messageFour = message_four.getText().toString();

                int selectedId = timeGroup.getCheckedRadioButtonId();//Get selected repeat button id
                selectedRadioButton = findViewById(selectedId);
                String repeatText = (String) selectedRadioButton.getText();//get selected repeat time button text

                int media = 0;


                //Maintain an integer value for save media type
                if (whatsapp.isChecked()) {
                    media = media + 1;
                }

                if (TextMessage.isChecked()) {
                    media = media + 2;
                }

                String mediaString = String.valueOf(media);

                if (!whatsapp.isChecked() && !TextMessage.isChecked()) {
                    Log.e("Media selection", "Nothing selected.");
                }

                if (validatingTheForm(title, contactNum, messageOne, messageTwo, messageThree, messageFour)) {

                    updateDatabse saveRunnable = new updateDatabse(id, title, contactNum, messageOne, messageTwo, messageThree, messageFour,
                            repeatText, mediaString);
                    new Thread(saveRunnable).start();

                    refreshActivity();

                    Intent intent = new Intent(UpdateAMesage.this, Services.class);
                    startService(intent);

                    Log.e("Service","Service started.");



                } else {
                    Toast.makeText(UpdateAMesage.this, "Please fill the fields properly!.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



    class updateDatabse implements Runnable {

        String titleString;
        String contactNum;
        String messageOne;
        String messageTwo;
        String messageThree;
        String messageFour;
        String repeatText;
        String media;
        String resId;

        updateDatabse(String id, String title, String contactNum, String messageOne, String messageTwo, String messageThree, String messageFour,
                           String repeatText, String media) {

            this.resId = id;
            this.titleString = title;
            this.contactNum = contactNum;
            this.messageOne = messageOne;
            this.messageTwo = messageTwo;
            this.messageThree = messageThree;
            this.messageFour = messageFour;
            this.repeatText = repeatText;
            this.media = media;

        }

        @Override
        public void run() {

            DatabaseHandler handler = new DatabaseHandler(UpdateAMesage.this);
            SQLiteDatabase mydb = handler.getWritableDatabase();

            ContentValues contentValues = new ContentValues();

            contentValues.put("TITLE", titleString);
            contentValues.put("CONTACT_NUMBER", contactNum);
            contentValues.put("CONTENT_ONE", messageOne);
            contentValues.put("CONTENT_TWO", messageTwo);
            contentValues.put("CONTENT_THREE", messageThree);
            contentValues.put("CONTENT_FOUR", messageFour);
            contentValues.put("SEND_TIME", setTime);
            contentValues.put("REPEAT", repeatText);
            contentValues.put("MEDIA", media);
            contentValues.put("ONCE_SEND", 0);

            int res = mydb.update("MESSAGE_DATA", contentValues, "MESSAGE_ID = ?", new String[]{resId});
            mydb.close();

            if (res > 0) {
                Log.e("Data Updated", "Successful.");
                UpdateAMesage.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });


            } else {
                Log.e("Data updating", "failed.");
                UpdateAMesage.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UpdateAMesage.this,"Not updated. Error occurred.",Toast.LENGTH_LONG).show();
                    }
                });

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Boolean validatingTheForm(String title, String contactNum, String messageOne, String messageTwo, String messageThree, String messageFour) {

        if (title.trim().length() != 0 && isPhoneNumberValid(contactNum) && setTime != null &&
                validMessageBody(messageOne, messageTwo, messageThree, messageFour) && validateMediaInput()) {

            Log.e("Full validation", "OK.");
            return true;

        } else {
            Log.e("Full validation", "Error.");
            return false;
        }
    }

    private Boolean validMessageBody(String first, String second, String third, String fourth) {


        if (first.trim().length() != 0 || second.trim().length() != 0 || third.trim().length() != 0 || fourth.trim().length() != 0) {

            Log.e("Message body", "message body validate is OK.");
            return true;

        } else {
            Log.e("Message body", "message body validate is failed.");
            return false;
        }

    }

    private Boolean validateMediaInput() {

        if (whatsapp.isChecked() || TextMessage.isChecked()) {
            Log.e("MediaType", "Media type validate is OK.");
            return true;

        } else {
            Log.e("Media type", "error.");
            return false;
        }
    }

    public boolean isPhoneNumberValid(String phoneNumber) {
        //NOTE: This should probably be a member variable.
        Boolean res = PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber);

        if (res) {
            Log.e("Phone number", " OK.");

        } else {
            Log.e("Phone number", "error");
        }

        return res;
    }



    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(0, 0, 0, hourOfDay, minute, 0);
        String timeFormat = new SimpleDateFormat("hh : mm").format(calendar.getTime());

        SimpleDateFormat fullTimeFormatter = new SimpleDateFormat("HH:mm:ss");
        setTime = fullTimeFormatter.format(calendar.getTime());

        String time;
        if (hourOfDay > 12) {

            time = "PM";
        } else {
            time = "AM";

        }

        time_text.setText(timeFormat + " " + time);
        Log.e("Selected time","Time : "+setTime);
    }

    public void refreshActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);

        Log.d("Refresh", "Activity refreshed.");
    }
}
