package com.atheeshproperty.messageassistantfinal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

public class BirthdayAlertReceiver extends BroadcastReceiver {

    DatabaseHandler databaseHandler;
    SQLiteDatabase mydbForWrite;

    String title, phoneNumber,message;
    int paused;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("String Key", "birthday intent received.");
        int id = 0;
        int entryID = 2;

        try {
            Log.e("received data", "data: " + intent.getIntExtra("birthdayID",1));
            id = intent.getIntExtra("birthdayID",1);


        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        databaseHandler = new DatabaseHandler(context);
        SQLiteDatabase mydb = databaseHandler.getReadableDatabase();

        String idString = Integer.toString(id);

        String query = "SELECT * FROM BIRTHDAY_DATA WHERE BIRTHDAY_ID = ?";

        @SuppressLint("Recycle") Cursor c = mydb.rawQuery(query, new String[]{idString});
        if (c.moveToFirst()) {
            do {
                title = c.getString(c.getColumnIndex("BIRTHDAY_TITLE"));
                phoneNumber = c.getString(c.getColumnIndex("BIRTHDAY_CONTACT_NUMBER"));
                message = c.getString(c.getColumnIndex("BIRTHDAY_CONTENT"));
                paused=c.getInt(c.getColumnIndex("BIRTHDAY_PAUSE"));


                if(paused == 0){
                    Log.e("Birthday alert receiver","Not passed.");
                    showBirthdayNotification(context,phoneNumber,message,entryID);
                    entryID = entryID+1;

                }else{
                    Log.e("Birthday alert receiver","Passed.");
                    entryID = entryID+1;
                }




            } while (c.moveToNext());

        }

        c.close();
        mydb.close();

    }

    private void showBirthdayNotification(Context context, String number, String message, int entryID){

        int requestID = (int) System.currentTimeMillis();
        //Create send SMS intent
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", number);
        smsIntent.putExtra("sms_body",message);
        PendingIntent parseintent = PendingIntent.getActivity(context, requestID, smsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Send wtsapp message
        PackageManager packageManager = context.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent wtsappIntent = null;


        try {
            String url = "https://api.whatsapp.com/send?phone+"+ number + "&text=" + URLEncoder.encode(message,"UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if(i.resolveActivity(packageManager) != null){
                //context.startActivity(i);

                wtsappIntent = PendingIntent.getActivity(context, requestID, i, PendingIntent.FLAG_UPDATE_CURRENT);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        final long[] pattern = new long[]{2000, 2000, 3000};
        lightUpTheScreen(context);

        Intent in = new Intent(context, MainActivity.class);//Intent to open the app when notification click
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, in, 0);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if(appInstalledOrNot("com.whatsapp",context)){

            Log.e("whatsapp installed","yes");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_access_time_black_24dp)
                    .setContentTitle("It's "+title+" s birthday today!")
                    .setContentText("Say Happy Birthday to "+title+" !")
                    //.setStyle(new NotificationCompat.BigTextStyle().bigText("Please give SMS permission to send text messages."))
                    .setVibrate(pattern)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(contentIntent)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .addAction(R.drawable.sms, "Text", parseintent)
                    .addAction(R.drawable.wtsapp,"Whatspp Message",wtsappIntent)
                    .setLights(Color.RED, 3000, 3000)
                    .setVisibility(1);

            notificationManager.notify(entryID, builder.build());

        }else{
            Log.e("whatsapp installed","no");

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_access_time_black_24dp)
                    .setContentTitle("It's "+title+" s birthday today!")
                    .setContentText("Say Happy Birthday to "+title+ " !")
                    //.setStyle(new NotificationCompat.BigTextStyle().bigText("Please give SMS permission to send text messages."))
                    .setVibrate(pattern)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setContentIntent(contentIntent)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .addAction(R.drawable.sms, "Text", parseintent)
                    .setLights(Color.RED, 3000, 3000)
                    .setVisibility(1);

            notificationManager.notify(entryID, builder.build());
        }





    }

    private boolean appInstalledOrNot(String uri, Context context) {
        PackageManager pm = context.getPackageManager();
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

    public void lightUpTheScreen(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        assert powerManager != null;
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Tag");
        wakeLock.acquire(5 * 60 * 1000L /*10 minutes*/);
        wakeLock.release();
    }

}
