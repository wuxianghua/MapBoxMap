package com.org.mylibrary.indoor;

import android.os.Bundle;
import android.view.ViewGroup;

import com.mapbox.services.commons.geojson.Feature;
import com.palmap.core.data.PlanarGraph;

/**
 * Created by wtm on 2017/9/30.
 */

public interface IMapViewController {

    static interface Action{
        void onAction();
    }

    static interface onSingTapListener{
        void onAction(double x, double y);
    }

    void attachMap(ViewGroup root, Bundle savedInstanceState, Action action);

    long getFloorId();

    void drawPlanarGraph(String mapDataPath);

    void drawPlanarGraph(PlanarGraph p);

    void resetNorth(long animTime);

    Feature selectFeature(double x, double y);

    Feature selectFeature(String name, PlanarGraph planarGraph);

    void setOnSingTapListener(onSingTapListener l);

    void addOverLayer(OverLayer overLayer);

    void setLocationMarkIcon(int resource, int width, int height);

    void addLocationMark(double x, double y);

    void onStart();

    void onStop();

    void onResume();

    void onPause();

    void onSaveInstanceState(Bundle outState);

    void onDestroy();

    void onLowMemory();

    void initMapView();

    void updateLocationOrientation(float degree);
}
