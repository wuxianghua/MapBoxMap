package com.org.nagradcore.dto;

import com.org.nagradcore.model.Payment;
import com.org.nagradcore.model.Type;
import com.vividsolutions.jts.geom.Geometry;

import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Created by sifan on 2016/7/6.
 */
@JsonPropertyOrder({ "id", "dataId", "version", "mapId", "buildingId", "planarGraphId" })
public class POIDto extends BaseDto implements Serializable, Comparable<POIDto> {
    private long id;
    private Long parent;
    private Set<Long> parents;
    private String address;
    private RegionDto region;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Set<Long> getParents() {
        return parents;
    }

    public void setParents(Set<Long> parents) {
        this.parents = parents;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public RegionDto getRegion() {
        return region;
    }

    public void setRegion(RegionDto region) {
        this.region = region;
    }

    public Integer getZipCode() {
        return zipCode;
    }

    public void setZipCode(Integer zipCode) {
        this.zipCode = zipCode;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getDetailType() {
        return detailType;
    }

    public void setDetailType(Type detailType) {
        this.detailType = detailType;
    }

    public Boolean getDefaultFloor() {
        return defaultFloor;
    }

    public void setDefaultFloor(Boolean defaultFloor) {
        this.defaultFloor = defaultFloor;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    public Boolean getSpecialName() {
        return specialName;
    }

    public void setSpecialName(Boolean specialName) {
        this.specialName = specialName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Boolean getCommonArea() {
        return commonArea;
    }

    public void setCommonArea(Boolean commonArea) {
        this.commonArea = commonArea;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Boolean getParking() {
        return parking;
    }

    public void setParking(Boolean parking) {
        this.parking = parking;
    }

    public Integer getParkingSpace() {
        return parkingSpace;
    }

    public void setParkingSpace(Integer parkingSpace) {
        this.parkingSpace = parkingSpace;
    }

    public String getParkingFee() {
        return parkingFee;
    }

    public void setParkingFee(String parkingFee) {
        this.parkingFee = parkingFee;
    }

    public Boolean getMembership() {
        return membership;
    }

    public void setMembership(Boolean membership) {
        this.membership = membership;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Payment> getPayments() {
        return payments;
    }

    public void setPayments(Set<Payment> payments) {
        this.payments = payments;
    }

    public Long getPlanarGraphId() {
        return planarGraphId;
    }

    public void setPlanarGraphId(Long planarGraphId) {
        this.planarGraphId = planarGraphId;
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

    public Integer getShapeLevel() {
        return shapeLevel;
    }

    public void setShapeLevel(Integer shapeLevel) {
        this.shapeLevel = shapeLevel;
    }

    public Long getRelatedBuildingId() {
        return relatedBuildingId;
    }

    public void setRelatedBuildingId(Long relatedBuildingId) {
        this.relatedBuildingId = relatedBuildingId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public Set<Long> getDoorIds() {
        return doorIds;
    }

    public void setDoorIds(Set<Long> doorIds) {
        this.doorIds = doorIds;
    }

    public Geometry getShape() {
        return shape;
    }

    public void setShape(Geometry shape) {
        this.shape = shape;
    }

    public double[] getPoint() {
        return point;
    }

    public void setPoint(double[] point) {
        this.point = point;
    }

    public Set<String> getHighlights() {
        return highlights;
    }

    public void setHighlights(Set<String> highlights) {
        this.highlights = highlights;
    }

    public void setOther(Map<String, Object> other) {
        this.other = other;
    }

    @JsonProperty("zip")
    private Integer zipCode;
    private Type type;
    private Type detailType;
    @JsonProperty("default")
    private Boolean defaultFloor;
    private Double altitude;
    private CategoryDto category;
    @JsonProperty("special_name")
    private Boolean specialName;
    private String name;
    @JsonProperty("english_name")
    private String englishName;
    private String display;
    private String logo;
    @JsonProperty("common_area")
    private Boolean commonArea;
    private String phone;
    private Set<String> tags;
    private Boolean parking;
    @JsonProperty("parking_space")
    private Integer parkingSpace;
    @JsonProperty("parking_fee")
    private String parkingFee;
    private Boolean membership;
    @JsonProperty("opening_time")
    private String openingTime;
    private String description;
    @JsonProperty("payment")
    private Set<Payment> payments;

    private Long planarGraphId;
    private Long mapId;
    private Long buildingId;
    private Integer shapeLevel;
    private Long relatedBuildingId;
    private Long areaId;
    private Set<Long> doorIds;
    private Geometry shape;
    private double[] point;

    private Set<String> highlights;

    @JsonIgnore
    private Map<String, Object> other;

    @JsonAnyGetter
    public Map<String, Object> getOther() {
        return other;
    }

    @Override
    public int compareTo(POIDto poi) {
        if (poi == null) return 1;
        if (Type.FLOOR.equals(poi.getType())) {
            return getAltitude() < poi.getAltitude() ? -1 : 1;
        } else {
            return 0;
        }
    }
}
