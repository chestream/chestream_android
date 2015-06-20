package kuchbhilabs.chestream;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import java.io.IOException;

public class LoginActivity extends Activity implements SurfaceHolder.Callback {
/*
    private MediaPlayer mediaPlayer;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder; */
    Button skip;

    private boolean isMediaPlayerInitialized = false;
    private boolean isSurfaceCreated = false;
    private boolean videoStarted = false;

    private String URL;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        skip = (Button) findViewById(R.id.btn_skip);
        /*
        surfaceView = (SurfaceView) findViewById(R.id.login_surface_view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        //TODO: Make it work to load from the assets directory
        mediaPlayer = new MediaPlayer(); */
        URL = "android.resource://"+getPackageName()+"/"+R.raw.vid4;
        VideoView videoView = (VideoView) findViewById(R.id.login_video_view);
        videoView.setVideoPath(URL);
        videoView.start();
//        File sdcard = Environment.getExternalStorageDirectory();
//        File videoFile = new File(sdcard, "video.mp4");
        isMediaPlayerInitialized = true;
        if (isSurfaceCreated) {
//            startMediaPlayer();
        }

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isSurfaceCreated = true;
        if (isMediaPlayerInitialized) {
//            startMediaPlayer();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        /*
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;*/
    }
/*
    private void startMediaPlayer() {
        synchronized (this) {
            if (!videoStarted) {
                try {
                    mediaPlayer.setDataSource(this, Uri.parse(URL));
                    mediaPlayer.setLooping(false);
                    mediaPlayer.setVolume(0, 0);

                    mediaPlayer.setDisplay(surfaceHolder);
                    mediaPlayer.prepareAsync();

                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
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
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        super.onPause();
    } */
}

