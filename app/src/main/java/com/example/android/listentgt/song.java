package com.example.android.listentgt;

public class song {
	private long id;
	private String title;
	private String artist;
	private String albumCover;
	
	public song(long songID, String songTitle, String songArtist, String songAlbumCover) {
		id = songID;
		title = songTitle;
		artist = songArtist;
		albumCover = songAlbumCover;
	}
	public long getID() { return id; }
	public String getTitle() { return title; }
	public String getArtists() { return artist; }
	public String getAlbumCover() { return albumCover; }
}
