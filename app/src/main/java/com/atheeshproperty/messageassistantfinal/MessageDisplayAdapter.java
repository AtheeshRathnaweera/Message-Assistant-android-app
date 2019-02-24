package com.atheeshproperty.messageassistantfinal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
        public TextView sendTime;
        public CardView itemCard;


        public messageViewHolder(@NonNull View itemView) {
            super(itemView);

            messageTitle = itemView.findViewById(R.id.diplayMessageName);
            contactNumber = itemView.findViewById(R.id.displayContactNumber);
            sendTime = itemView.findViewById(R.id.displaySendTime);
            itemCard = itemView.findViewById(R.id.messageitemcard);

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

        SimpleDateFormat fullTimeFormatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        SimpleDateFormat newFormat = new SimpleDateFormat("HH : mm : ss");

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
        messageViewHolder.sendTime.setText(time);

        messageViewHolder.itemCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final Dialog openDialog = new Dialog(myContext);
                openDialog.setContentView(R.layout.messsage_long_press_dialog);;

                Button delete = openDialog.findViewById(R.id.deleteEntry);
                Button look =  openDialog.findViewById(R.id.viewEntry);

                look.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("View Entry","View button clicked.");
                        openDialog.dismiss();
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("Delete Entry","Deleted button clicked.");

                        deleteAMessage(cardData.get(i).getId());

                        openDialog.dismiss();

                    }
                });

                openDialog.show();
                return true;
            }

        });

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

    }

    @Override
    public int getItemCount() {
        return cardData.size();
    }
}
