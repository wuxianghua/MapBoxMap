package com.org.nagradcore.model;

import com.org.nagradcore.model.category.Category;
import com.org.nagradcore.model.region.Region;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.Set;
/**
 * Created by wyx on 1/18/16.
 */
public class Map extends Base implements Serializable {
    private long id;
    private Boolean specialName;
    private String display;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Boolean getSpecialName() {
        return specialName;
    }

    public void setSpecialName(Boolean specialName) {
        this.specialName = specialName;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public Integer getZipCode() {
        return zipCode;
    }

    public void setZipCode(Integer zipCode) {
        this.zipCode = zipCode;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<Long> getPayments() {
        return payments;
    }

    public void setPayments(Set<Long> payments) {
        this.payments = payments;
    }

    public long getPoi() {
        return poi;
    }

    public void setPoi(long poi) {
        this.poi = poi;
    }

    public Boolean getPrivatelyOwned() {
        return privatelyOwned;
    }

    public void setPrivatelyOwned(Boolean privatelyOwned) {
        this.privatelyOwned = privatelyOwned;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Geometry getAngleLine() {
        return angleLine;
    }

    public void setAngleLine(Geometry angleLine) {
        this.angleLine = angleLine;
    }

    public boolean isMultiBuilding() {
        return multiBuilding;
    }

    public void setMultiBuilding(boolean multiBuilding) {
        this.multiBuilding = multiBuilding;
    }

    public Set<Long> getOwner() {
        return owner;
    }

    public void setOwner(Set<Long> owner) {
        this.owner = owner;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getLastModify() {
        return lastModify;
    }

    public void setLastModify(Long lastModify) {
        this.lastModify = lastModify;
    }

    private String englishName;
    private String address;
    private Region region;
    private Category category;
    private String logo;
    private Boolean parking;
    private Integer parkingSpace;
    private String parkingFee;
    private String phone;
    private Boolean membership;
    private String openingTime;
    private Integer zipCode;
    private Set<String> tags;
    private Set<Long> payments;
    private long poi;
    private Boolean privatelyOwned;
    private Coordinate coordinate;
    private Geometry angleLine;
    private boolean multiBuilding;
    @JsonProperty("user_id")
    private Set<Long> owner;
    private Long createTime;
    private Long lastModify;
}
