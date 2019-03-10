package com.atheeshproperty.messageassistantfinal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UpdateAMesage extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private EditText title_text,
            message_one, message_two, message_three,
            message_four;

    private TextView time_text, contact_number, dateLabel, dateText;
    private ImageButton time_picker, openContacts, datePicker;

    private Button cancel_button, save_button;

    private String setTime, setDate;

    private String id, repeat, media;

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

        dateLabel = findViewById(R.id.messageDateLabel);
        dateText = findViewById(R.id.messageDateText);
        datePicker = findViewById(R.id.message_date_picker);

        time_text = findViewById(R.id.message_time);
        time_picker = findViewById(R.id.message_time_picker);

        cancel_button = findViewById(R.id.cancel);
        save_button = findViewById(R.id.save);

        timeGroup = findViewById(R.id.timeRadioGroup);
        selectedRadioButton = findViewById(R.id.onceButton);

        openContacts = findViewById(R.id.contactListOpen);

        selectedRadioButton.setChecked(true);

        whatsapp = findViewById(R.id.message_type_whatsapp);
        TextMessage = findViewById(R.id.message_type_text);

        whatsapp.setVisibility(View.INVISIBLE);//Hide the wtsapp check box

        Intent intent = getIntent();

        Log.e("Update", "Started");

        id = intent.getExtras().getString("Id");

        title_text.setText(intent.getExtras().getString("Title"));

        contact_number.setText(intent.getExtras().getString("Number"));

        message_one.setText(intent.getExtras().getString("mOne"));
        message_two.setText(intent.getExtras().getString("mThree"));
        message_three.setText(intent.getExtras().getString("mThree"));
        message_four.setText(intent.getExtras().getString("mFour"));
        time_text.setText(intent.getExtras().getString("time"));
        dateText.setText(intent.getExtras().getString("date"));

        setTime = intent.getExtras().getString("time");
        setDate = intent.getExtras().getString("date");

        repeat = intent.getExtras().getString("repeat");
        media = intent.getExtras().getString("media");

        Log.e("Checking", " Title text" + title_text.getText());



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
            dateLabel.setText("Started from : ");
            datePicker.setVisibility(View.INVISIBLE);
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

        openContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(UpdateAMesage.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("Message permission", " requested.");

                    ActivityCompat.requestPermissions(UpdateAMesage.this, new String[]{Manifest.permission.READ_CONTACTS}, 2);


                } else {

                    Log.e("Message permission", " Already granted.");
                    openTheContactsList();

                }

            }
        });

        dateAndTimePicker();
        changeTheSetDateLableAccordingToTheButton();
        updatedata();
    }

    public void dateAndTimePicker(){
        time_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DataPickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

    }

    public void changeTheSetDateLableAccordingToTheButton() {

        timeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                Log.e("Selected radio", "id: " + checkedId);

                if (checkedId == R.id.everyDayButton) {
                    dateLabel.setText("Start from : ");
                    datePicker.setVisibility(View.INVISIBLE);

                } else {
                    dateLabel.setText("Send date : ");
                    datePicker.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void openTheContactsList() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        startActivityForResult(intent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case 1:
                    Cursor cursor = null;
                    String phoneNumber = null;
                    List<String> allnumbers = new ArrayList<String>();
                    int phoneIndex = 0;

                    try {

                        assert data != null;
                        Uri result = data.getData();
                        assert result != null;
                        String id = result.getLastPathSegment();
                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
                        assert cursor != null;
                        phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);

                        if (cursor.moveToFirst()) {
                            while (!cursor.isAfterLast()) {
                                phoneNumber = cursor.getString(phoneIndex);
                                allnumbers.add(phoneNumber);
                                cursor.moveToNext();

                            }
                        } else {
                            Toast.makeText(UpdateAMesage.this, "No numbers found", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {

                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }

                        final CharSequence[] items = allnumbers.toArray(new String[allnumbers.size()]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateAMesage.this);
                        builder.setTitle("Choose a number");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selectedNumber = items[which].toString();
                                selectedNumber = selectedNumber.replace("-", "");
                                contact_number.setText(selectedNumber);
                            }
                        });
                        AlertDialog alert = builder.create();
                        if (allnumbers.size() > 1) {
                            alert.show();
                        } else {
                            String selectedNumber = phoneNumber;
                            selectedNumber = selectedNumber.replace("-", "");
                            contact_number.setText(selectedNumber);
                        }

                        if (phoneNumber.length() == 0) {
                            Toast.makeText(UpdateAMesage.this, "No numbers found.", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
        } else {
            //activity result error action
        }
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

                    onBackPressed();

                    Intent intent = new Intent(UpdateAMesage.this, Services.class);
                    startService(intent);

                    Log.e("Service", "Service started.");


                } else {
                    Toast.makeText(UpdateAMesage.this, "Please fill the fields properly!.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(year, month, dayOfMonth, 0, 0, 0);
        String timeFormat = new SimpleDateFormat("yyyy MMMM dd").format(calendar.getTime());

        SimpleDateFormat fullTimeFormatter = new SimpleDateFormat("yyyy-MM-dd");
        setDate = fullTimeFormatter.format(calendar.getTime());

        dateText.setText(timeFormat);
        Log.e("Selected Date", "Date : " + setDate);
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
            contentValues.put("SEND_DATE", setDate);
            contentValues.put("MEDIA", media);
            contentValues.put("ONCE_SEND", 0);

            int res = mydb.update("MESSAGE_DATA", contentValues, "MESSAGE_ID = ?", new String[]{resId});
            mydb.close();

            if (res > 0) {
                Log.e("Data Updated", "Successful.");
                UpdateAMesage.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UpdateAMesage.this, "Updated successfully.", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(UpdateAMesage.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }
                });


            } else {
                Log.e("Data updating", "failed.");
                UpdateAMesage.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UpdateAMesage.this, "Not updated. Error occurred.", Toast.LENGTH_LONG).show();
                    }
                });

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Boolean validatingTheForm(String title, String contactNum, String messageOne, String messageTwo, String messageThree, String messageFour) {

        if (title.trim().length() != 0 && isPhoneNumberValid(contactNum) && setTime != null && setDate != null &&
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
        String phNo = phoneNumber.replaceAll("[()\\-\\s]", "");
        Log.e("Phone number", "This is the number: " + phoneNumber);
        Log.e("Phone number", " This is the updated number : " + phNo);

        Boolean res = PhoneNumberUtils.isGlobalPhoneNumber(phNo);

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
        Log.e("Selected time", "Time : " + setTime);
    }
}
