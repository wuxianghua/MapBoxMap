package com.org.nagradcore.dto;

import java.io.Serializable;
import java.util.List;
/**
 * Created by liupin on 2017/7/31.
 */
public class RoadDto extends BaseDto implements Serializable {

    private long mapId;
    private long planarGraphId;

    public long getMapId() {
        return mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
    }

    public long getPlanarGraphId() {
        return planarGraphId;
    }

    public void setPlanarGraphId(long planarGraphId) {
        this.planarGraphId = planarGraphId;
    }

    public List<VertexDto> getVertexes() {
        return vertexes;
    }

    public void setVertexes(List<VertexDto> vertexes) {
        this.vertexes = vertexes;
    }

    public List<PathDto> getPaths() {
        return paths;
    }

    public void setPaths(List<PathDto> paths) {
        this.paths = paths;
    }

    private List<VertexDto> vertexes;
    private List<PathDto> paths;

}
