package com.online.flyer.design.postermaker.Leaflet_model;

import java.io.Serializable;

public class SearchCategoryItem implements Serializable {
    public enum CategoryType {
        TEMPLATE,
        BACKGROUND
    }

    private final int id;
    private final String name;
    private final CategoryType type;
    private final String thumbUrl;

    public SearchCategoryItem(int id, String name, CategoryType type, String thumbUrl) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.thumbUrl = thumbUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CategoryType getType() {
        return type;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public boolean isTemplate() {
        return type == CategoryType.TEMPLATE;
    }
}
