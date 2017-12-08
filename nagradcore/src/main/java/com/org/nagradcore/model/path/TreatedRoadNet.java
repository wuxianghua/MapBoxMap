package com.org.nagradcore.model.path;

import com.org.nagradcore.geojson.GeoJsonReader;
import com.org.nagradcore.model.Base;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.index.quadtree.Quadtree;
import com.vividsolutions.jts.io.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by sifan on 2016/12/19.
 */
public class TreatedRoadNet extends Base implements Serializable {

    private long mapId;
    private List<Vertex> vertexes;
    private Map<Long, Map<Long, List<Path>>> paths;
    private HashMap<Long,Vertex> vertexList;

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

    public Map<Long, Quadtree> getQuadtrees() {
        return quadtrees;
    }

    public void setQuadtrees(Map<Long, Quadtree> quadtrees) {
        this.quadtrees = quadtrees;
    }

    private Map<Long, Map<Long, List<Connection>>> connections = new HashMap<>();
    private Map<Long,Map<Long,Connection>> tmpConnections = new HashMap<>();
    private Map<Long, Quadtree> quadtrees = new HashMap<>();
    private GeoJsonReader geoJsonReader;
    private HashMap<Long,HashMap<Long,Path>> tmpPaths;
    public TreatedRoadNet(long mapId, JSONArray vertexes, JSONObject rawPaths,JSONObject rawConnections) throws JSONException, ParseException {
        this.mapId = mapId;
        vertexList = new HashMap<>();
        this.vertexes = new ArrayList<>();
        this.paths = new HashMap<>();
        tmpPaths = new HashMap<>();
        geoJsonReader = new GeoJsonReader();
        for (int i = 0; i < vertexes.length(); i++) {
            JSONObject vertexJson = vertexes.getJSONObject(i);
            Vertex vertex = new Vertex(
                    vertexJson.optLong("mapId"),
                    geoJsonReader.read(vertexJson.optString("shape")),
                    vertexJson.optLong("planarGraphId"),
                    vertexJson.optDouble("altitude")
            );
            vertex.setId(vertexJson.optLong("id"));
            vertex.setDoorId(vertexJson.optLong("doorId"));
            vertex.setVirtual(vertexJson.optBoolean("virtual"));
            vertexList.put(vertex.getId(),vertex);
            this.vertexes.add(vertex);
        }
        Iterator<String> keys = rawPaths.keys();
        while (keys.hasNext()) {
            long planarGraphId = Long.parseLong(keys.next());
            JSONObject planarGraphPaths = rawPaths.getJSONObject(planarGraphId + "");
            HashMap<Long,Path> tempMap = new HashMap<>();
            tmpPaths.put(planarGraphId,tempMap);
            Quadtree tempQuadtree = new Quadtree();
            quadtrees.put(planarGraphId,tempQuadtree);
            HashMap<Long,List<Path>> tempPath = new HashMap<>();
            paths.put(planarGraphId,tempPath);
            Iterator<String> planarGraphPathsKeys = planarGraphPaths.keys();
            while (planarGraphPathsKeys.hasNext()) {
                long vertexId = Long.parseLong(planarGraphPathsKeys.next());
                JSONArray tempArr;
                tempArr = planarGraphPaths.getJSONArray(vertexId + "");
                List<Path> tempPathList = new ArrayList<>();
                tempPath.put(vertexId,tempPathList);
                for (int i = 0; i < tempArr.length(); i++) {
                    JSONObject mTempPath = null;
                    mTempPath = tempArr.getJSONObject(i);
                    long id = mTempPath.optLong("id");
                    Path path = this.tmpPaths.get(planarGraphId).get(id);
                    if (path == null) {
                        path = new Path(
                                mTempPath.optLong("mapId"),
                                mTempPath.optInt("rank"),
                                mTempPath.optString("direction"),
                                (LineString) geoJsonReader.read(mTempPath.optString("shape")),
                                mTempPath.optLong("planarGraphId"),
                                mTempPath.optLong("pathId"),
                                mTempPath.optDouble("altitude")
                        );
                        path.setId(id);
                        path.setFrom(vertexList.get(mTempPath.optLong("from")));
                        path.setTo(vertexList.get(mTempPath.optLong("to")));
                        tmpPaths.get(planarGraphId).put(id,path);
                        tempQuadtree.insert(path.getShape().getEnvelopeInternal(),path);
                    }
                    tempPathList.add(path);
                }
            }
        }
        Iterator<String> connectionsKeys = rawConnections.keys();
        while (connectionsKeys.hasNext()) {
            JSONObject planarGraphConnections = null;
            HashMap<Long,Connection> hashMap = new HashMap<>();
            HashMap<Long,List<Connection>> mappingMap = new HashMap<>();
            long planarGraphId = Long.parseLong(connectionsKeys.next());
            tmpConnections.put(planarGraphId,hashMap);
            connections.put(planarGraphId,mappingMap);
            planarGraphConnections = rawConnections.optJSONObject(planarGraphId+"");
            if (planarGraphConnections == null) {
                continue;
            }
            Iterator<String> vertexKeys = planarGraphConnections.keys();
            while (vertexKeys.hasNext()) {
                String vertexKey = vertexKeys.next();
                JSONArray verexConnections = null;
                ArrayList<Connection> mappingMapList = null;
                long vertexId = Long.parseLong(vertexKey);
                verexConnections = planarGraphConnections.optJSONArray(vertexKey);
                if (verexConnections == null) {
                    continue;
                }
                mappingMapList = new ArrayList<>();
                mappingMap.put(vertexId,mappingMapList);
                for (int i = 0; i < verexConnections.length(); i++) {
                    JSONObject rawConnection = verexConnections.optJSONObject(i);
                    if (rawConnection == null) {
                        continue;
                    }
                    if (hashMap.get(rawConnection.optLong("id")) == null) {
                        Connection connection = new Connection(
                                rawConnection.optLong("mapId"),
                                rawConnection.optString("direction"),
                                rawConnection.optInt("rank")
                        );
                        connection.setId(rawConnection.optLong("id"));
                        connection.setFrom(this.vertexList.get(rawConnection.optLong("from")));
                        connection.setTo(this.vertexList.get(rawConnection.optLong("to")));
                        connection.setCategoryId(rawConnection.optLong("categoryId"));
                        hashMap.put(rawConnection.optLong("id"), connection);
                        mappingMapList.add(connection);
                    } else {
                        Connection connection = hashMap.get(rawConnection.optLong("id"));
                        mappingMapList.add(connection);
                    }
                }
            }
        }
    }
}
