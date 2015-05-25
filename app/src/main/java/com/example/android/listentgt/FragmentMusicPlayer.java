package com.example.android.listentgt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class FragmentMusicPlayer extends Fragment implements View.OnClickListener{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity faActivity  = (FragmentActivity) super.getActivity();
        // Replace LinearLayout by the type of the root element of the layout you're trying to load
        RelativeLayout RLayout  = (RelativeLayout)  inflater.inflate(R.layout.player_page, container, false);
        ImageButton playlistButton = (ImageButton)RLayout.findViewById(R.id.btnPlaylist);
        playlistButton.setOnClickListener(this);
        RLayout.setOnClickListener(this);
        return RLayout; // We must return the loaded Layout
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlaylist:
                getFragmentManager().beginTransaction().remove(this).commit();
                //getFragmentManager().beginTransaction().replace(R.id.playerPage, new FragmentPlayList()).commit();
                break;
            default:
                Log.i("MusicPlayer default:", "default");
                break;
        }
    }
}
