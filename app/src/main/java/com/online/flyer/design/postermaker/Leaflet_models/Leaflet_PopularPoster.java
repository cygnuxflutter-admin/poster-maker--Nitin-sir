package com.online.flyer.design.postermaker.Leaflet_models;

import java.io.Serializable;

public class Leaflet_PopularPoster implements Serializable {
    private String id;
    private String name;
    private String category;
    private String imageUrl;

    public Leaflet_PopularPoster() {
    }

    public Leaflet_PopularPoster(String id, String name, String category, String imageUrl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
