package com.example.android.listentgt;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import java.util.Random;
import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
/**
 * Created by DanielY on 5/24/2015.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{

    //media player
    public MediaPlayer player;
    //song list
    private ArrayList<song> songs;
    //current position
    private int songPosn, duration;
    private final IBinder musicBind = new MusicBinder();
    private boolean newSong = true, shuffleOn = false, repeatOn = false;

    public void onCreate() {
        //create the service
        super.onCreate();
        //initialize position
        songPosn=-1;
        //create player
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer() {
        //set player properties
        player.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<song> theSongs) {
        songs=theSongs;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(repeatOn) {
            play();
        } else if(shuffleOn) {
            int min = 0;
            int max = songs.size() - 1;
            Random random = new Random();
            songPosn = random.nextInt(max - min + 1) + min;
            playSong();
        }
        else {
            playnext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        player.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
        duration = mp.getDuration();
    }
    public boolean isPlaying() {
        return player.isPlaying();
    }
    public void pause() {
        player.pause();
    }

    public void play() {
        if (newSong) {
            playSong();
        }
        else {
            player.start();
        }
    }

    public void playnext() {                //play the next song
        if (songPosn != songs.size() - 1) {
            songPosn++;
            playSong();
        }
    }

    public void playprev() {                //play previous song
        if (songPosn != 0) {
            songPosn--;
            playSong();
        }
    }

    public void shuffle()                 //play random song
    {
        shuffleOn = !shuffleOn;
    }

    public void playrepeat() {
        repeatOn = !repeatOn;
    }

    public void playSong(){
        //play a song
        newSong = false;
        player.reset();
        //get song
        song playSong = songs.get(songPosn);
        //get id
        long currSong = playSong.getID();

        //set uri
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    public void setSong(int songIndex) {
        if (songIndex == songPosn)
            return;
        songPosn = songIndex;
        newSong = true;
        playSong();
    }

    public int getPosn() {
        if(player != null)
            return player.getCurrentPosition();
        return 0;
    }

    public int getDur() {
        /*player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mp) {
                int duration = player.getDuration();
                return duration;

            }
        });
        try {
            player.prepare();
        } catch (Exception e) {

        }*/
        return duration;
    }

    public song getSong() {
        return songs.get(songPosn);
    }
}
