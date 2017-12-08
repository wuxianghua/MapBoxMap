package com.org.nagradcore.dto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by liupin on 2017/7/28.
 */
public class FullMapDto implements Serializable {

    private MapDto map;
    private RoadNetDto roadNet;
    private List<PlanarGraphDto> planarGraphs;
    private List<POIDto> pois;

    public MapDto getMap() {
        return map;
    }

    public void setMap(MapDto map) {
        this.map = map;
    }

    public RoadNetDto getRoadNet() {
        return roadNet;
    }

    public void setRoadNet(RoadNetDto roadNet) {
        this.roadNet = roadNet;
    }

    public List<PlanarGraphDto> getPlanarGraphs() {
        return planarGraphs;
    }

    public void setPlanarGraphs(List<PlanarGraphDto> planarGraphs) {
        this.planarGraphs = planarGraphs;
    }

    public List<POIDto> getPois() {
        return pois;
    }

    public void setPois(List<POIDto> pois) {
        this.pois = pois;
    }

    public FullMapDto addPOI(@Nonnull POIDto poi) {
        if (pois == null) {
            pois = new ArrayList<>();
        }
        pois.add(poi);
        return this;
    }

    public FullMapDto addPlanarGraph(@Nonnull PlanarGraphDto planarGraph) {
        if (planarGraphs == null) {
            planarGraphs = new ArrayList<>();
        }
        planarGraphs.add(planarGraph);
        return this;
    }

}
