package com.palmap.astar.navi.entity;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by caogl on 2015/4/9.
 */
public class Connection implements Serializable {

    private long id;

    private Vertex from;

    private Vertex to;

    private int rank;

    private Direction direction;

    private long mapId;

    private long categoryId;

    public Connection(long mapId, String direction, int rank) {
        this.mapId = mapId;
        this.rank = rank;
        this.direction = !TextUtils.isEmpty(direction)&&direction.toUpperCase().equals("ONEWAY") ? Direction.ONEWAY:Direction.TWOWAY;
    }

    public long getMapId() {
        return mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getId() {
        return id;
    }

    public Vertex getFrom() {
        return from;
    }

    public Vertex getTo() {
        return to;
    }

    public int getRank() {
        return rank;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setFrom(Vertex from) {
        this.from = from;
    }

    public void setTo(Vertex to) {
        this.to = to;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
