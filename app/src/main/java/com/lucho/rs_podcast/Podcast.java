package com.lucho.rs_podcast;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.lucho.rs_podcast.exceptions.InitPodcastException;
import com.lucho.rs_podcast.exceptions.MediaPlayerPlayException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class Podcast {
    private final Context mContext;
    private MediaPlayer mPlayer = new MediaPlayer();
    private ArrayList<String> songs;
    private Track trackLoaded =null;
    private int pauseLength;
    private Boolean readyForLooping=false;
    private String podcastState;

    public Podcast(Context mContext) throws InitPodcastException {
        this.mContext=mContext;
        init_podcast();
    }

    private void init_podcast() throws InitPodcastException {
        podcastState ="STOPPED";
        songs = new ArrayList<>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(mContext.getResources().openRawResource(R.raw.links)));
            String s;
            while((s=in.readLine())!=null) songs.add(s);
            in.close();
        } catch (Exception e) {
            throw new InitPodcastException(e);
        }
    }

    private String getTrack(){
        Random rand = new Random();
        String urlNextTrack = songs.get(rand.nextInt(songs.size()));
        trackLoaded = new Track(mContext, urlNextTrack);
        return urlNextTrack;
    }

    public void play() throws MediaPlayerPlayException{
        if(pauseLength!=0){
            resume();
            return;
        }
        readyForLooping = false;
        try {
            podcastState = "PLAYING";
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setLooping(false);
            String nextTrack = getTrack();
            mPlayer.setDataSource(nextTrack);
        } catch (IOException e) {
            stop();
            throw new MediaPlayerPlayException(e.getMessage());
        }
        mPlayer.prepareAsync();
        mPlayer.setOnPreparedListener(mediaPlayer -> mPlayer.start());
        mPlayer.setOnCompletionListener(mediaPlayer -> readyForLooping=true);
    }

    public void pause() {
        podcastState ="PAUSED";
        pauseLength=mPlayer.getCurrentPosition();
        mPlayer.pause();
    }

    public void resume() {
        podcastState ="RESUMED";
        mPlayer.seekTo(pauseLength);
        mPlayer.start();
        pauseLength=0;
    }

    public void stop() {
        podcastState ="STOPPED";
        if(mPlayer!=null) mPlayer.stop();
        mPlayer=null;
        pauseLength=0;
        trackLoaded =null;
    }

    public Track getSongLoaded() {
        return trackLoaded;
    }

    public Boolean getReadyForLooping() {
        return readyForLooping;
    }

    public String getState() {
        return podcastState;
    }
}
