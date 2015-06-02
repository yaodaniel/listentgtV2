package com.example.android.listentgt;

import com.example.android.listentgt.MusicService.MusicBinder;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.widget.RelativeLayout;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.ServiceConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FragmentPlayList extends Fragment implements View.OnClickListener{

    private ArrayList<song> songList;
    private ListView songView;
    //private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;
    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder)service;
            //get service
            MainActivity.musicSrv = binder.getService();
            //pass list
            MainActivity.musicSrv.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentActivity faActivity  = (FragmentActivity) super.getActivity();
        // Replace LinearLayout by the type of the root element of the layout you're trying to load
        RelativeLayout RLayout  = (RelativeLayout)  inflater.inflate(R.layout.playlist_page, container, false);

        // The FragmentActivity doesn't contain the layout directly so we must use our instance of     LinearLayout :
        songView = (ListView) RLayout.findViewById(R.id.song_list);
        songList = new ArrayList<song>();
        getSongList();
        Collections.sort(songList, new Comparator<song>() {
            public int compare(song a, song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        SongAdapter songAdt = new SongAdapter(this, faActivity, songList);
        songView.setAdapter(songAdt);

        return RLayout; // We must return the loaded Layout
    }

    @Override
    public void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this.getActivity(), MusicService.class);
            this.getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            this.getActivity().startService(playIntent);
        }
    }

    @Override
    public void onDestroy(){
        this.getActivity().stopService(playIntent);
        MainActivity.musicSrv=null;
        super.onDestroy();
    }

    public void songPicked(View view) {
        MainActivity.musicSrv.setSong(Integer.parseInt(view.getTag().toString()));
        //musicSrv.playSong();
    }

    public void getSongList() {
        FragmentActivity faActivity  = (FragmentActivity) super.getActivity();
        //retrieve song info
        ContentResolver musicResolver = faActivity.getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Albums.ALBUM_ART); //For album art possible future step
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisAlbumCover = "";
                songList.add(new song(thisId, thisTitle, thisArtist, thisAlbumCover));
            }
            while (musicCursor.moveToNext());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ASong:
                songPicked(v);
                //getActivity().setContentView(R.layout.player_page);
                getFragmentManager().beginTransaction().replace(R.id.playlistPage, new FragmentMusicPlayer()).commit();
                //getFragmentManager().beginTransaction().detach(this).attach(new FragmentMusicPlayer()).commit();
                //getFragmentManager().beginTransaction().hide(this).commit();
                //getFragmentManager().beginTransaction().show(new FragmentMusicPlayer()).commit();
                break;
            default:
                Log.i("default:", "default");
                break;
        }
    }
}
