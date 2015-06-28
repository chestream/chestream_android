package kuchbhilabs.chestream;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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

public class LoginActivity extends Activity implements SurfaceHolder.Callback {

    private MediaPlayer mediaPlayer;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Button skip;

    private boolean isMediaPlayerInitialized = false;
    private boolean isSurfaceCreated = false;
    private boolean videoStarted = false;

    private Button fbLogin;

    public static String URL;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        skip = (Button) findViewById(R.id.btn_skip);
        fbLogin = (Button) findViewById(R.id.btn_fb);

        surfaceView = (SurfaceView) findViewById(R.id.login_surface_view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        //TODO: Make it work to load from the assets directory
        mediaPlayer = new MediaPlayer();
        URL = "android.resource://"+getPackageName()+"/"+R.raw.vid4b;

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFacebookSignOn();
            }
        });

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
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void doFacebookSignOn() {
        List<String> permissions = Arrays.asList(
                "public_profile", "email");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                Log.d(TAG, "done with login");
                if (err != null) {
                    Log.w(TAG, "pe = " + err.getCode() + err.getMessage());
                    Toast.makeText(LoginActivity.this, err.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (user == null) {
                    Log.w(TAG, "Uh oh. The user cancelled the Facebook login.");
                } else {

                    if (user.isNew()) {
                        Log.d(TAG, "We've got a new user");
                        try {
                            user.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //TODO: download user data and sign up
                    } else {
                        Log.d(TAG, "Welcome back old user");
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        LoginActivity.this.startActivity(intent);
                    }
                }
            }
        });
    }
}

