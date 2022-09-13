package com.epam.kkorolkov.finalproject.db.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Category implements Serializable {
    private int id;
    private String tag;
    private Map<Integer, String> names;
    private Map<Integer, String> descriptions;

    public static Category create() {
        Category category = new Category();
        category.setId(0);
        category.setNames(new HashMap<>());
        category.setTag("");
        category.setDescriptions(new HashMap<>());
        return category;
    }

    public static Category create(String tag) {
        Category category = Category.create();
        category.setTag(tag);
        return category;
    }


    public Map<Integer, String> getNames() {
        return names;
    }

    public void setNames(Map<Integer, String> names) {
        this.names = names;
    }

    public Map<Integer, String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Map<Integer, String> descriptions) {
        this.descriptions = descriptions;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", tag='" + tag + '\'' +
                ", names=" + names +
                ", descriptions=" + descriptions +
                '}';
    }
}
