package kuchbhilabs.chestream.fragments.channels.NonSynchronous;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.activities.FullscreenPlayerActivityNoExo;

public class ChannelVideoFragmentNonSynchronousWithoutExo extends Fragment implements SurfaceHolder.Callback  {

    static  long playerPosition = 0;
    boolean playerNeedsPrepare = true;
    ImageView fullscreen;

    static Activity activity;
    List<String> videoIDS;


    static  String staticUrlrl = "";
//    long position;

    SurfaceView surfaceView;
    SurfaceHolder holder;
    static MediaPlayer mediaPlayer;
    private boolean isMediaPlayerInitialized = false;
    private boolean isSurfaceCreated = false;
    private boolean videoStarted = false;


    static VideoView vidView;
    static MediaController mediaController;

    static ProgressBar progressBar;

    public static ChannelVideoFragmentNonSynchronousWithoutExo newInstance(List<String> videoIds) {
        ChannelVideoFragmentNonSynchronousWithoutExo fragment = new ChannelVideoFragmentNonSynchronousWithoutExo();
        Bundle args = new Bundle();
        args.putStringArrayList("ids",new ArrayList<String>(videoIds));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragmentvideochannelwithoutexo, null);

        Toast.makeText(getActivity(), "Click on a video to play !!", Toast.LENGTH_LONG).show();

//        videoView = (VideoView) rootView.findViewById(R.id.dialogVV);
//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//            }
//        });
;

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressbar);

        vidView = (VideoView)rootView.findViewById(R.id.myVideo);

        videoIDS=getArguments().getStringArrayList("ids");

        activity = getActivity();
//        surfaceView = (SurfaceView) rootView.findViewById(R.id.main_surface_view);
//        holder = surfaceView.getHolder();
//        holder.addCallback(this);

//        mediaPlayer = new MediaPlayer();
//        isMediaPlayerInitialized = true;
//        if (isSurfaceCreated && videoIDS.get(0)!=null) {
//            startMediaPlayer(videoIDS.get(0));
//        }
        fullscreen=(ImageView) rootView.findViewById(R.id.fullscreen);
        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(activity, FullscreenPlayerActivityNoExo.class);
                intent.putExtra("url",staticUrlrl);
                if (vidView!=null)
                    intent.putExtra("position",vidView.getCurrentPosition());
                else intent.putExtra("position",0);
                startActivity(intent);
            }
        });


//        fullscreen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), FullscreenPlayerActivity.class);
//                intent.putExtra("url", staticUrlrl);
//                if (player != null)
//                    intent.putExtra("position", player.getCurrentPosition());
//                else intent.putExtra("position", 0);
//                startActivity(intent);
//            }
//        });

        return rootView;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isSurfaceCreated = true;
        if (isMediaPlayerInitialized) {
            startMediaPlayer(staticUrlrl);
            Log.d("urlrr", "14");

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
            Log.d("urlrr", "15");

        }
    }

    private void startMediaPlayer(final String url) {
        Log.d("urlrr", "1");

        new Thread(new Runnable() {

            public void run(){
                Log.d("urlrr", "2");

                synchronized (this) {
                    Log.d("urlrr", "3");

                    if (!videoStarted) {
                        try {
                            Log.d("urlrr", "4");

                            if (mediaPlayer == null) {
                                mediaPlayer = new MediaPlayer();
                            }
                            Log.d("urlrr", "5");

//                            mediaPlayer.setDataSource(activity, Uri.parse(url));
//                            mediaPlayer.setLooping(false);
//                            mediaPlayer.setVolume(0, 0);


                            mediaPlayer.setDataSource(url);
                            mediaPlayer.setLooping(false);

                            mediaPlayer.prepare();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                            Log.d("urlrr", "6");

                            mediaPlayer.setDisplay(holder);
//                            mediaPlayer.prepareAsync();

                            Log.d("urlrr", "7");

                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    Log.d("urlrr", "8");

                                    mediaPlayer.start();
                                    Log.d("urlrr", "9");

                                    videoStarted = true;
                                }
                            });
                        } catch (IOException e) {
                            Log.d("urlrr", "10");

                            e.printStackTrace();
                        }
                    }
                }            }
        }).start();
        Log.d("urlrr", "11");

    }

    @Override
    public void onPause() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;

            Log.d("urlrr", "12");

        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mediaPlayer=new MediaPlayer();
        Log.d("urlrr", "13");

        IntentFilter filter = new IntentFilter("intent.omerjerk");
    }


    public static void playVideo(String url){
        staticUrlrl = url;

        Log.d("urlrr", url);
        playerPosition = 0;

     play2(url);

    }

    public static void play2(String url){

//        vidView.setMediaController(null);

        if(mediaController==null){
            mediaController = new MediaController(activity);
            mediaController.setAnchorView(vidView);
            mediaController.setMediaPlayer(vidView);
        }
        vidView.setMediaController(mediaController);
        Log.d("urlrryo", url);
        vidView.setVideoURI(Uri.parse(url));
        vidView.start();
        progressBar.setVisibility(View.VISIBLE);
        vidView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                progressBar.setVisibility(View.GONE);
                mp.start();
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int arg1,
                                                   int arg2) {
                        // TODO Auto-generated method stub
                        progressBar.setVisibility(View.GONE);
                        mp.start();
                    }
                });
            }
        });

    }



}