package com.org.mapboxmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.org.mylibrary.indoor.IMapViewController;
import com.org.mylibrary.indoor.MapViewControllerFactory;
import com.palmap.core.overLayer.PulseMarkerViewOptions;

public class MainActivity extends AppCompatActivity {
    //地图容器
    private LinearLayout mapViewLayout;
    private IMapViewController iMapViewController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapViewLayout = (LinearLayout) findViewById(R.id.layout_mapView);
        iMapViewController = MapViewControllerFactory.createByMapBox(this);
        iMapViewController.attachMap(mapViewLayout, savedInstanceState, new IMapViewController.Action() {
            @Override
            public void onAction() {
                iMapViewController.drawPlanarGraph(App.parkData);
            }
        });
    }
}
