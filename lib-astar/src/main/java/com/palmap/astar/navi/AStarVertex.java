/**
 *
 */
package com.palmap.astar.navi;


import com.palmap.astar.navi.entity.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * @author Vito Zheng
 */
public class AStarVertex implements Comparable<AStarVertex> {

    private Vertex vertex;
    private VertexLoader loader;
    private AStarVertex parent;
    private HashMap<AStarVertex, AStarPath> paths;
    private boolean needCalcExtraPath;

    private double g;
    private double h;

    public AStarVertex(Vertex vertex, VertexLoader loader) {
        this.vertex = vertex;
        this.loader = loader;
    }

    public AStarVertex getParent() {
        return parent;
    }

    public void setParent(AStarVertex parent) {
        this.parent = parent;
    }

    public List<AStarPath> getPaths() {
        loadPaths();
        return new ArrayList<>(paths.values());
    }

    public AStarPath findPath(AStarVertex to) {
        return paths.get(to);
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public boolean isNeedCalcExtraPath() {
        return needCalcExtraPath;
    }

    public void setNeedCalcExtraPath(boolean needCalcExtraPath) {
        this.needCalcExtraPath = needCalcExtraPath;
    }

    private void loadPaths() {
        if (paths == null) {
            paths = new HashMap<>();
            for(AStarPath e : loader.loadPaths(vertex, needCalcExtraPath)){
                paths.put(e.getTo(),e);
            }
        }
    }

    public Vertex getVertex() {
        return vertex;
    }

    @Override
    public int compareTo(AStarVertex o) {
        return getG() + getH() - o.getG() - o.getH() > 0 ? 1 : -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AStarVertex that = (AStarVertex) o;
        return Objects.equals(vertex, that.vertex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertex);
    }

    @Override
    public String toString() {
        return "AStarVertex{" +
                "vertex=" + vertex +
                ", g=" + g +
                ", h=" + h +
                '}';
    }
}
