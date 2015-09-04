package kuchbhilabs.chestream.fragments.channels.NonSynchronous;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
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

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.activities.FullscreenPlayerActivity;
import kuchbhilabs.chestream.exoplayer.DemoPlayer;
import kuchbhilabs.chestream.exoplayer.EventLogger;
import kuchbhilabs.chestream.exoplayer.HlsRendererBuilder;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.widgets.RippleBackground;

public class ChannelVideoFragmentNonSynchronous extends Fragment implements SurfaceHolder.Callback,
        DemoPlayer.Listener, AudioCapabilitiesReceiver.Listener {


    public static AspectRatioFrameLayout videoFrame;
    SurfaceView surfaceView;
    SurfaceHolder holder;

    private static  DemoPlayer player;
    private AudioCapabilities audioCapabilities;
    private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
    static  long playerPosition = 0;
    boolean playerNeedsPrepare = true;
    private static  EventLogger eventLogger;
    private static HandlerThread handlerThread;
    private static  ExoPlayerHandler exoPlayerHandler;
    ImageView fullscreen;

    static  String staticUrlrl = "";
//    long position;


    public static ChannelVideoFragmentNonSynchronous newInstance(List<String> videoIds) {
        ChannelVideoFragmentNonSynchronous fragment = new ChannelVideoFragmentNonSynchronous();
        Bundle args = new Bundle();
        args.putStringArrayList("ids",new ArrayList<String>(videoIds));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_channel, null);

        audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getActivity(), this);

        Toast.makeText(getActivity(),"Click on a video to play !", Toast.LENGTH_LONG).show();

        videoFrame = (AspectRatioFrameLayout) rootView.findViewById(R.id.video_frame);
        surfaceView = (SurfaceView) rootView.findViewById(R.id.main_surface_view);
        fullscreen=(ImageView) rootView.findViewById(R.id.fullscreen);
        holder = surfaceView.getHolder();
        holder.addCallback(this);


        handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
        exoPlayerHandler = new ExoPlayerHandler(handlerThread.getLooper());

        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FullscreenPlayerActivity.class);
                intent.putExtra("url", staticUrlrl);
                if (player != null)
                    intent.putExtra("position", player.getCurrentPosition());
                else intent.putExtra("position", 0);
                startActivity(intent);
            }
        });

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
//            currentVideo.put(ParseTables.Videos.PLAYED, true);
//            currentVideo.saveInBackground();
//            if (videoPosition<videoIDS.size()) {
//                videoPosition += 1;
//                sendNextRequest();
//            }
//            QueueFragment.updateCurrentlyPlaying();
//            finish();
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
        String userAgent = Util.getUserAgent(getActivity(), "ExoPlayerDemo");
        return new HlsRendererBuilder(getActivity(), userAgent, staticUrlrl, audioCapabilities);
    }

    private void preparePlayer() {
        if (staticUrlrl.equals(""))
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

    private static void releasePlayer() {
        if (player != null) {
            playerPosition = 0;
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


    public static void playVideo(String url){
        staticUrlrl = url;

        playerPosition = 0;
        exoPlayerHandler.sendMessage(exoPlayerHandler.obtainMessage(
                ExoPlayerHandler.MSG_SET_RENDERER_BUILDER));
        exoPlayerHandler.sendMessage(exoPlayerHandler.obtainMessage(
                ExoPlayerHandler.MSG_PREPARE));

    }
}