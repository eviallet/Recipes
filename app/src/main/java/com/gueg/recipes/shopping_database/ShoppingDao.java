package com.gueg.recipes.shopping_database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.gueg.recipes.Ingredient;

import java.util.List;

@Dao
public interface ShoppingDao {
    @Query("SELECT * FROM ingredient")
    List<Ingredient> getAll();

    @Query("SELECT * FROM ingredient WHERE name LIKE :name LIMIT 1")
    Ingredient findByTitle(String name);


    @Insert
    void insertAll(Ingredient... ingredients);

    @Update
    void updateIngredients(Ingredient... ingredients);

    @Delete
    void delete(Ingredient ingredient);
}
