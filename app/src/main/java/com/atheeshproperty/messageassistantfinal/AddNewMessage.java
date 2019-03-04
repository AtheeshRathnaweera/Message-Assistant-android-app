package com.atheeshproperty.messageassistantfinal;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
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


public class AddNewMessage extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private EditText title_text,
            message_one, message_two, message_three,
            message_four;

    private TextView time_text, contact_number;
    private ImageButton time_picker, openContacts;

    private Button cancel_button, save_button;

    private String setTime;

    private RadioGroup timeGroup;
    private RadioButton selectedRadioButton;

    private CheckBox whatsapp, TextMessage;

    private DatabaseHandler databseHelper;
    private SQLiteDatabase mydb;

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

        openContacts = findViewById(R.id.contactListOpen);

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

        openContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(AddNewMessage.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("Message permission", " requested.");
                    // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},1);

                        ActivityCompat.requestPermissions(AddNewMessage.this, new String[]{Manifest.permission.READ_CONTACTS}, 2);


                } else {

                    Log.e("Message permission", " Already granted.");
                    openTheContactsList();

                }

            }
        });


        checkWhatsappInstalledOrNot();
        checkMessagePermissionWhenTextClick();
        saveData();


    }

    private void checkWhatsappInstalledOrNot(){

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appInstalledOrNot("com.whatsapp")){

                    Log.e("whatsapp installed","yes");

                }else{
                    Log.e("whatsapp installed","no");

                    final AlertDialog.Builder builder = new AlertDialog.Builder(AddNewMessage.this);
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

    private void checkMessagePermissionWhenTextClick() {

        TextMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddNewMessage.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("Message permission", " requested.");
                    // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},1);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ActivityCompat.requestPermissions(AddNewMessage.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                    } else {
                        ActivityCompat.requestPermissions(AddNewMessage.this, new String[]{Manifest.permission.SEND_SMS}, 1);

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode) {

                case 1:
                    Cursor cursor = null;
                    String phoneNumber= null;
                    List<String> allnumbers = new ArrayList<String>();
                    int phoneIndex = 0;

                    try{

                        assert data != null;
                        Uri result = data.getData();
                        assert result != null;
                        String id = result.getLastPathSegment();
                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[] {id},null);
                        assert cursor != null;
                        phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);

                        if(cursor.moveToFirst()){
                            while (!cursor.isAfterLast()){
                                phoneNumber = cursor.getString(phoneIndex);
                                allnumbers.add(phoneNumber);
                                cursor.moveToNext();

                            }
                        } else{
                            Toast.makeText(AddNewMessage.this,"No numbers found",Toast.LENGTH_LONG).show();
                        }
                    }catch (Exception e){
                            e.printStackTrace();
                    } finally {
                        if(cursor != null){
                            cursor.close();
                        }

                        final CharSequence[] items = allnumbers.toArray(new String[allnumbers.size()]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewMessage.this);
                        builder.setTitle("Choose a number");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selectedNumber = items[which].toString();
                                selectedNumber = selectedNumber.replace("-","");
                                contact_number.setText(selectedNumber);
                            }
                        });
                        AlertDialog alert = builder.create();
                        if(allnumbers.size() > 1){
                            alert.show();
                        }else{
                            String selectedNumber = phoneNumber;
                            assert selectedNumber != null;
                            selectedNumber = selectedNumber.replace("-","");
                            contact_number.setText(selectedNumber);
                        }

                        assert phoneNumber != null;
                        if (phoneNumber.length() == 0){
                            Toast.makeText(AddNewMessage.this,"No numbers found.",Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
        }else{
            //activity result error action
        }
    }

    public void checkWhetherInProtectedAppsOfHuawei(){


        final SharedPreferences settings = getSharedPreferences("ProtectedApps", MODE_PRIVATE);
        final String saveIfSkip = "skipProtectedAppsMessage";
        boolean skipMessage = settings.getBoolean(saveIfSkip, false);

        if("huawei".equalsIgnoreCase(android.os.Build.MANUFACTURER) && !skipMessage) {
            AlertDialog.Builder builder  = new AlertDialog.Builder(this);
            builder.setTitle("Warning").setMessage("This app is in protected app list. Please remove this app from protected app list for work properly!")
                    .setPositiveButton("Go to protected apps", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                            startActivity(intent);
                            settings.edit().putBoolean("protected",true).apply();
                        }
                    }).create().show();
        }


    }

    private void saveData() {

        save_button.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                databseHelper = new DatabaseHandler(getApplicationContext());
                mydb = databseHelper.getWritableDatabase();

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

                    saveDataToDatabase saveRunnable = new saveDataToDatabase(title, contactNum, messageOne, messageTwo, messageThree, messageFour,
                            repeatText, mediaString);
                    new Thread(saveRunnable).start();

                    refreshActivity();

                    Intent intent = new Intent(AddNewMessage.this, Services.class);
                    startService(intent);

                    Log.e("Service", "Service started.");

                } else {
                    Toast.makeText(AddNewMessage.this, "Please fill the fields properly!.", Toast.LENGTH_LONG).show();
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
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    class saveDataToDatabase implements Runnable {

        String titleString;
        String contactNum;
        String messageOne;
        String messageTwo;
        String messageThree;
        String messageFour;
        String repeatText;
        String media;

        saveDataToDatabase(String title, String contactNum, String messageOne, String messageTwo, String messageThree, String messageFour,
                           String repeatText, String media) {

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
            contentValues.put("PAUSE",0);

            long res = mydb.insert("MESSAGE_DATA", null, contentValues);

            if (res == -1) {
                Log.e("Data saving", "not saved error.");
                AddNewMessage.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddNewMessage.this, "Not saved. Error occurred.", Toast.LENGTH_LONG).show();
                    }
                });


            } else {
                Log.e("Data saving", "Successful");
                AddNewMessage.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddNewMessage.this, "Saved successfully", Toast.LENGTH_LONG).show();
                        finish();
                        Intent intent = new Intent(AddNewMessage.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
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
        Log.e("Selected time", "Time : " + setTime);
    }

    public void refreshActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);

        Log.d("Refresh", "Activity refreshed.");
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



}
