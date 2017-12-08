package com.org.nagradcore.model.graph;

import com.org.nagradcore.model.Feature;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;

/**
 * Created by sifan on 2016/11/30.
 */
public class Door extends Feature implements Serializable {

    @JsonIgnore
    public Long getPlanarGraph() {
        return getLongProperty("planar_graph");
    }

    public void setPlanarGraph(Long planarGraph) {
        setProperty("planar_graph", planarGraph);
    }

    @JsonIgnore
    public Long getParentId() {
        return getLongProperty("parent_id");
    }

    public void setParentId(Long parentId) {
        setProperty("parent_id", parentId);
    }

    @JsonIgnore
    public Long getLocationId() {
        return getLongProperty("location_id");
    }

    public void setLocationId(Long locationId) {
        setProperty("location_id", locationId);
    }

    @JsonIgnore
    public String getDetailType() {
        return getProperty("detail_type");
    }

    public void setDetailType(String type) {
        setProperty("detail_type", type);
    }

    @JsonIgnore
    public String getLocationType() {
        return getProperty("location_type");
    }

    public void setLocationType(String type) {
        setProperty("location_type", type);
    }

    @JsonIgnore
    public String getDataId() {
        return getProperty("data_id");
    }

    public void setDataId(String dataId) {
        setProperty("data_id", dataId);
    }
}
