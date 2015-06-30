package kuchbhilabs.chestream.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
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

import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.comments.CommentsFragment;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by naman on 20/06/15.
 */
public class VideoFragment extends Fragment implements SurfaceHolder.Callback{

    String url="";
    String upvotes="";
    String location="";
    String title="";
    String username = "";
    String avatar= "";
    ParseObject currentVideoObject=null;

    TextView tvideoTitle;
    TextView tlocation;
    TextView tusername;
    TextView ttotalVotes;
    SimpleDraweeView tdraweeView;

    Activity activity;
    public static SlidingUpPanelLayout slidingUpPanelLayout;

    SurfaceView surfaceView;
    SurfaceHolder holder;
    static MediaPlayer mediaPlayer;
    private static TextView commentFloating;
    private static TimerCommentText timerCommentText;

    private static final String TAG = "VideoFragment";

    private static final String TEST_URL = "http://128.199.128.227/chestream_raw/video_1434859043/video_1434859043.mp4";

    private boolean isMediaPlayerInitialized = false;
    private boolean isSurfaceCreated = false;
    private boolean isUrlFetched = false;
    private boolean videoStarted = false;

    private CommentsBroadcastReceiver receiver;


    private int i = 0;

    ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_video, null);


        activity = getActivity();
        receiver = new CommentsBroadcastReceiver();


        tvideoTitle = (TextView)rootView.findViewById(R.id.video_title);
        tlocation = (TextView)rootView.findViewById(R.id.video_location);
        tusername = (TextView)rootView.findViewById(R.id.username);
        ttotalVotes = (TextView)rootView.findViewById(R.id.video_score);
        tdraweeView=(SimpleDraweeView) rootView.findViewById(R.id.profile_picture);

        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Initializing the stream...");
        mProgressDialog.show();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Videos");
        query.orderByDescending(ParseTables.Videos.UPVOTE);
        query.whereEqualTo(ParseTables.Videos.PLAYED, false);
        query.whereEqualTo(ParseTables.Videos.COMPILED, true);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject videos, ParseException e) {
                if (videos == null) {
                    Log.d("vid", "The getFirst request failed.");
                } else {
                    Log.d("vid", "Retrieved the object.");

                    currentVideoObject = videos;

                    url = videos.getString(ParseTables.Videos.URL_M3U8);
                    upvotes = videos.getString(ParseTables.Videos.UPVOTE);
                    location = videos.getString(ParseTables.Videos.LOCATION);
                    title = videos.getString(ParseTables.Videos.TITLE);
                    try {
                        username = videos.getParseUser(ParseTables.Videos.USER).fetchIfNeeded()
                                .getString(ParseTables.Users.USERNAME);
                        avatar = videos.getParseUser(ParseTables.Videos.USER).fetchIfNeeded()
                                .getString(ParseTables.Users.AVATAR);

                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
                isUrlFetched = true;
                startMediaPlayer();
            }
        });

        mediaPlayer = new MediaPlayer();


        slidingUpPanelLayout=(SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setOverlayed(true);
        slidingUpPanelLayout.setEnableDragViewTouchEvents(true);

        commentFloating=(TextView) rootView.findViewById(R.id.commentText);

        CommentsFragment commentsFragment = new CommentsFragment();
        getChildFragmentManager().beginTransaction().add(R.id.comments, commentsFragment).commit();

        surfaceView = (SurfaceView) rootView.findViewById(R.id.main_surface_view);
        holder = surfaceView.getHolder();
        holder.addCallback(this);

        isMediaPlayerInitialized = true;
        //TODO: For now only
//        startMediaPlayer();

        return rootView;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isSurfaceCreated = true;
//        startMediaPlayer();
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
        if (isMediaPlayerInitialized && isSurfaceCreated && isUrlFetched) {
            synchronized (this) {
                if (!videoStarted) {
                    try {
                        if (mediaPlayer == null) {
                            mediaPlayer = new MediaPlayer();
                        }


//                        if (urlList.size() == 0) {
//                            Toast.makeText(activity, "Stream is empty.", Toast.LENGTH_SHORT).show();
//                            if (mProgressDialog != null) {
//                                mProgressDialog.dismiss();
//                            }
//                            return;
//                        }

                        String urlSet = url;
                        tvideoTitle.setText(title);
                        tusername.setText(username);
                        tlocation.setText(location);
                        ttotalVotes.setText(upvotes);
                        Uri uri = Uri.parse(avatar);
                        tdraweeView.setImageURI(uri);

                        mediaPlayer.setDataSource(activity, Uri.parse(urlSet));
                        mediaPlayer.setLooping(false);
//                        mediaPlayer.setVolume(0, 0);

                        mediaPlayer.setDisplay(holder);
                        mediaPlayer.prepareAsync();

                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                if (mProgressDialog != null) {
                                    mProgressDialog.cancel();
                                    mProgressDialog = null;
                                }
                                mediaPlayer.start();
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {

                                        currentVideoObject.put("played",true);
                                        currentVideoObject.saveInBackground();

                                        Log.d(TAG, "COMPLETION");
                                        mediaPlayer.reset();

                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Videos");
                                        query.orderByDescending(ParseTables.Videos.UPVOTE);
                                        query.whereEqualTo(ParseTables.Videos.PLAYED, false);
                                        query.whereEqualTo(ParseTables.Videos.COMPILED, true);
                                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                                            public void done(ParseObject videos, ParseException e) {
                                                if (videos == null) {
                                                    Log.d("vid", "The getFirst request failed.");
                                                } else {
                                                    Log.d("vid", "Retrieved the object.");

                                                    currentVideoObject = videos;

                                                    url = videos.getString(ParseTables.Videos.URL_M3U8);
                                                    upvotes = videos.getString(ParseTables.Videos.UPVOTE);
                                                    location = videos.getString(ParseTables.Videos.LOCATION);
                                                    title = videos.getString(ParseTables.Videos.TITLE);
                                                    try {
                                                        username = videos.getParseUser(ParseTables.Videos.USER).fetchIfNeeded()
                                                                .getString(ParseTables.Users.USERNAME);
                                                        avatar = videos.getParseUser(ParseTables.Videos.USER).fetchIfNeeded()
                                                                .getString(ParseTables.Users.AVATAR);

                                                    } catch (ParseException e1) {
                                                        e1.printStackTrace();
                                                    }


                                                    String urlSet = url;
                                                    tvideoTitle.setText(title);
                                                    tusername.setText(username);
                                                    tlocation.setText(location);
                                                    ttotalVotes.setText(upvotes);
                                                    Uri uri = Uri.parse(avatar);
                                                    tdraweeView.setImageURI(uri);


                                                    try {
                                                        mediaPlayer.setDataSource(activity, Uri.parse(urlSet));
                                                        mediaPlayer.prepare();
                                                        mediaPlayer.start();
                                                    } catch (IOException e1) {
                                                        e1.printStackTrace();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                                videoStarted = true;
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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