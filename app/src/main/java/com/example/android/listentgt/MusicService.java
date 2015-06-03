package com.example.android.listentgt;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
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
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();
    private boolean newSong = true, shuffleOn = false, repeatOn = false;



    //notice that player is created in the service
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

    //if you are a client, you just pause your own player
    //but if you are a server, you pause the client's player too
    public void pause() {

        //other cases, just pause (if it is just a client)
        player.pause();

        //if it is a server than it broadcast the stop command
        if (((DeviceListFragment) MainActivity.getFragment2()).getIsServer() && !((DeviceListFragment) MainActivity.getFragment2()).getIsClient())
        {
            Log.i("Music Service", "Server in music service trying to stop music");
            DeviceListFragment fragment = (DeviceListFragment) MainActivity.getFragment2();
            fragment.stopMusicOnClients();
        }



    }

    //if the player is a server, should also handle the client player control
    public void play() {
        Log.i("Play", "Playcalled");
        if (newSong) {
            Log.i("Play", "Play a new song");
            playSong();
        }
        else {
            Log.i("Play", "Continue Playing");
            player.start();
            //if it is a server
            if (((DeviceListFragment) MainActivity.getFragment2()).getIsServer() && !((DeviceListFragment) MainActivity.getFragment2()).getIsClient())
            {
                //continue playing on client too
                DeviceListFragment fragment = (DeviceListFragment) MainActivity.getFragment2();
                fragment.continueMusicOnClients();
            }
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




        //not a server and not a client
        if(!((DeviceListFragment) MainActivity.getFragment2()).getIsServer() && !((DeviceListFragment) MainActivity.getFragment2()).getIsClient()) {

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
        //if it is a server than it broadcast
        else if (((DeviceListFragment) MainActivity.getFragment2()).getIsServer() && !((DeviceListFragment) MainActivity.getFragment2()).getIsClient())
        {
            Log.i("Music Service", "Server in music service trying to broadcast music");

            //get song
            song playSong = songs.get(songPosn);
            //get id
            long currSong = playSong.getID();

            //set uri
            Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);

            try {
                player.setDataSource(getApplicationContext(), trackUri);
                player.prepare();
            } catch (Exception e) {
                Log.e("MUSIC SERVICE", "Error setting data source or preparing", e);
            }

            DeviceListFragment fragment = (DeviceListFragment) MainActivity.getFragment2();

                //not sure this works or not

            //getRealPath so we can copy the file to the server
            String filePath = fragment.getFileFromURI(trackUri);
            File audioFile = new File(filePath);
            Log.i("Music Service AP", filePath);
            fragment.playMusicOnClients(audioFile);
        }

    }

    //play song as a client
    public void clientPlaySong(String url){

            Log.i("ClientPlaySong", "Playing A New Song");
            newSong = false;
            player.reset();
            try {
                player.setDataSource(url);
            } catch (IOException e) {
                Log.e(MainActivity.TAG, "IOException");
            }

            player.setAudioStreamType(AudioManager.STREAM_MUSIC);


            try {
                player.prepare();
            } catch (IOException e) {
                Log.e(MainActivity.TAG, "IOException when client plays song");
            }
//        }
    }


    public void setSong(int songIndex) {
        if (songIndex == songPosn)
            return;
        songPosn = songIndex;
        newSong = true;
        playSong();
    }



    public int getPosn() {
        return player.getCurrentPosition();
    }

    public int getDur() {
        return player.getDuration();
    }

    public song getSong() {
        return songs.get(songPosn);
    }
}
