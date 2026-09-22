package com.online.flyer.design.postermaker.Leaflet_model;

import java.io.Serializable;
import java.util.ArrayList;

public class Leaflet_BgModel implements Serializable {

    private int category_id;
    private String category_name;
    private ArrayList<Leaflet_BgImage> category_list;


    public Leaflet_BgModel(String category_id, String category_name, ArrayList<Leaflet_BgImage> category_list) {
        this.category_id = Integer.parseInt(category_id);
        this.category_name = category_name;
        this.category_list = category_list;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public ArrayList<Leaflet_BgImage> getCategory_list() {
        return category_list;
    }

    public void setCategory_list(ArrayList<Leaflet_BgImage> category_list) {
        this.category_list = category_list;
    }
}
