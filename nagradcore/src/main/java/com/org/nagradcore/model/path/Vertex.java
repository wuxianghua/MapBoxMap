package com.org.nagradcore.model.path;

import com.org.nagradcore.geojson.GeoJsonReader;
import com.vividsolutions.jts.geom.Geometry;
import java.io.Serializable;

/**
 * Created by Nick on 2016/8/17.
 */
public class Vertex implements Serializable {

    public Vertex() {}

    public Vertex(Geometry geometry) {
        this.shape = geometry;
    }

    public Vertex(long mapId,Geometry shape,long planarGraphId,double altitude) {
        this.mapId = mapId;
        this.shape = shape;
        this.planarGraphId = planarGraphId;
        this.altitude = altitude;
    }

    private Long id;
    private Long mapId;
    private Geometry shape;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMapId() {
        return mapId;
    }

    public void setMapId(Long mapId) {
        this.mapId = mapId;
    }

    public Geometry getShape() {
        return shape;
    }

    public void setShape(Geometry shape) {
        this.shape = shape;
    }

    public Long getPlanarGraphId() {
        return planarGraphId;
    }

    public void setPlanarGraphId(Long planarGraphId) {
        this.planarGraphId = planarGraphId;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Boolean getVirtual() {
        return virtual;
    }

    public void setVirtual(Boolean virtual) {
        this.virtual = virtual;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDoorId() {
        return doorId;
    }

    public void setDoorId(Long doorId) {
        this.doorId = doorId;
    }

    public Long getEscalatorId() {
        return escalatorId;
    }

    public void setEscalatorId(Long escalatorId) {
        this.escalatorId = escalatorId;
    }

    public Long getPublicServiceId() {
        return publicServiceId;
    }

    public void setPublicServiceId(Long publicServiceId) {
        this.publicServiceId = publicServiceId;
    }

    public boolean isFacility() {
        return facility;
    }

    public void setFacility(boolean facility) {
        this.facility = facility;
    }

    private Long planarGraphId;
    private Double altitude;
    private Boolean virtual;
    private String nodeId;
    private String name;
    private Long doorId;
    private Long escalatorId;
    private Long publicServiceId;
    private boolean facility;

}
