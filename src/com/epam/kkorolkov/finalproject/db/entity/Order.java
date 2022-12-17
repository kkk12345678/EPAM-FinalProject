package com.epam.kkorolkov.finalproject.db.entity;

import java.util.Date;
import java.util.Map;

/**
 * Represents a record in <i>orders</i> table.
 */
public class Order extends Entity {
    /** Order status */
    private Status status;

    /** Customer */
    private User user;

    /** Sum of the order */
    private Double total;

    /** Date of order placement */
    private Date dateAdded;

    /** Contains details of the order, i.e. titles and quantities */
    private Map<Book, Integer> details;

    public Map<Book, Integer> getDetails() {
        return details;
    }

    public void setDetails(Map<Book, Integer> details) {
        this.details = details;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

}
