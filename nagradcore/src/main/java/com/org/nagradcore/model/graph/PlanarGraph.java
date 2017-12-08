package com.org.nagradcore.model.graph;

import com.org.nagradcore.model.Base;

import java.io.Serializable;

/**
 * Created by wyx on 2/8/16.
 */
public class PlanarGraph extends Base implements Serializable {
    private Long id;
    private Frames frames;
    private Areas areas;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Frames getFrames() {
        return frames;
    }

    public void setFrames(Frames frames) {
        this.frames = frames;
    }

    public Areas getAreas() {
        return areas;
    }

    public void setAreas(Areas areas) {
        this.areas = areas;
    }

    public Facilities getFacilities() {
        return facilities;
    }

    public void setFacilities(Facilities facilities) {
        this.facilities = facilities;
    }

    public Doors getDoors() {
        return doors;
    }

    public void setDoors(Doors doors) {
        this.doors = doors;
    }

    public Long getMapId() {
        return mapId;
    }

    public void setMapId(Long mapId) {
        this.mapId = mapId;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    private Facilities facilities;
    private Doors doors;
    private Long mapId;
    private Long buildingId;
}
