package com.example.android.listentgt;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class FragmentMusicPlayer extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{

    private SeekBar songProgressBar;
    private TextView songCurrentDur;
    private TextView songTotalDur;
    private ImageButton repeatButton;
    private ImageButton shuffleButton;
    private Handler seekHandler;
    private ImageButton playButton;
    private TextView songTitle;
    private boolean repeatToggle = false;
    private boolean shuffleToggle = false;
    private boolean playToggle = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity faActivity  = (FragmentActivity) super.getActivity();
        // Replace LinearLayout by the type of the root element of the layout you're trying to load
        RelativeLayout RLayout  = (RelativeLayout)  inflater.inflate(R.layout.player_page, container, false);

        songTitle = (TextView)RLayout.findViewById(R.id.songTitle);
        ImageButton playlistButton = (ImageButton)RLayout.findViewById(R.id.btnPlaylist);
        songProgressBar = (SeekBar)RLayout.findViewById(R.id.songProgressBar);    //Seeker Bar
        repeatButton = (ImageButton)RLayout.findViewById(R.id.btnRepeat);
        ImageButton previousButton= (ImageButton)RLayout.findViewById(R.id.btnPrevious);
        playButton = (ImageButton)RLayout.findViewById(R.id.btnPlay);
        playButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.img_btn_pause, null));
        ImageButton nextButton = (ImageButton)RLayout.findViewById(R.id.btnNext);
        shuffleButton = (ImageButton)RLayout.findViewById(R.id.btnShuffle);
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
            if(MainActivity.musicSrv.player != null && MainActivity.musicSrv.getPosn() < MainActivity.musicSrv.getDur()) {
                if (songProgressBar.getMax() != MainActivity.musicSrv.getDur())
                    songProgressBar.setMax(MainActivity.musicSrv.getDur());
                songProgressBar.setProgress(MainActivity.musicSrv.getPosn());
                songTitle.setText(MainActivity.musicSrv.getSong().getTitle() + " - " + MainActivity.musicSrv.getSong().getArtists());
                int curSeconds = (MainActivity.musicSrv.getPosn() / 1000) % 60;
                long curMinutes = ((MainActivity.musicSrv.getPosn() - curSeconds) / 1000) / 60;
                int totSeconds = (MainActivity.musicSrv.getDur() / 1000) % 60;
                long totMinutes = ((MainActivity.musicSrv.getDur() - totSeconds) / 1000) / 60;
                songCurrentDur.setText(String.format("%d:%02d", curMinutes, curSeconds));
                songTotalDur.setText(String.format("%d:%02d", totMinutes, totSeconds));
            }
                seekHandler.postDelayed(updateSeekBarTime, 500);
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
                if(repeatToggle)
                    repeatButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.img_btn_repeat, null));
                else
                    repeatButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.img_btn_repeat_pressed, null));
                repeatToggle = !repeatToggle;
                break;
            case R.id.btnPlay:
                if(playToggle)
                    playButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.img_btn_pause, null));
                else
                    playButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.img_btn_play, null));
                playToggle = !playToggle;
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
                if(shuffleToggle)
                    shuffleButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.img_btn_shuffle, null));
                else
                    shuffleButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.img_btn_shuffle_pressed, null));
                shuffleToggle = !shuffleToggle;
                break;
            default:
                Log.i("MusicPlayer default:", "default");
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        MainActivity.musicSrv.player.seekTo(seekBar.getProgress());
    }
}

