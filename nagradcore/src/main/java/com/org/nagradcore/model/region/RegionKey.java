package com.org.nagradcore.model.region;

import java.io.Serializable;

/**
 * Created by wyx on 1/18/16.
 */

public class RegionKey implements Serializable {
    private String country;
    private Long code;

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
}
