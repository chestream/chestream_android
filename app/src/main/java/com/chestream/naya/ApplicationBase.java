package com.chestream.naya;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by omerjerk on 15/8/15.
 */
public class ApplicationBase extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
