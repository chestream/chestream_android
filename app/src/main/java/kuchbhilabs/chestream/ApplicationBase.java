package kuchbhilabs.chestream;

import android.app.Application;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by root on 20/6/15.
 */
public class ApplicationBase extends Application {

    public static String userpass = "stomatrix@gmail.com" + ":" + "sauravclusterpoint";
    public static String basicAuth = "Basic "
            + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());

        Parse.initialize(this, "M5tnZk2K6PdF82Ra8485bG2VQwPjpeZLeL96VLPj",
                "0Sg7WlkNmt0jkC6dOQ91qkOUbGBoyiCqIG8xqU7z");

        ParseFacebookUtils.initialize(this);

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

        Fresco.initialize(this);
    }
}
