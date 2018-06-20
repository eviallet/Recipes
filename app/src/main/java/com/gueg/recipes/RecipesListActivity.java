package com.gueg.recipes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class RecipesListActivity extends AppCompatActivity {

    public static final String KEY_TYPE = "com.gueg.recipes.recipeslistactivity.type";
    public static final String KEY_ENTREES = "com.gueg.recipes.recipeslistactivity.entrees";
    public static final String KEY_PLATS = "com.gueg.recipes.recipeslistactivity.palts";
    public static final String KEY_DESSERTS = "com.gueg.recipes.recipeslistactivity.desserts";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String type = getIntent().getStringExtra(KEY_TYPE);



    }

}