package com.org.nagradcore;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateArrays;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Polygon;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/4/004.
 */

public class ST_ToMultiSegments {
    public ST_ToMultiSegments() {
    }
    public static MultiLineString createSegments(Geometry geom) {
        if(geom != null) {
            GeometryFactory gf = GeometryFactories.default_();
            if(geom.getDimension() > 0) {
                LinkedList result = new LinkedList();
                createSegments((Geometry)geom, result);
                return gf.createMultiLineString((LineString[])result.toArray(new LineString[result.size()]));
            } else {
                return gf.createMultiLineString((LineString[])null);
            }
        } else {
            return null;
        }
    }

    private static void createSegments(Geometry geom, List<LineString> result) {
        if(geom instanceof LineString) {
            createSegments((LineString)geom, result);
        } else if(geom instanceof Polygon) {
            createSegments((Polygon)geom, result);
        } else if(geom instanceof GeometryCollection) {
            createSegments((GeometryCollection)geom, result);
        }

    }

    public static void createSegments(LineString geom, List<LineString> result) {
        Coordinate[] coords = CoordinateArrays.removeRepeatedPoints(geom.getCoordinates());

        for(int j = 0; j < coords.length - 1; ++j) {
            LineString lineString = GeometryFactories.default_().createLineString(new Coordinate[]{coords[j], coords[j + 1]});
            result.add(lineString);
        }

    }

    private static void createSegments(Polygon polygon, List<LineString> result) {
        createSegments(polygon.getExteriorRing(), result);

        for(int i = 0; i < polygon.getNumInteriorRing(); ++i) {
            createSegments(polygon.getInteriorRingN(i), result);
        }

    }

    private static void createSegments(GeometryCollection geometryCollection, List<LineString> result) {
        for(int i = 0; i < geometryCollection.getNumGeometries(); ++i) {
            createSegments(geometryCollection.getGeometryN(i), result);
        }

    }
}
