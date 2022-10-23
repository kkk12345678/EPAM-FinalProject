package com.epam.kkorolkov.finalproject.db.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Order extends Entity {
    private Status status;
    private User user;
    private Double total;
    private Date dateAdded;
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
