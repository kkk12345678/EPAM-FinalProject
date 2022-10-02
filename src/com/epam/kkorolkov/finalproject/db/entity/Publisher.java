package com.epam.kkorolkov.finalproject.db.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Publisher extends Entity {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Publisher)) return false;
        Publisher publisher = (Publisher) o;
        return id == publisher.id &&
                tag.equals(publisher.tag) &&
                names.equals(publisher.names) &&
                Objects.equals(descriptions, publisher.descriptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tag, names, descriptions);
    }
}
