package com.atheeshproperty.messageassistantfinal;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MessageAssistant.db";

    //Main data table
    public static final String MESSAGE_TABLE_NAME = "MESSAGE_DATA";
    public static final String MESSAGE_ID = "MESSAGE_ID";
    public static final String TITLE = "TITLE";
    public static final String CONTACT_NUMBER = "CONTACT_NUMBER";
    public static final String CONTENT_ONE = "CONTENT_ONE";
    public static final String CONTENT_TWO = "CONTENT_TWO";
    public static final String CONTENT_THREE = "CONTENT_THREE";
    public static final String CONTENT_FOUR = "CONTENT_FOUR";
    public static final String SEND_TIME = "SEND_TIME";
    public static final String REPEAT = "REPEAT";
    public static final String SEND_DATE = "SEND_DATE";
    public static final String MEDIA = "MEDIA";
    public static final String ONCE_SEND = "ONCE_SEND";
    public static final String PAUSE = "PAUSE";

    //Birthday data table
    public static final String BIRTHDAY_TABLE_NAME = "BIRTHDAY_DATA";
    public static final String BIRTHDAY_ID = "BIRTHDAY_ID";
    public static final String BIRTHDAY_TITLE = "BIRTHDAY_TITLE";
    public static final String BIRTHDAY_DATE = "BIRTHDAY_DATE";
    public static final String BIRTHDAY_CONTACT_NUMBER = "BIRTHDAY_CONTACT_NUMBER";
    public static final String BIRTHDAY_CONTENT= "BIRTHDAY_CONTENT";
    public static final String BIRTHDAY_SEND_TIME = "BIRTHDAY_SEND_TIME";
    public static final String BIRTHDAY_MEDIA = "BIRTHDAY_MEDIA";
    public static final String  BIRTHDAY_AUTO_TEXT = "BIRTHDAY_AUTO";
    public static final String BIRTHDAY_PAUSE = "BIRTHDAY_PAUSE";

    //History table
    public static final String HISTORY_TABLE_NAME = "HISTORY_TABLE";
    public static final String HISTORY_ID = "HISTORY_ID";
    public static final String HISTORY_MES_TYPE = "HISTORY_MES_TYPE";
    public static final String HISTORY_MES_ID = "HISTORY_MES_ID";
    public static final String HISTORY_MES_TITLE = "HISTORY_MES_TITLE";
    public static final String HISTORY_MES_NUMBER = "HISTORY_MES_NUMBER";
    public static final String HISTORY_MES_CONTENT = "HISTORY_MES_CONTENT";
    public static final String HISTORY_MES_SENTTIME = "HISTORY_SENT_TIME";
    public static final String HISTORY_MES_STATUS = "HISTORY_STATUS";

    public DatabaseHandler(Context context) {

        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_MESSAGE_TABLE = " CREATE TABLE " + MESSAGE_TABLE_NAME + "(" +
                MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TITLE + " TEXT,"
                + CONTACT_NUMBER + " TEXT,"
                + CONTENT_ONE + " TEXT,"
                + CONTENT_TWO + " TEXT,"
                + CONTENT_THREE + " TEXT,"
                + CONTENT_FOUR + " TEXT,"
                + SEND_TIME + " TEXT,"
                + REPEAT + " TEXT,"
                + SEND_DATE + " TEXT,"
                + MEDIA + " TEXT,"
                + ONCE_SEND + " INTEGER,"
                + PAUSE + " INTEGER)";

        db.execSQL(CREATE_MESSAGE_TABLE);
        Log.d("Database Helper", "On create executed.Database created.");

        String CREATE_BIRTHDAY_TABLE = " CREATE TABLE " + BIRTHDAY_TABLE_NAME + "(" +
                BIRTHDAY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BIRTHDAY_TITLE + " TEXT,"
                + BIRTHDAY_DATE + " TEXT,"
                + BIRTHDAY_CONTACT_NUMBER + " TEXT,"
                + BIRTHDAY_CONTENT + " TEXT,"
                + BIRTHDAY_SEND_TIME + " TEXT,"
                + BIRTHDAY_MEDIA + " TEXT,"
                + BIRTHDAY_AUTO_TEXT + " TEXT,"
                + BIRTHDAY_PAUSE + " INTEGER)";

        db.execSQL(CREATE_BIRTHDAY_TABLE);
        Log.d("Database Helper", "On create executed.Birthday table created.");

        String CREATE_HISTORY_TABLE = " CREATE TABLE " + HISTORY_TABLE_NAME + "(" +
                HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HISTORY_MES_ID + " TEXT,"
                + HISTORY_MES_TITLE + " TEXT,"
                + HISTORY_MES_TYPE + " TEXT,"
                + HISTORY_MES_NUMBER + " TEXT,"
                + HISTORY_MES_CONTENT + " TEXT,"
                + HISTORY_MES_SENTTIME + " TEXT,"
                + HISTORY_MES_STATUS + " TEXT)";

        db.execSQL(CREATE_HISTORY_TABLE);
        Log.d("Database Helper", "On create executed.History table created.");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + BIRTHDAY_TABLE_NAME);
        onCreate(db);
    }

    public void checkExistence() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table'", null);
        ArrayList<String[]> result = new ArrayList<String[]>();
        int i = 0;
        result.add(c.getColumnNames());
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String[] temp = new String[c.getColumnCount()];
            for (i = 0; i < temp.length; i++) {
                temp[i] = c.getString(i);
                Log.e("Database data", "Table name: " + temp[i]);

                Cursor c1 = db.rawQuery(
                        "SELECT * FROM " + temp[i], null);
                c1.moveToFirst();
                String[] COLUMNS = c1.getColumnNames();
                for (int j = 0; j < COLUMNS.length; j++) {
                    c1.move(j);
                    System.out.println("    COLUMN - " + COLUMNS[j]);
                    Log.e("Column data", "Column names: " + COLUMNS[j]);
                }
            }
            result.add(temp);
        }

        c.close();
        db.close();
    }
}
