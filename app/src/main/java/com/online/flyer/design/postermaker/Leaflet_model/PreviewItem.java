package com.online.flyer.design.postermaker.Leaflet_model;

import java.io.Serializable;

public class PreviewItem implements Serializable {
    private String id;
    private String title;
    private String subtitle;
    private String imageUrl;
    private String ratio; // e.g. "1:1", "4:5", "9:16", "16:9"
    private int catId;
    private int postId;
    private boolean isPremium;
    private boolean isPreviewUnlocked = false;

    public PreviewItem() {
    }

    public PreviewItem(String id, String title, String subtitle, String imageUrl, String ratio, int catId, int postId, boolean isPremium) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
        this.ratio = ratio;
        this.catId = catId;
        this.postId = postId;
        this.isPremium = isPremium;
        this.isPreviewUnlocked = false;
    }

    public static PreviewItem fromPosterImage(Leaflet_PosterImage img, String catName, boolean isPremium) {
        if (img == null) return null;
        String id = String.valueOf(img.getPost_id());
        String title = catName != null && !catName.isEmpty() ? catName : "Poster Design";
        String subtitle = "Template #" + img.getPost_id();
        return new PreviewItem(id, title, subtitle, img.getPost_thumb(), img.getRatio(), img.getCat_id(), img.getPost_id(), isPremium);
    }

    public String getId() {
        return id != null ? id : String.valueOf(postId);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title != null ? title : "Poster Preview";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle != null ? subtitle : "";
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getImageUrl() {
        return imageUrl != null ? imageUrl : "";
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRatio() {
        return ratio != null ? ratio : "1:1";
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public boolean isPreviewUnlocked() {
        return isPreviewUnlocked;
    }

    public void setPreviewUnlocked(boolean previewUnlocked) {
        this.isPreviewUnlocked = previewUnlocked;
    }
}
