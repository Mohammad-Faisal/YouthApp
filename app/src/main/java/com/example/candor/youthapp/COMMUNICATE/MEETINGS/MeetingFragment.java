package com.example.candor.youthapp.COMMUNICATE.MEETINGS;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.candor.youthapp.R;


public class MeetingFragment extends Fragment {


    public MeetingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_meeting, container, false);





        return view;
    }

}
