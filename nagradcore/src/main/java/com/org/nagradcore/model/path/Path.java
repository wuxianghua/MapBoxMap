package com.org.nagradcore.model.path;

import android.text.TextUtils;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

import java.io.Serializable;

/**
 * Created by Nick on 2016/8/17.
 */
public class Path implements Serializable {

    private Long id;
    private Long mapId;
    private Integer rank;

    public Path() {}

    public Path(long mapId, int rank, String direction, LineString shape, long planarGraphId, long pathId, double altitude) {
        this.mapId = mapId;
        this.rank = rank;
        this.direction = !TextUtils.isEmpty(direction)&&direction.toUpperCase().equals("ONEWAY") ? Direction.ONEWAY:Direction.TWOWAY;
        this.shape = shape;
        this.planarGraphId = planarGraphId;
        this.id = pathId;
        this.altitude = altitude;
    }

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

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Geometry getShape() {
        return shape;
    }

    public void setShape(Geometry shape) {
        this.shape = shape;
    }

    public Vertex getFrom() {
        return from;
    }

    public void setFrom(Vertex from) {
        this.from = from;
    }

    public Vertex getTo() {
        return to;
    }

    public void setTo(Vertex to) {
        this.to = to;
    }

    public Long getPlanarGraphId() {
        return planarGraphId;
    }

    public void setPlanarGraphId(Long planarGraphId) {
        this.planarGraphId = planarGraphId;
    }

    public Long getPathId() {
        return pathId;
    }

    public void setPathId(Long pathId) {
        this.pathId = pathId;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Long getFromLocationId() {
        return fromLocationId;
    }

    public void setFromLocationId(Long fromLocationId) {
        this.fromLocationId = fromLocationId;
    }

    public Long getToLocationId() {
        return toLocationId;
    }

    public void setToLocationId(Long toLocationId) {
        this.toLocationId = toLocationId;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Integer getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(Integer speedLimit) {
        this.speedLimit = speedLimit;
    }

    private Direction direction;
    private Geometry shape;
    private Vertex from;
    private Vertex to;
    private Long planarGraphId;
    private Long pathId;
    private Double altitude;
    private Long fromLocationId;
    private Long toLocationId;

    private String linkId;
    private String name;
    private Integer weight;
    private String attribute;
    private Integer speedLimit;

}
