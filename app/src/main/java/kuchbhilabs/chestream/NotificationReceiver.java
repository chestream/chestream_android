package kuchbhilabs.chestream;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import kuchbhilabs.chestream.fragments.VideoFragment;

public class NotificationReceiver extends ParsePushBroadcastReceiver {

    private static final String TAG = "NotificationReceiver";
    public static final String ACTION_VOTE = "kuchbhilabs.chestream.votes";

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        JSONObject pushData = null;

        try {
            try {
                pushData = new JSONObject(intent.getStringExtra("com.parse.Data"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String message = pushData.getString("alert");
            JSONObject object;
            try {
                object = new JSONObject(message);
                if (object.getString("messageType").equals("votes")) {
                    Log.d(TAG, "Voting change notification received");
                    Intent broadcast = new Intent();
                    broadcast.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    broadcast.setAction(ACTION_VOTE);
                    context.sendBroadcast(broadcast);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            VideoFragment.commentReceived(message);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
