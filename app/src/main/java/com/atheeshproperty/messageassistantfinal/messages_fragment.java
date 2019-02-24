package com.atheeshproperty.messageassistantfinal;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link messages_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link messages_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class messages_fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;

    private OnFragmentInteractionListener mListener;
    private SQLiteDatabase mydb;

    private Context context;

    Handler mainHandler = new Handler();

    public messages_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment messages_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static messages_fragment newInstance(String param1, String param2) {
        messages_fragment fragment = new messages_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        DatabaseHandler DatabaseHelper = new DatabaseHandler(getContext());
        mydb = DatabaseHelper.getReadableDatabase();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages_fragment, container, false);

        context = view.getContext();
        recyclerView = view.findViewById(R.id.HomeMessageRecyclerView);

        LinearLayoutManager layout = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layout);

        if(recyclerView == null){
            Log.e("OnCreateView","Recycler view null");
        }else{
            Log.e("OnCreateView","Recycler view found.");
            populateRecyclerView runnable = new populateRecyclerView();
            new Thread(runnable).start();

        }

        if(context == null){
            Log.e("OnCreateView","context null");

        }else{
            Log.e("OnCreateView","context not null"+context.toString());
        }



        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class populateRecyclerView implements Runnable{

        populateRecyclerView(){


        }

        @Override
        public void run() {

            Log.e("background status", "Populate home recycler background started.");
            List<MessageObject> messageItems = new ArrayList<>();

            String allData = " SELECT * FROM MESSAGE_DATA ";
            Cursor c = mydb.rawQuery(allData, null);

            if (c.moveToFirst()) {
                do {
                    MessageObject message = new MessageObject();
                    message.setId(Integer.parseInt(c.getString(c.getColumnIndex("MESSAGE_ID"))));
                    message.setTitle(c.getString(c.getColumnIndex("TITLE")));
                    message.setConatactNumber(c.getString(c.getColumnIndex("CONTACT_NUMBER")));
                    message.setMessageOne(c.getString(c.getColumnIndex("CONTENT_ONE")));
                    message.setMessageTwo(c.getString(c.getColumnIndex("CONTENT_TWO")));
                    message.setMessageThree(c.getString(c.getColumnIndex("CONTENT_THREE")));
                    message.setMessageFour(c.getString(c.getColumnIndex("CONTENT_FOUR")));
                    message.setSendTime(c.getString(c.getColumnIndex("SEND_TIME")));
                    message.setRepeat(c.getString(c.getColumnIndex("REPEAT")));
                    message.setMedia(c.getString(c.getColumnIndex("MEDIA")));

                    messageItems.add(message);
                } while (c.moveToNext());

            }

            c.close();

            Log.e("received", "received number of data : " + messageItems.size());

            final MessageDisplayAdapter myAdapter = new MessageDisplayAdapter(context, messageItems);

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("Handler","Handler started ");
                    recyclerView.setAdapter(myAdapter);
                    myAdapter.notifyDataSetChanged();
                }
            });

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("Message Fragment","OnResume executed.");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("Message Fragment","OnPause executed.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Message Fragment","OnDestroy executed.");
    }
}
