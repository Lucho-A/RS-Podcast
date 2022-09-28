package com.lucho.rs_podcast;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class Track {
    private String trackTitle;
    private String trackAlbum;
    private Bitmap trackCover;
    private Boolean trackInfoUpdated;


    public Track(Context mContext, String url){
        trackTitle ="Trying to retrieve track info...";
        trackAlbum ="";
        trackCover = BitmapFactory.decodeResource(mContext.getResources(), R.raw.rsgirlbw3);
        trackInfoUpdated =false;

        Thread searchInfo= new Thread(() -> {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            try {
                mmr.setDataSource(url, new HashMap<>());
            }catch (Exception e){
                mmr.close();
                trackInfoUpdated =true;
                e.printStackTrace();
            }
            trackTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            if(trackTitle==null) trackTitle ="Title not found";
            trackAlbum =mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            if(trackAlbum==null) trackAlbum ="Album not found";
            try {
                int i;
                for(i=url.length()-1; i>=0;i--){
                    if(url.charAt(i)=='/') break;
                }
                String urlImg = url.substring(0,i+1) + "Cover.jpg";
                trackCover =BitmapFactory.decodeStream(new URL(urlImg).openConnection().getInputStream());
            } catch (IOException e) {
                trackCover = BitmapFactory.decodeResource(mContext.getResources(), R.raw.rsgirlbw3);
                trackInfoUpdated =true;
                e.printStackTrace();
            }
            trackInfoUpdated =true;
        });

        searchInfo.start();
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public String getTrackAlbum() {
        return trackAlbum;
    }

    public Bitmap getTrackCover() {
        return trackCover;
    }

    public Boolean getTrackInfoUpdated() {
        return trackInfoUpdated;
    }
}
