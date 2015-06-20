package kuchbhilabs.chestream;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    static final int REQUEST_VIDEO_CAPTURE = 1;

    Activity activity;

    private static final File EXT_DIR = Environment.getExternalStorageDirectory();
    private static final String INPUT_VIDEO = new File(EXT_DIR, "test_in.mp4").getPath();
    private static final String OUTPUT_VIDEO = new File(EXT_DIR, "test_out.mp4").getPath();

    private static final String COMPRESS_CMD = "-y -i %s -strict " +
            "experimental -vcodec libx264 -preset ultrafast -crf 24 -acodec aac -ar 44100 -ac 2 " +
            "-b:a 96k -s 320x240 -aspect 4:3 %s";

    private static final String TAG = "CHESTREAM";

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        activity = getActivity();

        loadFFmpeg();

        Button button = (Button) rootView.findViewById(R.id.button_compress);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                compress();
            }
        });
        return rootView;
    }

    public void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void loadFFmpeg() {
        FFmpeg ffmpeg = FFmpeg.getInstance(activity);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d(TAG, "FFMPEG onstart");
                }

                @Override
                public void onFailure() {
                    Log.d(TAG, "FFMPEG onFailure");
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG, "FFMPEG onSuccess");
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "FFMPEG onFinish");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
            e.printStackTrace();
        }
    }

    private void compress() {
        FFmpeg ffmpeg = FFmpeg.getInstance(activity);
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(String.format(COMPRESS_CMD, INPUT_VIDEO, OUTPUT_VIDEO),
                    new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.d(TAG, "FFMPEG onStart");
                }

                @Override
                public void onProgress(String message) {
                    Log.d(TAG, "FFMPEG onProgress " + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.e(TAG, "FFMPEG onFailure " + message);
                }

                @Override
                public void onSuccess(String message) {
                    Log.d(TAG, "FFMPEG onSuccess " + message);
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "FFMPEG onFinish");
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            e.printStackTrace();
        }

    }
}
