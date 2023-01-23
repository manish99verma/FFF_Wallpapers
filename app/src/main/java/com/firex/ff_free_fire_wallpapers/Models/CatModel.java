package com.firex.ff_free_fire_wallpapers.Models;

public class CatModel {
    String url = "";
    String title = "";


    public CatModel() {
    }

    public String getTitle() {
        return title;
    }

    public CatModel(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
