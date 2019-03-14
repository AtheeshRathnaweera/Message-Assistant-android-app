package com.atheeshproperty.messageassistantfinal;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DisplayHistory extends AppCompatActivity {

    public RecyclerView historyRecycler;
    public TextView emptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);

        historyRecycler = findViewById(R.id.HistoryRecyclerView);
        emptyView = findViewById(R.id.empty_view);

        populateProcess p = new populateProcess();
        p.execute();


    }

    private class populateProcess extends AsyncTask< Void, Void, Boolean> {

        DatabaseHandler access;
        SQLiteDatabase myDB;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("History Asynch Status","Asynch task preExecute started.");

            access = new DatabaseHandler(DisplayHistory.this);
            myDB = access.getReadableDatabase();

            //recyclerView.setHasFixedSize(true);
            historyRecycler.setLayoutManager(new LinearLayoutManager(DisplayHistory.this));
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.d("Background status","Do in background started.");

            ArrayList<HistoryObject> messageItems = new ArrayList<>();

            String allData = " SELECT * FROM HISTORY_TABLE ";
            Cursor c = myDB.rawQuery(allData, null);

            if (c.moveToFirst()) {
                do {
                    HistoryObject entry = new HistoryObject();
                    entry.setHistoryID(Integer.parseInt(c.getString(c.getColumnIndex("HISTORY_ID"))));
                    entry.setMesID(c.getString(c.getColumnIndex("HISTORY_MES_ID")));
                    entry.setMesTitle(c.getString(c.getColumnIndex("HISTORY_MES_TITLE")));
                    entry.setMesType(c.getString(c.getColumnIndex("HISTORY_MES_TYPE")));
                    entry.setConatactNumber(c.getString(c.getColumnIndex("HISTORY_MES_NUMBER")));
                    entry.setHistoryContent(c.getString(c.getColumnIndex("HISTORY_MES_CONTENT")));
                    entry.setSendTime(c.getString(c.getColumnIndex("HISTORY_SENT_TIME")));
                    entry.setStatus(c.getString(c.getColumnIndex("HISTORY_STATUS")));

                    messageItems.add(entry);
                } while (c.moveToNext());

            }

            c.close();
            myDB.close();

           History_Display_Adapter myAdapter = new History_Display_Adapter(DisplayHistory.this,messageItems);
            historyRecycler.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();

            if(!messageItems.isEmpty()){
                Log.d("Display history","Received data in background.");
                return true;
            }else{
                return  false;
            }


        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.d("Display history","Data received to main thread.");
            super.onPostExecute(aBoolean);

            if(!aBoolean){
                emptyView.setVisibility(View.VISIBLE);
            }else{
                emptyView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
