package com.atheeshproperty.messageassistantfinal;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Services extends Service {

    final class TheThread implements Runnable {
        int serviceID;
        DatabaseHandler dbHandler;

        TheThread(int serviceID) {

            this.serviceID = serviceID;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {

            Log.d("alert", "Service started running in thread.");
            dbHandler = new DatabaseHandler(getApplicationContext());
            SQLiteDatabase mydb = dbHandler.getReadableDatabase();

            final long ONE_MINUTE_IN_MILLIS = 60000;

            String query = "SELECT MESSAGE_ID,SEND_TIME,REPEAT,ONCE_SEND FROM MESSAGE_DATA";
            @SuppressLint("Recycle") Cursor res = mydb.rawQuery(query,null);

            int id;
            String time = null;
            String repeat = null;
            int once_send;

            SimpleDateFormat fullTimeFormatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            SimpleDateFormat defaultFormatter = new SimpleDateFormat("HH:mm:ss");

            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(Services.this, AlertReceiver.class);

            if (res.moveToFirst()) {
                do {

                    id = res.getInt(res.getColumnIndex("MESSAGE_ID"));
                    time = res.getString(res.getColumnIndex("SEND_TIME"));
                    repeat = res.getString(res.getColumnIndex("REPEAT"));
                    once_send = res.getInt(res.getColumnIndex("ONCE_SEND"));

                    Calendar currentTime = Calendar.getInstance();//Getting current system time

                    //Compare with current time
                    String now = defaultFormatter.format(currentTime.getTime());
                    String savedSendTime = defaultFormatter.format(time);

                    SimpleDateFormat onlyDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                    String todaydate = onlyDateFormatter.format(currentTime.getTime());//get today date for create full date object

                    String completeTime = todaydate +"-"+savedSendTime;//Adding today date string with morning time string

                    Date savedAlertTime = null;


                    try {
                        savedAlertTime = fullTimeFormatter.parse(completeTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    intent.putExtra("id",id);

                        if(repeat.equals("Once")){
                            Log.e("Not passed alarms","Once alarms ");
                            if(once_send == 0){

                                if(now.compareTo(savedSendTime) < 0){
                                    //not passed
                                    PendingIntent pendingIntentOnce = PendingIntent.getBroadcast(Services.this,1, intent, 0);
                                    alarm.setExact(AlarmManager.RTC_WAKEUP,savedAlertTime.getTime(), pendingIntentOnce);
                                    Log.e("Not passed alarms","Once alarm set. id: "+id);

                                }else{

                                    //passed

                                }


                            }

                        }else{

                            Date updatedDate = null;

                            Random rand = new Random();

                            int addMinutes = rand.nextInt(120);
                            assert savedAlertTime != null;
                            long t = savedAlertTime.getTime();
                            updatedDate = new Date(t + (addMinutes * ONE_MINUTE_IN_MILLIS));

                            String updatedTime = defaultFormatter.format(updatedDate);


                            if(now.compareTo(updatedTime) < 0){
                                //not passed
                                PendingIntent pendingIntentEveryDay = PendingIntent.getBroadcast(Services.this,1, intent, 0);
                                alarm.setExact(AlarmManager.RTC_WAKEUP,updatedDate.getTime(), pendingIntentEveryDay);

                                Log.e("Not passed alarms","Everyday alarm set. id: "+id + " added minutes: "+addMinutes+" new time: "+updatedDate.toString());
                            }else{

                                //passed
                            }



                        }




                } while (res.moveToNext());

            }


            res.close();
            mydb.close();


        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(Services.this, "Message Assistant Service started", Toast.LENGTH_LONG).show();
        Log.d("String Key", "Service is started");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(Services.this, "Message Assistant Service destroyed.", Toast.LENGTH_LONG).show();
        Log.d("String Key", "Service destroyed.");

        super.onDestroy();
    }
}
