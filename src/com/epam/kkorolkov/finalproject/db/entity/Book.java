package com.epam.kkorolkov.finalproject.db.entity;

import java.sql.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 * Represents a record in the database table
 * which represents books in the catalogue.
 */
public class Book extends CatalogueEntity {

    /**
     * Each book has a unique ISBN (international serial number).
     * This field is used to identify a book as well as an image filename.
     */
    private String isbn;

    /** Current quantity of a book on stock */
    private int quantity;

    /** Publisher of a book */
    private Publisher publisher;

    /** Category of a book */
    private Category category;

    /** Publishing date */
    private Date date;

    /** Price of a book */
    private Double price;

    /**
     * @return a stub of an instance of a {@code Book} class.
     */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return this.getId() == book.getId() &&
                this.getTag().equals(book.getTag()) &&
                quantity == book.quantity &&
                isbn.equals(book.isbn) &&
                publisher.equals(book.publisher) &&
                category.equals(book.category) &&
                date.equals(book.date) &&
                price.equals(book.price) &&
                this.getNames().equals(book.getNames()) &&
                Objects.equals(this.getDescriptions(), book.getDescriptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getTag(), isbn, quantity, publisher, category, date, price, this.getNames(), this.getDescriptions());
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + this.getId() +
                ", tag='" + this.getTag() + '\'' +
                ", isbn=" + isbn +
                ", quantity=" + quantity +
                ", publisher=" + publisher +
                ", category=" + category +
                ", date=" + date +
                ", price=" + price +
                ", names=" + this.getNames() +
                ", descriptions=" + this.getDescriptions() +
                "} ";
    }
}
