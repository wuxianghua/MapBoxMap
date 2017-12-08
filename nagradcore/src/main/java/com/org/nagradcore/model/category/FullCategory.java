package com.org.nagradcore.model.category;

import java.io.Serializable;
/**
 * Created by sifan on 2017/5/11.
 */
public class FullCategory implements Serializable {
    private Long level1Code;
    private Long level2Code;

    public Long getLevel1Code() {
        return level1Code;
    }

    public void setLevel1Code(Long level1Code) {
        this.level1Code = level1Code;
    }

    public Long getLevel2Code() {
        return level2Code;
    }

    public void setLevel2Code(Long level2Code) {
        this.level2Code = level2Code;
    }

    public Long getLevel3Code() {
        return level3Code;
    }

    public void setLevel3Code(Long level3Code) {
        this.level3Code = level3Code;
    }

    public Long getLevel4Code() {
        return level4Code;
    }

    public void setLevel4Code(Long level4Code) {
        this.level4Code = level4Code;
    }

    private Long level3Code;
    private Long level4Code;
}
