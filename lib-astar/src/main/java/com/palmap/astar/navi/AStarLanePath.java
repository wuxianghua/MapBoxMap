package com.palmap.astar.navi;


import com.palmap.astar.navi.entity.Path;

/**
 * Created by wyx on 8/31/15.
 */
public class AStarLanePath extends AStarPath {

    private Path path;
    private boolean reverse;

    public AStarLanePath(Path path, VertexLoader loader, boolean reverse) {
        this.path = path;
        if (!reverse) {
            from = new AStarVertex(path.getFrom(), loader);
            to = new AStarVertex(path.getTo(), loader);
        } else {
            from = new AStarVertex(path.getTo(), loader);
            to = new AStarVertex(path.getFrom(), loader);
        }
        this.reverse = reverse;
    }

    public boolean isReverse() {
        return reverse;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public double getAltitude() {
        return 0;
    }

    @Override
    public double getWeight() {
        return path.getShape().getLength() * (path.getRank() + 1);
    }
}
