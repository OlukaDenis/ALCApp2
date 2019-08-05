package com.mcdenny.alcapp2.model;

import java.io.Serializable;

public class TravelDeal implements Serializable {
    private String id;
    private String title;
    private String description;
    private String price;
    private String image;
    private String imageName;

    public TravelDeal() {
    }

    public TravelDeal(String title, String description, String price, String image, String imageName) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.image = image;
        this.image = imageName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
