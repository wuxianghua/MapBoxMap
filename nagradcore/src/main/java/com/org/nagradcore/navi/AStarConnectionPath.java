package com.org.nagradcore.navi;

import com.org.nagradcore.model.path.Connection;

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
            Long fromLocationId = connection.getFromLocationId();
            Long toLocationId = connection.getToLocationId();
            connection.setFromLocationId(toLocationId);
            connection.setToLocationId(fromLocationId);
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
        return getTo().getVertex().getAltitude();
    }

    public Connection getConnection() {
        return this.connection;
    }
}
