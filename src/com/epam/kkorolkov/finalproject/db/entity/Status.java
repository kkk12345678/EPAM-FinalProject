package com.epam.kkorolkov.finalproject.db.entity;

/**
 * {@code Status} represents a record in <i>statuses</i> table.
 * There is only one non-key field <i>name</i>;
 */
public class Status extends Entity {
    /** Name of the status */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
