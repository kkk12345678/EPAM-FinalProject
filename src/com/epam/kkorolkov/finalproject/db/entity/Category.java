package com.epam.kkorolkov.finalproject.db.entity;

import java.util.HashMap;
import java.util.Objects;

public class Category extends CatalogueEntity {
    public static Category create() {
        Category category = new Category();
        category.setId(0);
        category.setNames(new HashMap<>());
        category.setTag("");
        category.setDescriptions(new HashMap<>());
        return category;
    }

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
