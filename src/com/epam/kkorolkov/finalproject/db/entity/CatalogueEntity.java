package com.epam.kkorolkov.finalproject.db.entity;

import java.util.Map;

/**
 * Represents a record in the database table
 * which represents entities in the catalogue.
 * Essentially includes following tables:
 * <i>publishers</i>, <i>categories</i>, <i>books</i>.
 */
public abstract class CatalogueEntity extends Entity {
    /**
     * {@code tag} represents 'SEO url' and must be unique.
     * It is added to <i>context path</i> to render entity
     * information to a user.
     */
    private String tag;

    /**
     * Each entity has <i>names</i> and <i>descriptions</i> records
     * in different languages. These maps are to represent information
     * in the following tables: <i>category_descriptions</i>,
     * <i>publisher_descriptions</i>, and <i>book_descriptions</i>.
     * Key is <i>language_id</i>.
     */
    private Map<Integer, String> names;
    private Map<Integer, String> descriptions;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Map<Integer, String> getNames() {
        return names;
    }

    public void setNames(Map<Integer, String> names) {
        this.names = names;
    }

    public Map<Integer, String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Map<Integer, String> descriptions) {
        this.descriptions = descriptions;
    }
}
