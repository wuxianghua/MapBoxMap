/**
 *
 */
package com.palmap.astar.navi;


import com.palmap.astar.navi.entity.Path;
import com.palmap.astar.navi.entity.Vertex;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.linearref.LengthIndexedLine;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

/**
 * @author Vito Zheng
 */
public class AStar {

    private VertexLoader vertexLoader;
    private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(),3857);
    private static final double OFFSET = 1E-8;

    private final G g;
    private final H h;

    public AStar(G g, H h, VertexLoader vertexLoader) {
        this.g = g;
        this.h = h;
        this.vertexLoader = vertexLoader;
    }

    public List<AStarPath> astar(Point from, long fromPlanarGraphId, Point to, long toPlanarGraphId) {

        PriorityQueue<AStarVertex> openList = new PriorityQueue<>();
        HashSet<AStarVertex> closeList = new HashSet<>();

        ArrayList<AStarPath> paths = new ArrayList<>();

        // 1. find nearest linestring
        // 2. find nearest vertex on that linestring as start or end vertex
        // 3. calculate navigation based on start and end
        // 4. find from's projection on the nearest linestring
        // 5. if first path contains projection, then remove line from start to projection
        // 6. if first path doesn't contains projection, then add line from projection to start
        Path fromNearestPath = vertexLoader.findNearestPath(from, fromPlanarGraphId);
        Path toNearestPath = vertexLoader.findNearestPath(to, toPlanarGraphId);

        AStarVertex start = vertexLoader.findNearestVertexOnNearestPath(from, fromNearestPath);
        AStarVertex end = vertexLoader.findNearestVertexOnNearestPath(to, toNearestPath);
//        AStarVertex start = vertexLoader.findNearestVertex(from, fromPlanarGraphId);
//        AStarVertex end = vertexLoader.findNearestVertex(to, toPlanarGraphId);
        if (start == null || end == null) {
            return Collections.emptyList();
        }
        if(start.getVertex().getId() == end.getVertex().getId()) {
            paths.add(createAStarPath(start, end));
            return paths;
        }
        boolean onSameFloor = fromPlanarGraphId == toPlanarGraphId;
        if (!start.equals(end)) {
            openList.offer(start);
            AStarVertex current;
            ret:
            while (openList.size() > 0) {
                current = openList.poll();
                closeList.add(current);
                if(needCalcExtraPath(toNearestPath, start, end, current)) {
                    current.setNeedCalcExtraPath(true);
                }
                for (AStarPath path : current.getPaths()) {
                    AStarVertex vertex = path.getTo();
                    if (closeList.contains(vertex))
                        continue;

                    double g = this.g.G(current, path, onSameFloor) + current.getG();
                    double h = this.h.H(current, end);

                    if (openList.contains(vertex)) {
                        if (g + h < vertex.getG() + vertex.getH()) {
                            vertex.setG(g);
                            vertex.setH(h);
                            vertex.setParent(current);
                            openList.remove(vertex);
                            openList.add(vertex);
                        }
                    } else {
                        vertex.setG(g);
                        vertex.setH(h);
                        vertex.setParent(current);
                        openList.add(vertex);
                    }

                    if (vertex.equals(end)) {
                        do {
                            paths.add(vertex.getParent().findPath(vertex));
                            vertex = vertex.getParent();
                        } while (!vertex.equals(start));
                        break ret;
                    }
                }
            }

        }
        Collections.reverse(paths);
        adjustPaths(paths, fromNearestPath, toNearestPath, from, to);
        filterExtraPath(paths);
        return paths;
    }

    private boolean needCalcExtraPath(Path toNearestPath, AStarVertex start, AStarVertex end, AStarVertex current) {
        long currentId = current.getVertex().getId();
        return currentId == start.getVertex().getId() || currentId == end.getVertex().getId() || currentId == toNearestPath.getFrom().getId() || currentId == toNearestPath.getTo().getId();
    }

    private AStarPath createAStarPath(AStarVertex start, AStarVertex end) {
        long planarGraph = start.getVertex().getPlanarGraph();
        Path newPath = new Path();

        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(start.getVertex().getShape().getCoordinate());
        coordinates.add(end.getVertex().getShape().getCoordinate());
        LineString lineString = geometryFactory.createLineString(new CoordinateArraySequence(coordinates.toArray(new Coordinate[0])));
        newPath.setShape(lineString);
        newPath.setFrom(genNewVertex(lineString.getStartPoint(), planarGraph));
        newPath.setTo(genNewVertex(lineString.getEndPoint(), planarGraph));
        AStarPath aStarPath = new AStarLanePath(newPath, this.vertexLoader, false);
        return aStarPath;
    }

    private void adjustPaths(List<AStarPath> paths, Path fromNearestPath, Path toNearestPath, Point from, Point to) {
        adjustFirstPath(paths, fromNearestPath, from);
        adjustLastPath(paths, toNearestPath, to);
        filterExtraPath(paths);
    }

    private void adjustFirstPath(List<AStarPath> paths, Path fromNearestPath, Point from) {
        Coordinate fromProject = null;
        if(DistanceOp.distance(fromNearestPath.getShape(), from) >= Double.MAX_VALUE) {
            LengthIndexedLine lil = new LengthIndexedLine(fromNearestPath.getShape());
            fromProject = lil.extractPoint(fromNearestPath.getShape().getLength() / 2.0);
        } else {
            fromProject = DistanceOp.nearestPoints(fromNearestPath.getShape(), from)[0];
        }
        Point fromProjectPoint = geometryFactory.createPoint(fromProject);
        exactPaths(paths, fromProjectPoint, true);
    }

    private void adjustLastPath(List<AStarPath> paths, Path toNearestPath, Point to) {
        Coordinate toProject = null;
        if(DistanceOp.distance(toNearestPath.getShape(), to) >= Double.MAX_VALUE) {
            LengthIndexedLine lil = new LengthIndexedLine(toNearestPath.getShape());
            toProject = lil.extractPoint(toNearestPath.getShape().getLength() / 2.0);
        } else {
            toProject = DistanceOp.nearestPoints(toNearestPath.getShape(), to)[0];
        }
        Point toProjectPoint = geometryFactory.createPoint(toProject);
        exactPaths(paths, toProjectPoint, false);
    }

    private void exactPaths(List<AStarPath> paths, Point point, boolean isFromAdj) {
        if(paths == null || paths.size() == 0)
            return;
        int index = 0;
        if(!isFromAdj) {
            index = paths.size()-1;
        }
        AStarPath aStarPath = paths.get(index);
        if(!(aStarPath instanceof AStarLanePath))
            return;
        boolean isPathContainsProject = checkIfPointOnLine(point, ((AStarLanePath) aStarPath).getPath().getShape());
        AStarPath exactPath = genExactPath(((AStarLanePath) aStarPath), point, isFromAdj, isPathContainsProject);
        if(isPathContainsProject) {
            paths.set(index, exactPath);
        } else {
            if(isFromAdj)
                paths.add(index, exactPath);
            else
                paths.add(exactPath);
        }
    }

    private AStarPath genExactPath(AStarLanePath aStarPath, Point point, boolean isFromAdj, boolean isPathContainsProject) {
        long planarGraph = aStarPath.getPath().getPlanarGraph();
        Path newPath = new Path();
        Path origPath = aStarPath.getPath();
        newPath.setPlanarGraph(planarGraph);
        newPath.setDirection(origPath.getDirection());
        newPath.setRank(origPath.getRank());
        LineString[] segments = splitPath(aStarPath);
        List<Coordinate> coordinates = new ArrayList<>();

        if(isPathContainsProject) {
            int indexOfProjectLiedOn = getIndexProjectliedOn(point, segments);
            if(isFromAdj) {
                coordinates.add(point.getCoordinate());
                LineString[] validSegments = Arrays.copyOfRange(segments, indexOfProjectLiedOn, segments.length);
                for (int i = indexOfProjectLiedOn+1; i < validSegments.length; i++) {
                    coordinates.addAll(Arrays.asList(validSegments[i].getCoordinates()));
                }
                if(coordinates.size() == 1) {
                    int index = validSegments.length > indexOfProjectLiedOn ? indexOfProjectLiedOn : validSegments.length-1;
                    coordinates.add(validSegments[index].getEndPoint().getCoordinate());
                }
            } else {
                LineString[] validSegments = Arrays.copyOfRange(segments, 0, indexOfProjectLiedOn+1);
                for(int i = 0; i < indexOfProjectLiedOn; i++) {
                    coordinates.addAll(Arrays.asList(validSegments[i].getCoordinates()));
                }
                if(coordinates.size() == 0) {
                    int index = validSegments.length > indexOfProjectLiedOn ? indexOfProjectLiedOn : validSegments.length-1;
                    coordinates.add(validSegments[index].getStartPoint().getCoordinate());
                }
                coordinates.add(point.getCoordinate());
            }
        } else {
            if(isFromAdj) {
                coordinates.add(point.getCoordinate());
                coordinates.add(aStarPath.getFrom().getVertex().getShape().getCoordinate());
            } else {
                coordinates.add(aStarPath.getTo().getVertex().getShape().getCoordinate());
                coordinates.add(point.getCoordinate());
            }
        }
        LineString lineString = geometryFactory.createLineString(new CoordinateArraySequence(coordinates.toArray(new Coordinate[0])));
        newPath.setShape(lineString);
        newPath.setFrom(genNewVertex(lineString.getStartPoint(), planarGraph));
        newPath.setTo(genNewVertex(lineString.getEndPoint(), planarGraph));
        AStarPath exactPath = new AStarLanePath(newPath, this.vertexLoader, false);
        return exactPath;
    }

    private int getIndexProjectliedOn(Point point, LineString[] segments) {
        int target = 0;
        for(int i=0; i<segments.length; i++) {
            if(checkIfPointOnLine(point, segments[i])) {
                target = i;
            }
        }
        return target;
    }

    private LineString[] splitPath(AStarLanePath aStarPath) {
        Path path = aStarPath.getPath();
        LineString shape = (LineString) path.getShape().clone();
        List<Coordinate> coordinates = Arrays.asList(shape.getCoordinates());
        if(aStarPath.isReverse()) {
            Collections.reverse(coordinates);
        }
        LineString[] segments = new LineString[coordinates.size()-1];
        for(int i=0; i<coordinates.size()-1; i++) {
            Coordinate start = coordinates.get(i);
            Coordinate end = coordinates.get(i+1);
            LineString segment = new LineString(new CoordinateArraySequence(new Coordinate[]{start, end}), geometryFactory);
            segments[i] = segment;
        }
        return segments;
    }

    private Vertex genNewVertex(Point point, long planarGraph) {
        if(point == null)
            return null;
        Vertex vertex = new Vertex();
        vertex.setPlanarGraph(planarGraph);
        vertex.setShape(point);
        return vertex;
    }

    private boolean checkIfPointOnLine(Point point, LineString line) {
        double distance = DistanceOp.distance(point, line);
        return distance < OFFSET;
    }

    private void filterExtraPath(List<AStarPath> paths) {
        if(paths == null || paths.size() == 0)
            return;
        AStarPath first = paths.get(0);
        AStarPath last = null;
        if(paths.size() > 1) {
            last = paths.get(paths.size() - 1);
        }
        checkThenRemove(paths, first);
        checkThenRemove(paths, last);
    }

    private void checkThenRemove(List<AStarPath> paths,AStarPath path) {
        if(path == null)
            return;
        if(path instanceof AStarLanePath) {
            int firstNodeRank = ((AStarLanePath) path).getPath().getRank();
            if (firstNodeRank == 10) {
                paths.remove(path);
            }
        }
    }
}
