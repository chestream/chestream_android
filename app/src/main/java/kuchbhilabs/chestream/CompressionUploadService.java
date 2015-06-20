package kuchbhilabs.chestream;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.File;
import java.io.FileInputStream;

public class CompressionUploadService extends Service {

    int id = 1;

    private static final File EXT_DIR = Environment.getExternalStorageDirectory();
    private static String INPUT_VIDEO;
    private static String OUTPUT_VIDEO = new File(EXT_DIR, "test_out.mp4").getPath();
    private final String storageConnectionString =
            "DefaultEndpointsProtocol=http;" + "AccountName=fo0;" +
                    "AccountKey=AT3WGE4H6+s0PtRiaDCFCKMf81P+lCj5IKvfgD26r0wQzGEHKX5B5Dvp5D/bM8sAcVYRZL+vp+J7kdLwibxPnw==";

    private static final String COMPRESS_CMD = "-y -i %s -strict " +
            "experimental -vcodec libx264 -preset ultrafast -crf 24 -acodec aac -ar 44100 -ac 2 " +
            "-b:a 96k -s 480x270 %s";

    private static final String TAG = "CHESTREAM";

    public CompressionUploadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        INPUT_VIDEO = intent.getStringExtra("path");

        Notification.Builder mBuilder = new Notification.Builder(this)
                .setContentTitle("Chestream")
                .setContentText("Compression in progress")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setProgress(0, 0, true);

        startForeground(id, mBuilder.build());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                loadFFmpeg();
                compress();

                return null;
            }
        }.execute();

        return START_NOT_STICKY;
    }

    private void loadFFmpeg() {
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
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
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
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
                            stopForeground(true);
                        }

                        @Override
                        public void onSuccess(String message) {
                            Log.d(TAG, "FFMPEG onSuccess " + message);
                            try {
                                // Retrieve storage account from connection-string.
                                CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

                                // Create the blob client.
                                CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                                // Retrieve reference to a previously created container.
                                CloudBlobContainer container = blobClient.getContainerReference("videos");

                                // Create or overwrite the blob with contents from a local file.
                                String timeStamp = System.currentTimeMillis()/1000 + "";
                                CloudBlockBlob blob = container.getBlockBlobReference("video_" + timeStamp + ".mp4");
                                File file = new File(INPUT_VIDEO);
                                blob.upload(new FileInputStream(file), file.length());
                            }
                            catch (Exception e)
                            {
                                // Output the stack trace.
                                e.printStackTrace();
                            }
                            stopForeground(true);
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
