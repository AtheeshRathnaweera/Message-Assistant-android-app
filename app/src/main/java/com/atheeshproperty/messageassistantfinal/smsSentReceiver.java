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

public class smsSentReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        String resString = null;
        String titleRes = null;
        String resID = null;
        String resultText = null;

        switch (getResultCode())
        {
            case Activity.RESULT_OK:
                Log.e("Message sent","SMS sent");
                resultText = "Sent successfully!.";
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
            titleRes = intent.getExtras().getString("Title");
            resID = intent.getExtras().getString("ID");

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

            notificationManager.notify(2, builder.build());

            updateTheOnceSent(resID, context);

        }
    }

    private void updateTheOnceSent(String id, Context context){

        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        SQLiteDatabase mydbForWrite = databaseHandler.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put("ONCE_SEND", 1);

        int res = mydbForWrite.update("MESSAGE_DATA", contentValues, "MESSAGE_ID = ?", new String[]{id});

        if (res > 0) {
            Log.e("Update OnceSendColumn", "Succeed.");


        } else {
            Log.e("Update OnceSendColumn", "nothing updated. Error occurred.");

        }

        mydbForWrite.close();
    }
}
