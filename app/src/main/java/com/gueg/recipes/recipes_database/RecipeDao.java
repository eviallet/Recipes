package com.gueg.recipes.recipes_database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.gueg.recipes.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {
    @Query("SELECT * FROM recipe")
    List<Recipe> getAll();

    @Query("SELECT * FROM recipe WHERE cat IN (:category)")
    List<Recipe> loadByCategories(int category);

    @Query("SELECT * FROM recipe WHERE url LIKE :url LIMIT 1")
    Recipe findByUrl(String url);


    @Insert
    void insertAll(Recipe... recipes);

    @Update
    void updateRecipes(Recipe... recipes);

    @Delete
    void delete(Recipe recipe);
}
