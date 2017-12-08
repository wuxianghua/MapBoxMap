package com.org.nagradcore.model;

import com.vividsolutions.jts.geom.Geometry;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by Administrator on 2017/12/5/005.
 */

public class PoiInfo implements Serializable {
    public Long planarGraphId;
    public Set<Long> doorIds;
    public Geometry shape;
}
