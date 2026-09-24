package com.online.flyer.design.postermaker.Leaflet_models;

import java.io.Serializable;

public class Leaflet_HeroBanner implements Serializable {
    private String id;
    private String imageUrl;
    private String title;
    private String actionUrl;

    public Leaflet_HeroBanner() {
    }

    public Leaflet_HeroBanner(String id, String imageUrl, String title, String actionUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.actionUrl = actionUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
}
