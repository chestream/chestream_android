package kuchbhilabs.chestream;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.fragments.sign.SignInFragment;

public class LoginActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    public static final boolean DEBUG = ApplicationBase.LOG_DEBUG;
    public static final boolean INFO = ApplicationBase.LOG_INFO;
    private static final String TAG = "LoginActivity";


    public static final String KEY_PREF_SIGNED_IN = "signed_in";

    private MediaPlayer mediaPlayer;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    private boolean isMediaPlayerInitialized = false;
    private boolean isSurfaceCreated = false;
    private boolean videoStarted = false;

    public static String URL;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        surfaceView = (SurfaceView) findViewById(R.id.login_surface_view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        SignInFragment newFragment = new SignInFragment();
        transaction.replace(R.id.sign_in_container, newFragment,"SignInFragment").commit();

        //TODO: Make it work to load from the assets directory
        mediaPlayer = new MediaPlayer();
        URL = "android.resource://"+getPackageName()+"/"+R.raw.compressedvideo;


        ParseUser pUser = ParseUser.getCurrentUser();
        if ((pUser != null)
                && (pUser.isAuthenticated())
                && (pUser.getSessionToken() != null)
                /*&& (pUser.getBoolean(ParseTables.Users.FULLY_REGISTERED))*/) {
            Log.d(TAG, pUser.getUsername() + pUser.getSessionToken());
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);

            finish();
        }

        isMediaPlayerInitialized = true;
        if (isSurfaceCreated) {
            startMediaPlayer();
        }
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
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void startMediaPlayer() {
        synchronized (this) {
            if (!videoStarted) {
                try {
                    mediaPlayer.setDataSource(this, Uri.parse(URL));
                    mediaPlayer.setLooping(true);
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
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (DEBUG) Log.d(TAG, "onActivityResult called");
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("SignOnFragment");
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode,data);
        }
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}

