package kuchbhilabs.chestream.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by naman on 20/06/15.
 */
public class VideoFragment extends Fragment implements SurfaceHolder.Callback{

    Activity activity;
    SlidingUpPanelLayout slidingUpPanelLayout;

    SurfaceView surfaceView;
    SurfaceHolder holder;
    static MediaPlayer mediaPlayer;
    private static TextView commentFloating;
    private static TimerCommentText timerCommentText;

    private static final String TAG = "VideoFragment";

    private static final String TEST_URL = "http://devimages.apple.com/iphone/samples/bipbop" +
            "/bipbopall.m3u8";

    private boolean isMediaPlayerInitialized = false;
    private boolean isSurfaceCreated = false;
    private boolean videoStarted = false;

    private CommentsBroadcastReceiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_video, null);


        activity = getActivity();

        receiver = new CommentsBroadcastReceiver();

        slidingUpPanelLayout=(SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setOverlayed(true);
        slidingUpPanelLayout.setEnableDragViewTouchEvents(true);

        commentFloating=(TextView) rootView.findViewById(R.id.commentText);

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
        activity.unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mediaPlayer=new MediaPlayer();
        IntentFilter filter = new IntentFilter("intent.omerjerk");
        activity.registerReceiver(receiver, filter);
    }

    public static void commentReceived(String message) {
        //TODO: show floating view
        commentFloating.setVisibility(View.VISIBLE);
        commentFloating.setText(message);

        if (timerCommentText!=null) {
            timerCommentText.cancel();
        }

            timerCommentText = new TimerCommentText(7 * 1000, 1000);

            timerCommentText.start();


    }
    private class CommentsBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            Log.d(TAG, "intent received");
            if(intent.getAction().equals("intent.omerjerk")){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "comment received = " + intent.getStringExtra("comment"),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
    public static class TimerCommentText extends CountDownTimer {

        public void startCountdownTimer() {
        }

        public TimerCommentText(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            commentFloating.setVisibility(View.GONE);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }
    }

}