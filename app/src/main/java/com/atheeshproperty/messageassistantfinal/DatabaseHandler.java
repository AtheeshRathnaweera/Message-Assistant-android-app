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
    public static final String MEDIA = "MEDIA";
    public static final String ONCE_SEND = "ONCE_SEND";

    //History table
    public static final String HISTORY_TABLE_NAME = "HISTORY_TABLE";
    public static final String HISTORY_ID = "HISTORY_ID";
    public static final String HISTORY_MES_ID = "HISTORY_MES_ID";
    public static final String HISTORY_MES_TITLE = "HISTORY_MES_TITLE";
    public static final String HISTORY_MES_NUMBER = "HISTORY_MES_NUMBER";

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
                + MEDIA + " TEXT,"
                + ONCE_SEND + " INTEGER)";

        db.execSQL(CREATE_MESSAGE_TABLE);
        Log.d("Database Helper", "On create executed.Database created.");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE_NAME);
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
