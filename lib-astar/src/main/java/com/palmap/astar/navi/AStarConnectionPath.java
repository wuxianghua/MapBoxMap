package com.palmap.astar.navi;


import com.palmap.astar.navi.entity.Connection;

/**
 * Created by wyx on 8/31/15.
 */
public class AStarConnectionPath extends AStarPath {

    private Connection connection;

    public AStarConnectionPath(Connection connection, VertexLoader loader, boolean reverse) {
        this.connection = connection;
        if (!reverse) {
            from = new AStarVertex(connection.getFrom(), loader);
            to = new AStarVertex(connection.getTo(), loader);
        } else {
            from = new AStarVertex(connection.getTo(), loader);
            to = new AStarVertex(connection.getFrom(), loader);
        }
    }

    @Override
    public double getWeight() {
        return connection.getRank();
    }

    @Override
    public double getAltitude() {
        return 0;
    }
}
