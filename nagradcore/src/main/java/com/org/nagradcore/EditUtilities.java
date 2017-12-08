package com.org.nagradcore;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import com.vividsolutions.jts.operation.distance.GeometryLocation;

/**
 * Created by Administrator on 2017/12/4/004.
 */

public class EditUtilities {
    public EditUtilities() {
    }

    public static GeometryLocation getVertexToSnap(Geometry g, Point p, double tolerance) {
        DistanceOp distanceOp = new DistanceOp(g, p);
        GeometryLocation snapedPoint = distanceOp.nearestLocations()[0];
        return tolerance != 0.0D && snapedPoint.getCoordinate().distance(p.getCoordinate()) > tolerance?null:snapedPoint;
    }
}
