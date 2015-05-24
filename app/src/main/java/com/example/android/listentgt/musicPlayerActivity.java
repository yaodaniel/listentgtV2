package com.example.android.listentgt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class musicPlayerActivity extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity faActivity  = (FragmentActivity) super.getActivity();
        // Replace LinearLayout by the type of the root element of the layout you're trying to load
        RelativeLayout RLayout  = (RelativeLayout)  inflater.inflate(R.layout.player_page, container, false);


        return RLayout; // We must return the loaded Layout
    }

}
