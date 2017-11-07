package com.org.mylibrary.indoor;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.Geometry;
import com.palmap.astar.navi.geojson.GeoJsonReader;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.io.ParseException;

/**
 * Created by wtm on 2017/10/12.
 */
public class Utils {

    private static final GeoJsonReader GEO_JSON_READER = new GeoJsonReader();

    private Utils() {
    }

    /**
     * 获取 feature 的中心点
     * @param feature
     * @return
     * @throws NullPointerException feature为null
     * @throws ParseException 解析错误
     */
    public static LatLng getFeatureCentroid(Feature feature) throws NullPointerException,ParseException {
        if (feature == null) {
            throw new NullPointerException("feature is null !!");
        }
        Geometry geometry = feature.getGeometry();
        com.vividsolutions.jts.geom.Geometry result = GEO_JSON_READER.read(geometry.toJson());
        Coordinate coordinate = result.getCentroid().getCoordinate();
        return new LatLng(coordinate.y, coordinate.x);
    }

}
