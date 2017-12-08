package com.org.nagradcore.model.graph;

import com.org.nagradcore.model.Feature;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;

/**
 * Created by wyx on 2/18/16.
 */
public class Facility extends Feature implements Serializable {

    @JsonIgnore
    public Long getPlanarGraph() {
        return getLongProperty("planar_graph");
    }

    public void setPlanarGraph(Long id) {
        setProperty("planar_graph", id);
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
    public String getName() {
        return getProperty("name");
    }

    public void setName(String name) {
        setProperty("name", name);
    }

    @JsonIgnore
    public String getAddress() {
        return getProperty("address");
    }

    public void setAddress(String address) {
        setProperty("address", address);
    }

    @JsonIgnore
    public String getDisplay() {
        return getProperty("display");
    }

    public void setDisplay(String display) {
        setProperty("display", display);
    }

    @JsonIgnore
    public Long getCategory() {
        return getLongProperty("category");
    }

    public void setCategory(Long category) {
        setProperty("category", category);
    }

    @JsonIgnore
    public String getLogo() {
        return getProperty("logo");
    }

    public void setLogo(String logo) {
        setProperty("logo", logo);
    }

    @JsonIgnore
    public boolean isCommonArea() {
        return getProperty("common_area");
    }

    public void setCommonArea(boolean commonArea) {
        setProperty("common_area", commonArea);
    }

    @JsonIgnore
    public Long getAreaId() {
        return getLongProperty("area_id");
    }

    public void setAreaId(Long areaId) {
        setProperty("area_id", areaId);
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

    @JsonIgnore
    public String getDataId() {
        return getProperty("data_id");
    }

    public void setDataId(String dataId) {
        setProperty("data_id", dataId);
    }
}
