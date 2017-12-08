package com.org.nagradcore.model;

import com.org.nagradcore.Constants;

import java.io.Serializable;

/**
 * Created by liupin on 2017/8/20.
 */
public abstract class Base implements Serializable {

    private String dataId;
    private String version;
    private long sceneId = Constants.DEFAULT_SCENE;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getSceneId() {
        return sceneId;
    }

    public void setSceneId(long sceneId) {
        this.sceneId = sceneId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private boolean active;

}
