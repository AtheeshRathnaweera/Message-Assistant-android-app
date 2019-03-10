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

            String query = "SELECT MESSAGE_ID,SEND_TIME,REPEAT,SEND_DATE,ONCE_SEND,PAUSE FROM MESSAGE_DATA";//Retrieve data from message table
            @SuppressLint("Recycle") Cursor res = mydb.rawQuery(query,null);

            int id;
            String time = null;
            String repeat = null;
            String date = null;
            int paused ;
            int once_send;

            SimpleDateFormat fullTimeFormatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            SimpleDateFormat defaultFormatter = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat onlyDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            int requestCode = 0;


            if (res.moveToFirst()) {
                do {

                    id = res.getInt(res.getColumnIndex("MESSAGE_ID"));
                    time = res.getString(res.getColumnIndex("SEND_TIME"));
                    repeat = res.getString(res.getColumnIndex("REPEAT"));
                    date = res.getString(res.getColumnIndex("SEND_DATE"));
                    once_send = res.getInt(res.getColumnIndex("ONCE_SEND"));
                    paused = res.getInt(res.getColumnIndex("PAUSE"));

                    Calendar currentTime = Calendar.getInstance();//Getting current system time

                    //Compare with current time
                    String now = fullTimeFormatter.format(currentTime.getTime());

                    Intent intent = new Intent(Services.this, AlertReceiver.class);
                    intent.putExtra("id",id);

                    requestCode = requestCode +1;

                    if(paused == 0){

                        Log.e("Check paused","Not paused.");

                        if(repeat.equals("Once")){
                            Log.e("once alarms","Once alarms ");

                            String completeTime = date +"-"+time;//Adding date string with time string

                            Date savedCompleteDate = null;

                            try {
                                savedCompleteDate = fullTimeFormatter.parse(completeTime);// complete date object
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if(once_send == 0){

                                if(now.compareTo(completeTime) < 0){
                                    //not passed
                                    PendingIntent pendingIntentOnce = PendingIntent.getBroadcast(Services.this,1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    alarm.setExact(AlarmManager.RTC_WAKEUP,savedCompleteDate.getTime(), pendingIntentOnce);
                                    Log.e("Not passed once alarms","Once alarm set. id: "+id+" time: "+completeTime);

                                }else{

                                    //passed
                                    Log.e("Passed alarms","Once updated.");
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(savedCompleteDate);
                                    c.add(Calendar.DATE,1);

                                    Date newDate = c.getTime();

                                    PendingIntent pendingIntentOnce = PendingIntent.getBroadcast(Services.this,1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    alarm.setExact(AlarmManager.RTC_WAKEUP,newDate.getTime(), pendingIntentOnce);
                                    Log.e("Not passed alarms","Once alarm set. id: "+id+" new date: "+newDate.toString());

                                }


                            }

                        }else{

                            String todayDateString = onlyDateFormatter.format(currentTime.getTime());

                            String fullDateString = todayDateString+"-"+time;
                            Date completeDate = null;

                            try {
                                 completeDate = fullTimeFormatter.parse(fullDateString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Date updatedDate = null;

                            Random rand = new Random();

                            int addMinutes = rand.nextInt(120);
                            assert completeDate != null;
                            long t = completeDate.getTime();
                            updatedDate = new Date(t + (addMinutes * ONE_MINUTE_IN_MILLIS));

                            String updatedTime = defaultFormatter.format(updatedDate);


                            if(now.compareTo(updatedTime) < 0){
                                //not passed
                                PendingIntent pendingIntentEveryDay = PendingIntent.getBroadcast(Services.this,1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarm.setExact(AlarmManager.RTC_WAKEUP,updatedDate.getTime(), pendingIntentEveryDay);

                                Log.e("Not passed alarms","Everyday alarm set. id: "+id + " added minutes: "+addMinutes+" new time: "+updatedDate.toString());
                            }else{

                                //passed

                                Log.e("Passed alarms","Once updated.");
                                Calendar c = Calendar.getInstance();
                                c.setTime(updatedDate);
                                c.add(Calendar.DATE,1);

                                Date newDate = c.getTime();

                                PendingIntent pendingIntentEveryDay = PendingIntent.getBroadcast(Services.this,1, intent,PendingIntent.FLAG_UPDATE_CURRENT );
                                alarm.setExact(AlarmManager.RTC_WAKEUP,newDate.getTime(), pendingIntentEveryDay);

                                Log.e("Passed alarms","Everyday alarm set. id: "+id + " new time: "+newDate.toString());


                            }

                        }

                    }else{

                        Log.e("Check paused","paused. Nothing will happen.");
                        //When found a paused entry
                    }

                } while (res.moveToNext());

            }


            res.close();



            String brithdayDataQuery = "SELECT BIRTHDAY_ID,BIRTHDAY_DATE,BIRTHDAY_SEND_TIME,BIRTHDAY_PAUSE FROM BIRTHDAY_DATA";
            Cursor resBirthday = mydb.rawQuery(brithdayDataQuery,null);
            hugry(resBirthday);


            mydb.close();

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void hugry(Cursor received){
        SimpleDateFormat fullTimeFormatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        int birthdayId, paused;
        String birthdayDate, birthdayTime;

        if (received.moveToFirst()) {
            do{
                birthdayId = received.getInt(received.getColumnIndex("BIRTHDAY_ID"));
                birthdayDate = received.getString(received.getColumnIndex("BIRTHDAY_DATE"));
                birthdayTime = received.getString(received.getColumnIndex("BIRTHDAY_SEND_TIME"));
                paused = received.getInt(received.getColumnIndex("BIRTHDAY_PAUSE"));

                Calendar currentTime = Calendar.getInstance();//Getting current system time

                //Compare with current time
                String now = fullTimeFormatter.format(currentTime.getTime());

                String completeTime = birthdayDate +"-"+birthdayTime;//Adding date and time strings to create full length date string

                Date savedCompleteTime = null;

                try {
                    savedCompleteTime = fullTimeFormatter.parse(completeTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(Services.this, BirthdayAlertReceiver.class);
                intent.putExtra("birthdayID",birthdayId);

                if(paused == 0){

                    if(now.compareTo(completeTime) < 0){
                        Log.e("Birthday notification","Not paused and passed.");

                        PendingIntent pendingIntentOnce = PendingIntent.getBroadcast(Services.this,1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarm.setExact(AlarmManager.RTC_WAKEUP,savedCompleteTime.getTime(), pendingIntentOnce);

                        Log.e("Not passed birthdays","Birthday id: "+birthdayId+" time: "+savedCompleteTime.getTime());

                    }else{
                        Log.e("Birthday notification","Birthday not paused but passed.");
                        Calendar c = Calendar.getInstance();
                        c.setTime(savedCompleteTime);
                        c.add(Calendar.YEAR,1);

                        Date newDate = c.getTime();

                        PendingIntent pendingIntentOnce = PendingIntent.getBroadcast(Services.this,1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarm.setExact(AlarmManager.RTC_WAKEUP,newDate.getTime(), pendingIntentOnce);
                        Log.e("Passed birthdays","Birthday id: "+birthdayId+" new date: "+newDate.toString());

                    }

                }else{
                    Log.e("Birthday notification","Birthday paused. Nothing will happen.");

                }

            }while (received.moveToNext());
        }

        received.close();



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

        Thread thread = new Thread(new TheThread(startId));
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(Services.this, "Message Assistant Service destroyed.", Toast.LENGTH_LONG).show();
        Log.d("String Key", "Service destroyed.");

        super.onDestroy();
    }


}
