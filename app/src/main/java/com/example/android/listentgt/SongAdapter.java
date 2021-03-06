package com.example.android.listentgt;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdapter extends BaseAdapter {

	private ArrayList<song> songs;
	private LayoutInflater songInf;
	private Fragment parentFrag;
	
	public SongAdapter(Fragment obj, Context c, ArrayList<song> theSongs) {
		songs = theSongs;
		songInf = LayoutInflater.from(c);
		parentFrag = obj;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return songs.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//map to song layout
		  LinearLayout songLay = (LinearLayout)songInf.inflate(R.layout.song, parent, false);
		  songLay.setOnClickListener((FragmentPlayList)parentFrag);
		  //get title and artist views
		  TextView songView = (TextView)songLay.findViewById(R.id.song_title);
		  TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
		  //get song using position
		  song currSong = songs.get(position);
		  //get title and artist strings
		  songView.setText(currSong.getTitle());
		  artistView.setText(currSong.getArtists());
		  //set position as tag
		  songLay.setTag(position);
		  return songLay;
	}

}
