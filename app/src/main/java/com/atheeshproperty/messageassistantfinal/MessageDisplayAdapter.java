package com.atheeshproperty.messageassistantfinal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessageDisplayAdapter extends RecyclerView.Adapter<MessageDisplayAdapter.messageViewHolder> {

    private Context myContext;
    private List<MessageObject> cardData;

    public MessageDisplayAdapter(Context myContext,  List<MessageObject> cardData){
        this.myContext = myContext;
        this.cardData = cardData;

    }


    public static class messageViewHolder extends RecyclerView.ViewHolder {


        public TextView messageTitle;
        public TextView contactNumber;
        private TextView repeatType;
        public TextView sendTime;
        public CardView itemCard;
        private ImageButton notification;
        boolean paused;
        private TextView displayType, displayTime;


        public messageViewHolder(@NonNull View itemView) {
            super(itemView);

            messageTitle = itemView.findViewById(R.id.diplayMessageName);
            contactNumber = itemView.findViewById(R.id.displayContactNumber);
            sendTime = itemView.findViewById(R.id.displaySendTime);
            repeatType = itemView.findViewById(R.id.repeatType);
            itemCard = itemView.findViewById(R.id.messageitemcard);
            notification = itemView.findViewById(R.id.pauseButton);

            displayTime = itemView.findViewById(R.id.displayBirthdayDate);
            displayType = itemView.findViewById(R.id.displayBirthdayMonth);

        }
    }


    @NonNull
    @Override
    public messageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Log.e("MessageDisplayAdapter","Adapter started.");
        LayoutInflater myinflater = LayoutInflater.from(myContext);
        View view = myinflater.inflate(R.layout.message_item_display_layout, viewGroup,  false);
        return new messageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final messageViewHolder messageViewHolder, final int i) {

        Log.e("MessageDisplayAdapter","Bind method started.");


        SimpleDateFormat fullTimeFormatter = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm");

        String repeatText = cardData.get(i).getRepeat();
        Date date = null;
        try {
            date = fullTimeFormatter.parse(cardData.get(i).getSendTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String time = newFormat.format(date);

        Log.e("Date","this is the time : "+time);

        messageViewHolder.messageTitle.setText(cardData.get(i).getTitle());
        messageViewHolder.contactNumber.setText(cardData.get(i).getConatactNumber());

        messageViewHolder.displayType.setText(repeatText);
        messageViewHolder.displayTime.setText(time);

        //Show media type
        String media = cardData.get(i).getMedia();
        if(media.equals("2")){

            messageViewHolder.sendTime.setText("Text message");
        }


        messageViewHolder.itemCard.setOnLongClickListener(new View.OnLongClickListener() {
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

                        Intent in = new Intent(myContext, UpdateAMesage.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                        String idString = Integer.toString(cardData.get(i).getId());
                        in.putExtra("Id",idString);
                        in.putExtra("Title",cardData.get(i).getTitle());
                        in.putExtra("Number",cardData.get(i).getConatactNumber());
                        in.putExtra("mOne",cardData.get(i).getMessageOne());
                        in.putExtra("mTwo",cardData.get(i).getMessageTwo());
                        in.putExtra("mThree",cardData.get(i).getMessageThree());
                        in.putExtra("mFour",cardData.get(i).getMessageFour());
                        in.putExtra("time",cardData.get(i).getSendTime());
                        in.putExtra("repeat",cardData.get(i).getRepeat());
                        in.putExtra("date",cardData.get(i).getSendDate());
                        in.putExtra("media",cardData.get(i).getMedia());

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



        if(!repeatText.equals("Once")){

            messageViewHolder.repeatType.setText("Started from : "+cardData.get(i).getSendDate());

            if(cardData.get(i).getPause() == 0 ){
                Log.e("Pause","Not paused.");
                messageViewHolder.notification.setImageResource(R.drawable.ic_notifications_active_black_24dp);
                messageViewHolder.paused = false;

            }else{
                Log.e("Pause","Paused.");
                messageViewHolder.notification.setImageResource(R.drawable.ic_notifications_off_black_24dp);
                messageViewHolder.paused = true;
            }

            messageViewHolder.notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(messageViewHolder.paused ){


                        messageViewHolder.notification.setImageResource(R.drawable.ic_notifications_active_black_24dp);
                        updateThePause(cardData.get(i).getId(),0);
                        messageViewHolder.paused = false;

                    }else{
                        Log.e("Pause","Not paused.");
                        messageViewHolder.notification.setImageResource(R.drawable.ic_notifications_off_black_24dp);
                        updateThePause(cardData.get(i).getId(),1);
                        messageViewHolder.paused = true;
                    }
                }
            });

        }else{

            if(cardData.get(i).getOnceSend() != 0){
                Log.e("Once","Once message has sent.");
                messageViewHolder.notification.setImageResource(R.drawable.ic_check_circle_black_active);
            }else{
                Log.d("Once","Once message not sent yet.");
                messageViewHolder.notification.setImageResource(R.drawable.ic_check_circle_black_24dp);
            }

        }





    }

    private void deleteAMessage(int id){

        DatabaseHandler handler = new DatabaseHandler(myContext);
        SQLiteDatabase mydb = handler.getWritableDatabase();

        String idString = Integer.toString(id);

        Log.e("Delete Entry","Deletion started. id : "+idString);

        int res = mydb.delete("MESSAGE_DATA", "MESSAGE_ID = ? ", new String[]{idString});

        if(res > 0){
            Log.e("Delete Entry","Deleted successfully.");
        }else{
            Log.e("Delete Entry","Delete failed..");
        }

        mydb.close();

    }

    private void updateThePause(int id, int val){
        DatabaseHandler handler = new DatabaseHandler(myContext);
        SQLiteDatabase mydb = handler.getWritableDatabase();

        String idRes= Integer.toString(id);

        ContentValues contentValues = new ContentValues();

        contentValues.put("PAUSE", val);

        Log.e("Update pause","Updated the entry : "+val);

        int res = mydb.update("MESSAGE_DATA", contentValues, "MESSAGE_ID = ?", new String[]{idRes});
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

    @Override
    public int getItemCount() {
        return cardData.size();
    }
}
