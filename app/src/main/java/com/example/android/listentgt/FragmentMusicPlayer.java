package com.example.android.listentgt;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class FragmentMusicPlayer extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{

    SeekBar songProgressBar;
    TextView songCurrentDur;
    TextView songTotalDur;
    private Handler seekHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity faActivity  = (FragmentActivity) super.getActivity();
        // Replace LinearLayout by the type of the root element of the layout you're trying to load
        RelativeLayout RLayout  = (RelativeLayout)  inflater.inflate(R.layout.player_page, container, false);

        ImageButton playlistButton = (ImageButton)RLayout.findViewById(R.id.btnPlaylist);
        songProgressBar = (SeekBar)RLayout.findViewById(R.id.songProgressBar);    //Seeker Bar
        ImageButton repeatButton = (ImageButton)RLayout.findViewById(R.id.btnRepeat);
        ImageButton previousButton= (ImageButton)RLayout.findViewById(R.id.btnPrevious);
        ImageButton playButton = (ImageButton)RLayout.findViewById(R.id.btnPlay);
        ImageButton nextButton = (ImageButton)RLayout.findViewById(R.id.btnNext);
        ImageButton shuffleButton = (ImageButton)RLayout.findViewById(R.id.btnShuffle);
        songCurrentDur = (TextView)RLayout.findViewById(R.id.songCurrentDurationLabel);
        songTotalDur = (TextView)RLayout.findViewById(R.id.songTotalDurationLabel);

        RLayout.setOnClickListener(this);
        playlistButton.setOnClickListener(this);
        songProgressBar.setOnSeekBarChangeListener(this);
        repeatButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        shuffleButton.setOnClickListener(this);

        //RLayout.setOnClickListener(this);
        seekHandler = new Handler();
        seekHandler.post(updateSeekBarTime);
        return RLayout; // We must return the loaded Layout
    }

    private Runnable updateSeekBarTime = new Runnable() {
        @Override
        public void run() {
            //if(MainActivity.musicSrv.isPlaying()) {
            //Log.i("updateSeekBarTime:", "Running");
            int curSeconds = (MainActivity.musicSrv.getPosn() / 1000) % 60;
            long curMinutes = ((MainActivity.musicSrv.getPosn() - curSeconds) / 1000) / 60;
            int totSeconds = (MainActivity.musicSrv.getDur() / 1000) % 60;
            long totMinutes = ((MainActivity.musicSrv.getDur() - totSeconds) / 1000) / 60;
            songCurrentDur.setText(String.format("%d:%02d", curMinutes, curSeconds));
            songTotalDur.setText(String.format("%d:%02d", totMinutes, totSeconds));
            songProgressBar.setMax(MainActivity.musicSrv.getDur());
            songProgressBar.setProgress(MainActivity.musicSrv.getPosn());
            seekHandler.postDelayed(updateSeekBarTime, 1000);
            //}
        }
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlaylist:
                getFragmentManager().beginTransaction().remove(this).commit();
                //getFragmentManager().beginTransaction().replace(R.id.playerPage, new FragmentPlayList()).commit();
                break;
            case R.id.btnRepeat:
                MainActivity.musicSrv.playrepeat();
                break;
            case R.id.btnPlay:
                if(MainActivity.musicSrv.isPlaying())
                    MainActivity.musicSrv.pause();
                else
                    MainActivity.musicSrv.play();
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser) {
            MainActivity.musicSrv.player.seekTo((int)(((double)progress/100)*MainActivity.musicSrv.getDur()));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}

