package com.palmap.astar.navi.entity;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import java.util.Objects;

/**
 * Created by wyx on 8/26/15.
 */
public class Vertex {

    private double altitude;

    private long id;

    private Point shape;

    private long planarGraph;

    private boolean virtual;

    private long mapId;

    public Vertex() {
    }

    public Vertex(long mapId, Point shape, long planarGraphId, double altitude) {
        this.mapId = mapId;
        this.shape = shape;
        this.planarGraph = planarGraphId;
        this.altitude = altitude;
    }

    public long getMapId() {
        return mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
    }

    public long getId() {
        return id;
    }

    public Geometry getShape() {
        return shape;
    }

    public long getPlanarGraph() {
        return planarGraph;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setShape(Point shape) {
        this.shape = shape;
    }

    public void setPlanarGraph(long planarGraph) {
        this.planarGraph = planarGraph;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return Objects.equals(id, vertex.id);
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "id=" + id +
                ", shape=" + shape +
                ", planarGraph=" + planarGraph +
                '}';
    }
}
