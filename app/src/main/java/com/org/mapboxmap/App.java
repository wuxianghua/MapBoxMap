package com.org.mapboxmap;

import android.app.Application;
import android.content.Intent;

import com.palmap.core.MapEngine;
import com.palmap.core.data.PlanarGraph;

import java.util.Timer;
import java.util.TimerTask;

import static com.palmap.core.util.UtilsKt.loadFromAsset;

/**
 * Created by Administrator on 2017/11/7/007.
 */

public class App extends Application {

    public static App instance;
    public static PlanarGraph parkData = null;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MapEngine.INSTANCE.start(this,"pk.eyJ1IjoiY2FtbWFjZSIsImEiOiJjaW9vbGtydnQwMDAwdmRrcWlpdDVoM3pjIn0.Oy_gHelWnV12kJxHQWV7XQ");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
//        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                if (parkData == null) {
                    parkData = new com.palmap.core.data.PlanarGraph(loadFromAsset(getApplicationContext(),"h2Date.json"),16);
                }
                parkData.resolveData();
                parkData.loadStyle(
                        MapEngine.INSTANCE.getRenderableByName("default").getRenderer()
                );
            }
        }, 2000);
    }
}
