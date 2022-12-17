package com.epam.kkorolkov.finalproject.db.entity;

import java.util.Objects;

/**
 * Represents a record in the database table <i>categories</i>
 * which represents categories in the catalogue.
 */
public class Category extends CatalogueEntity {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return this.getId() == category.getId() &&
                this.getTag().equals(category.getTag()) &&
                this.getNames().equals(category.getNames()) &&
                Objects.equals(this.getDescriptions(), category.getDescriptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getTag(), this.getNames(), this.getDescriptions());
    }
}
