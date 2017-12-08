package com.org.nagradcore.dto;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by admin on 2016/7/21.
 */
@JsonPropertyOrder({ "id", "dataId", "version", "name" })
public class MapDto extends BaseDto implements Serializable {
    private long id;
    private String name;
    private String address;
    private long poi;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getPoi() {
        return poi;
    }

    public void setPoi(long poi) {
        this.poi = poi;
    }

    public CategoryDto getCategory() {
        return category;
    }

    public void setCategory(CategoryDto category) {
        this.category = category;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Boolean getSpecialName() {
        return specialName;
    }

    public void setSpecialName(Boolean specialName) {
        this.specialName = specialName;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getMembership() {
        return membership;
    }

    public void setMembership(Boolean membership) {
        this.membership = membership;
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

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public Set<Long> getPayment() {
        return payment;
    }

    public void setPayment(Set<Long> payment) {
        this.payment = payment;
    }

    public Long getZipCode() {
        return zipCode;
    }

    public void setZipCode(Long zipCode) {
        this.zipCode = zipCode;
    }

    public RegionDto getRegion() {
        return region;
    }

    public void setRegion(RegionDto region) {
        this.region = region;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Set<String> getHighlights() {
        return highlights;
    }

    public void setHighlights(Set<String> highlights) {
        this.highlights = highlights;
    }

    public Geometry getAngleLine() {
        return angleLine;
    }

    public void setAngleLine(Geometry angleLine) {
        this.angleLine = angleLine;
    }

    public boolean isPrivatelyOwned() {
        return privatelyOwned;
    }

    public void setPrivatelyOwned(boolean privatelyOwned) {
        this.privatelyOwned = privatelyOwned;
    }

    public boolean isMultiBuilding() {
        return multiBuilding;
    }

    public void setMultiBuilding(boolean multiBuilding) {
        this.multiBuilding = multiBuilding;
    }

    private CategoryDto category;
    private Set<String> tags;
    private Coordinate coordinate;
    @JsonProperty("is_special_name")
    private Boolean specialName;
    @JsonProperty("english_name")
    private String englishName;
    private String display;
    private String phone;
    private Boolean membership;
    private Boolean parking;
    @JsonProperty("parking_space")
    private Integer parkingSpace;
    @JsonProperty("parking_fee")
    private String parkingFee;
    @JsonProperty("opening_time")
    private String openingTime;
    private Set<Long> payment;
    @JsonProperty("zip_code")
    private Long zipCode;
    private RegionDto region;
    private String logo;
    private Set<String> highlights;
    @JsonProperty("angle_line")
    private Geometry angleLine;
    @JsonIgnore
    private boolean privatelyOwned;
    private boolean multiBuilding;
}
