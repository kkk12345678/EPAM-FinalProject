package com.epam.kkorolkov.finalproject.db.entity;

import java.util.HashMap;
import java.util.Objects;

/**
 * Represents a record in the database table <i>publishers</i>
 * which represents publishers in the catalogue.
 */
public class Publisher extends CatalogueEntity {
    /**
     * @return a stub of an instance of {@code Publisher} class.
     */
    public static Publisher create() {
        Publisher publisher = new Publisher();
        publisher.setId(0);
        publisher.setNames(new HashMap<>());
        publisher.setTag("");
        publisher.setDescriptions(new HashMap<>());
        return publisher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Publisher)) return false;
        Publisher publisher = (Publisher) o;
        return this.getId() == publisher.getId() &&
                this.getTag().equals(publisher.getTag()) &&
                this.getNames().equals(publisher.getNames()) &&
                Objects.equals(this.getDescriptions(), publisher.getDescriptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getTag(), this.getNames(), this.getDescriptions());
    }
}
