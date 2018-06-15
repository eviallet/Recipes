package com.gueg.recipes;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RecipesListActivity extends AppCompatActivity {

    public static final String KEY_TYPE = "com.gueg.recipes.recipeslistactivity.type";
    public static final String KEY_ENTREES = "com.gueg.recipes.recipeslistactivity.entrees";
    public static final String KEY_PLATS = "com.gueg.recipes.recipeslistactivity.palts";
    public static final String KEY_DESSERTS = "com.gueg.recipes.recipeslistactivity.desserts";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String type = getIntent().getStringExtra(KEY_TYPE);

        new Database(this).execute(type);



    }


    // https://stackoverflow.com/a/46166223/8308507
    private static class Database extends AsyncTask<String, Void, ArrayList<Recipe>> {
        private WeakReference<RecipesListActivity> _context;

        Database(RecipesListActivity context) {
            _context = new WeakReference<>(context);
        }

        @Override
        protected ArrayList<Recipe> doInBackground(String... params) {

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Recipe> result) {
            // get a reference to the activity if it is still there
            RecipesListActivity activity = _context.get();
            if (activity == null || activity.isFinishing()) return;
        }
    }
}