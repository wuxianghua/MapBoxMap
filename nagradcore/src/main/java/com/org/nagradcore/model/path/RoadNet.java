package com.org.nagradcore.model.path;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/4/004.
 */

public class RoadNet {
    private long mapId;
    private List<Vertex> vertexes;
    private Map<Long, Map<Long, List<Path>>> paths;

    public long getMapId() {
        return mapId;
    }

    public void setMapId(long mapId) {
        this.mapId = mapId;
    }

    public List<Vertex> getVertexes() {
        return vertexes;
    }

    public void setVertexes(List<Vertex> vertexes) {
        this.vertexes = vertexes;
    }

    public Map<Long, Map<Long, List<Path>>> getPaths() {
        return paths;
    }

    public void setPaths(Map<Long, Map<Long, List<Path>>> paths) {
        this.paths = paths;
    }

    public Map<Long, Map<Long, List<Connection>>> getConnections() {
        return connections;
    }

    public void setConnections(Map<Long, Map<Long, List<Connection>>> connections) {
        this.connections = connections;
    }
    private Map<Long, Map<Long, List<Connection>>> connections;
}
