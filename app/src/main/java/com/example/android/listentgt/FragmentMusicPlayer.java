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
        ImageButton playButton = (ImageButton)RLayout.findViewById(R.id.btnPlay);
        ImageButton nextButton = (ImageButton)RLayout.findViewById(R.id.btnNext);
        ImageButton shuffleButton = (ImageButton)RLayout.findViewById(R.id.btnShuffle);
        ImageButton previousButton= (ImageButton)RLayout.findViewById(R.id.btnPrevious);
        //SeekBar songProgressBar=(SeekBar)RLayout.findViewById(R.id.songProgressBar);    //Seeker Bar
        playlistButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        shuffleButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);

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

            case R.id.btnPlay:

                if(MainActivity.musicSrv.isPlaying())
                    MainActivity.musicSrv.pause();
                else
                    MainActivity.musicSrv.play();
                break;
            case R.id.btnRepeat:
                MainActivity.musicSrv.playrepeat();
                break;

            case R.id.btnPrevious:
                MainActivity.musicSrv.playprev();
                break;

            case R.id.btnNext:
                MainActivity.musicSrv.playnext();
                break;


            case R.id.btnShuffle:
                MainActivity.musicSrv.shuffle();
                break;

            default:
                Log.i("MusicPlayer default:", "default");
                break;
        }
    }
}
