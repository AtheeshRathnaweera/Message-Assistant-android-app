package com.atheeshproperty.messageassistantfinal;

import android.content.Context;
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
 * {@link fragment_birthdays.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fragment_birthdays#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_birthdays extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;

    private SQLiteDatabase mydb;

    private Context context;

    Handler mainHandler = new Handler();

    private OnFragmentInteractionListener mListener;

    public fragment_birthdays() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_birthdays.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_birthdays newInstance(String param1, String param2) {
        fragment_birthdays fragment = new fragment_birthdays();
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
        View view = inflater.inflate(R.layout.fragment_fragment_birthdays, container, false);

        context = view.getContext();
        recyclerView = view.findViewById(R.id.HomeBirthdayRecyclerView);

        LinearLayoutManager layout = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layout);

        if(recyclerView == null){
            Log.e("OnCreateView","Recycler view null");
        }else{
            Log.e("OnCreateView","Recycler view found.");
            fragment_birthdays.populateRecyclerView runnable = new fragment_birthdays.populateRecyclerView();
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
            List<BirthdayMessageObject> birthdayItems = new ArrayList<>();

            String allData = " SELECT * FROM BIRTHDAY_DATA ";
            Cursor c = mydb.rawQuery(allData, null);

            if (c.moveToFirst()) {
                do {
                    BirthdayMessageObject birthday = new BirthdayMessageObject();
                    birthday.setId(Integer.parseInt(c.getString(c.getColumnIndex("BIRTHDAY_ID"))));
                    birthday.setName(c.getString(c.getColumnIndex("BIRTHDAY_TITLE")));
                    birthday.setBirthdate(c.getString(c.getColumnIndex("BIRTHDAY_DATE")));
                    birthday.setContactNumber(c.getString(c.getColumnIndex("BIRTHDAY_CONTACT_NUMBER")));
                   birthday.setMessage(c.getString(c.getColumnIndex("BIRTHDAY_CONTENT")));
                    birthday.setSendTime(c.getString(c.getColumnIndex("BIRTHDAY_SEND_TIME")));
                    birthday.setMedia(c.getString(c.getColumnIndex("BIRTHDAY_MEDIA")));
                    birthday.setRepeat(c.getString(c.getColumnIndex("BIRTHDAY_REPEAT")));
                    birthday.setPause(Integer.parseInt(c.getString(c.getColumnIndex("BIRTHDAY_PAUSE"))));

                    birthdayItems.add(birthday);
                } while (c.moveToNext());

            }

            c.close();

            Log.e("received", "received number of data : " + birthdayItems.size());


            final BirthdayMessageDisplayAdapter myAdpater = new BirthdayMessageDisplayAdapter(context, birthdayItems);

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("Handler","Handler started ");
                    recyclerView.setAdapter(myAdpater);
                    myAdpater.notifyDataSetChanged();
                }
            });

        }
    }
}
