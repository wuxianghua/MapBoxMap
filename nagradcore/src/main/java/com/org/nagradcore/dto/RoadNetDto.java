package com.org.nagradcore.dto;
import java.io.Serializable;
import java.util.List;

/**
 * Created by liupin on 2017/7/31.
 */
public class RoadNetDto extends BaseDto implements Serializable {

    private long mapId;
    private List<RoadDto> roads;

    public long getMapId() {
        return mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
    }

    public List<RoadDto> getRoads() {
        return roads;
    }

    public void setRoads(List<RoadDto> roads) {
        this.roads = roads;
    }

    public List<ConnectionDto> getConnections() {
        return connections;
    }

    public void setConnections(List<ConnectionDto> connections) {
        this.connections = connections;
    }

    private List<ConnectionDto> connections;

}
