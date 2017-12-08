package com.org.nagradcore;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/1/001.
 */

public class GeometryFactories {
    private static final Map<Integer, GeometryFactory> GF_POOL = new HashMap();

    public GeometryFactories() {
    }

    public static GeometryFactory factory(int srid) {
        if (GF_POOL.get(Integer.valueOf(srid)) != null) {
            return GF_POOL.get(Integer.valueOf(srid));
        }else {
            GF_POOL.put(Integer.valueOf(srid),new GeometryFactory(new PrecisionModel(),Integer.valueOf(srid)));
            return new GeometryFactory(new PrecisionModel(),Integer.valueOf(srid));
        }
    }

    public static GeometryFactory default_() {
        return factory(0);
    }

    public static GeometryFactory WGS84() {
        return factory(4326);
    }

    public static GeometryFactory pseudoMercator() {
        return factory(3857);
    }
}
