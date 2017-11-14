package com.org.mylibrary.indoor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.org.mylibrary.R;
import com.palmap.core.data.PlanarGraph;

/**
 * Created by Administrator on 2017/11/7/007.
 */

public class MainMapActivity extends AppCompatActivity{
    //地图容器
    private LinearLayout mapViewLayout;
    private IMapViewController iMapViewController;
    private static PlanarGraph mParkData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        mapViewLayout = (LinearLayout) findViewById(R.id.layout_mapView);
        iMapViewController = MapViewControllerFactory.createByMapBox(this);
        iMapViewController.attachMap(mapViewLayout, savedInstanceState, new IMapViewController.Action() {
            @Override
            public void onAction() {
                iMapViewController.drawPlanarGraph(mParkData);
            }
        });
    }

    public static void navigatorThis(Context that) {
        Intent intent = new Intent(that, MainMapActivity.class);
        that.startActivity(intent);
    }
}
