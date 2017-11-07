/**
 *
 */
package com.palmap.astar.navi;

import com.palmap.astar.navi.entity.Connection;
import com.palmap.astar.navi.entity.Path;
import com.palmap.astar.navi.entity.Vertex;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.index.quadtree.Quadtree;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * load vertexes, cache vertexes which are loaded
 *
 * @author Vito Zheng
 */
public class VertexLoader {

    private PathService pathService;

    private ConcurrentHashMap<Vertex, SoftReference<List<Path>>> pathCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Vertex, SoftReference<List<Path>>> extraPathCache = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Vertex, SoftReference<List<Connection>>> connectionCache = new ConcurrentHashMap<>();

    public VertexLoader(PathService pathService) {
        this.pathService = pathService;
    }

    public AStarVertex findNearestVertexOnNearestPath(Point point, Path nearestPath) {
        if (nearestPath == null)
            return null;
        Vertex vertex = point.distance(nearestPath.getFrom().getShape()) < point.distance(nearestPath.getTo().getShape()) ? nearestPath.getFrom() : nearestPath.getTo();
        return new AStarVertex(vertex, this);
    }

    public Path findNearestPath(Point point, long planarGraphId) {
        Quadtree quadtree = this.pathService.queryQuadTree(planarGraphId);
        if (quadtree == null) {
            return null;
        }
        Envelope envelop = point.getEnvelopeInternal();
        Envelope resultEnvelop = new Envelope(
                envelop.getMinX() - 200,
                envelop.getMaxX() + 200,
                envelop.getMinY() - 200,
                envelop.getMaxY() + 200
        );
        List paths = quadtree.query(resultEnvelop);
        if (paths.isEmpty()) {
            paths = this.pathService.queryAllPathFromIndex(planarGraphId);
        }
        Path result = null;
        double minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < paths.size(); i++) {
            Path path = (Path) paths.get(i);
            Geometry shape = path.getShape();
            double distance = DistanceOp.distance(point, shape);
            if (minDistance > distance) {
                minDistance = distance;
                result = path;
            }
        }
        return result;
    }

    public List<AStarPath> loadPaths(Vertex vertex, boolean needCalcExtraPath) {
        if (this.pathService != null) {
            List<AStarPath> aStarPaths = loadPathsFromRedisCache(vertex, needCalcExtraPath);
            if (aStarPaths != null && !aStarPaths.isEmpty()) {
                return aStarPaths;
            }
        }
        List<AStarPath> aStarPaths = new ArrayList<>();
        List<Path> paths = null;
        if (needCalcExtraPath) {
            paths = getPathsWithExtraPath(vertex);
        } else {
            paths = getPathsWithRealPath(vertex);
        }

        for (Path p: paths){
            if (p.getFrom().getId() == vertex.getId()){
                aStarPaths.add(new AStarLanePath(p, this, false));
            }else{
                aStarPaths.add(new AStarLanePath(p, this, true));
            }
        }

        List<Connection> connections = null;
        SoftReference<List<Connection>> ref2 = connectionCache.get(vertex);
        if (ref2 != null)
            connections = ref2.get();

        if (connections == null) {
            connections = getConnectionsFromDB(vertex);
            connectionCache.put(vertex, new SoftReference<>(connections));
        }

        for (Connection c: connections){
            if (c.getFrom().getId() == vertex.getId()){
                aStarPaths.add(new AStarConnectionPath(c, this, false));
            }else{
                aStarPaths.add(new AStarConnectionPath(c, this, true));
            }
        }

        return aStarPaths;
    }

    private List<Connection> getConnectionsFromDB(Vertex vertex) {
        return this.pathService.queryConnectionsByVertex(vertex);
    }

    private List<Path> getPathsWithRealPath(Vertex vertex) {
        List<Path> paths = null;
        SoftReference<List<Path>> ref = pathCache.get(vertex);
        if (ref != null)
            paths = ref.get();
        if (paths == null) {
            paths = getPathsFromDB(vertex);
            pathCache.put(vertex, new SoftReference<>(paths));
        }
        return paths;
    }

    private List<Path> getPathsFromDB(Vertex vertex) {
        return pathService.queryPathsByVertex(vertex);
    }

    private List<Path> getPathsWithExtraPath(Vertex vertex) {
        return pathService.queryPathsByVertex(vertex);
    }

    private List<AStarPath> loadPathsFromRedisCache(Vertex vertex, boolean needCalcExtraPath) {
        List<AStarPath> aStarPaths = new ArrayList<>();
        try {
            List<Path> paths = null;
            if (needCalcExtraPath) {
                paths = getPathsWithExtraPath(vertex);
            } else {
                paths = pathService.queryPathsByVertex(vertex);
            }
            checkPathSerializable(paths);

            for (Path p : paths){
                if (p.getFrom().getId() == vertex.getId()){
                    aStarPaths.add(new AStarLanePath(p, this, false));
                }else{
                    aStarPaths.add(new AStarLanePath(p, this, true));
                }
            }

            List<Connection> connections = pathService.queryConnectionsByVertex(vertex);
            for (Connection c : connections){
                if (c.getFrom().getId() == vertex.getId()){
                    aStarPaths.add(new AStarConnectionPath(c, this, false));
                }else{
                    aStarPaths.add(new AStarConnectionPath(c, this, true));
                }
            }
        } catch (Exception e) {
            return null;
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
            Point pathStart = path.getShape().getStartPoint();
            double check = DistanceOp.distance(from.getShape(), pathStart);
            if (check < 0.001) {
                continue;
            }
            LineString lineString = (LineString) path.getShape().clone();
            path.setShape((LineString) lineString.getFactory().createLineString(new CoordinateArraySequence(lineString.getCoordinateSequence())).reverse());
        }
    }
}
