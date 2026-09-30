package com.online.flyer.design.postermaker.Leaflet_utils;

public class Leaflet_NotificationModel {
    private int id;
    private String title;
    private String message;
    private long timestamp;
    private boolean isRead;
    private String image;
    private String launchUrl;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getLaunchUrl() { return launchUrl; }
    public void setLaunchUrl(String launchUrl) { this.launchUrl = launchUrl; }
}

