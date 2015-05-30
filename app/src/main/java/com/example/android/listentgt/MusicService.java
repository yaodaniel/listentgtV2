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
/**
 * Created by DanielY on 5/24/2015.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{

    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<song> songs;
    //current position
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();
    private boolean newSong = true;

    public void onCreate() {
        //create the service
        super.onCreate();
        //initialize position
        songPosn=0;
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

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
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
            //updateSeekBar.start();
        }
        else {
            player.start();
            // updateSeekBar.start();
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
        int min = 0;
        int max = songs.size() - 1;
        Random random = new Random();
        songPosn = random.nextInt(max - min + 1) + min;
        playSong();

    }

    public void playrepeat() {
        playSong();

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

    public void setSong(int songIndex){
        songPosn=songIndex;
    }
}
