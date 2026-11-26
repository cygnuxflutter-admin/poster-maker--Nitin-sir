package com.online.flyer.design.postermaker.Leaflet_models;

/**
 * Model representing a single item in the Home "Browse by Category" horizontal list.
 * Supports backend-driven icon, pastel tile colors, and dynamic navigation actions,
 * with local drawable fallbacks for built-in defaults.
 */
public class Leaflet_HomeCategory {
    private String id;
    private String name;
    private String iconUrl;
    private String bgColor;
    private String borderColor;
    private String actionType;
    private String catId;
    private String catName;
    private String actionUrl;
    private int localBgRes;
    private int localIconRes;
    private boolean isMoreTile;

    public Leaflet_HomeCategory(String id,
                                String name,
                                String iconUrl,
                                String bgColor,
                                String borderColor,
                                String actionType,
                                String catId,
                                String catName,
                                String actionUrl,
                                int localBgRes,
                                int localIconRes,
                                boolean isMoreTile) {
        this.id = id;
        this.name = name;
        this.iconUrl = iconUrl;
        this.bgColor = bgColor;
        this.borderColor = borderColor;
        this.actionType = actionType;
        this.catId = catId;
        this.catName = catName;
        this.actionUrl = actionUrl;
        this.localBgRes = localBgRes;
        this.localIconRes = localIconRes;
        this.isMoreTile = isMoreTile;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getBgColor() {
        return bgColor;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public String getActionType() {
        return actionType;
    }

    public String getCatId() {
        return catId;
    }

    public String getCatName() {
        return catName;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public int getLocalBgRes() {
        return localBgRes;
    }

    public int getLocalIconRes() {
        return localIconRes;
    }

    public boolean isMoreTile() {
        return isMoreTile;
    }
}
