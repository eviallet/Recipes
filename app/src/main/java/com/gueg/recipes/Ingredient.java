package com.gueg.recipes;

import java.io.Serializable;

public class Ingredient implements Serializable {

    private String name;
    private String quantity;
    private String imageUrl;

    public Ingredient(String name, String quantity){
        this(name,quantity,"");
    }

    public Ingredient(String name, String quantity, String imageUrl){
        this.name = name;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }


    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return this.quantity+" "+this.name+" - "+this.imageUrl;
    }
}
