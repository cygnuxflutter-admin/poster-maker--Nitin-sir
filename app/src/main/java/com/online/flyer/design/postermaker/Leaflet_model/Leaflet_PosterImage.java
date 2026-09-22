package com.online.flyer.design.postermaker.Leaflet_model;

import java.io.Serializable;

public class Leaflet_PosterImage implements Serializable {
    int cat_id;
    int post_id;
    String post_thumb;
    String ratio;

    public Leaflet_PosterImage(String post_id, String post_thumb, String ratio, String cat_id) {
        this.post_id = Integer.parseInt(post_id);
        this.post_thumb = post_thumb;
        this.ratio = ratio;
        this.cat_id = Integer.parseInt(cat_id);
    }

    public int getCat_id() {
        return cat_id;
    }

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public String getPost_thumb() {
        return post_thumb;
    }

    public void setPost_thumb(String post_thumb) {
        this.post_thumb = post_thumb;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }
}
