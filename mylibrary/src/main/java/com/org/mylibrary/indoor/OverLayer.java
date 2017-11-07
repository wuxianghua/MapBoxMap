package com.org.mylibrary.indoor;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Created by wtm on 2017/10/9.
 */
public interface OverLayer {

    Coordinate getCoordinate();

    long getFloorId();

    int getResource();
}
