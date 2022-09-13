package com.epam.kkorolkov.finalproject.db.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Publisher implements Serializable {
    private int id;
    private String tag;
    private Map<Integer, String> names;
    private Map<Integer, String> descriptions;

    public static Publisher create() {
        Publisher publisher = new Publisher();
        publisher.setId(0);
        publisher.setNames(new HashMap<>());
        publisher.setTag("");
        publisher.setDescriptions(new HashMap<>());
        return publisher;
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

    @Override
    public String toString() {
        return "Publisher{" +
                "id=" + id +
                ", tag='" + tag + '\'' +
                ", names=" + names +
                ", descriptions=" + descriptions +
                '}';
    }
}
