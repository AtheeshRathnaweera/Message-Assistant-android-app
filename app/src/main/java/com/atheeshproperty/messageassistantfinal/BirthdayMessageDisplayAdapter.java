package com.atheeshproperty.messageassistantfinal;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BirthdayMessageDisplayAdapter extends RecyclerView.Adapter<BirthdayMessageDisplayAdapter.BirthdayMessageViewHolder>{

    private Context myContext;
    private List<BirthdayMessageObject> cardData;

    public BirthdayMessageDisplayAdapter(Context myContext,  List<BirthdayMessageObject> cardData){
        this.myContext = myContext;
        this.cardData = cardData;

    }

    public static class BirthdayMessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageTitle;
        public TextView contactNumber;
        public TextView sendTime;
        public CardView itemCard;
        public TextView monthDisplay, dateDisplay;
        public TextView autoText;
        private ImageButton notification;
        boolean paused;


        public BirthdayMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            messageTitle = itemView.findViewById(R.id.diplayMessageName);
            contactNumber = itemView.findViewById(R.id.displayContactNumber);
            sendTime = itemView.findViewById(R.id.displaySendTime);
            itemCard = itemView.findViewById(R.id.messageitemcard);
            notification = itemView.findViewById(R.id.pauseButton);

            autoText = itemView.findViewById(R.id.autoText);

            monthDisplay = itemView.findViewById(R.id.displayBirthdayMonth);
            dateDisplay = itemView.findViewById(R.id.displayBirthdayDate);

        }
    }

    @NonNull
    @Override
    public BirthdayMessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,int i) {
        Log.e("MessageDisplayAdapter","Adapter started.");
        LayoutInflater myinflater = LayoutInflater.from(myContext);
        View view = myinflater.inflate(R.layout.birthday_item_display_layout, viewGroup,  false);
        return new BirthdayMessageDisplayAdapter.BirthdayMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BirthdayMessageViewHolder birthdayMessageViewHolder, final int i) {
        Log.e("BirthdayMessageDisplay","Bind method started.");

        SimpleDateFormat fullTimeFormatter = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat newFormat = new SimpleDateFormat("HH : mm : ss");
        SimpleDateFormat fullDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat newUpdateFormat = new SimpleDateFormat("MMMM");
        SimpleDateFormat newDateFormat = new SimpleDateFormat("dd");

        Date date = null;
        Date Bdate = null;
        try {
            date = fullTimeFormatter.parse(cardData.get(i).getSendTime());
            Bdate = fullDateFormatter.parse(cardData.get(i).getBirthdate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String time = newFormat.format(date);
        String birthMonth = newUpdateFormat.format(Bdate);
        String birthDate = newDateFormat.format(Bdate);

        Log.e("Date","this is the time : "+time);

        birthdayMessageViewHolder.messageTitle.setText(cardData.get(i).getName());
        birthdayMessageViewHolder.contactNumber.setText(cardData.get(i).getContactNumber());
       // birthdayMessageViewHolder.sendTime.setVisibility(View.INVISIBLE);
        birthdayMessageViewHolder.sendTime.setText(cardData.get(i).getSendTime());
        birthdayMessageViewHolder.autoText.setText(cardData.get(i).getAutoText());

        birthdayMessageViewHolder.monthDisplay.setText(birthMonth);
        birthdayMessageViewHolder.dateDisplay.setText(birthDate);

        if(cardData.get(i).getPause() == 0 ){
            Log.e("Pause","Not paused.");
            birthdayMessageViewHolder.notification.setImageResource(R.drawable.ic_notifications_active_black_24dp);
            birthdayMessageViewHolder.paused = false;

        }else{
            birthdayMessageViewHolder.notification.setImageResource(R.drawable.ic_notifications_off_black_24dp);
            birthdayMessageViewHolder.paused = true;
        }

        birthdayMessageViewHolder.notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(birthdayMessageViewHolder.paused ){

                    birthdayMessageViewHolder.notification.setImageResource(R.drawable.ic_notifications_active_black_24dp);
                    updateThePause(cardData.get(i).getId(),0);
                    birthdayMessageViewHolder.paused = false;

                }else{
                    Log.e("Pause","Not paused.");
                    birthdayMessageViewHolder.notification.setImageResource(R.drawable.ic_notifications_off_black_24dp);
                    updateThePause(cardData.get(i).getId(),1);
                    birthdayMessageViewHolder.paused = true;
                }
            }
        });

        birthdayMessageViewHolder.itemCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final Dialog openDialog = new Dialog(myContext);
                openDialog.setContentView(R.layout.messsage_long_press_dialog);

                Button delete = openDialog.findViewById(R.id.deleteEntry);
                Button look =  openDialog.findViewById(R.id.viewEntry);

                look.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("View Entry","View button clicked.");

                        Intent in = new Intent(myContext, UpdateABirthday.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                        String idString = Integer.toString(cardData.get(i).getId());
                        in.putExtra("Id",idString);
                        in.putExtra("Name",cardData.get(i).getName());
                        in.putExtra("Number",cardData.get(i).getContactNumber());
                        in.putExtra("Bdate",cardData.get(i).getBirthdate());
                        in.putExtra("Message",cardData.get(i).getMessage());
                        in.putExtra("time",cardData.get(i).getSendTime());
                        in.putExtra("media",cardData.get(i).getMedia());
                        in.putExtra("auto",cardData.get(i).getAutoText());

                        myContext.startActivity(in);

                        openDialog.dismiss();
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("Delete Entry","Deleted button clicked.");

                        deleteAMessage(cardData.get(i).getId());

                        Intent intent = new Intent(myContext, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        myContext.startActivity(intent);
                        openDialog.dismiss();

                    }
                });

                openDialog.show();
                return true;
            }

        });

    }

    private void updateThePause(int id, int val){
        DatabaseHandler handler = new DatabaseHandler(myContext);
        SQLiteDatabase mydb = handler.getWritableDatabase();

        String idRes= Integer.toString(id);

        ContentValues contentValues = new ContentValues();

        contentValues.put("BIRTHDAY_PAUSE", val);

        Log.e("Update pause","Updated the entry : "+val);

        int res = mydb.update("BIRTHDAY_DATA", contentValues, "BIRTHDAY_ID = ?", new String[]{idRes});
        mydb.close();

        if(res > 0){
            Log.e("pause update","Successful.");

            Intent intent = new Intent(myContext, Services.class);
            myContext.startService(intent);

            Log.e("Service refreshed","Successful.");

        }else{
            Log.e("pause update","Error.");
        }

    }
    private void deleteAMessage(int id){

        DatabaseHandler handler = new DatabaseHandler(myContext);
        SQLiteDatabase mydb = handler.getWritableDatabase();

        String idString = Integer.toString(id);

        Log.e("Delete Entry","Deletion started. id : "+idString);

        int res = mydb.delete("BIRTHDAY_DATA", "BIRTHDAY_ID = ? ", new String[]{idString});

        if(res > 0){
            Log.e("Delete Entry","Deleted successfully.");
        }else{
            Log.e("Delete Entry","Delete failed..");
        }

        mydb.close();

    }

    @Override
    public int getItemCount() {
        return cardData.size();
    }

}
