package com.epam.kkorolkov.finalproject.db.entity;

public class Status extends Entity {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Status{" +
                "id=" + this.getId() +
                ", name='" + name + '\'' +
                '}';
    }
}
