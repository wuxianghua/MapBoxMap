package com.org.nagradcore;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Created by Administrator on 2017/12/1/001.
 */

public class ST_Disjoint {
    public ST_Disjoint() {
    }

    public static Boolean geomDisjoint(Geometry a, Geometry b) {
        return a != null && b != null?Boolean.valueOf(a.disjoint(b)):null;
    }
}
