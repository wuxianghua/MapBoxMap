package com.org.mylibrary.indoor.impl;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.org.mylibrary.indoor.IMapViewController;
import com.org.mylibrary.indoor.OverLayer;
import com.palmap.core.IndoorMapView;
import com.palmap.core.MapEngine;
import com.palmap.core.data.PlanarGraph;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

import static com.palmap.core.util.UtilsKt.loadFromAsset;

/**
 * Created by wtm on 2017/9/30.
 */
public class MapBoxMapViewController implements IMapViewController {

    private IndoorMapView indoorMapView;
    private Context context;

    public MapBoxMapViewController(Context context) {
        this.context = context;
        MapEngine.INSTANCE.start(context,"pk.eyJ1IjoiY2FtbWFjZSIsImEiOiJjaW9vbGtydnQwMDAwdmRrcWlpdDVoM3pjIn0.Oy_gHelWnV12kJxHQWV7XQ");
        indoorMapView = IndoorMapView.Companion.create(context);
    }

    @Override
    public void attachMap(ViewGroup root, Bundle savedInstanceState,final  Action action) {
        indoorMapView.attachMap(root, savedInstanceState, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                action.onAction();
                return null;
            }
        });
    }

    @Override
    public long getFloorId() {
        return indoorMapView.getFloorId();
    }

    @Override
    public void drawPlanarGraph(String mapDataPath) {
        PlanarGraph planarGraph = new PlanarGraph(loadFromAsset(context,mapDataPath),16);
        indoorMapView.drawPlanarGraph(planarGraph);
    }

    @Override
    public void drawPlanarGraph(PlanarGraph p) {
        if (p == null) return;
        indoorMapView.drawPlanarGraph(p);
    }

    @Override
    public void resetNorth(long animTime) {
        indoorMapView.resetNorth(animTime);
    }

    @Override
    public Feature selectFeature(double x, double y) {
        return indoorMapView.selectFeature(x,y);
    }

    @Override
    public Feature selectFeature(String name,PlanarGraph planarGraph) {
        for (Feature feature : planarGraph.getDataMap().get("Area").getFeatures()) {
            if(feature.getProperties() == null|| feature.getProperties().get("name")==null) continue;
            if (name.equals(feature.getProperties().get("name").getAsString())) {
                return feature;
            }
        }
        return null;
    }

    @Override
    public void setOnSingTapListener(final onSingTapListener l) {
        indoorMapView.setOnSingTapListener(new Function2<Double, Double, Unit>() {
            @Override
            public Unit invoke(Double aDouble, Double aDouble2) {
                l.onAction(aDouble,aDouble2);
                return null;
            }
        });
    }

    @Override
    public void addOverLayer(final OverLayer overLayer) {
        if (overLayer==null){
            return;
        }
        indoorMapView.addOverLayer(new com.palmap.core.OverLayer() {
            @Override
            public int getResource() {
                return overLayer.getResource();
            }

            @NotNull
            @Override
            public double[] getCoordinate() {
                return new double[]{
                        overLayer.getCoordinate().x,
                        overLayer.getCoordinate().y,
                };
            }

            @Override
            public long getFloorId() {
                return overLayer.getFloorId();
            }
        });
    }

    @Override
    public void setLocationMarkIcon(int resource,int w , int h) {
        indoorMapView.setLocationMarkIcon(resource,w ,h);
    }



    @Override
    public void addLocationMark(double x, double y) {
        if (x == 0 || y == 0) {
            indoorMapView.addLocationMark(null);
        }else {
            indoorMapView.addLocationMark(new LatLng(x,y));
        }
    }

    /**
     * 设置定位样式
     * @param resource
     */
    public void setMapBoxLocationMark(int resource){
        indoorMapView.setMapBoxLocationDrawable(context.getResources().getDrawable(resource));
    }

    /**
     * 开启maobox定位
     * @param locationEngine
     * @param isFollow 是否使用跟随模式
     */
    public void openMapBoxLocation(LocationEngine locationEngine,boolean isFollow){
        indoorMapView.openMapBoxLocation(locationEngine,isFollow,false);
    }

    @Override
    public void onStart() {
        indoorMapView.onStart();
    }

    @Override
    public void onStop() {
        indoorMapView.onStop();
    }

    @Override
    public void onResume() {
        indoorMapView.onResume();
    }

    @Override
    public void onPause() {
        indoorMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        indoorMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        indoorMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        indoorMapView.onLowMemory();
    }

    @Override
    public void initMapView() {

    }

    @Override
    public void updateLocationOrientation(float degree) {
        indoorMapView.updateOrientation(degree);
    }

    public MapboxMap getMapBox(){
        return indoorMapView.getMapBoxMap();
    }

    public void showRoute(FeatureCollection route) {
        MapboxMap mapboxMap = getMapBox();
        if (mapboxMap.getSourceAs("RouteSB") == null) {
            GeoJsonSource geoJsonSource = new GeoJsonSource("RouteSB", route);
            mapboxMap.addSource(geoJsonSource);
            Layer layer = new LineLayer("layerRoute", "RouteSB");
            layer.setProperties(
                    //PropertyFactory.lineDasharray(new Float[]{0.03f, 2f}),
                    //PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    //PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    PropertyFactory.lineWidth(6f),
                    PropertyFactory.lineColor(Color.parseColor("#4fb5f5"))
            );
            mapboxMap.addLayerBelow(layer,"layer_location");
        } else {
            if(route == null){
                route = FeatureCollection.fromFeatures(new Feature[]{});
            }
            GeoJsonSource source = mapboxMap.getSourceAs("RouteSB");
            source.setGeoJson(route);
        }
    }

    public void showCarHover(FeatureCollection featureCollection){
        indoorMapView.setHoverData("Area",featureCollection);
    }

    public void showCarPark(FeatureCollection featureCollection){
        indoorMapView.setParkData("Area",featureCollection);
    }
}
