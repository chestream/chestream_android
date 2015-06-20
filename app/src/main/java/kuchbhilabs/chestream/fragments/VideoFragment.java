package kuchbhilabs.chestream.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import java.io.IOException;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by naman on 20/06/15.
 */
public class VideoFragment extends Fragment implements SurfaceHolder.Callback{

    Activity activity;
    SlidingUpPanelLayout slidingUpPanelLayout;
    CommentsBroadcastReciever broadcastReciever=new CommentsBroadcastReciever();
    IntentFilter intentFilter=createIntentFilter();

    SurfaceView surfaceView;
    SurfaceHolder holder;
    MediaPlayer mediaPlayer;

    private static final String TEST_URL = "http://devimages.apple.com/iphone/samples/bipbop" +
            "/bipbopall.m3u8";

    private boolean isMediaPlayerInitialized = false;
    private boolean isSurfaceCreated = false;
    private boolean videoStarted = false;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_video, null);

        activity = getActivity();

        slidingUpPanelLayout=(SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setOverlayed(true);

        CommentsFragment commentsFragment = new CommentsFragment();
        getChildFragmentManager().beginTransaction().add(R.id.comments, commentsFragment).commit();
/*
        VideoView videoView = (VideoView) rootView.findViewById(R.id.main_video_view);
        videoView.setVideoPath(TEST_URL);
        videoView.start();
*/
        surfaceView = (SurfaceView) rootView.findViewById(R.id.main_surface_view);
        holder = surfaceView.getHolder();
        holder.addCallback(this);

        mediaPlayer = new MediaPlayer();
        isMediaPlayerInitialized = true;
        if (isSurfaceCreated) {
            startMediaPlayer();
        }

        registerCommentsReceiver();

        return rootView;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isSurfaceCreated = true;
        if (isMediaPlayerInitialized) {
            startMediaPlayer();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void startMediaPlayer() {
        synchronized (this) {
            if (!videoStarted) {
                try {
                    mediaPlayer.setDataSource(activity, Uri.parse(TEST_URL));
                    mediaPlayer.setLooping(false);
                    mediaPlayer.setVolume(0, 0);

                    mediaPlayer.setDisplay(holder);
                    mediaPlayer.prepareAsync();

                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mediaPlayer.start();
                            videoStarted = true;
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPause() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onPause();
    }

    private static IntentFilter createIntentFilter(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("ask omerjerk");
        return filter;
    }

    protected void registerCommentsReceiver() {
        getActivity().registerReceiver(broadcastReciever, intentFilter);
    }

    private class CommentsBroadcastReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("ask omerjerk")){

            }
        }
    }
}