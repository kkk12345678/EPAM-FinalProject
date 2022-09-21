package com.epam.kkorolkov.finalproject.db.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class Book implements Serializable {
    private int id;
    private String isbn;
    private int quantity;
    private Publisher publisher;
    private Category category;
    private Date date;
    private Double price;
    private String tag;
    private Map<Integer, String> descriptions;
    private Map<Integer, String> titles;

    public static Book create() {
        Book book = new Book();
        book.setId(0);
        book.setIsbn("");
        book.setQuantity(0);
        book.setCategory(null);
        book.setPublisher(null);
        book.setDate(null);
        book.setTag("");
        book.setPrice(0.0);
        book.setTitles(new HashMap<>());
        book.setDescriptions(new HashMap<>());
        return book;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Map<Integer, String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Map<Integer, String> descriptions) {
        this.descriptions = descriptions;
    }

    public Map<Integer, String> getTitles() {
        return titles;
    }

    public void setTitles(Map<Integer, String> titles) {
        this.titles = titles;
    }
}
