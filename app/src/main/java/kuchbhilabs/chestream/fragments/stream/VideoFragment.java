package kuchbhilabs.chestream.fragments.stream;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.RandomTransitionGenerator;
import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.util.Util;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kuchbhilabs.chestream.ApplicationBase;
import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.VolleySingleton;
import kuchbhilabs.chestream.comments.CommentsFragment;
import kuchbhilabs.chestream.exoplayer.DemoPlayer;
import kuchbhilabs.chestream.exoplayer.EventLogger;
import kuchbhilabs.chestream.exoplayer.HlsRendererBuilder;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.fragments.channels.ChannelFragment;
import kuchbhilabs.chestream.fragments.channels.ChannelModel;
import kuchbhilabs.chestream.fragments.queue.QueueFragment;
import kuchbhilabs.chestream.helpers.Helper;
import kuchbhilabs.chestream.helpers.Utilities;
import kuchbhilabs.chestream.parse.ParseVideo;
import kuchbhilabs.chestream.slidinguppanel.SlidingUpPanelLayout;
import kuchbhilabs.chestream.widgets.FrameLayoutWithHole;
import kuchbhilabs.chestream.widgets.RippleBackground;

public class VideoFragment extends Fragment implements SurfaceHolder.Callback,
        DemoPlayer.Listener, AudioCapabilitiesReceiver.Listener, TextureView.SurfaceTextureListener,
        View.OnTouchListener {

    Dialog dialog;
    String url = "";
    String upvotes = "";
    String location = "";
    String title = "";
    String username = "";
    String avatar = "";
    public static ParseObject currentVideo;

    Button goToChannel;

    ParseUser currentParseUser;

    OtherUserProfileAdapter adapter;
    RecyclerView recyclerView;
    LinearLayout userLayout;

    public static SimpleDraweeView gifView;

    private SpringSystem mSpringSystem1,mSpringSystem2,mSpringSystem3;
    private Spring mSpring1,mSpring2,mSpring3;
    private double TENSION = 300;
    private double DAMPER = 15; //friction

    ParseUser pUser;
    TextView tvideoTitle;
    TextView tlocation;
    TextView tusername,tusernameComments;
    TextView ttotalVotes,ttotalVotesComments;
    SimpleDraweeView tdraweeView,tdraweeViewComments;
    private SimpleDraweeView bufferScreenProfile;
    private ImageView bufferScreenPreview;
    private TextView bufferScreenTitle,bufferScreenUsername;
    private KenBurnsView patternView;

    Activity activity;
    public static SlidingUpPanelLayout slidingUpPanelLayout; //slidingUpPanelLayout2;

    public static AspectRatioFrameLayout videoFrame;
    SurfaceView surfaceView;
    SurfaceHolder holder;
    private static TextView commentFloating;
    private static TimerCommentText timerCommentText;
    private FrameLayout bufferScreen;
    private Bitmap previewBitmap = null;

    private static final String TAG = "VideoFragment";

    private static final String TEST_URL = "http://128.199.128.227/chestream_raw/video_1434859043/video_1434859043.mp4";
    private static final String NEXT_URL = "http://104.131.207.33:8800/";

    private CommentsBroadcastReceiver receiver;

    private int i = 0;

//    View loadingLayout;
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
    private HandlerThread handlerThread;
    private ExoPlayerHandler exoPlayerHandler;

    private TextureView cameraPreview;
    private Camera camera;
    private CameraHandler cameraHandler;

    private boolean fingerDown = false;
    private Handler uiHandler;
    float fingerDownX, fingerDownY;

    FrameLayout dragCommentsView;
    FrameLayout videoBackground;
    RippleBackground rippleBackground;

    int[] patternImages = {R.drawable.pattern1, R.drawable.pattern2,R.drawable.pattern3,R.drawable.pattern4,R.drawable.pattern5,R.drawable.pattern6,R.drawable.pattern7,R.drawable.pattern8};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_video, null);

        activity = getActivity();
        receiver = new CommentsBroadcastReceiver();
        uiHandler = new Handler();

        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(activity, this);

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(R.layout.fragment_profile);
        goToChannel = (Button)rootView.findViewById(R.id.goTo_channel_button);

        tvideoTitle = (TextView) rootView.findViewById(R.id.video_title);
        tlocation = (TextView) rootView.findViewById(R.id.video_location);
        tusername = (TextView) rootView.findViewById(R.id.username);
        tusernameComments = (TextView) rootView.findViewById(R.id.username_comments);
        ttotalVotes = (TextView) rootView.findViewById(R.id.video_score);
        ttotalVotesComments = (TextView) rootView.findViewById(R.id.video_score_comments);
        tdraweeView = (SimpleDraweeView) rootView.findViewById(R.id.profile_picture);
        tdraweeViewComments = (SimpleDraweeView) rootView.findViewById(R.id.profile_picture_comments);
//        loadingLayout = rootView.findViewById(R.id.loading_layout);
        bufferScreen = (FrameLayout) rootView.findViewById(R.id.buffer_screen);
        bufferScreenPreview = (ImageView) rootView.findViewById(R.id.buffer_screen_preview);
        bufferScreenTitle = (TextView) rootView.findViewById(R.id.buffer_screen_video_title);
        bufferScreenUsername = (TextView) rootView.findViewById(R.id.buffer_screen_username);
        bufferScreenProfile = (SimpleDraweeView) rootView.findViewById(R.id.buffer_screen_profile_picture);
        patternView=(KenBurnsView) rootView.findViewById(R.id.patternView);
        dragCommentsView=(FrameLayout) rootView.findViewById(R.id.dragCommentsView);
        videoBackground=(FrameLayout) rootView.findViewById(R.id.videoBackground);
        rippleBackground=(RippleBackground) rootView.findViewById(R.id.ripple);

        commentsCount=(TextView) rootView.findViewById(R.id.commentsCount);
//        loadingProgress=(LoadingProgress) rootView.findViewById(R.id.loading_progress);
        loadingFrame=(View) rootView.findViewById(R.id.loading_frame);
        dividerView=rootView.findViewById(R.id.dividerView);


        cameraPreview = (TextureView) rootView.findViewById(R.id.camera_preview);
        cameraPreview.setSurfaceTextureListener(this);
        handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
        cameraHandler = new CameraHandler(handlerThread.getLooper());
        cameraHandler.sendMessage(cameraHandler.obtainMessage(CameraHandler.MSG_INITIALIZE));

        setupSpringSystem();

        sendNextRequest();

        slidingUpPanelLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setOverlayed(true);
        slidingUpPanelLayout.setEnableDragViewTouchEvents(true);

        setPanelSlideListeners();

//        commentFloating = (TextView) rootView.findViewById(R.id.commentText);

        ChannelModel channelGlobal =  new ChannelModel("O4xopsCg4Y","","global","GLOBAL","Other",0,null);
        CommentsFragment commentsFragment = new CommentsFragment().newInstance(channelGlobal);
        getChildFragmentManager().beginTransaction().add(R.id.comments, commentsFragment).commit();

        videoFrame = (AspectRatioFrameLayout) rootView.findViewById(R.id.video_frame);
        surfaceView = (SurfaceView) rootView.findViewById(R.id.main_surface_view);
        holder = surfaceView.getHolder();
        holder.addCallback(this);

        exoPlayerHandler = new ExoPlayerHandler(handlerThread.getLooper());

        goToChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("yo","clicked channel");
                Fragment nextFrag= new ChannelFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.videoBackground, nextFrag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        userLayout = (LinearLayout)rootView.findViewById(R.id.user_details);
        userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Tracker mTracker;
                ApplicationBase application = (ApplicationBase) activity.getApplication();
                mTracker = application.getDefaultTracker();
                mTracker.setScreenName("ProfileFragment");
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("UploaderProfile")
                        .setAction("preview")
                        .setLabel(Utilities.getUserEmail(activity))
                        .build());

                final FrameLayout header;
                TextView username ;
                ImageView profile;

                profile=(ImageView) dialog.findViewById(R.id.profile_picture);
                header=(FrameLayout) dialog.findViewById(R.id.header);
                recyclerView=(RecyclerView) dialog.findViewById(R.id.recycler_view);
                username=(TextView) dialog.findViewById(R.id.username);
                gifView = (SimpleDraweeView) dialog.findViewById(R.id.preview_gif);

                    username.setText(tusername.getText().toString());
                ImageLoader.getInstance().displayImage(avatar, profile,
                        new DisplayImageOptions.Builder().cacheInMemory(true)
                                .cacheOnDisk(true)
                                .resetViewBeforeLoading(true)
                                .displayer(new FadeInBitmapDisplayer(400))
                                .build(), new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                header.setBackground(Helper.createBlurredImageFromBitmap(loadedImage, getActivity(),8));
                            }
                        });
//                    profile.setImageURI(Uri.parse(avatar));

                    ParseQuery<ParseVideo> query = ParseQuery.getQuery(ParseVideo.class);
                    query.orderByDescending(ParseTables.Videos.UPVOTE);
                    query.whereEqualTo(ParseTables.Videos.COMPILED, true);
                    query.whereEqualTo("user", currentParseUser);
                    query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
                    query.findInBackground(new FindCallback<ParseVideo>() {
                        @Override
                        public void done(List<ParseVideo> parseObjects, ParseException e) {
                            if(parseObjects.isEmpty())
                            {
                                Log.d(TAG, "Empty");
                            }
                            else {
                                adapter = new OtherUserProfileAdapter(getActivity(), new ArrayList<ParseVideo>());
                                adapter.updateDataSet(parseObjects);
                                adapter.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    });

                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setHasFixedSize(true);

                dialog.show();
            }
        });
        videoFrame.setOnTouchListener(this);
        dragCommentsView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                videoFrame.dispatchTouchEvent(motionEvent);
                return true;
            }

        });

//        setupOverlay();


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
        setUpBufferScreen();
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
                            final long playEpoch = Long.parseLong(responseObject.getString("play_epoch"));
                            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseTables.Videos._NAME);
                            query.whereEqualTo("objectId", videoId);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (list != null) {
                                        currentVideo = list.get(0);

//                                        loadingLayout.setVisibility(View.GONE);
//                                        if (Helper.isKitkat()) {
//                                            TransitionManager.beginDelayedTransition(slidingUpPanelLayout);
//                                        }

                                        dialog.dismiss();
                                        upvotes = currentVideo.getString(ParseTables.Videos.UPVOTE);
                                        location = currentVideo.getString(ParseTables.Videos.LOCATION);
                                        title = currentVideo.getString(ParseTables.Videos.TITLE);
                                        currentVideo.getParseUser(ParseTables.Videos.USER)
                                                .fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                                                    @Override
                                                    public void done(ParseUser parseObject, ParseException e) {
                                                        currentParseUser = parseObject;
                                                        username = parseObject.getUsername();
                                                        avatar = parseObject.getString(ParseTables.Users.AVATAR);
                                                        setVideoDetails();

//                                                        if (Helper.isKitkat()) {
//                                                            TransitionManager.beginDelayedTransition(slidingUpPanelLayout);
//                                                        }

                                                        bufferScreenProfile.setImageURI(Uri.parse(avatar));
                                                        bufferScreenUsername.setText(username);
                                                    }
                                                });
                                        ImageLoader.getInstance().displayImage(currentVideo.getString(ParseTables.Videos.VIDEO_THUMBNAIL), bufferScreenPreview,
                                                new DisplayImageOptions.Builder().cacheInMemory(true)
                                                        .cacheOnDisk(true)
                                                        .resetViewBeforeLoading(true)
                                                        .displayer(new FadeInBitmapDisplayer(400))
                                                        .build(),new SimpleImageLoadingListener() {
                                                    @Override
                                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                        mSpring2.setEndValue(1);
                                                        previewBitmap=loadedImage;
                                                    }
                                                });

                                        bufferScreenTitle.setText(title);
                                        mSpring1.setEndValue(1);

//                                        float mOrigY = bufferScreenTitle.getY();
//                                        mSpring.setEndValue(mOrigY - 500f);

                                        bufferStartTime = System.currentTimeMillis();

//                                        downloadBackgroundBitmap(currentVideo.getString(
//                                                ParseTables.Videos.VIDEO_THUMBNAIL));

//                                        CommentsFragment.setUpComments();
                                        url = currentVideo.getString(ParseTables.Videos.URL_M3U8);

                                        exoPlayerHandler.sendMessage(exoPlayerHandler.obtainMessage(
                                                ExoPlayerHandler.MSG_SET_RENDERER_BUILDER, playEpoch));
                                        exoPlayerHandler.sendMessage(exoPlayerHandler.obtainMessage(
                                                ExoPlayerHandler.MSG_PREPARE));
                                    } else {
                                        Toast.makeText(activity, "Shit Happened!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (Exception e) {
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
            }
        });
    }

    private void setUpBufferScreen(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bufferScreen.setVisibility(View.VISIBLE);
                Random random = new Random();
                int rndInt = random.nextInt(patternImages.length);
                if (getActivity()!=null) {
                    BitmapDrawable pattern = new BitmapDrawable(BitmapFactory.decodeResource(getActivity().getResources(), patternImages[rndInt]));
                    pattern.setTileModeX(Shader.TileMode.REPEAT);
                    pattern.setTileModeY(Shader.TileMode.REPEAT);

                    patternView.setImageBitmap(pattern.getBitmap());
                }

                Interpolator interpolator=new LinearInterpolator();
                RandomTransitionGenerator
                        generator = new RandomTransitionGenerator(4000, interpolator);
                patternView.setTransitionGenerator(generator);

                rippleBackground.startRippleAnimation();
            }
        });

    }

    private void setupSpringSystem(){

        mSpringSystem1 = SpringSystem.create();

        mSpring1 = mSpringSystem1.createSpring();
        mSpring1.addListener(new SimpleSpringListener(){

            @Override
            public void onSpringUpdate(Spring spring) {
               renderTitles();
            }
        });

        SpringConfig config = new SpringConfig(TENSION, DAMPER);
        mSpring1.setSpringConfig(config);

        mSpringSystem2 = SpringSystem.create();

        mSpring2 = mSpringSystem2.createSpring();
        mSpring2.addListener(new SimpleSpringListener(){

            @Override
            public void onSpringUpdate(Spring spring) {
                renderPreviewImage();
            }
        });

        SpringConfig config2 = new SpringConfig(TENSION, DAMPER);
        mSpring2.setSpringConfig(config2);

        mSpringSystem3 = SpringSystem.create();

        mSpring3 = mSpringSystem3.createSpring();
        mSpring3.addListener(new SimpleSpringListener(){

            @Override
            public void onSpringUpdate(Spring spring) {

            }
        });

        SpringConfig config3 = new SpringConfig(TENSION, DAMPER);
        mSpring3.setSpringConfig(config3);

        renderTitles();
        renderPreviewImage();
    }

    private void renderTitles() {
        double value = mSpring1.getCurrentValue();

//        float barPosition2 =
//                (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, bufferScreenUsername.getHeight(), 0);
//        bufferScreenUsername.setTranslationY(barPosition2);
        float barPosition =
                (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, bufferScreenTitle.getHeight(), 0);
        bufferScreenTitle.setTranslationY(barPosition);

    }

    private void renderPreviewImage(){
        double value = mSpring2.getCurrentValue();

        float barPosition =
                (float) SpringUtil.mapValueFromRangeToRange(value, 0, 1, bufferScreenPreview.getHeight(), 0);
        bufferScreenPreview.setTranslationY(barPosition);
    }

    private void downloadBackgroundBitmap(String url) {
        VolleySingleton.getInstance(activity).getImageLoader().get(url,
                new com.android.volley.toolbox.ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer response, boolean isImmediate) {
                        previewBitmap = response.getBitmap();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "TEXTURE AVAILABLE NOW");
        try {
            camera.setPreviewTexture(surface);
            Log.d(TAG, "width = " + width + " height = " + height);
            cameraHandler.sendMessage(cameraHandler.obtainMessage(CameraHandler.MSG_START_PREVIEW));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private class ExoPlayerHandler extends Handler {
        public static final int MSG_PREPARE = 0;
        public static final int MSG_RELEASE = 1;
        public static final int MSG_SET_RENDERER_BUILDER = 2;

        public ExoPlayerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PREPARE:
                    preparePlayer();
                    break;
                case MSG_RELEASE:
                    releasePlayer();
                    break;
                case MSG_SET_RENDERER_BUILDER:
                    long playEpoch = (long)msg.obj;
                    setRendererBuilder(playEpoch);
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    private void setRendererBuilder(long playEpoch) {
        if (player != null) {
            long currentEpochSec = System.currentTimeMillis()/1000;
            player.updateRendererBuilder(getRendererBuilder());
            if (currentEpochSec - playEpoch > 5) {
                player.seekTo(currentEpochSec - playEpoch);
            } else {
                player.seekTo(0);
            }
        }
        playerNeedsPrepare = true;
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
            uiHandler.removeCallbacksAndMessages(null);
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
    public void onVideoSizeChanged(final int width, final int height, final float pixelWidthAspectRatio) {
//        shutterView.setVisibility(View.GONE);
//        surfaceView.setVideoWidthHeightRatio(
//                height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
        Log.d(TAG, "width = " + width + " height = " + height);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                videoFrame.setAspectRatio(
                        height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
            }
        });
    }

    public DemoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(activity, "ExoPlayerDemo");
        return new HlsRendererBuilder(activity, userAgent, url, audioCapabilities);
    }

    private void preparePlayer() {
        if (url.equals(""))
            return;
        if (player == null) {
            player = new DemoPlayer(getRendererBuilder());
            player.addListener(this);
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

            if (player != null) {
                player.setPlayWhenReady(true);
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    hideWithReveal();
                    if (previewBitmap != null) {
                        //TODO: Blur the image and apply to the background
                        videoBackground.setBackground(Helper.createBlurredImageFromBitmap(previewBitmap,activity,12));
                    } else {
                        Log.d(TAG, "PREVIEW BITMAPP IS NULL");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (previewBitmap != null)
                                    videoBackground.setBackground(Helper.createBlurredImageFromBitmap(previewBitmap,activity,12));
                                else
                                    Log.d(TAG, "This shit is still null");
                            }
                        }, 3000);
                    }
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void hideWithReveal(){
        rippleBackground.stopRippleAnimation();
        if (Helper.isLollipop()) {
            int cx = (bufferScreen.getLeft() + bufferScreen.getRight()) / 2;
            int cy = (bufferScreen.getBottom());


            int initialRadius = bufferScreen.getWidth();

            Point point=Helper.getLocationInView(bufferScreen,tdraweeView);

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(bufferScreen, point.x, point.y, initialRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    bufferScreen.setVisibility(View.GONE);
                    if (activity!=null)
                    bufferScreenProfile.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_launcher));
                    bufferScreenPreview.setImageDrawable(null);
                }
            });
            anim.setDuration(500);
            anim.start();
        } else {
            if (Helper.isKitkat()){
                TransitionManager.beginDelayedTransition(bufferScreen);
            }
            bufferScreen.setVisibility(View.GONE);
            if (activity!=null)
            bufferScreenProfile.setImageDrawable(activity.getResources().getDrawable(R.mipmap.ic_launcher));
            bufferScreenPreview.setImageDrawable(null);
        }
    }

    @Override
    public void onPause() {
        activity.unregisterReceiver(receiver);
        super.onPause();
        releasePlayer();
        finalizeCamera();
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
                tvideoTitle.setAlpha(1 - slideOffset);
                tusername.setAlpha(1 - slideOffset);
                tlocation.setAlpha(1 - slideOffset);
                tdraweeView.setAlpha(1 - slideOffset);
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

    private void initializeCamera() {
        if (camera == null) {
            try {
                camera = Camera.open(1);
                Log.d(TAG, "OPENING THE CAMERA");
                camera.setDisplayOrientation(90);
                Camera.Parameters parameters = camera.getParameters();
//                parameters.setPreviewSize(320, 240);
                setPreviewSize(parameters, 320);
                camera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
                camera = null;
            }
        }
    }

    private void setPreviewSize(Camera.Parameters parameters, int expectedWidth) {
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Log.d(TAG, "======Preview Sizes======");
        int minDiff = 100000000;
        Camera.Size minSize = null;
        for (Camera.Size size : sizes) {
            Log.d(TAG, size.width + "x" + size.height);
            if (minDiff > Math.abs(expectedWidth - size.width)) {
                minDiff = Math.abs(expectedWidth - size.width);
                minSize = size;
            }
        }
        Log.d(TAG, "choosing " + minSize.width + "x" + minSize.height);
        parameters.setPreviewSize(minSize.width, minSize.height);

        sizes = parameters.getSupportedPictureSizes();
        minDiff = 100000000;
        Log.d(TAG, "=====PICTURE SIZES=====");
        for (Camera.Size size : sizes) {
            Log.d(TAG, size.width + "x" + size.height);
            if (minDiff > Math.abs(expectedWidth - size.width)) {
                minDiff = Math.abs(expectedWidth - size.width);
                minSize = size;
            }
        }
        Log.d(TAG, "choosing " + minSize.width + "x" + minSize.height);
        parameters.setPictureSize(minSize.width, minSize.height);
        parameters.setRotation(270);
    }

    private void finalizeCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    public class CameraHandler extends Handler {

        public static final int MSG_INITIALIZE = 0;
        public static final int MSG_FINALIZE = 1;
        public static final int MSG_START_PREVIEW = 2;

        public CameraHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INITIALIZE:
                    initializeCamera();
                    break;
                case MSG_START_PREVIEW:
                    if (camera != null) {
                        camera.startPreview();
                        Log.d(TAG, "PREVIEW STARTED");
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Wrong option.");
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        Log.d(TAG, "onTouch");
        if (currentVideo!=null) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                fingerDownX = e.getX();
                fingerDownY = e.getY();
                fingerDown = true;
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        while (camera == null) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                        uiHandler.postDelayed(startPreview, 1000);
                    }
                });
                return true;
            }
            if (e.getAction() == MotionEvent.ACTION_UP) {
                fingerDown = false;
                Log.d(TAG, "Finger up");
                if (camera != null) {
                    camera.stopPreview();
                }
                cameraPreview.setVisibility(View.GONE);
                uiHandler.removeCallbacksAndMessages(null);
            }
            if (e.getAction() == MotionEvent.ACTION_MOVE) {
                //TODO: we're not receiving ACTION_MOVE. Move this logic somewhere else
                Log.d(TAG, "gap x = " + Math.abs(e.getX() - fingerDownX));
                Log.d(TAG, "gap y = " + Math.abs(e.getY() - fingerDownY));
                Log.d(TAG,String.valueOf(e.getY())+" , "+String.valueOf(fingerDownY));
                if (Math.abs(e.getX() - fingerDownX) > 20 || Math.abs(e.getY() - fingerDownY) > 20) {
                    //It's a swipe. Fall back.
                    uiHandler.removeCallbacksAndMessages(null);
                }
                return true;
            }
        }
        return false;
    }

    private Runnable startPreview = new Runnable(){
        @Override
        public void run() {
            camera.startPreview();
            cameraPreview.setVisibility(View.VISIBLE);
            Toast.makeText(activity, "Capturing the photo in 3 secs", Toast.LENGTH_SHORT).show();
            uiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    camera.takePicture(null, null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(final byte[] data, Camera camera) {
                            VideoFragment.this.onPictureTaken(data, camera);
                        }
                    });
                }
            }, 3000);
        }
    };

    private void onPictureTaken(final byte[] data, Camera camera) {
        Log.d(TAG, "Number of pixels = " + data.length);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                uploadImageToParse(data);
            }
        });
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "Uploading photo!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageToParse(byte[] image) {
        ParseFile imageFile = new ParseFile("image.jpg", image);
        imageFile.saveInBackground();
        ParseObject comment = new ParseObject(ParseTables.Comments._NAME);
        comment.put(ParseTables.Comments.IMAGE, imageFile);
        comment.put(ParseTables.Comments.USER, ParseUser.getCurrentUser());
        comment.put(ParseTables.Comments.TEXT, "");
        comment.put(ParseTables.Comments.VIDEO, currentVideo);
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ParsePush push = new ParsePush();
                JSONObject message = new JSONObject();
                try {
                    message.put("messageType", "comment");
                    push.setMessage(message.toString());
                    push.sendInBackground();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        Log.d(TAG, "position = " + position + " positionOffset = " + positionOffset);
        if (positionOffset > 0.001) {
            fingerDown = false;
            uiHandler.removeCallbacksAndMessages(null);
        }
    }

    private void setupOverlay(){
        FrameLayoutWithHole childLayout = new FrameLayoutWithHole(getActivity(),tdraweeView);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        ((ViewGroup) getActivity().getWindow().getDecorView()).addView(childLayout, params);
    }
}