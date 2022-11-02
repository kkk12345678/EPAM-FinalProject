package com.epam.kkorolkov.finalproject.db.entity;

/**
 * {@code Entity} that represents records in <i>statuses</i> table.
 * There is only one non-key field <i>name</i>;
 */
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
