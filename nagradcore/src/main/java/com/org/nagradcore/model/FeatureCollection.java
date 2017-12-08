package com.org.nagradcore.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyx on 2/18/16.
 */
public class FeatureCollection<T extends Feature> implements Serializable {

    private String type = "FeatureCollection";

    private List<T> features = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addFeature(T feature) {
        features.add(feature);
    }

    public List<T> getFeatures() {
        return features;
    }

    public void setFeatures(List<T> features) {
        this.features = features;
    }
}
