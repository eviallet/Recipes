package com.gueg.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gueg.recipes.sql.SQLUtility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLUtility.init(this);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);
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
                startActivity(new Intent(MainActivity.this, RecipesSearchActivity.class));
                break;
        }
    }



}
