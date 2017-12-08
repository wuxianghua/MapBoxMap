package com.org.nagradcore.model;

import com.vividsolutions.jts.geom.Geometry;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wyx on 2/18/16.
 */
public class Feature implements Serializable {

    private String type = "Feature";
    private Map<String, Object> properties = new HashMap<String, Object>();
    private Geometry geometry;

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    @JsonIgnore
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key) {
        return (T) properties.get(key);
    }

    @JsonIgnore
    public Long getLongProperty(String key) {
        return getProperty(key) == null ? null : Long.valueOf(getProperty(key).toString());
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Long getId() {
        return getLongProperty("id");
    }

    public void setId(long id) {
        setProperty("id", id);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public Integer getShapeLevel() {
        return getProperty("shape_level");
    }

    public void setShapeLevel(Integer shapeLevel) {
        setProperty("shape_level", shapeLevel);
    }
}