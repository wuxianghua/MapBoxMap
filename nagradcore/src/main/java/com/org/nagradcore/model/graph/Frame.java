package com.org.nagradcore.model.graph;

import com.org.nagradcore.model.Feature;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;

/**
 * Created by wyx on 2/18/16.
 */
public class Frame extends Feature implements Serializable {

    @JsonIgnore
    public Long getPlanarGraph() {
        return getLongProperty("planar_graph");
    }

    public void setPlanarGraph(Long planarGraph) {
        setProperty("planar_graph", planarGraph);
    }

    public void setMaxScale(Double maxScale) {
        setProperty("max_scale", maxScale);
    }

    @JsonIgnore
    public Double getMaxScale() {
        return getProperty("max_scale");
    }

    public void setMinScale(Double minScale) {
        setProperty("min_scale", minScale);
    }

    @JsonIgnore
    public Double getMinScale() {
        return getProperty("min_scale");
    }
}
