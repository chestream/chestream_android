package kuchbhilabs.chestream;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class CompressionUploadService extends Service {

    private static final File EXT_DIR = Environment.getExternalStorageDirectory();
    private static final String COMPRESS_CMD = "-y -i %s -strict " +
            "experimental -vcodec libx264 -preset ultrafast -crf 24 -acodec aac -ar 44100 -ac 2 " +
            "-b:a 96k -s 480x270 %s";
    private static final String TAG = "CHESTREAM";
    private static String INPUT_VIDEO;
    private static String OUTPUT_VIDEO = new File(EXT_DIR, "test_out.mp4").getPath();
    private final String storageConnectionString =
            "DefaultEndpointsProtocol=http;" + "AccountName=fo0;" +
                    "AccountKey=AT3WGE4H6+s0PtRiaDCFCKMf81P+lCj5IKvfgD26r0wQzGEHKX5B5Dvp5D/bM8sAcVYRZL+vp+J7kdLwibxPnw==";
    int id = 1;
    private String videoName;

    Notification.Builder mBuilder;
    NotificationManager mNotificationManager;

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

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new Notification.Builder(this)
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
                            mBuilder.setContentTitle("Uploading in progress");
                            mNotificationManager.notify(id, mBuilder.build());
                            Log.d(TAG, "FFMPEG onSuccess " + message);
                            new AsyncTask<Void, Void, Void>() {

                                @Override
                                protected Void doInBackground(Void... voids) {
                                    try {
                                        // Retrieve storage account from connection-string.
                                        CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

                                        // Create the blob client.
                                        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                                        // Retrieve reference to a previously created container.
                                        CloudBlobContainer container = blobClient.getContainerReference("videos");

                                        // Create or overwrite the blob with contents from a local file.
                                        String timeStamp = System.currentTimeMillis() / 1000 + "";
                                        videoName = "video_" + timeStamp;
                                        CloudBlockBlob blob = container.getBlockBlobReference(videoName + ".mp4");
                                        File file = new File(INPUT_VIDEO);
                                        blob.upload(new FileInputStream(file), file.length());
                                    } catch (Exception exception) {
                                        exception.printStackTrace();
                                    }
                                    return null;
                                }
                            }.execute();
                            StringRequest request = new StringRequest(Request.Method.POST,
                                    "https://api-eu.clusterpoint.com/1104/chestream.json", new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, response);
                                    stopForeground(true);
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace();
                                }
                            }) {

                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map headers = new HashMap();
                                    headers.put("Authorization", ApplicationBase.basicAuth);
                                    return headers;
                                }

                                @Override
                                public byte[] getBody() throws AuthFailureError {
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("id", videoName);
                                        jsonObject.put("user_location", "India");
                                        jsonObject.put("video_title", "Sexy video");
                                        jsonObject.put("video_url", "https://fo0.blob.core.windows.net/videos/" + videoName + ".mp4");
                                        jsonObject.put("video_updates", 0);
                                        jsonObject.put("user_name", "Prempal");
                                        jsonObject.put("video_played", "False");
                                        jsonObject.put("user_avatar", "http://www.loanstreet.in/loanstreet-b2c-theme/img/avatar-blank.jpg");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d(TAG, jsonObject.toString());
                                    return jsonObject.toString().getBytes();
                                }
                            };
                            request.setShouldCache(false);
                            VolleySingleton.getInstance(getApplicationContext()).getRequestQueue().add(request);

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
