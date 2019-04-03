package com.atheeshproperty.messageassistantfinal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class History_Display_Adapter extends RecyclerView.Adapter<History_Display_Adapter.HistoryViewHolder> {

    private Context myContext;
    private List<HistoryObject> cardData;
    private String lastDate = null;
    private Date todayDate;

    public History_Display_Adapter(Context context, List<HistoryObject> receivedObjects){
        myContext = context;
        cardData = receivedObjects;
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        public TextView date;
        public TextView type;
        public TextView title;
        public TextView contactNumber;
        public CardView historyItem;
        public TextView time;
        public TextView status;


        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            historyItem = itemView.findViewById(R.id.historyitemcard);
            date = itemView.findViewById(R.id.historyDate);
            type = itemView.findViewById(R.id.historytype);
            title = itemView.findViewById(R.id.historytitle);
            contactNumber = itemView.findViewById(R.id.historycontactNumber);
            time = itemView.findViewById(R.id.sentTime);
            status = itemView.findViewById(R.id.status);

        }
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.e("History_Display_Adapter","History Adapter started.");
        LayoutInflater myinflater = LayoutInflater.from(myContext);
        View view = myinflater.inflate(R.layout.history_item_display, viewGroup,  false);
        todayDate = getTodayDate();//Get today date and store it as todayDate
        return new History_Display_Adapter.HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder historyViewHolder, int i) {


        if(lastDate == null){
            lastDate = getTimePart(cardData.get(i).getSendTime(),2);
            historyViewHolder.date.setVisibility(View.VISIBLE);

            if(checkWhetherToday(lastDate)){
                historyViewHolder.date.setText("Today");
            }else{
                historyViewHolder.date.setText(lastDate);
            }



        }else{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String recentDate = getTimePart(cardData.get(i).getSendTime(),2);
            Date lastStored = null;
            Date recent = null;

            try {
                lastStored = formatter.parse(lastDate);
                recent = formatter.parse(recentDate);

                if(recent.after(lastStored)){
                    historyViewHolder.date.setVisibility(View.VISIBLE);

                    if(checkWhetherToday(recentDate)){
                        historyViewHolder.date.setText("Today");
                    }else{
                        historyViewHolder.date.setText(recentDate);
                    }
                    lastDate = recentDate;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        historyViewHolder.title.setText(cardData.get(i).getMesTitle());
        historyViewHolder.type.setText(cardData.get(i).getMesType());
        historyViewHolder.contactNumber.setText(cardData.get(i).getConatactNumber());
        historyViewHolder.time.setText(getTimePart(cardData.get(i).getSendTime(),1));

        historyViewHolder.date.setText(getTimePart(cardData.get(i).getSendTime(),2));

        if((cardData.get(i).getStatus()).equals("Sent successfully!")){//Thitha
            historyViewHolder.status.setText("Sent");
            Log.e("Received status",cardData.get(i).getStatus());

        }else{
            historyViewHolder.status.setText("Failed");
            historyViewHolder.status.setTextColor((Color.parseColor("#FF0000")));
            Log.e("Received status",cardData.get(i).getStatus());
        }

        historyViewHolder.historyItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });


    }

    private Date getTodayDate(){//Get today date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

       String to = formatter.format(c.getTime());
       Date today = null;

        try {
            today =  formatter.parse(to);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return today;

    }

    private boolean checkWhetherToday(String date){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date received = null;
        Boolean result = false;

        try {
           received = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(received != null & todayDate != null){

            if(received.compareTo(todayDate) == 0){// 0 when equals
                result = true;
            }
        }

        return result;

    }



    private String getTimePart(String fullDateAndTime, int requestType){
        Date receivedFull = null;
        String returnText;

        SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");



        try {
            receivedFull = fullFormat.parse(fullDateAndTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(requestType == 1){

            SimpleDateFormat defaultFormatter = new SimpleDateFormat("hh : mm");
            SimpleDateFormat onlyHour = new SimpleDateFormat("HH");

            returnText = defaultFormatter.format(receivedFull);

            String receivedHour = onlyHour.format(receivedFull);

            int hour = Integer.parseInt(receivedHour);

            if(hour < 12){
                return  returnText+" "+"AM";

            }else{
                return  returnText+" "+"PM";

            }



        }else{
            SimpleDateFormat onlyDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = onlyDateFormatter.format(receivedFull);
            return formattedDate;
        }

       // SimpleDateFormat onlyDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    }

    @Override
    public int getItemCount() {
        return cardData.size();
    }
}
