package com.org.nagradcore;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.overlay.snap.GeometrySnapper;

/**
 * Created by Administrator on 2017/12/1/001.
 */

public class ST_Snap {
    public ST_Snap() {
    }

    public static Geometry snap(Geometry geometryA, Geometry geometryB, double distance) {
        if(geometryA != null && geometryB != null) {
            Geometry[] snapped = GeometrySnapper.snap(geometryA, geometryB, distance);
            return snapped[0];
        } else {
            return null;
        }
    }
}
