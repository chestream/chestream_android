package kuchbhilabs.chestream.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.util.Util;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.VolleySingleton;
import kuchbhilabs.chestream.comments.CommentsFragment;
import kuchbhilabs.chestream.exoplayer.DemoPlayer;
import kuchbhilabs.chestream.exoplayer.EventLogger;
import kuchbhilabs.chestream.exoplayer.HlsRendererBuilder;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.fragments.queue.QueueFragment;
import kuchbhilabs.chestream.helpers.Helper;
import kuchbhilabs.chestream.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by naman on 20/06/15.
 */
public class VideoFragment extends Fragment implements SurfaceHolder.Callback,
        DemoPlayer.Listener, AudioCapabilitiesReceiver.Listener{

    String url = "";
    String upvotes = "";
    String location = "";
    String title = "";
    String username = "";
    String avatar = "";
    public static ParseObject currentVideo;

    TextView tvideoTitle;
    TextView tlocation;
    TextView tusername,tusernameComments;
    TextView ttotalVotes,ttotalVotesComments;
    SimpleDraweeView tdraweeView,tdraweeViewComments;
    private SimpleDraweeView bufferScreenProfile;
    private ImageView bufferScreenPreview;
    private TextView bufferScreenTitle,bufferScreenUsername;

    Activity activity;
    public static SlidingUpPanelLayout slidingUpPanelLayout; //slidingUpPanelLayout2;

    SurfaceView surfaceView;
    SurfaceHolder holder;
    private static TextView commentFloating;
    private static TimerCommentText timerCommentText;
    private FrameLayout bufferScreen;

    private static final String TAG = "VideoFragment";

    private static final String TEST_URL = "http://128.199.128.227/chestream_raw/video_1434859043/video_1434859043.mp4";
    private static final String NEXT_URL = "http://128.199.128.227:8800/";

    private CommentsBroadcastReceiver receiver;

    private int i = 0;

    View loadingLayout;
    static View loadingFrame,dividerView;
    public static TextView commentsCount;

    private static final long MIN_BUFFER_TIME_MILLIS = 5000;
    private long bufferStartTime;

    private DemoPlayer player;
    private AudioCapabilities audioCapabilities;
    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    long playerPosition = 0;
    boolean playerNeedsPrepare = true;
    private EventLogger eventLogger;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_video, null);

        activity = getActivity();
        receiver = new CommentsBroadcastReceiver();

        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(activity, this);

        tvideoTitle = (TextView) rootView.findViewById(R.id.video_title);
        tlocation = (TextView) rootView.findViewById(R.id.video_location);
        tusername = (TextView) rootView.findViewById(R.id.username);
        tusernameComments = (TextView) rootView.findViewById(R.id.username_comments);
        ttotalVotes = (TextView) rootView.findViewById(R.id.video_score);
        ttotalVotesComments = (TextView) rootView.findViewById(R.id.video_score_comments);
        tdraweeView = (SimpleDraweeView) rootView.findViewById(R.id.profile_picture);
        tdraweeViewComments = (SimpleDraweeView) rootView.findViewById(R.id.profile_picture_comments);
        loadingLayout = rootView.findViewById(R.id.loading_layout);
        bufferScreen = (FrameLayout) rootView.findViewById(R.id.buffer_screen);
        bufferScreenPreview = (ImageView) rootView.findViewById(R.id.buffer_screen_preview);
        bufferScreenTitle = (TextView) rootView.findViewById(R.id.buffer_screen_video_title);
        bufferScreenUsername = (TextView) rootView.findViewById(R.id.buffer_screen_username);
        bufferScreenProfile = (SimpleDraweeView) rootView.findViewById(R.id.buffer_screen_profile_picture);

        commentsCount=(TextView) rootView.findViewById(R.id.commentsCount);
//        loadingProgress=(LoadingProgress) rootView.findViewById(R.id.loading_progress);
        loadingFrame=(View) rootView.findViewById(R.id.loading_frame);
        dividerView=rootView.findViewById(R.id.dividerView);

        sendNextRequest();

        slidingUpPanelLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
       // slidingUpPanelLayout2 = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout2);
        slidingUpPanelLayout.setOverlayed(true);
        slidingUpPanelLayout.setEnableDragViewTouchEvents(true);


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
        if (player != null) {
            player.setSurface(holder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (player != null) {
            player.blockingClearSurface();
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
                            final String videoId = responseObject.getString("video_id");
                            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseTables.Videos._NAME);
                            query.whereEqualTo("objectId", videoId);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (list != null) {
                                        currentVideo = list.get(0);

                                        loadingLayout.setVisibility(View.GONE);
                                        if (Helper.isKitkat()) {
                                            TransitionManager.beginDelayedTransition(slidingUpPanelLayout);
                                        }
                                        bufferScreen.setVisibility(View.VISIBLE);

                                        upvotes = currentVideo.getString(ParseTables.Videos.UPVOTE);
                                        location = currentVideo.getString(ParseTables.Videos.LOCATION);
                                        title = currentVideo.getString(ParseTables.Videos.TITLE);
                                        currentVideo.getParseUser(ParseTables.Videos.USER)
                                                .fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                                                    @Override
                                                    public void done(ParseUser parseObject, ParseException e) {
                                                        username = parseObject.getUsername();
                                                        avatar = parseObject.getString(ParseTables.Users.AVATAR);
                                                        setVideoDetails();

                                                        bufferScreenProfile.setImageURI(Uri.parse(avatar));
                                                        bufferScreenUsername.setText(username);


                                                    }
                                                });
                                        ImageLoader imageLoader= VolleySingleton.getInstance(activity).getImageLoader();
                                        imageLoader.get(currentVideo.getString(
                                                ParseTables.Videos.VIDEO_THUMBNAIL), new ImageLoader.ImageListener() {
                                            @Override
                                            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                                                if(response.getBitmap() != null) {
                                                    bufferScreenPreview.setImageBitmap(response.getBitmap());
                                                    bufferScreen.setBackground(Helper.createBlurredImageFromBitmap(response.getBitmap(), activity));
                                                }
                                            }

                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        });
//                                        bufferScreenPreview.setImageURI(Uri.parse(currentVideo.getString(ParseTables.Videos.VIDEO_THUMBNAIL)));

                                        bufferScreenTitle.setText(title);

                                        bufferStartTime = System.currentTimeMillis();

                                        CommentsFragment.setUpComments();
                                        url = currentVideo.getString(ParseTables.Videos.URL_M3U8);

                                        if (player != null) {
                                            player.updateRendererBuilder(getRendererBuilder());
                                            player.seekTo(0);
                                        }
                                        playerNeedsPrepare = true;
                                        preparePlayer();


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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    private void setVideoDetails() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvideoTitle.setText(title);
                tusername.setText(username);
                tusernameComments.setText(username);
                tlocation.setText(location);
                ttotalVotes.setText(upvotes);
                ttotalVotesComments.setText(upvotes);
                Uri uri = Uri.parse(avatar);
                tdraweeView.setImageURI(uri);
                tdraweeViewComments.setImageURI(uri);
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

    @Override
    public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
        boolean audioCapabilitiesChanged = !audioCapabilities.equals(this.audioCapabilities);
        if (player == null || audioCapabilitiesChanged) {
            this.audioCapabilities = audioCapabilities;
            releasePlayer();
            preparePlayer();
            Log.d(TAG, "AUDIO CAPABILITIES");
        } else if (player != null) {
            player.setBackgrounded(false);
        }
    }

    @Override
    public void onError(Exception e) {
        /*
        if (e instanceof UnsupportedDrmException) {
            // Special case DRM failures.
            UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
            int stringId = unsupportedDrmException.reason == UnsupportedDrmException.REASON_NO_DRM
                    ? R.string.drm_error_not_supported
                    : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                    ? R.string.drm_error_unsupported_scheme
                    : R.string.drm_error_unknown;
            Toast.makeText(activity, stringId, Toast.LENGTH_LONG).show();
        }*/
        e.printStackTrace();
        playerNeedsPrepare = true;
    }

    @Override
    public void onStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            currentVideo.put(ParseTables.Videos.PLAYED, true);
            currentVideo.saveInBackground();
            sendNextRequest();
            QueueFragment.updateCurrentlyPlaying();
        }
        String text = "playWhenReady=" + playWhenReady + ", playbackState=";
        switch(playbackState) {
            case ExoPlayer.STATE_BUFFERING:
                text += "buffering";
                break;
            case ExoPlayer.STATE_ENDED:
                text += "ended";
                break;
            case ExoPlayer.STATE_IDLE:
                text += "idle";
                break;
            case ExoPlayer.STATE_PREPARING:
                text += "preparing";
                break;
            case ExoPlayer.STATE_READY:
                removeBufferScreen();
                text += "ready";
                break;
            default:
                text += "unknown";
                break;
        }
        Log.d(TAG, "text = " + text);
    }

    @Override
    public void onVideoSizeChanged(int width, int height, float pixelWidthAspectRatio) {
//        shutterView.setVisibility(View.GONE);
//        surfaceView.setVideoWidthHeightRatio(
//                height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
        Log.d(TAG, "width = " + width + " height = " + height);
    }

    public DemoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(activity, "ExoPlayerDemo");
        return new HlsRendererBuilder(activity, userAgent, url, null, audioCapabilities);
    }

    private void preparePlayer() {
        if (url.equals(""))
            return;
        if (player == null) {
            player = new DemoPlayer(getRendererBuilder());
            player.addListener(this);
            player.setTextListener(null);
            player.setMetadataListener(null);
            player.seekTo(playerPosition);
            playerNeedsPrepare = true;

            eventLogger = new EventLogger();
            eventLogger.startSession();
            player.addListener(eventLogger);
            player.setInfoListener(eventLogger);
            player.setInternalErrorListener(eventLogger);
        }
        if (playerNeedsPrepare) {
            player.prepare();
            playerNeedsPrepare = false;
//            updateButtonVisibilities();
        }
        player.setSurface(surfaceView.getHolder().getSurface());
        player.setPlayWhenReady(false);
    }

    private void releasePlayer() {
        if (player != null) {
            playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
            eventLogger.endSession();
            eventLogger = null;
        }
    }

    private void removeBufferScreen() {
        try {
            Log.d(TAG, "Going to sleep");
            while (System.currentTimeMillis() - bufferStartTime < MIN_BUFFER_TIME_MILLIS) {
                Thread.sleep(100);
            }
            Log.d(TAG, "Waking up");

            player.setPlayWhenReady(true);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Helper.isKitkat()) {
                        TransitionManager.beginDelayedTransition(slidingUpPanelLayout);
                    }
                    bufferScreen.setVisibility(View.GONE);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        activity.unregisterReceiver(receiver);
        super.onPause();
        releasePlayer();
        audioCapabilitiesReceiver.unregister();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("intent.omerjerk");
        activity.registerReceiver(receiver, filter);
        audioCapabilitiesReceiver.register();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
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

    private void setPanelSlideListeners() {
        slidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                tvideoTitle.setAlpha(1-slideOffset);
                tusername.setAlpha(1-slideOffset);
                tlocation.setAlpha(1-slideOffset);
                tdraweeView.setAlpha(1-slideOffset);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                tvideoTitle.setAlpha(1);
                tusername.setAlpha(1);
                tlocation.setAlpha(1);
                tdraweeView.setAlpha(255);
            }

            @Override
            public void onPanelExpanded(View panel) {
                tvideoTitle.setAlpha(0);
                tusername.setAlpha(0);
                tlocation.setAlpha(0);
                tdraweeView.setAlpha(0);
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