package com.epam.kkorolkov.finalproject.db.entity;

import java.io.Serializable;

/**
 * Represents a record in the database table
 * which has a primary key field ID.
 * Essentially includes following tables:
 * <i>publishers</i>, <i>categories</i>, <i>books</i>,
 * <i>statuses</i>, <i>languages</i>, <i>orders</i>, <i>users</i>.
 */
public abstract class Entity implements Serializable {
    private int id;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
