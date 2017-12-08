package com.org.nagradcore.model.category;
import java.io.Serializable;

/**
 * Created by wyx on 1/18/16.
 */
public class Category implements Serializable {

    private long id;
    private String name1;
    private String name2;

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

    private String name3;
    private String name4;

    private String logoName;        //logo名
    private CategoryLevel categoryLevel;
    private FullCategory fullCategory;
}
