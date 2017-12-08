package com.org.nagradcore.model.region;

import java.io.Serializable;

/**
 * Created by wyx on 2/8/16.
 */
public class Region extends RegionKey implements Serializable {
    private String name;
    private FullRegion fullRegion;
    private RegionType regionType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FullRegion getFullRegion() {
        return fullRegion;
    }

    public void setFullRegion(FullRegion fullRegion) {
        this.fullRegion = fullRegion;
    }

    public RegionType getRegionType() {
        return regionType;
    }

    public void setRegionType(RegionType regionType) {
        this.regionType = regionType;
    }
}
