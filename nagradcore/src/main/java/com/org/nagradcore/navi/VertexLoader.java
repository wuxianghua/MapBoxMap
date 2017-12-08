package com.org.nagradcore.navi;
import android.util.Log;

import com.org.nagradcore.GeometryFactories;
import com.org.nagradcore.Lists;
import com.org.nagradcore.dto.POIDto;
import com.org.nagradcore.model.Feature;
import com.org.nagradcore.model.PoiInfo;
import com.org.nagradcore.model.path.Connection;
import com.org.nagradcore.model.path.Direction;
import com.org.nagradcore.model.path.Path;
import com.org.nagradcore.model.path.TreatedRoadNet;
import com.org.nagradcore.model.path.Vertex;
import com.org.nagradcore.utils.GeometryUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.index.quadtree.Quadtree;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * load vertexes, cache vertexes which are loaded
 *
 * @author Vito Zheng
 */
public class VertexLoader {

    private static final int GEN_RANK = 10;

    private TreatedRoadNet treatedRoadNet;
    private Map<Long, List<Path>> tmpVertexPathMap = new HashMap<>();
    private List<Path> removedPaths = new ArrayList<>();

    // generate vertex/path set id negative
    private AtomicLong vertexAtomicLong = new AtomicLong(0);
    private AtomicLong pathAtomicLong = new AtomicLong(0);

    public VertexLoader(TreatedRoadNet treatedRoadNet) {
        this.treatedRoadNet = treatedRoadNet;
    }

    public AStarVertex genVertexOnArea(Point point, PoiInfo poi) {
        Vertex vertex = null;
        Set<Long> doorIds = poi.doorIds;
        List<Path> paths = new ArrayList<>();
        if (doorIds != null && doorIds.size() > 0) {
            List<Vertex> doorVertexes = new ArrayList<>();
            for (Long doorId : doorIds) {
                if (getVertexByDoorId(doorId) != null) {
                    doorVertexes.add(getVertexByDoorId(doorId));
                }
            }
            if (doorVertexes.size() > 0) {
                vertex = genNewVertex(point, doorVertexes.get(0));
                for (Vertex doorVertex : doorVertexes) {
                    LineString lineString = GeometryFactories.pseudoMercator()
                            .createLineString(new Coordinate[]{ vertex.getShape().getCoordinate(), doorVertex.getShape().getCoordinate() });
                    Path path = genNewPath(vertex, doorVertex, lineString, GEN_RANK, Direction.TWOWAY);
                    paths.add(path);
                    List<Path> doorVertexPaths = new ArrayList<>();
                    doorVertexPaths.add(path);
                    doorVertexPaths.addAll(queryPathsByVertex(doorVertex));
                    tmpVertexPathMap.put(doorVertex.getId(), doorVertexPaths);
                }

                tmpVertexPathMap.put(vertex.getId(), paths);
            }
        }

        if (vertex == null) return null;
        return new AStarVertex(vertex, this);
    }

    @SuppressWarnings("unchecked")
    public boolean checkAreaHavePaths(PoiInfo poi, Point point) {
        List<Path> nearPaths = getQuadtree(poi.planarGraphId).query(point.getEnvelopeInternal());
        for (Path nearPath : nearPaths) {
            if (poi.shape.contains(nearPath.getShape())) {
                return true;
            }
        }
        return false;
    }

    public AStarVertex genVertexOnNearestPath(Point point, Path nearestPath) {
        if (nearestPath == null) {
            return null;
        }

        //找到起终点对应的路网    切割最近的路网   并将路网更新
        Coordinate coordinate = GeometryUtils.getProjectOnLine(point, (LineString) nearestPath.getShape());
        Point projectPoint = GeometryFactories.pseudoMercator().createPoint(coordinate);

        Vertex vertex;
        Vertex from = nearestPath.getFrom();
        Vertex to = nearestPath.getTo();

        // 如果最近路网的一端是联通设施，取路网的另一端
        List<Connection> connections = queryConnectionsByVertex(from);
        if (!connections.isEmpty()) {
            return new AStarVertex(to, this);
        }
        connections = queryConnectionsByVertex(to);
        if (!connections.isEmpty()) {
            return new AStarVertex(from, this);
        }

        Long planarGraphId = nearestPath.getPlanarGraphId();
        Map<Long, List<Path>> pathsMap = treatedRoadNet.getPaths().get(planarGraphId);
        List<Path> fromPaths = pathsMap.get(from.getId());
        List<Path> toPaths = pathsMap.get(to.getId());

        if (projectPoint.distance(from.getShape()) < NaviConstants.TOLERANCE) {
            vertex = from;
        } else if (projectPoint.distance(to.getShape()) < NaviConstants.TOLERANCE) {
            vertex = to;
        } else {
            vertex = genNewVertex(projectPoint, from);
            List<Geometry> spilt = GeometryUtils.spilt(nearestPath.getShape(), projectPoint);
            Path path1 = genNewPath(from, vertex, nearestPath, (LineString) spilt.get(0));
            Path path2 = genNewPath(vertex, to, nearestPath, (LineString) spilt.get(1));

            List<Path> genFromPaths = Lists.newArrayList();
            genFromPaths.addAll(fromPaths);
            genFromPaths.remove(nearestPath);
            genFromPaths.add(path1);
            tmpVertexPathMap.put(from.getId(), genFromPaths);

            List<Path> genToPaths = Lists.newArrayList();
            genToPaths.addAll(toPaths);
            genToPaths.remove(nearestPath);
            genToPaths.add(path2);
            tmpVertexPathMap.put(to.getId(), genToPaths);
            tmpVertexPathMap.put(vertex.getId(), Lists.newArrayList(path1, path2));

            removedPaths.add(nearestPath);
        }
        return new AStarVertex(vertex, this);
    }

    @SuppressWarnings("unchecked")
    public Path findNearestPath(Point point, long planarGraphId) {
        Quadtree quadtree = getQuadtree(planarGraphId);
        if (quadtree == null) {
            return null;
        }

        List<Path> paths = quadtree.queryAll();
        if (paths.size() > 100) {
            paths = quadtree.query(point.getEnvelopeInternal());
        }
        if (paths.isEmpty()) {
            return null;
        }

        checkPathSerializable(paths);
        SortedSet<DistancePath> set = new TreeSet<>(new SortByDistance());
        for (Path path : paths) {
            Geometry line = path.getShape();
            double distance = DistanceOp.distance(point, line);
            set.add(new DistancePath(distance, path));
        }
        return set.first().getPath();
    }

    public List<AStarPath> loadPaths(Vertex vertex, boolean needCalcExtraPath) {
        List<AStarPath> aStarPaths = new ArrayList<>();
        List<Path> paths = queryPathsByVertex(vertex);
        List<Path> tempPaths = new ArrayList<>();
        if (!needCalcExtraPath) {
            for (Path path : paths) {
                if (path.getRank() == 10) continue;
                tempPaths.add(path);
            }
            paths.clear();
            paths.addAll(tempPaths);
        }
        if (!paths.isEmpty()) {
            checkPathSerializable(paths);
            for (Path path : paths) {
                if (path.getFrom().getId().equals(vertex.getId())) {
                    aStarPaths.add(new AStarLanePath(path,this,false));
                }else {
                    aStarPaths.add(new AStarLanePath(path,this,true));
                }
            }
        }

        List<Connection> connections = new ArrayList<>();
        List<Connection> mconnections = queryConnectionsByVertex(vertex);
        for (Connection connection : mconnections) {
            if (connection.getTo() != null) {
                connections.add(connection);
            }
        }
        if (connections.isEmpty()) {
            return aStarPaths;
        }
        for (Connection connection : connections) {
            if (connection.getFrom().getId().equals(vertex.getId())) {
                aStarPaths.add(new AStarConnectionPath(connection,this,false));
            }else {
                aStarPaths.add(new AStarConnectionPath(connection,this,false));
            }
        }
        return aStarPaths;
    }

    /**
     * This is a temp approach to fix linestring serializable bug
     *
     * @param paths
     */
    private void checkPathSerializable(List<Path> paths) {
        for (Path path : paths) {
            Vertex from = path.getFrom();
            Point pathStart = ((LineString) path.getShape()).getStartPoint();
            double check = DistanceOp.distance(from.getShape(), pathStart);
            if (check < 0.001) {
                continue;
            }
            LineString lineString = (LineString) path.getShape().clone();
            path.setShape(lineString.getFactory().createLineString(new CoordinateArraySequence(lineString.getCoordinateSequence())).reverse());
        }
    }

    private Vertex genNewVertex(Point point, Vertex refer) {
        if (point == null) {
            return null;
        }
        Vertex vertex = new Vertex();
        vertex.setId(vertexAtomicLong.decrementAndGet());
        vertex.setShape(point);
        vertex.setPlanarGraphId(refer.getPlanarGraphId());
        vertex.setMapId(refer.getMapId());
        vertex.setAltitude(refer.getAltitude());
        return vertex;
    }

    public Path genNewPath(Vertex from, Vertex to, Path refer, LineString lineString) {
        Path path = new Path();
        path.setId(pathAtomicLong.decrementAndGet());
        path.setShape(lineString);
        path.setFrom(from);
        path.setTo(to);
        path.setPlanarGraphId(refer.getPlanarGraphId());
        path.setMapId(refer.getMapId());
        path.setDirection(refer.getDirection());
        path.setRank(refer.getRank());
        path.setPathId(refer.getId());
        path.setAltitude(refer.getAltitude());
        return path;
    }

    public Path genNewPath(Vertex from, Vertex to, LineString lineString, int rank, Direction direction) {
        Path path = new Path();
        path.setId(pathAtomicLong.decrementAndGet());
        path.setShape(lineString);
        path.setFrom(from);
        path.setTo(to);
        path.setPlanarGraphId(from.getPlanarGraphId());
        path.setMapId(from.getMapId());
        path.setAltitude(from.getAltitude());
        path.setPathId(path.getId());
        path.setRank(rank);
        path.setDirection(direction);
        return path;
    }

    public Quadtree getQuadtree(long planarGraphId) {
        if (treatedRoadNet == null) return null;
        return treatedRoadNet.getQuadtrees().get(planarGraphId);
    }

    private Vertex getVertexByDoorId(long doorId) {
        if (treatedRoadNet == null) return null;
        for (Vertex vertex : treatedRoadNet.getVertexes()) {
            if (vertex.getDoorId() != null && vertex.getDoorId().equals(doorId)) {
                return vertex;
            }
        }
        return null;
    }

    public List<Path> queryPathsByVertex(Vertex vertex) {
        List<Path> paths = new ArrayList<>();
        if (tmpVertexPathMap.containsKey(vertex.getId())) {
            paths = tmpVertexPathMap.get(vertex.getId());
        } else {
            if (treatedRoadNet != null) {
                Map<Long, List<Path>> vertexPathsMap = treatedRoadNet.getPaths().get(vertex.getPlanarGraphId());
                if (vertexPathsMap != null && vertexPathsMap.get(vertex.getId()) != null) {
                    paths = vertexPathsMap.get(vertex.getId());
                }
            }
        }

        paths.removeAll(removedPaths);
        return paths;
    }

    @SuppressWarnings("unchecked")
    public List<Path> queryAllPathsFromIndex(long planarGraphId) {
        if (treatedRoadNet == null) return new ArrayList<>();
        return treatedRoadNet.getQuadtrees().get(planarGraphId).queryAll();
    }

    public List<Connection> queryConnectionsByVertex(Vertex vertex) {
        if (treatedRoadNet == null) return new ArrayList<>();
        Map<Long, List<Connection>> vertexConnectionsMap = treatedRoadNet.getConnections().get(vertex.getPlanarGraphId());
        if (vertexConnectionsMap == null || vertexConnectionsMap.get(vertex.getId()) == null) {
            return new ArrayList<>();
        } else {
            return vertexConnectionsMap.get(vertex.getId());
        }
    }

    private class SortByDistance implements Comparator<DistancePath> {
        @Override
        public int compare(DistancePath o1, DistancePath o2) {
            return o1.getDistance().compareTo(o2.getDistance());
        }
    }
}
