package kuchbhilabs.chestream;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import kuchbhilabs.chestream.parse.ParseVideo;

/**
 * Created by root on 20/6/15.
 */
public class ApplicationBase extends MultiDexApplication {

    public static final boolean LOG_DEBUG = true;
    public static final boolean LOG_INFO = true;

    public static String userpass = "stomatrix@gmail.com" + ":" + "sauravclusterpoint";
    public static String basicAuth = "Basic "
            + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);

    public static GoogleAnalytics analytics;
    public static Tracker tracker;

    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */


    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-65642271-1"); // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);


        FacebookSdk.sdkInitialize(getApplicationContext());

        ParseObject.registerSubclass(ParseVideo.class);
        Parse.initialize(this, getString(R.string.i),
                getString(R.string.p));

        ParseFacebookUtils.initialize(this);
        ParseTwitterUtils.initialize("JdSrIpONnSYiOichfx59MNdlP",
                "iH3md4EEpHkcfjyT0Yz0LyuFFrE7N9ys3cTc3pdq5iYz31qYLu");

        ParseACL defaultACL = new ParseACL();
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

        ImageLoaderConfiguration localImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(localImageLoaderConfiguration);
    }


    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
