package com.org.nagradcore.dto;

import com.org.nagradcore.model.path.Direction;
import com.vividsolutions.jts.geom.Geometry;
import java.io.Serializable;

/**
 * Created by sifan on 2016/12/17.
 */
public class PathDto implements Serializable {

    private Long id;
    private Long mapId;
    private Integer rank;
    private Direction direction;

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

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
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

    public Long getPathId() {
        return pathId;
    }

    public void setPathId(Long pathId) {
        this.pathId = pathId;
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

    private Geometry shape;
    private Long from;
    private Long to;
    private Long planarGraphId;
    private Double altitude;
    private Long pathId;
    private Long fromLocationId;
    private Long toLocationId;

}
