package com.epam.kkorolkov.finalproject.db.entity;

import java.util.Map;

public abstract class CatalogueEntity extends Entity {
    private String tag;
    private Map<Integer, String> names;
    private Map<Integer, String> descriptions;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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
}
