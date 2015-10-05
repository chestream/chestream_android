package kuchbhilabs.chestream.fragments.channels.NonSynchronous;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.exoplayer.AspectRatioFrameLayout;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.util.Util;

import java.util.ArrayList;
import java.util.List;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.activities.FullscreenPlayerActivity;
import kuchbhilabs.chestream.exoplayer.DemoPlayer;
import kuchbhilabs.chestream.exoplayer.EventLogger;
import kuchbhilabs.chestream.exoplayer.HlsRendererBuilder;

public class ChannelVideoFragmentNonSynchronousWithoutExo extends Fragment  {

    static  long playerPosition = 0;
    boolean playerNeedsPrepare = true;
    ImageView fullscreen;

    VideoView videoView;
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

        View rootView = inflater.inflate(R.layout.fragmentvideochannelwithoutexo, null);

        Toast.makeText(getActivity(), "Click on a video to play !", Toast.LENGTH_LONG).show();

        videoView = (VideoView) rootView.findViewById(R.id.dialogVV);
//        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//            }
//        });
;

        fullscreen=(ImageView) rootView.findViewById(R.id.fullscreen);


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


    public static void playVideo(String url){
        staticUrlrl = url;

        Log.d("urlrr", url);
        playerPosition = 0;

      new ChannelVideoFragmentNonSynchronousWithoutExo().play2(url);

    }

    public void play2(String url){
        videoView.stopPlayback();
        videoView.suspend();


        MediaController mediaController = new MediaController(getActivity());
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse(url));
        videoView.requestFocus();
        videoView.start();
    }
}