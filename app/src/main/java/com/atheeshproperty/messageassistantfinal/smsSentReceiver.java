package com.atheeshproperty.messageassistantfinal;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class smsSentReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        String resString = null;
        String titleRes = null;
        String resID = null;
        String resultText = null;
        String resNumber = null;
        String content = null;
        String mesType = null;


        switch (getResultCode())
        {
            case Activity.RESULT_OK:
                Log.e("Message sent","SMS sent");
                resultText = "Sent successfully!";
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Log.e("Message sent","Generic failure");
                resultText = "Sending failed.";
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Log.e("Message sent","No service");
                resultText = "Sending failed. Service not found.";
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Log.e("Message sent","Null PDU");
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Log.e("Message sent","Radio off");
                break;
        }


        try{
            resString = intent.getExtras().getString("Sent");

            resID = intent.getExtras().getString("ID");
            titleRes = intent.getExtras().getString("Title");
            resNumber = intent.getExtras().getString("number");
            content = intent.getExtras().getString("message");
            mesType = intent.getExtras().getString("Type");

        }catch (NullPointerException e){
            e.printStackTrace();
        }


        assert resString != null;
        if(resString.equals("SENT")){
            Log.e("Message sent","Message sent successfully.");

            final long[] pattern = new long[]{2000, 2000, 3000};

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_access_time_black_24dp)
                    .setContentTitle("Message title: "+titleRes)
                    .setContentText("Status: "+resultText)
                    //.setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle("Message title: "+titleRes).bigText("Status: Message sent!"))
                    .setVibrate(pattern)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    //.setContentIntent(contentIntent)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    //.addAction(R.drawable.done, "Stop Notifying me", parseintent)
                    .setLights(Color.RED, 3000, 3000)
                    .setVisibility(1);

            notificationManager.notify(4, builder.build());

            updateTheOnceSent(resID, context, titleRes, resNumber, content, resultText, mesType);

        }
    }

    public void updateTheOnceSent(String id, Context context, String title, String number, String message, String status, String type){

        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        SQLiteDatabase mydbForWrite = databaseHandler.getWritableDatabase();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

        String nowTime = format.format(c.getTime());

        ContentValues contentValues = new ContentValues();
        ContentValues historyValues = new ContentValues();

        if(type.equals("Once")){
            contentValues.put("ONCE_SEND", 1);

            int res = mydbForWrite.update("MESSAGE_DATA", contentValues, "MESSAGE_ID = ? ", new String[]{id});

            if (res > 0) {
                Log.e("Update OnceSendColumn", " Updated only once type Succeed.");


            } else {
                Log.e("Update OnceSendColumn", "nothing updated. Error occurred.");

            }
        }



        historyValues.put("HISTORY_MES_ID",id);
        historyValues.put("HISTORY_MES_TITLE",title);
        historyValues.put("HISTORY_MES_TYPE", type);
        historyValues.put("HISTORY_MES_NUMBER",number);
        historyValues.put("HISTORY_MES_CONTENT",message);
        historyValues.put("HISTORY_SENT_TIME",nowTime);
        historyValues.put("HISTORY_STATUS",status);

        long done = mydbForWrite.insert("HISTORY_TABLE", null, historyValues);

        if (done == -1){
            Log.e("Add to history table", "Not added. Error occured.");

        }else{
            Log.e("Add to history table", "Added successfully.");

        }

        mydbForWrite.close();
    }

}
