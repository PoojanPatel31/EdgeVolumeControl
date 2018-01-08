package com.poojan.edge.volume.control;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Poochi on 12/18/2017.
 */

public class VolumeControlApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.ENABLE_CRASH_LOG) {
            // enable crash log here
            Fabric.with(this.getApplicationContext(), new Crashlytics());
        }
    }
}
