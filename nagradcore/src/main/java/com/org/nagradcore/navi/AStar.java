package com.org.nagradcore.navi;
import android.util.Log;

import com.org.nagradcore.GeometryFactories;
import com.org.nagradcore.Lists;
import com.org.nagradcore.dto.POIDto;
import com.org.nagradcore.model.PoiInfo;
import com.org.nagradcore.model.path.Path;
import com.org.nagradcore.model.path.Vertex;
import com.org.nagradcore.utils.GeometryUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import java.util.*;


/**
 * @author Vito Zheng
 */
public class AStar {

    private VertexLoader vertexLoader;

    private final G g;
    private final H h;

    public AStar(G g, H h, VertexLoader vertexLoader) {
        this.g = g;
        this.h = h;
        this.vertexLoader = vertexLoader;
    }

    public List<AStarPath> astar(Point from, long fromPlanarGraphId, Point to, long toPlanarGraphId, double floorHeightDiff) {
        return astar(from, fromPlanarGraphId, null, to, toPlanarGraphId, null, floorHeightDiff);
    }

    public List<AStarPath> astar(Point from, long fromPlanarGraphId, PoiInfo fromArea, Point to, long toPlanarGraphId, PoiInfo toArea, double floorHeightDiff) {
        List<AStarPath> paths = new ArrayList<>();
        if (DistanceOp.distance(from, to) < NaviConstants.TOLERANCE) {
            return paths;
        }

        PriorityQueue<AStarVertex> openList = new PriorityQueue<>();
        HashSet<AStarVertex> closeList = new HashSet<>();

        AStarVertex start = null;
        AStarVertex end = null;
        if (fromArea != null && !vertexLoader.checkAreaHavePaths(fromArea, from)) {
            start = vertexLoader.genVertexOnArea(from, fromArea);
        }

        if (toArea != null && !vertexLoader.checkAreaHavePaths(toArea, to)) {
            end = vertexLoader.genVertexOnArea(to, toArea);
        }

        Path fromNearestPath = null;
        Path toNearestPath = null;
        if (start == null) {
            // 找起始点/终点最近的路网
            fromNearestPath = vertexLoader.findNearestPath(from, fromPlanarGraphId);
            // 以起始点/终点在最近路网的投影将路网分割，加入到此次路算中。并生成AstarVertex
            start = vertexLoader.genVertexOnNearestPath(from, fromNearestPath);
        }

        if (end == null) {
            // 找起始点/终点最近的路网
            toNearestPath = vertexLoader.findNearestPath(to, toPlanarGraphId);
            // 以起始点/终点在最近路网的投影将路网分割，加入到此次路算中。并生成AstarVertex
            end = vertexLoader.genVertexOnNearestPath(to, toNearestPath);
        }

        if (start == null || end == null) {
            return Collections.emptyList();
        }

        // 起点终点在同一条路网，直接相连
        if (fromNearestPath != null && toNearestPath != null && fromNearestPath.equals(toNearestPath)) {
            AStarLanePath aStarLanePath = genPathForSameNearst(start.getVertex(), end.getVertex(), fromNearestPath);
            if (aStarLanePath != null) {
                paths.add(aStarLanePath);
            }
            return paths;
        }

        // A* 寻路算法：  需要两个列表  Open： 所有被考虑来寻找最短路径的节点   Closed：不被考虑的路节点
        if (!start.equals(end)) {
            openList.offer(start);
            AStarVertex current;
            ret:
            while (openList.size() > 0) {
                current = openList.poll();
                closeList.add(current);
                List<AStarPath> mPaths = current.getPaths();
                for (AStarPath path : mPaths) {
                    AStarVertex vertex = path.getTo();
                    if (closeList.contains(vertex)) {
                        continue;
                    }

                    // 路径增量  每一条路网  G + H 的值
                    // 在同一楼层联通设施G  40  否则 20
                    // AStarLanePath  G 为 weight  即 path.getShape().getLength() * (path.getRank() + 1)
                    double g = this.g.G(vertex, path, floorHeightDiff) + current.getG();   // G （开始到当前的移动量）
                    double h = this.h.H(vertex, end);                                  // H 当前到目标的移动量估值  暂时为高度差与距离差的和

                    if (openList.contains(vertex)) {
                        AStarVertex[] vertices = openList.toArray(new AStarVertex[openList.size()]);
                        ArrayList<AStarVertex> starVertices = Lists.newArrayList(vertices);
                        int i = starVertices.indexOf(vertex);
                        AStarVertex aStarVertex = starVertices.get(i);

                        if (g + h < aStarVertex.getG() + aStarVertex.getH()) {
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
        filterExtraPath(paths);
        paths = splitPaths(paths);
        //paths = combineCollinear(paths);
        return paths;
    }

    /**
     * 起点终点在同一条路网，直接相连
     *
     * @param from       起点投影
     * @param to         终点投影
     * @param nearstPath 最近的路网
     * @return
     */
    private AStarLanePath genPathForSameNearst(Vertex from, Vertex to, Path nearstPath) {
        // 如果起始点终点投影在同一条路网上，将路网分成三段，取出中间一段即为要返回的路径
        // 1. 起点投影将路径分为两段
        Point startPoint = (Point) from.getShape();
        Point endPoint = (Point) to.getShape();

        List<Geometry> startSpilt = GeometryUtils.spilt(nearstPath.getShape(), startPoint);
        // 2. 取出分割的第二段再用终点投影分割得到的第一段
        List<Geometry> endSpilt = GeometryUtils.spilt(startSpilt.get(0), endPoint);
        if (endSpilt.isEmpty()) {
            endSpilt = GeometryUtils.spilt(startSpilt.get(1), endPoint);
        }

        LineString lineString = null;
        for (Geometry geometry : endSpilt) {
            LineString line = (LineString) geometry;
            if (line.getStartPoint().distance(startPoint) < NaviConstants.TOLERANCE && line.getEndPoint().distance(endPoint) < NaviConstants.TOLERANCE) {
                lineString = line;
            } else if (line.getEndPoint().distance(startPoint) < NaviConstants.TOLERANCE && line.getStartPoint().distance(endPoint) < NaviConstants.TOLERANCE) {
                lineString = (LineString) line.reverse();
            }
        }

        if (lineString == null) return null;
        Path path = vertexLoader.genNewPath(from, to, nearstPath, lineString);
        return new AStarLanePath(path, vertexLoader, false);
    }

    /**
     * 处理可以直接相连的路径（相邻路径在同一直线上的）
     *
     * @param paths
     * @return
     */
    private List<AStarPath> combineCollinear(List<AStarPath> paths) {
        int size = paths.size();
        if (size < 2) {
            return paths;
        }
        if (!hasCanCombined(paths)) {
            return paths;
        }
        List<AStarPath> newPaths = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (i >= paths.size()) {
                break;
            }

            AStarPath path1 = paths.get(i);
            if (i == paths.size() - 1) {
                newPaths.add(path1);
                break;
            }
            if (path1 instanceof AStarConnectionPath) {
                newPaths.add(path1);
                continue;
            }
            AStarPath path2 = paths.get(i + 1);
            if (path2 instanceof AStarConnectionPath) {
                newPaths.add(path1);
                continue;
            }
            if (checkIfCanCombine((AStarLanePath) path1, (AStarLanePath) path2)) {
                AStarLanePath newPath = combineTwoPaths((AStarLanePath) path1, (AStarLanePath) path2);
                newPaths.add(newPath);
                paths.remove(path2);
            } else {
                newPaths.add(path1);
            }
        }
        return combineCollinear(newPaths);
    }

    /**
     * 检查是否有可以连接起来的路径
     *
     * @param paths
     * @return
     */
    private boolean hasCanCombined(List<AStarPath> paths) {
        for (int i = 0; i < paths.size() - 1; i++) {
            AStarPath path1 = paths.get(i);
            if (path1 instanceof AStarConnectionPath) {
                continue;
            }
            AStarPath path2 = paths.get(i + 1);
            if (path2 instanceof AStarConnectionPath) {
                continue;
            }
            if (checkIfCanCombine((AStarLanePath) path1, (AStarLanePath) path2)) {
                return true;
            }
        }
        return false;
    }

    private AStarLanePath combineTwoPaths(AStarLanePath path1, AStarLanePath path2) {
        Path path = new Path();
        path.setId(path1.getPath().getId());
        path.setPlanarGraphId(path1.getPath().getPlanarGraphId());
        path.setMapId(path1.getPath().getMapId());
        LineString shape1 = (LineString) path1.getPath().getShape().clone();
        LineString shape2 = (LineString) path2.getPath().getShape().clone();
        if (path1.isReverse() != path2.isReverse()) {
            shape2 = (LineString) shape2.reverse();
        }
        LineString shape = mergeTwoLineString(shape1, shape2, path1.isReverse());
        path.setShape(shape);
        return new AStarLanePath(path, this.vertexLoader, path1.isReverse());
    }

    private LineString mergeTwoLineString(LineString shape1, LineString shape2, boolean reverse) {
        LineString lineString;
        if (reverse) {
            lineString = GeometryFactories.pseudoMercator().createLineString(new CoordinateArraySequence(
                    new Coordinate[]{shape2.getStartPoint().getCoordinate(), shape1.getEndPoint().getCoordinate()}));
        } else {
            lineString = GeometryFactories.pseudoMercator().createLineString(new CoordinateArraySequence(
                    new Coordinate[]{shape1.getStartPoint().getCoordinate(), shape2.getEndPoint().getCoordinate()}));
        }
        return lineString;
    }

    /**
     * 检查两条路径是否可以连接
     *
     * @param path1
     * @param path2
     * @return
     */
    private boolean checkIfCanCombine(AStarLanePath path1, AStarLanePath path2) {
        if (!path1.getPath().getPlanarGraphId().equals(path2.getPath().getPlanarGraphId())) {
            return false;
        }
        if (checkIfMultiPoints(path1) || checkIfMultiPoints(path2)) {
            return false;
        }
//        if(DistanceOp.distance(path1.getPath().getShape().getEndPoint(),path2.getPath().getShape().getStartPoint()) > OFFSET)
//            return false;
        return checkIfCollinear(path1, path2);
    }

    /**
     * 检查两条路径是否在一直线上
     *
     * @param path1
     * @param path2
     * @return
     */
    private boolean checkIfCollinear(AStarLanePath path1, AStarLanePath path2) {
        double slope1 = getSlope(path1);
        double slope2 = getSlope(path2);
        if (Double.isInfinite(slope1) && Double.isInfinite(slope2)) {
            return true;
        }
        double arctan1 = Math.atan(slope1);
        double acrtan2 = Math.atan(slope2);
        return (Math.abs(acrtan2 - arctan1) < 0.05) || (Math.PI - Math.abs(acrtan2 - arctan1) < 0.05);
    }

    /**
     * 获取路径斜率
     *
     * @param path
     * @return
     */
    private double getSlope(AStarLanePath path) {
        LineString shape = (LineString) path.getPath().getShape();
        double x1 = shape.getStartPoint().getX();
        double y1 = shape.getStartPoint().getY();
        double x2 = shape.getEndPoint().getX();
        double y2 = shape.getEndPoint().getY();
        return (y2 - y1) / (x2 - x1);
    }

    private boolean checkIfMultiPoints(AStarLanePath path) {
        return path.getPath().getShape().getCoordinates().length > 2;
    }

    /**
     * 去除辅助导航的路网
     *
     * @param paths
     */
    private void filterExtraPath(List<AStarPath> paths) {
        if (paths == null || paths.size() == 0) {
            return;
        }
        AStarPath first = paths.get(0);
        AStarPath last = null;
        if (paths.size() > 1) {
            last = paths.get(paths.size() - 1);
        }
        checkThenRemove(paths, first);
        checkThenRemove(paths, last);
    }

    private void checkThenRemove(List<AStarPath> paths, AStarPath path) {
        if (path == null) {
            return;
        }
        if (path instanceof AStarLanePath) {
            int firstNodeRank = ((AStarLanePath) path).getPath().getRank();
            if (firstNodeRank == 10) {
                paths.remove(path);
            }
        }
    }

    private List<AStarPath> splitPaths(List<AStarPath> paths) {
        List<AStarPath> aStarPaths = new ArrayList<>();
        for (AStarPath path : paths) {
            if (path instanceof AStarLanePath) {
                aStarPaths.addAll(splitPath((AStarLanePath) path));
            } else {
                aStarPaths.add(path);
            }
        }
        return aStarPaths;
    }

    private List<AStarLanePath> splitPath(AStarLanePath aStarLanePath) {
        List<AStarLanePath> aStarPaths = new ArrayList<>();
        LineString pathLineString = (LineString) aStarLanePath.getPath().getShape().clone();
        int count = aStarLanePath.getPath().getShape().getCoordinates().length;
        if (count > 2) {
            if (!aStarLanePath.isReverse()) {
                for (int i = 0; i < count - 1; i++) {
                    LineString lineString = GeometryFactories.pseudoMercator().createLineString(new CoordinateArraySequence(
                            new Coordinate[]{pathLineString.getPointN(i).getCoordinate(), pathLineString.getPointN(i + 1).getCoordinate()}));
                    Path path = new Path();
                    path.setId(aStarLanePath.getPath().getId());
                    Vertex fromVertex = new Vertex();
                    Vertex toVertex = new Vertex();
                    fromVertex.setShape(pathLineString.getPointN(i));
                    toVertex.setShape(pathLineString.getPointN(i + 1));
                    path.setFrom(fromVertex);
                    path.setTo(toVertex);
                    path.setPlanarGraphId(aStarLanePath.getPath().getPlanarGraphId());
                    path.setMapId(aStarLanePath.getPath().getMapId());
                    path.setShape(lineString);
                    aStarPaths.add(new AStarLanePath(path, this.vertexLoader, false));
                }
            } else {
                for (int i = count - 1; i > 0; i--) {
                    LineString lineString = GeometryFactories.pseudoMercator().createLineString(new CoordinateArraySequence(
                            new Coordinate[]{pathLineString.getPointN(i).getCoordinate(), pathLineString.getPointN(i - 1).getCoordinate()}));
                    Path path = new Path();
                    path.setId(aStarLanePath.getPath().getId());
                    Vertex fromVertex = new Vertex();
                    Vertex toVertex = new Vertex();
                    fromVertex.setShape(pathLineString.getPointN(i));
                    toVertex.setShape(pathLineString.getPointN(i - 1));
                    path.setFrom(fromVertex);
                    path.setTo(toVertex);
                    path.setPlanarGraphId(aStarLanePath.getPath().getPlanarGraphId());
                    path.setMapId(aStarLanePath.getPath().getMapId());
                    aStarLanePath.getFrom().getVertex().getShape();
                    path.setShape(lineString);
                    aStarPaths.add(new AStarLanePath(path, this.vertexLoader, false));
                }
            }
            return aStarPaths;
        }else {
            aStarPaths.add(aStarLanePath);
            return aStarPaths;
        }
    }
}
