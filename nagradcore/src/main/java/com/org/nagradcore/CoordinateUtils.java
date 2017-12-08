package com.org.nagradcore;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.math.Vector3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * Created by Administrator on 2017/12/4/004.
 */

public final class CoordinateUtils {
    public static double[] zMinMax(Coordinate[] cs) {
        boolean validZFound = false;
        double[] result = new double[2];
        double zmin = 0.0D / 0.0;
        double zmax = 0.0D / 0.0;

        for(int t = cs.length - 1; t >= 0; --t) {
            double z = cs[t].z;
            if(!Double.isNaN(z)) {
                if(validZFound) {
                    if(z < zmin) {
                        zmin = z;
                    }

                    if(z > zmax) {
                        zmax = z;
                    }
                } else {
                    validZFound = true;
                    zmin = z;
                    zmax = z;
                }
            }
        }

        result[0] = zmin;
        result[1] = zmax;
        return result;
    }

    public static double interpolate(Coordinate firstCoordinate, Coordinate lastCoordinate, Coordinate toBeInterpolated) {
        return Double.isNaN(firstCoordinate.z)?0.0D / 0.0:(Double.isNaN(lastCoordinate.z)?0.0D / 0.0:firstCoordinate.z + (lastCoordinate.z - firstCoordinate.z) * firstCoordinate.distance(toBeInterpolated) / (firstCoordinate.distance(toBeInterpolated) + toBeInterpolated.distance(lastCoordinate)));
    }

    public static boolean contains2D(Coordinate[] coords, Coordinate coord) {
        Coordinate[] var2 = coords;
        int var3 = coords.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Coordinate coordinate = var2[var4];
            if(coordinate.equals2D(coord)) {
                return true;
            }
        }

        return false;
    }

    public static Coordinate vectorIntersection(Coordinate p1, Vector3D v1, Coordinate p2, Vector3D v2) {
        Coordinate i = null;
        double delta = v1.getX() * -v2.getY() - -v1.getY() * v2.getX();
        if(delta != 0.0D) {
            double k = ((p2.x - p1.x) * -v2.getY() - (p2.y - p1.y) * -v2.getX()) / delta;
            i = new Coordinate(p1.x + k * v1.getX(), p1.y + k * v1.getY(), p1.z + k * v1.getZ());
            if((new LineSegment(p1, new Coordinate(p1.x + v1.getX(), p1.y + v1.getY()))).projectionFactor(i) < 0.0D || (new LineSegment(p2, new Coordinate(p2.x + v2.getX(), p2.y + v2.getY()))).projectionFactor(i) < 0.0D) {
                return null;
            }
        }

        return i;
    }

    public static Coordinate[] removeDuplicatedCoordinates(Coordinate[] coords, boolean closeRing) {
        LinkedHashSet finalCoords = new LinkedHashSet();
        Coordinate prevCoord = coords[0];
        finalCoords.add(prevCoord);
        int nbCoords = coords.length;

        for(int coordsFinal = 1; coordsFinal < nbCoords; ++coordsFinal) {
            Coordinate closedCoords = coords[coordsFinal];
            if(!closedCoords.equals2D(prevCoord)) {
                finalCoords.add(closedCoords);
                prevCoord = closedCoords;
            }
        }

        if(closeRing) {
            Coordinate[] var8 = (Coordinate[])finalCoords.toArray(new Coordinate[finalCoords.size()]);
            Coordinate[] var9 = (Coordinate[]) Arrays.copyOf(var8, var8.length + 1);
            var9[var9.length - 1] = prevCoord;
            return var9;
        } else {
            return (Coordinate[])finalCoords.toArray(new Coordinate[finalCoords.size()]);
        }
    }

    public static Coordinate[] removeRepeatedCoordinates(Coordinate[] coords, double tolerance, boolean duplicateFirstLast) {
        ArrayList finalCoords = new ArrayList();
        Coordinate prevCoord = coords[0];
        finalCoords.add(prevCoord);
        int nbCoords = coords.length;

        for(int i = 1; i < nbCoords; ++i) {
            Coordinate currentCoord = coords[i];
            if(currentCoord.distance(prevCoord) > tolerance) {
                finalCoords.add(currentCoord);
                prevCoord = currentCoord;
            }
        }

        if(!duplicateFirstLast) {
            if(prevCoord.distance(prevCoord) <= tolerance) {
                finalCoords.remove(finalCoords.size() - 1);
            }
        } else {
            finalCoords.add(prevCoord);
        }

        return (Coordinate[])finalCoords.toArray(new Coordinate[finalCoords.size()]);
    }

    private CoordinateUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
