package com.org.nagradcore.navi;

import com.org.nagradcore.model.path.Path;

/**
 * Created by Nick on 2016/8/17.
 */
public class DistancePath {
    private Double distance;
    private Path path;

    public DistancePath(Double distance, Path path) {
        this.distance = distance;
        this.path = path;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DistancePath that = (DistancePath) o;

        if (distance != null ? !distance.equals(that.distance) : that.distance != null) return false;
        return path != null ? path.equals(that.path) : that.path == null;

    }

    @Override
    public int hashCode() {
        int result = distance != null ? distance.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }
}