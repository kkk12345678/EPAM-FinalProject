package com.epam.kkorolkov.finalproject.db.entity;

/**
 * Represents a record in the database table
 * which represents languages used in <i>category_descriptions</i>,
 * <i>publisher_descriptions</i>, and <i>book_descriptions</i>.
 *
 * Each instance of a class that extends {@code CatalogueEntity} has
 * <i>names</i> and <i>descriptions</i> in each of <i>languages</i> record.
 */
public class Language extends Entity {
    /** Name of a language */
    private String name;

    /** Language image filename */
    private String image;

    /** Locale corresponded with the language */
    private String locale;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
