package com.gueg.recipes.shopping_database;


import android.arch.persistence.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.gueg.recipes.Ingredient;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converter {
    @TypeConverter
    public static ArrayList<Ingredient> fromStringToIngredients(String ingredientsString) {
        Type listType = new TypeToken<ArrayList<Ingredient>>() {}.getType();
        return new Gson().fromJson(ingredientsString, listType);
    }

    @TypeConverter
    public static String fromIngredientsToString(ArrayList<Ingredient> ingredients) {
        Gson gson = new Gson();
        return gson.toJson(ingredients);
    }

}
