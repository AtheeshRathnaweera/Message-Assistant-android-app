package com.atheeshproperty.messageassistantfinal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;


public class AlertReceiver extends BroadcastReceiver {

    String body_one, body_two, body_three, body_four;
    String repeatType, title, phone_number;
    int media;
    DatabaseHandler databaseHandler;
    SQLiteDatabase mydbForWrite;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("String Key", "intent received.");
        int id = 0;


        try {
            Log.e("received data", "data: " + intent.getIntExtra("id",1));
            id = intent.getIntExtra("id",1);


        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        databaseHandler = new DatabaseHandler(context);
        SQLiteDatabase mydb = databaseHandler.getReadableDatabase();
        mydbForWrite = databaseHandler.getWritableDatabase();

        String idString = Integer.toString(id);

        String query = "SELECT * FROM MESSAGE_DATA WHERE MESSAGE_ID = ?";

        @SuppressLint("Recycle") Cursor c = mydb.rawQuery(query, new String[]{idString});

        if (c.moveToFirst()) {
            do {
                title = c.getString(c.getColumnIndex("TITLE"));
                phone_number = c.getString(c.getColumnIndex("CONTACT_NUMBER"));
                body_one = c.getString(c.getColumnIndex("CONTENT_ONE"));
                body_two = c.getString(c.getColumnIndex("CONTENT_TWO"));
                body_three = c.getString(c.getColumnIndex("CONTENT_THREE"));
                body_four = c.getString(c.getColumnIndex("CONTENT_FOUR"));
                repeatType = c.getString(c.getColumnIndex("REPEAT"));
                media = c.getInt(c.getColumnIndex("MEDIA"));

            } while (c.moveToNext());

        }

        ArrayList<String> messageArray = new ArrayList<String>();
        if(!body_one.trim().isEmpty()){
            messageArray.add(body_one);
        }
        if(!body_two.trim().isEmpty()){
            messageArray.add(body_two);
        }
        if(!body_three.trim().isEmpty()){
            messageArray.add(body_three);
        }
        if(!body_four.trim().isEmpty()){
            messageArray.add(body_four);
        }

        Log.e("A message entry","Message name: "+title+" Found not null entries: "+messageArray.size());

        if (repeatType.equals("Once")) {

            if (media == 1) {
                //wtsapp
                sendViaWhatsapp();
            }
            if (media == 2) {
                //Text message
                sendAText(phone_number,messageArray, context);
                Log.e("Text message","Sent");
            }

            if (media == 3) {
                //both
                sendViaWhatsapp();
                sendAText(phone_number,messageArray, context);
                Log.e("Text message","Sent");

            }
            updateTheOnceSendColumn update = new updateTheOnceSendColumn(idString);
            new Thread(update).start();


        } else {
            if (media == 1) {
                //wtsapp
                sendViaWhatsapp();
            }
            if (media == 2) {
                //Text message
                sendAText(phone_number, messageArray, context);
                Log.e("Text message","Sent");
            }

            if (media == 3) {
                //both
                sendViaWhatsapp();
                sendAText(phone_number, messageArray, context);
                Log.e("Text message","Sent");

            }

        }


    }

    private void sendViaWhatsapp() {

        Random rand = new Random();

        int addMinutes = rand.nextInt(4);

    }


    private void sendAText(String number, ArrayList<String> messages, Context context) {

        int bound = messages.size();

        Random rand = new Random();
        int addMinutes = rand.nextInt(bound);

        String res_message = messages.get(addMinutes);

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            Log.e("Message permission"," Not granted.");
            permissionNeededNotification(context);

        }else{

            Intent intent = new Intent(context, smsSentReceiver.class);
            intent.putExtra("Sent","SENT");
            intent.putExtra("Title",title);
            PendingIntent sentPI = PendingIntent.getBroadcast(context,1, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(number, null, res_message, sentPI, null);

            Log.e("Text message","text message sent. phone number : "+number);
        }

    }

    private void permissionNeededNotification(Context context){
        //Pop up a notification to request message permission

        Intent in = new Intent(context, MainActivity.class);//Intent to open the app when notification click
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, in, 0);

        final long[] pattern = new long[]{2000, 2000, 3000};
        lightUpTheScreen(context);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_access_time_black_24dp)
                .setContentTitle("Message Assistant !")
                .setContentText("Please give SMS permission to send text messages.")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Please give SMS permission to send text messages."))
                .setVibrate(pattern)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(contentIntent)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                //.addAction(R.drawable.done, "Stop Notifying me", parseintent)
                .setLights(Color.RED, 3000, 3000)
                .setVisibility(1);

        notificationManager.notify(1, builder.build());

    }

    class updateTheOnceSendColumn implements Runnable {

        String resId;

        updateTheOnceSendColumn(String id) {

            this.resId = id;

        }

        @Override
        public void run() {

            ContentValues contentValues = new ContentValues();

            contentValues.put("ONCE_SEND", 1);

            int res = mydbForWrite.update("MESSAGE_DATA", contentValues, "MESSAGE_ID = ?", new String[]{resId});
            mydbForWrite.close();

            if (res > 0) {
                Log.e("Update OnceSendColumn", "Succeed.");


            } else {
                Log.e("Update OnceSendColumn", "nothing updated. Error occured.");

            }
        }
    }

    public void lightUpTheScreen(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        assert powerManager != null;
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Tag");
        wakeLock.acquire(9 * 60 * 1000L /*10 minutes*/);
        wakeLock.release();
    }


}
