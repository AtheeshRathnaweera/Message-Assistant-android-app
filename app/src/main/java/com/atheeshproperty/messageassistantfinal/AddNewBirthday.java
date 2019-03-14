package com.atheeshproperty.messageassistantfinal;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
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

public class AddNewBirthday extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private String setTime, setDate;
    private TextView time_text, contact_number, birth_date;
    private EditText person_name, content;
    private ImageButton date_picker, contact_list_open, message_time_picker;

    private CheckBox whatsapp, TextMessage;

    private RadioGroup timeGroup;
    private RadioButton selectedRadioButton;

    private Button cancel_button, save_button;

    private DatabaseHandler databseHelper;
    private SQLiteDatabase mydb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_new_birthday);

        person_name = findViewById(R.id.person_name);
        birth_date = findViewById(R.id.birthday_date);
        date_picker = findViewById(R.id.birthday_date_picker);
        contact_number = findViewById(R.id.message_contact);
        contact_list_open = findViewById(R.id.contactListOpen);
        content = findViewById(R.id.message);
        time_text = findViewById(R.id.message_time);
        message_time_picker = findViewById(R.id.birthday_time_picker);

        whatsapp = findViewById(R.id.message_type_whatsapp);
        TextMessage = findViewById(R.id.message_type_text);

        timeGroup = findViewById(R.id.autoRadioGroup);
        selectedRadioButton = findViewById(R.id.noButton);
        selectedRadioButton.setChecked(true);

        cancel_button = findViewById(R.id.cancel);
        save_button = findViewById(R.id.save);


        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        contact_list_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(AddNewBirthday.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("Message permission", " requested.");
                    // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},1);

                    ActivityCompat.requestPermissions(AddNewBirthday.this, new String[]{Manifest.permission.READ_CONTACTS}, 3);


                } else {

                    Log.e("Message permission", " Already granted.");
                    openTheContactsList();

                }

            }
        });

        message_time_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DataPickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        checkWhatsappInstalledOrNot();
        checkMessagePermissionWhenTextClick();
        saveData();
    }

    private void checkWhatsappInstalledOrNot() {

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appInstalledOrNot("com.whatsapp")) {

                    Log.e("whatsapp installed", "yes");

                } else {
                    Log.e("whatsapp installed", "no");

                    final AlertDialog.Builder builder = new AlertDialog.Builder(AddNewBirthday.this);
                    builder.setTitle("Notice");
                    builder.setMessage("Whatsapp is not installed in your phone! To use this feature please install whatspp to your phone!");

                    builder.setPositiveButton("Ok!",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();

                                }
                            });

                    AlertDialog deleteAlert = builder.create();
                    deleteAlert.show();

                    whatsapp.setChecked(false);
                }
            }
        });


    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    private void checkMessagePermissionWhenTextClick() {

        TextMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddNewBirthday.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("Message permission", " requested.");
                    // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},1);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ActivityCompat.requestPermissions(AddNewBirthday.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                    } else {
                        ActivityCompat.requestPermissions(AddNewBirthday.this, new String[]{Manifest.permission.SEND_SMS}, 1);

                    }
                } else {

                    Log.e("Message permission", " Already granted.");

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

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    private void communicateWithMain() {
        Log.e("Communicate with main", "Executed.");
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
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
                            Toast.makeText(AddNewBirthday.this, "No numbers found", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }

                        final CharSequence[] items = allnumbers.toArray(new String[allnumbers.size()]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewBirthday.this);
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
                            assert selectedNumber != null;
                            selectedNumber = selectedNumber.replace("-", "");
                            contact_number.setText(selectedNumber);
                        }

                        assert phoneNumber != null;
                        if (phoneNumber.length() == 0) {
                            Toast.makeText(AddNewBirthday.this, "No numbers found.", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
        } else {
            //activity result error action
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Boolean validatingTheForm(String name, String contactNum, String message) {

        if (name.trim().length() != 0 && isPhoneNumberValid(contactNum) && setTime != null &&
                setDate != null && validMessageBody(message) && validateMediaInput()) {

            Log.e("Full validation", "OK.");
            return true;

        } else {
            Log.e("Full validation", "Error.");
            return false;
        }
    }

    private Boolean validMessageBody(String message) {

        if (message.trim().length() != 0) {

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

    private void saveData() {

        save_button.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                databseHelper = new DatabaseHandler(getApplicationContext());
                mydb = databseHelper.getWritableDatabase();

                String title = person_name.getText().toString();

                String phNo = contact_number.getText().toString();
                String contactNum = phNo.replaceAll("[()\\-\\s]", "");

                String message = content.getText().toString();

                int media = 0;
                int autoText = 0;

                //Maintain an integer value for save media type
                if (whatsapp.isChecked()) {
                    media = media + 1;
                }

                if (TextMessage.isChecked()) {
                    media = media + 2;
                }

                int selectedId = timeGroup.getCheckedRadioButtonId();//Get selected repeat button id
                selectedRadioButton = findViewById(selectedId);
                String autoTypeText = (String) selectedRadioButton.getText();//get selected repeat time button text

                String mediaString = String.valueOf(media);

                if (!whatsapp.isChecked() && !TextMessage.isChecked()) {
                    Log.e("Media selection", "Nothing selected.");
                }

                if (validatingTheForm(title, contactNum, message)) {

                    AddNewBirthday.saveDataToDatabase saveRunnable = new AddNewBirthday.saveDataToDatabase(title, contactNum, message, mediaString, autoTypeText);
                    new Thread(saveRunnable).start();

                    refreshActivity();

                    //Intent intent = new Intent(AddNewBirthday.this, Services.class);
                    // startService(intent);

                    // Log.e("Service", "Service started.");

                } else {
                    Toast.makeText(AddNewBirthday.this, "Please fill the fields properly!.", Toast.LENGTH_LONG).show();
                }
            }


        });
    }

    public void refreshActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);

        Log.d("Refresh", "Activity refreshed.");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();

        calendar.set(year, month, dayOfMonth, 0, 0, 0);
        String timeFormat = new SimpleDateFormat("MMMM dd").format(calendar.getTime());

        SimpleDateFormat fullTimeFormatter = new SimpleDateFormat("yyyy-MM-dd");
        setDate = fullTimeFormatter.format(calendar.getTime());

        birth_date.setText(timeFormat);
        Log.e("Selected Date", "Date : " + setDate);

    }

    class saveDataToDatabase implements Runnable {

        String name;
        String contactNum;
        String message;
        String media;
        String autoT;

        saveDataToDatabase(String name, String contactNum, String message, String media, String autoText) {

            this.name = name;
            this.contactNum = contactNum;
            this.message = message;
            this.media = media;
            this.autoT = autoText;

        }

        @Override
        public void run() {

            ContentValues contentValues = new ContentValues();

            contentValues.put("BIRTHDAY_TITLE", name);
            contentValues.put("BIRTHDAY_DATE", setDate);
            contentValues.put("BIRTHDAY_CONTACT_NUMBER", contactNum);
            contentValues.put("BIRTHDAY_CONTENT", message);
            contentValues.put("BIRTHDAY_SEND_TIME", setTime);
            contentValues.put("BIRTHDAY_MEDIA", media);
            contentValues.put("BIRTHDAY_PAUSE", 0);
            contentValues.put("BIRTHDAY_AUTO", autoT);

            long res = mydb.insert("BIRTHDAY_DATA", null, contentValues);

            if (res == -1) {
                Log.e("Data saving", "not saved error.");
                AddNewBirthday.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddNewBirthday.this, "Not saved. Error occurred.", Toast.LENGTH_LONG).show();
                    }
                });


            } else {
                Log.e("Data saving", "Successful");
                AddNewBirthday.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddNewBirthday.this, "Saved successfully", Toast.LENGTH_LONG).show();
                        finish();
                        Intent intent = new Intent(AddNewBirthday.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }
                });

            }
        }
    }


}
