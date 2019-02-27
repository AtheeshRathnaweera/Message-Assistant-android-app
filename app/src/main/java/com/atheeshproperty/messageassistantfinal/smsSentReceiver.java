package com.atheeshproperty.messageassistantfinal;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class smsSentReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        String resString = null;
        String titleRes = null;
        try{
            resString = intent.getExtras().getString("Sent");
            titleRes = intent.getExtras().getString("Title");

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
                    .setContentText("Status: Message sent!")
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

        }
    }
}
