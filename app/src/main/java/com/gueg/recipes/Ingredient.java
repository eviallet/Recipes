package com.gueg.recipes;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity
public class Ingredient implements Serializable {

    @PrimaryKey @NonNull
    private String name;
    private String quantity;
    private String imageUrl;
    private int shoppingQuantity;

    public Ingredient(String name, String quantity, String imageUrl){
        this.name = name;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        shoppingQuantity = 1;
    }

    public void addShoppingQuantity() {
        shoppingQuantity++;
    }

    public void dimShoppingQuantity() {
        shoppingQuantity--;
    }

    public int getShoppingQuantity() {
        return shoppingQuantity;
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

    public void setShoppingQuantity(int shoppingQuantity) {
        this.shoppingQuantity = shoppingQuantity;
    }

    @Override
    public String toString() {
        return this.quantity+" "+this.name+" - "+this.imageUrl;
    }
}
