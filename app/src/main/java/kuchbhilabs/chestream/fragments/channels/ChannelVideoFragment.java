package kuchbhilabs.chestream.fragments.channels;

import android.app.Activity;
import android.app.Dialog;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.exoplayer.AspectRatioFrameLayout;
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

import java.util.ArrayList;
import java.util.List;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.exoplayer.DemoPlayer;
import kuchbhilabs.chestream.exoplayer.EventLogger;
import kuchbhilabs.chestream.exoplayer.HlsRendererBuilder;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.widgets.RippleBackground;

public class ChannelVideoFragment extends Fragment implements SurfaceHolder.Callback,
        DemoPlayer.Listener, AudioCapabilitiesReceiver.Listener {

    Dialog dialog;
    String url = "";
    String upvotes = "";
    String location = "";
    String title = "";
    String username = "";
    String avatar = "";
    public static ParseObject currentVideo;

    ParseUser currentParseUser;

    Activity activity;

    public static AspectRatioFrameLayout videoFrame;
    SurfaceView surfaceView;
    SurfaceHolder holder;


    private static final String TAG = "VideoFragment";

    private static final String TEST_URL = "http://128.199.128.227/chestream_raw/video_1434859043/video_1434859043.mp4";
    private static final String NEXT_URL = "http://104.131.207.33:8800/";

    private DemoPlayer player;
    private AudioCapabilities audioCapabilities;
    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    long playerPosition = 0;
    boolean playerNeedsPrepare = true;
    private EventLogger eventLogger;
    private HandlerThread handlerThread;
    private ExoPlayerHandler exoPlayerHandler;


    FrameLayout dragCommentsView;
    FrameLayout videoBackground;
    RippleBackground rippleBackground;

    List<String> videoIDS;
    int videoPosition =0;

    int[] patternImages = {R.drawable.pattern1, R.drawable.pattern2,R.drawable.pattern3,R.drawable.pattern4,R.drawable.pattern5,R.drawable.pattern6,R.drawable.pattern7,R.drawable.pattern8};

    public static ChannelVideoFragment newInstance(List<String> videoIds) {
        ChannelVideoFragment fragment = new ChannelVideoFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("ids",new ArrayList<String>(videoIds));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_channel, null);

        activity = getActivity();

        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(activity, this);

        videoIDS=getArguments().getStringArrayList("ids");


        dragCommentsView=(FrameLayout) rootView.findViewById(R.id.dragCommentsView);
        videoBackground=(FrameLayout) rootView.findViewById(R.id.videoBackground);
        rippleBackground=(RippleBackground) rootView.findViewById(R.id.ripple);


        handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();

        videoFrame = (AspectRatioFrameLayout) rootView.findViewById(R.id.video_frame);
        surfaceView = (SurfaceView) rootView.findViewById(R.id.main_surface_view);
        holder = surfaceView.getHolder();
        holder.addCallback(this);

        exoPlayerHandler = new ExoPlayerHandler(handlerThread.getLooper());

        sendNextRequest();

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
                            final String videoId = videoIDS.get(videoPosition);
                            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseTables.Videos._NAME);
                            query.whereEqualTo("objectId", videoId);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (list != null) {
                                        currentVideo = list.get(0);
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

                                                    }
                                                });



                                        url = currentVideo.getString(ParseTables.Videos.URL_M3U8);

                                        exoPlayerHandler.sendMessage(exoPlayerHandler.obtainMessage(
                                                ExoPlayerHandler.MSG_SET_RENDERER_BUILDER));
                                        exoPlayerHandler.sendMessage(exoPlayerHandler.obtainMessage(
                                                ExoPlayerHandler.MSG_PREPARE));
                                    } else {
                                        Toast.makeText(activity, "Shit Happened!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

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
                    setRendererBuilder();
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    private void setRendererBuilder() {
        if (player != null) {
            player.updateRendererBuilder(getRendererBuilder());
            player.seekTo(0);
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
            if (videoPosition<videoIDS.size()) {
                videoPosition += 1;
                sendNextRequest();
            }
//            QueueFragment.updateCurrentlyPlaying();
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
                if (player != null) {
                    player.setPlayWhenReady(true);
                }
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
//        Log.d(TAG, "width = " + width + " height = " + height);
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                videoFrame.setAspectRatio(
//                        height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
//            }
//        });
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



    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
        audioCapabilitiesReceiver.unregister();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("intent.omerjerk");
        audioCapabilitiesReceiver.register();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }


}