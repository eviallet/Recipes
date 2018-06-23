package com.gueg.recipes.shopping_database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.gueg.recipes.Ingredient;

@Database(entities = {Ingredient.class}, version = 2)
public abstract class ShoppingDatabase extends RoomDatabase {

    public abstract ShoppingDao shoppingDao();

    private static ShoppingDatabase INSTANCE;


    public static ShoppingDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ShoppingDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ShoppingDatabase.class, "shopping_database")
                            .build();

                }
            }
        }
        return INSTANCE;
    }
}