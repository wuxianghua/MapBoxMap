package com.org.nagradcore.dto;
import com.org.nagradcore.model.graph.Areas;
import com.org.nagradcore.model.graph.Doors;
import com.org.nagradcore.model.graph.Facilities;
import com.org.nagradcore.model.graph.Frames;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import java.io.Serializable;
/**
 * Created by liupin on 2017/7/24.
 */
// FIXME: 2017/8/22 Frame Area Facility 必须在Json文档的最前面且顺序不能改变（旧版SDK的实现有问题）
@JsonPropertyOrder({"Frame", "Area", "Facility", "id", "dataId", "version", "mapId", "buildingId"})
public class PlanarGraphDto extends BaseDto implements Serializable {

    @JsonProperty("Frame")
    private Frames frames;

    public Frames getFrames() {
        return frames;
    }

    public void setFrames(Frames frames) {
        this.frames = frames;
    }

    public Areas getAreas() {
        return areas;
    }

    public void setAreas(Areas areas) {
        this.areas = areas;
    }

    public Facilities getFacilities() {
        return facilities;
    }

    public void setFacilities(Facilities facilities) {
        this.facilities = facilities;
    }

    public Doors getDoors() {
        return doors;
    }

    public void setDoors(Doors doors) {
        this.doors = doors;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMapId() {
        return mapId;
    }

    public void setMapId(Long mapId) {
        this.mapId = mapId;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    @JsonProperty("Area")

    private Areas areas;
    @JsonProperty("Facility")
    private Facilities facilities;
    @JsonIgnore
//    @JsonProperty("Door")
    private Doors doors;

    private Long id;
    private Long mapId;
    private Long buildingId;
}
