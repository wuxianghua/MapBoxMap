package com.palmap.astar.navi;

import com.palmap.astar.navi.entity.Connection;
import com.palmap.astar.navi.entity.Path;
import com.palmap.astar.navi.entity.Vertex;
import com.palmap.astar.navi.geojson.GeoJsonReader;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.index.quadtree.Quadtree;
import com.vividsolutions.jts.io.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wtm on 2017/8/2.
 */
public class PathService {

    private static final String TAG = "PathService";

    //////////////////
    //private WKTReader wktReader;

    private GeoJsonReader geoJsonReader;

    //////////////////
    private HashMap<Long, Vertex> vertexList;

    //////////////////
    private HashMap<Long, HashMap<Long, Path>> _paths;
    private HashMap<Long, Quadtree> _quadtreeMapping = new HashMap<>();
    private HashMap<Long, HashMap<Long, ArrayList<Path>>> _pathsMapping = new HashMap<>();


    //////////////////
    private HashMap<Long, HashMap<Long, Connection>> _connections = new HashMap<>();
    private HashMap<Long, HashMap<Long, ArrayList<Connection>>> _connectionsMapping = new HashMap<>();

    public PathService(String vertexes, String paths, String connections) throws JSONException, ParseException, NumberFormatException {
        this(new JSONArray(vertexes), new JSONObject(paths), new JSONObject(connections));
    }

    public PathService(JSONArray vertexes, JSONObject rawPaths, JSONObject rawConnections) throws JSONException, ParseException {
        vertexList = new HashMap<>();
        _paths = new HashMap<>();
        //wktReader = new WKTReader();

        geoJsonReader = new GeoJsonReader();

        for (int i = 0; i < vertexes.length(); i++) {
            JSONObject vertexJson = vertexes.getJSONObject(i);

            Vertex vertex = new Vertex(
                    vertexJson.optLong("mapId"),
                    (Point) geoJsonReader.read(vertexJson.optString("shape")),
                    vertexJson.optLong("planarGraphId"),
                    vertexJson.optDouble("altitude"));

            vertex.setId(vertexJson.optLong("id"));
            vertex.setVirtual(vertexJson.optBoolean("virtual"));
            vertexList.put(vertex.getId(), vertex);
        }

        Iterator<String> keys = rawPaths.keys();
        while (keys.hasNext()) {
            long planarGraphId = Long.parseLong(keys.next());
            JSONObject planarGraphPaths = rawPaths.getJSONObject(planarGraphId + "");
            HashMap<Long, Path> tempMap = new HashMap<>();
            this._paths.put(planarGraphId, tempMap);
            Quadtree tempQuadtree = new Quadtree();
            this._quadtreeMapping.put(planarGraphId, tempQuadtree);
            HashMap<Long, ArrayList<Path>> tempPathsMapping = new HashMap<>();

            this._pathsMapping.put(planarGraphId, tempPathsMapping);

            Iterator<String> planarGraphPathsKeys = planarGraphPaths.keys();
            while (planarGraphPathsKeys.hasNext()) {
                long vertexId = Long.parseLong(planarGraphPathsKeys.next());
                JSONArray tempArr = null;
                try {
                    tempArr = planarGraphPaths.getJSONArray(vertexId + "");
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                ArrayList<Path> tempPathList = new ArrayList<>();
                tempPathsMapping.put(vertexId, tempPathList);
                for (int i = 0; i < tempArr.length(); i++) {
                    JSONObject tempPath = null;
                    try {
                        tempPath = tempArr.getJSONObject(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    long id = tempPath.optLong("id");
                    Path path = this._paths.get(planarGraphId).get(id);
                    if (path == null) {
                        path = new Path(
                                tempPath.optLong("mapId"),
                                tempPath.optInt("rank"),
                                tempPath.optString("direction"),
                                (LineString) geoJsonReader.read(tempPath.optString("shape")),
                                tempPath.optLong("planarGraphId"),
                                tempPath.optLong("pathId"),
                                tempPath.optDouble("altitude")
                        );
                        path.setId(id);
                        path.setFrom(vertexList.get(tempPath.optLong("from")));
                        path.setTo(vertexList.get(tempPath.optLong("to")));

                        this._paths.get(planarGraphId).put(id, path);
                        tempQuadtree.insert(path.getShape().getEnvelopeInternal(), path);
                    }
                    tempPathList.add(path);
                }
            }
        }


        Iterator<String> connectionsKeys = rawConnections.keys();
        while (connectionsKeys.hasNext()) {
            JSONObject planarGraphConnections = null;
            HashMap<Long, Connection> hashMap = new HashMap<>();
            HashMap<Long, ArrayList<Connection>> mappingMap = new HashMap<>();
            long planarGraphId = Long.parseLong(connectionsKeys.next());
            this._connections.put(planarGraphId, hashMap);
            this._connectionsMapping.put(planarGraphId, mappingMap);
            planarGraphConnections = rawConnections.optJSONObject(planarGraphId + "");
            if (null == planarGraphConnections) {
                continue;
            }

            Iterator<String> vertexKeys = planarGraphConnections.keys();
            while (vertexKeys.hasNext()) {

                String vertexKey = vertexKeys.next();


                JSONArray vertexConnections = null;
                ArrayList<Connection> mappingMapList = null;
                long vertexId = Long.parseLong(vertexKey);
                vertexConnections = planarGraphConnections.optJSONArray(vertexKey);
                if (null == vertexConnections) {
                    continue;
                }
                mappingMapList = new ArrayList<>();
                mappingMap.put(vertexId, mappingMapList);
                for (int i = 0; i < vertexConnections.length(); i++) {
                    JSONObject rawConnection = vertexConnections.optJSONObject(i);
                    if (null == rawConnection) {
                        continue;
                    }
                    if (hashMap.get(rawConnection.optLong("id")) == null) {
                        Connection connection = new Connection(
                                rawConnection.optLong("mapId"),
                                rawConnection.optString("direction"),
                                rawConnection.optInt("rank"));

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


    public Quadtree queryQuadTree(long planarGraphId) {
        return this._quadtreeMapping.get(planarGraphId);
    }

    public ArrayList<Path> queryPathsByVertex(Vertex vertex) {
        HashMap<Long, ArrayList<Path>> paths = this._pathsMapping.get(vertex.getPlanarGraph());
        return paths != null && paths.get(vertex.getId()) != null ? paths.get(vertex.getId()) : new ArrayList<Path>();
    }

    public ArrayList<Connection> queryConnectionsByVertex(Vertex vertex) {
        HashMap<Long, ArrayList<Connection>> connections = this._connectionsMapping.get(vertex.getPlanarGraph());
        return connections != null && connections.get(vertex.getId()) != null ? connections.get(vertex.getId()) : new ArrayList<Connection>();
    }

    public List queryAllPathFromIndex(long planarGraphId) {
        Quadtree quadtree = this._quadtreeMapping.get(planarGraphId);
        return quadtree != null ? quadtree.queryAll() : Collections.emptyList();
    }

}
