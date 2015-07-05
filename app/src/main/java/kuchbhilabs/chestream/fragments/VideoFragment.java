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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.comments.CommentsFragment;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.helpers.LoadingProgress;
import kuchbhilabs.chestream.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by naman on 20/06/15.
 */
public class VideoFragment extends Fragment implements SurfaceHolder.Callback {

    String url = "";
    String upvotes = "";
    String location = "";
    String title = "";
    String username = "";
    String avatar = "";
    public static ParseObject currentVideo;

    TextView tvideoTitle;
    TextView tlocation;
    TextView tusername;
    TextView ttotalVotes;
    SimpleDraweeView tdraweeView;

    Activity activity;
    public static SlidingUpPanelLayout slidingUpPanelLayout; //slidingUpPanelLayout2;

    SurfaceView surfaceView;
    SurfaceHolder holder;
    MediaPlayer mediaPlayer;
    private static TextView commentFloating;
    private static TimerCommentText timerCommentText;

    private static final String TAG = "VideoFragment";

    private static final String TEST_URL = "http://128.199.128.227/chestream_raw/video_1434859043/video_1434859043.mp4";
    private static final String NEXT_URL = "http://128.199.128.227:8800/";

    private boolean isMediaPlayerInitialized = false;
    private boolean isSurfaceCreated = false;
    private boolean isUrlFetched = false;
    private boolean videoStarted = false;

    private CommentsBroadcastReceiver receiver;
    private MediaHandler handler;

    private int i = 0;

    ProgressDialog mProgressDialog;
    View loadingLayout;
    LoadingProgress loadingProgress;
   static View loadingFrame,dividerView;
    public static TextView commentsCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);



        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                handler = new MediaHandler();
                mediaPlayer = new MediaPlayer();
                isMediaPlayerInitialized = true;
                startMediaPlayer();
                Looper.loop();
            }
        }).start();

        View rootView = inflater.inflate(R.layout.fragment_video, null);

        activity = getActivity();
        receiver = new CommentsBroadcastReceiver();



        tvideoTitle = (TextView) rootView.findViewById(R.id.video_title);
        tlocation = (TextView) rootView.findViewById(R.id.video_location);
        tusername = (TextView) rootView.findViewById(R.id.username);
        ttotalVotes = (TextView) rootView.findViewById(R.id.video_score);
        tdraweeView = (SimpleDraweeView) rootView.findViewById(R.id.profile_picture);
        loadingLayout = rootView.findViewById(R.id.loading_layout);
        commentsCount=(TextView) rootView.findViewById(R.id.commentsCount);
//        loadingProgress=(LoadingProgress) rootView.findViewById(R.id.loading_progress);
        loadingFrame=(View) rootView.findViewById(R.id.loading_frame);
        dividerView=rootView.findViewById(R.id.dividerView);

//        loadingProgress.show();

//        loadingFrame.setBackground(Helper.createBlurredImage(getResources().getDrawable(R.drawable.zombie_icon), activity));

        sendNextRequest();

        slidingUpPanelLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
       // slidingUpPanelLayout2 = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout2);
        slidingUpPanelLayout.setOverlayed(true);
        slidingUpPanelLayout.setEnableDragViewTouchEvents(true);

        slidingUpPanelLayout.expandPanel();

//        slidingUpPanelLayout2.setOverlayed(true);
//        slidingUpPanelLayout2.setEnableDragViewTouchEvents(true);

        setPanelSlideListeners();

        commentFloating = (TextView) rootView.findViewById(R.id.commentText);

        CommentsFragment commentsFragment = new CommentsFragment();
        getChildFragmentManager().beginTransaction().add(R.id.comments, commentsFragment).commit();


        surfaceView = (SurfaceView) rootView.findViewById(R.id.main_surface_view);
        holder = surfaceView.getHolder();
        holder.addCallback(this);

        return rootView;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isSurfaceCreated = true;
        if (handler != null) {
            handler.sendMessage(handler.obtainMessage(MediaHandler.MSG_START));
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

    private void sendNextRequest() {
        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, NEXT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response = " + response);
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            responseObject = responseObject.getJSONObject("data");
                            String videoId = responseObject.getString("video_id");
                            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseTables.Videos._NAME);
                            query.whereEqualTo("objectId", videoId);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (list != null) {
                                        currentVideo = list.get(0);
                                        CommentsFragment.setUpComments();
                                        url = currentVideo.getString(ParseTables.Videos.URL_M3U8);
                                        upvotes = currentVideo.getString(ParseTables.Videos.UPVOTE);
                                        location = currentVideo.getString(ParseTables.Videos.LOCATION);
                                        title = currentVideo.getString(ParseTables.Videos.TITLE);
                                        currentVideo.getParseUser(ParseTables.Videos.USER)
                                                .fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                                                    @Override
                                                    public void done(ParseUser parseObject, ParseException e) {
                                                        username = parseObject.getUsername();
                                                        avatar = parseObject.getString(ParseTables.Users.AVATAR);
                                                        isUrlFetched = true;
                                                        setVideoDetails();
                                                        if (!videoStarted) {
                                                            handler.sendMessage(handler.obtainMessage(MediaHandler.MSG_START));
                                                        } else {
                                                            handler.sendMessage(handler.obtainMessage(
                                                                    MediaHandler.MSG_CHANGE_SOURCE, url));
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(activity, "Shit Happened!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(stringRequest);
    }

    private void setVideoDetails() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvideoTitle.setText(title);
                tusername.setText(username);
                tlocation.setText(location);
                ttotalVotes.setText(upvotes);
                Uri uri = Uri.parse(avatar);
                tdraweeView.setImageURI(uri);
//                slidingUpPanelLayout2.expandPanel();
//                slidingUpPanelLayout2.setPanelHeight(75);
//                Handler handlerCollapse=new Handler();
//                handlerCollapse.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        slidingUpPanelLayout2.collapsePanel();
//                    }
//                },5000);
            }
        });
    }

    private void startMediaPlayer() {
        Log.d(TAG, "Initial call");
        if (isMediaPlayerInitialized && isSurfaceCreated && isUrlFetched && !videoStarted) {
            synchronized (this) {
                try {
                    videoStarted = true;
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                    }

                    Log.d(TAG, "STARTING MEDIA PLAYER");
                    //TODO: Add a check if there is no stream available

                    mediaPlayer.setDataSource(activity, Uri.parse(url));
                    mediaPlayer.setLooping(false);
                    mediaPlayer.setVolume(0, 0);

                    mediaPlayer.setDisplay(holder);
                    mediaPlayer.prepare();

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingLayout.setVisibility(View.GONE);
                        }
                    });
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            currentVideo.put(ParseTables.Videos.PLAYED, true);
                            currentVideo.saveInBackground();
                            Log.d(TAG, "COMPLETION");
                            sendNextRequest();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    loadingLayout.setVisibility(View.GONE);
                }
            }
        } else {
            Log.d(TAG, "Returning");
            Log.d(TAG, "isMediaPlayerInitialized = " + isMediaPlayerInitialized);
            Log.d(TAG, "isSurfaceCreated = " + isSurfaceCreated);
            Log.d(TAG, "isUrlFetched = " + isUrlFetched);
            Log.d(TAG, "videoStarted = " + videoStarted);
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
        mediaPlayer = new MediaPlayer();
        IntentFilter filter = new IntentFilter("intent.omerjerk");
        activity.registerReceiver(receiver, filter);
    }

    public static void commentReceived(String message) {
        //TODO: show floating view
        commentFloating.setVisibility(View.VISIBLE);
        commentFloating.setText(message);

        if (timerCommentText != null) {
            timerCommentText.cancel();
        }
        timerCommentText = new TimerCommentText(7 * 1000, 1000);
        timerCommentText.start();
    }

    private class CommentsBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            Log.d(TAG, "intent received");
            if (intent.getAction().equals("intent.omerjerk")) {
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

    private class MediaHandler extends Handler {

        public static final int MSG_START = 1;
        public static final int MSG_CHANGE_SOURCE = 2;

        @Override
        public void handleMessage(Message what) {
            int code = what.what;
            Log.d(TAG, "CODE = " + code);
            switch (code) {
                case MSG_START:
                    Log.d(TAG, "STARTING the media player");
                    startMediaPlayer();
                    break;
                case MSG_CHANGE_SOURCE:
                    String source = (String) what.obj;
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(activity, Uri.parse(source));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private void setPanelSlideListeners() {
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                tvideoTitle.setAlpha(slideOffset);
                tlocation.setAlpha(slideOffset);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                tvideoTitle.setAlpha(0);
                tlocation.setAlpha(0);
            }

            @Override
            public void onPanelExpanded(View panel) {
//                if (slidingUpPanelLayout2.isPanelExpanded()) {
//                    slidingUpPanelLayout2.collapsePanel();
//                }
                tvideoTitle.setAlpha(255);
                tlocation.setAlpha(255);
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });
    }
    public static void setCommentsCount(String count){
        commentsCount.setText(count);
        dividerView.setVisibility(View.VISIBLE);

    }

}