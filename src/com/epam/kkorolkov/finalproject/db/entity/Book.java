package com.epam.kkorolkov.finalproject.db.entity;

import java.sql.Date;
import java.util.HashMap;

public class Book extends CatalogueEntity {
    private String isbn;
    private int quantity;
    private Publisher publisher;
    private Category category;
    private Date date;
    private Double price;


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
        book.setNames(new HashMap<>());
        book.setDescriptions(new HashMap<>());
        return book;
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

}
