package com.palmap.astar.navi.entity;

import android.text.TextUtils;

import com.vividsolutions.jts.geom.LineString;

/**
 * Created by caogl on 2015/4/1.
 */
public class Path {

    private long id;

    private int rank;

    private Direction direction;

    private LineString shape;

    private Vertex from;

    private Vertex to;

    private long planarGraph;

    private long mapId;

    private double altitude;


    public Path() {
    }

    public Path(long mapId, int rank, String direction, LineString shape, long planarGraphId, long pathId, double altitude) {
        this.mapId = mapId;
        this.rank = rank;
        this.direction = !TextUtils.isEmpty(direction)&&direction.toUpperCase().equals("ONEWAY") ? Direction.ONEWAY:Direction.TWOWAY;
        this.shape = shape;
        this.planarGraph = planarGraphId;
        this.id = pathId;
        this.altitude = altitude;
    }

    public long getMapId() {
        return mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public long getId() {
        return id;
    }

    public int getRank() {
        return rank;
    }

    public Direction getDirection() {
        return direction;
    }

    public Vertex getFrom() {
        return from;
    }

    public Vertex getTo() {
        return to;
    }

    public LineString getShape() {
        return shape;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setShape(LineString shape) {
        this.shape = shape;
    }

    public void setFrom(Vertex from) {
        this.from = from;
    }

    public void setTo(Vertex to) {
        this.to = to;
    }

    public long getPlanarGraph() {
        return planarGraph;
    }

    public void setPlanarGraph(long planarGraph) {
        this.planarGraph = planarGraph;
    }
}
