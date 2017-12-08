package com.org.nagradcore.dto;
import com.org.nagradcore.model.category.CategoryLevel;
import com.org.nagradcore.model.category.FullCategory;

import org.codehaus.jackson.annotate.JsonIgnore;
import java.io.Serializable;

/**
 * Created by sifan on 2016/10/11.
 */
public class CategoryDto implements Serializable {
    private long id;
    private String name1;
    private String name2;
    private String name3;
    private String name4;

    private String logo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getName4() {
        return name4;
    }

    public void setName4(String name4) {
        this.name4 = name4;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogoName() {
        return logoName;
    }

    public void setLogoName(String logoName) {
        this.logoName = logoName;
    }

    public CategoryLevel getCategoryLevel() {
        return categoryLevel;
    }

    public void setCategoryLevel(CategoryLevel categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public FullCategory getFullCategory() {
        return fullCategory;
    }

    public void setFullCategory(FullCategory fullCategory) {
        this.fullCategory = fullCategory;
    }

    @JsonIgnore
    private String logoName;//logo名
    @JsonIgnore
    private CategoryLevel categoryLevel;
    @JsonIgnore
    private FullCategory fullCategory;
}
