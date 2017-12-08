package com.org.nagradcore.model.path;

import android.text.TextUtils;

import com.org.nagradcore.model.Base;

import java.io.Serializable;

/**
 * Created by Nick on 2016/8/17.
 */
public class Connection extends Base implements Serializable {

    private Long id;
    private Long mapId;

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

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
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

    public Long getFromPlanarGraphId() {
        return fromPlanarGraphId;
    }

    public void setFromPlanarGraphId(Long fromPlanarGraphId) {
        this.fromPlanarGraphId = fromPlanarGraphId;
    }

    public Long getToPlanarGraphId() {
        return toPlanarGraphId;
    }

    public void setToPlanarGraphId(Long toPlanarGraphId) {
        this.toPlanarGraphId = toPlanarGraphId;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    private Direction direction;
    private Integer rank;
    private Long fromLocationId;
    private Long toLocationId;
    private Long fromPlanarGraphId;
    private Long toPlanarGraphId;
    private Vertex from;
    private Vertex to;
    private Long categoryId;

    public Connection(Long mapId, String direction, Integer rank) {
        this.mapId = mapId;
        this.direction = !TextUtils.isEmpty(direction)&&direction.toUpperCase().equals("ONEWAY") ? Direction.ONEWAY:Direction.TWOWAY;
        this.rank = rank;
    }

}
