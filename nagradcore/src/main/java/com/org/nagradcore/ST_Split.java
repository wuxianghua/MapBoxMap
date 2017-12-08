package com.org.nagradcore;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.distance.GeometryLocation;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import com.vividsolutions.jts.operation.union.UnaryUnionOp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Administrator on 2017/12/1/001.
 */

public class ST_Split {
    public static final double PRECISION = 1.0E-5D;
    public static Geometry split(Geometry geomA, Geometry geomB) {
        if(geomA != null && geomB != null) {
            if(geomA instanceof Polygon) {
                return splitPolygonWithLine((Polygon)geomA, (LineString)geomB);
            } else if(geomA instanceof MultiPolygon) {
                return splitMultiPolygonWithLine((MultiPolygon)geomA, (LineString)geomB);
            } else {
                if(geomA instanceof LineString) {
                    if(geomB instanceof LineString) {
                        return splitLineStringWithLine((LineString)geomA, (LineString)geomB);
                    }

                    if(geomB instanceof Point) {
                        return splitLineWithPoint((LineString)geomA, (Point)geomB, 1.0E-5D);
                    }
                } else if(geomA instanceof MultiLineString) {
                    if(geomB instanceof LineString) {
                        return splitMultiLineStringWithLine((MultiLineString)geomA, (LineString)geomB);
                    }

                    if(geomB instanceof Point) {
                        return splitMultiLineStringWithPoint((MultiLineString)geomA, (Point)geomB, 1.0E-5D);
                    }
                }

                throw new SpatialException("Split a " + geomA.getGeometryType() + " by a " + geomB.getGeometryType() + " is not supported.");
            }
        } else {
            return null;
        }
    }

    private static Geometry splitMultiLineStringWithLine(MultiLineString input, LineString cut) {
        Geometry lines = input.difference(cut);
        return (Geometry)(lines instanceof LineString?GeometryFactories.default_().createMultiLineString(new LineString[]{(LineString)lines.getGeometryN(0)}):lines);
    }

    private static MultiLineString splitMultiLineStringWithPoint(MultiLineString multiLineString, Point pointToSplit, double tolerance) {
        ArrayList linestrings = new ArrayList();
        boolean notChanged = true;
        int nb = multiLineString.getNumGeometries();

        for(int i = 0; i < nb; ++i) {
            LineString subGeom = (LineString)multiLineString.getGeometryN(i);
            LineString[] result = splitLineStringWithPoint(subGeom, pointToSplit, tolerance);
            if(result != null) {
                Collections.addAll(linestrings, result);
                notChanged = false;
            } else {
                linestrings.add(subGeom);
            }
        }

        if(!notChanged) {
            return GeometryFactories.default_().createMultiLineString((LineString[])linestrings.toArray(new LineString[linestrings.size()]));
        } else {
            return null;
        }
    }

    private static MultiLineString splitLineWithPoint(LineString line, Point pointToSplit, double tolerance) {
        return GeometryFactories.default_().createMultiLineString(splitLineStringWithPoint(line, pointToSplit, tolerance));
    }

    private static LineString[] splitLineStringWithPoint(LineString line, Point pointToSplit, double tolerance) {
        Coordinate[] coords = line.getCoordinates();
        Coordinate firstCoord = coords[0];
        Coordinate lastCoord = coords[coords.length - 1];
        Coordinate coordToSplit = pointToSplit.getCoordinate();
        if(coordToSplit.distance(firstCoord) > 1.0E-5D && coordToSplit.distance(lastCoord) > 1.0E-5D) {
            ArrayList firstLine = new ArrayList();
            firstLine.add(coords[0]);
            ArrayList secondLine = new ArrayList();
            GeometryLocation geometryLocation = EditUtilities.getVertexToSnap(line, pointToSplit, tolerance);
            if(geometryLocation != null) {
                int segmentIndex = geometryLocation.getSegmentIndex();
                Coordinate coord = geometryLocation.getCoordinate();
                boolean index = true;

                for(int gf = 1; gf < coords.length; ++gf) {
                    int var17 = gf - 1;
                    if(var17 < segmentIndex) {
                        firstLine.add(coords[gf]);
                    } else if(var17 == segmentIndex) {
                        coord.z = CoordinateUtils.interpolate(coords[gf - 1], coords[gf], coord);
                        firstLine.add(coord);
                        secondLine.add(coord);
                        if(!coord.equals2D(coords[gf])) {
                            secondLine.add(coords[gf]);
                        }
                    } else {
                        secondLine.add(coords[gf]);
                    }
                }

                GeometryFactory var18 = GeometryFactories.default_();
                LineString lineString1 = var18.createLineString((Coordinate[])firstLine.toArray(new Coordinate[firstLine.size()]));
                LineString lineString2 = var18.createLineString((Coordinate[])secondLine.toArray(new Coordinate[secondLine.size()]));
                return new LineString[]{lineString1, lineString2};
            } else {
                return null;
            }
        } else {
            return new LineString[]{line};
        }
    }

    private static Geometry splitLineStringWithLine(LineString input, LineString cut) {
        return input.difference(cut);
    }

    private static Geometry splitMultiPolygonWithLine(MultiPolygon multiPolygon, LineString lineString) {
        ArrayList allPolygons = new ArrayList();

        for(int i = 0; i < multiPolygon.getNumGeometries(); ++i) {
            Collection polygons = splitPolygonizer((Polygon)multiPolygon.getGeometryN(i), lineString);
            if(polygons != null) {
                allPolygons.addAll(polygons);
            }
        }

        if(!allPolygons.isEmpty()) {
            return GeometryFactories.default_().buildGeometry(allPolygons);
        } else {
            return null;
        }
    }

    private static Geometry splitPolygonWithLine(Polygon polygon, LineString lineString) {
        Collection pols = polygonWithLineSplitter(polygon, lineString);
        return pols != null? GeometryFactories.default_().buildGeometry(polygonWithLineSplitter(polygon, lineString)):null;
    }

    private static Collection<Polygon> polygonWithLineSplitter(Polygon polygon, LineString lineString) {
        Collection polygons = splitPolygonizer(polygon, lineString);
        if(polygons != null && polygons.size() > 1) {
            ArrayList pols = new ArrayList();
            Iterator var4 = polygons.iterator();

            while(var4.hasNext()) {
                Polygon pol = (Polygon)var4.next();
                if(polygon.contains(pol.getInteriorPoint())) {
                    pols.add(pol);
                }
            }

            return pols;
        } else {
            return null;
        }
    }

    private static Collection<Polygon> splitPolygonizer(Polygon polygon, LineString lineString) {
        LinkedList result = new LinkedList();
        ST_ToMultiSegments.createSegments(polygon.getExteriorRing(), result);
        result.add(lineString);
        int holes = polygon.getNumInteriorRing();

        for(int uOp = 0; uOp < holes; ++uOp) {
            ST_ToMultiSegments.createSegments(polygon.getInteriorRingN(uOp), result);
        }

        UnaryUnionOp var8 = new UnaryUnionOp(result);
        Geometry union = var8.union();
        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(union);
        Collection polygons = polygonizer.getPolygons();
        return polygons.size() > 1?polygons:null;
    }
}
