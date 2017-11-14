package com.org.mapboxmap;

import android.app.Application;

import com.palmap.core.MapEngine;
import com.palmap.core.data.PlanarGraph;
import com.palmaplus.nagrand.core.Engine;

import java.util.Timer;
import java.util.TimerTask;

import static com.palmap.core.util.UtilsKt.loadFromAsset;

/**
 * Created by Administrator on 2017/11/7/007.
 */

public class App extends Application {

    public static App instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MapEngine.INSTANCE.start(this,"pk.eyJ1IjoiY2FtbWFjZSIsImEiOiJjaW9vbGtydnQwMDAwdmRrcWlpdDVoM3pjIn0.Oy_gHelWnV12kJxHQWV7XQ");
        Engine.getInstance();
    }
}
