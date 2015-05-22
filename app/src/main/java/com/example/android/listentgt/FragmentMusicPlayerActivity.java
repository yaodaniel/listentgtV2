package com.example.android.listentgt;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.widget.RelativeLayout;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FragmentMusicPlayerActivity extends Fragment {
	private ArrayList<song> songList;
	private ListView songView;
	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.playlist_page);
//        songView = (ListView)findViewById(R.id.song_list);
//        songList = new ArrayList<song>();
//        getSongList();
//        Collections.sort(songList, new Comparator<song>() {
//            public int compare(song a, song b) {
//                return a.getTitle().compareTo(b.getTitle());
//            }
//        });
//        SongAdapter songAdt = new SongAdapter(this, songList);
//        songView.setAdapter(songAdt);
//    }

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
        SongAdapter songAdt = new SongAdapter(faActivity, songList);
        songView.setAdapter(songAdt);

        return RLayout; // We must return the loaded Layout
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
			  //add songs to list
			  do {
			    long thisId = musicCursor.getLong(idColumn);
			    String thisTitle = musicCursor.getString(titleColumn);
			    String thisArtist = musicCursor.getString(artistColumn);
			    songList.add(new song(thisId, thisTitle, thisArtist));
			  }
			  while (musicCursor.moveToNext());
		}
	}
}
