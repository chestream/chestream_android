package kuchbhilabs.chestream;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by root on 20/6/15.
 */
public class ApplicationBase extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "M5tnZk2K6PdF82Ra8485bG2VQwPjpeZLeL96VLPj",
                "0Sg7WlkNmt0jkC6dOQ91qkOUbGBoyiCqIG8xqU7z");

        ParseUser.enableAutomaticUser();

        Log.d("OMERJERK", "Signing up for parse");
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    e.printStackTrace();
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }
}
