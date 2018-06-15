package com.gueg.recipes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.activity_main_entrees:
                Intent intentEntrees = new Intent(MainActivity.this, RecipesListActivity.class);
                intentEntrees.putExtra(RecipesListActivity.KEY_TYPE, RecipesListActivity.KEY_ENTREES);
                startActivity(intentEntrees);
                break;
            case R.id.activity_main_plats:
                Intent intentPlats = new Intent(MainActivity.this, RecipesListActivity.class);
                intentPlats.putExtra(RecipesListActivity.KEY_TYPE, RecipesListActivity.KEY_PLATS);
                startActivity(intentPlats);
                break;
            case R.id.activity_main_desserts:
                Intent intentDesserts = new Intent(MainActivity.this, RecipesListActivity.class);
                intentDesserts.putExtra(RecipesListActivity.KEY_TYPE, RecipesListActivity.KEY_DESSERTS);
                startActivity(intentDesserts);
                break;
            case R.id.activity_main_ajouter:
                new NetworkThread().execute("http://www.marmiton.org/recettes/recette_tarte_10607.aspx");
                break;
        }
    }

    private static class NetworkThread extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strs) {
            try {
                ArrayList<Recipe> recipes = Recipe.search("tarte");
                recipes.get(0).loadInformations();
                Log.d(":-:",recipes.get(0).toString());

                /*
                Recipe r = new Recipe("Recette",strs[0]);
                r.loadInformations();
                Log.d(":-:",r.toString());
                */
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // https://stackoverflow.com/a/8993175/8308507
    private static class BitmapHelper extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strs) {
            try {
                URL url = new URL(strs[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
