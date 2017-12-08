package com.org.nagradcore.utils;
import com.org.nagradcore.ST_Disjoint;
import com.org.nagradcore.ST_Snap;
import com.org.nagradcore.ST_Split;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.linearref.LengthIndexedLine;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sifan on 2016/12/13.
 */
public class GeometryUtils {

    public static boolean disjoint(Geometry a, Geometry b, double tolerance) {
        return ST_Disjoint.geomDisjoint(ST_Snap.snap(a, b, tolerance), b);
    }

    /**
     * split line a and line b
     *
     * @param geometryA
     * @param geometryB
     * @return
     */
    public static List<Geometry> spilt(Geometry geometryA, Geometry geometryB) {
        List<Geometry> splits = new ArrayList<>();
        if (geometryA instanceof LineString || geometryA instanceof MultiLineString) {
            if (geometryB instanceof Point) {
                Geometry split = ST_Split.split(geometryA, geometryB);
                splits.addAll(splitMultiLineString(split));
                return splits;
            }
        }

        Geometry intersection = geometryA.intersection(geometryB);
        Geometry differenceA = geometryA.difference(geometryB);

        if (intersection instanceof LineString || intersection instanceof MultiLineString) {
            splits.addAll(splitMultiLineString(intersection));
        }
        splits.addAll(splitMultiLineString(differenceA));
        return splits;
    }

    private static List<Geometry> splitMultiLineString(Geometry geometry) {
        List<Geometry> splits = new ArrayList<>();
        if (!geometry.isEmpty()) {
            if (geometry instanceof LineString) {
                splits.add(geometry);
            } else if (geometry instanceof MultiLineString) {
                for (int i = 0; i < geometry.getNumGeometries(); i++) {
                    Geometry geometryN = geometry.getGeometryN(i);
                    if (!geometryN.isEmpty()) {
                        splits.add(geometryN);
                    }
                }
            } else {
                throw new RuntimeException("Unsupported collection " + geometry.toText());
            }
        }
        return splits;
    }

    /**
     * 找到点与线最近的坐标(投影)
     *
     * @param point
     * @param line
     * @return
     */
    public static Coordinate getProjectOnLine(Point point, LineString line) {
        Coordinate project;
        if (DistanceOp.distance(line, point) >= Double.MAX_VALUE) {
            LengthIndexedLine lil = new LengthIndexedLine(line);
            project = lil.extractPoint(line.getLength() / 2.0);
        } else {
            project = DistanceOp.nearestPoints(line, point)[0];
        }
        return project;
    }
}
