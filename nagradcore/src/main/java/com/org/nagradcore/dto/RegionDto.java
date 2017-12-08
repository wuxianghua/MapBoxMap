package com.org.nagradcore.dto;
import java.io.Serializable;

/**
 * Created by liupin on 2017/7/24.
 */
public class RegionDto implements Serializable {
    private String country;
    private Long code;
    private String name;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
