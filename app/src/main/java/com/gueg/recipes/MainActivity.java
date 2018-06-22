package com.gueg.recipes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gueg.recipes.tools.VerticalSpaceItemDecoration;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView _shoppingList;
    ShoppingListAdapter _shoppingAdapter;
    ArrayList<Ingredient> _shoppingListItems = new ArrayList<>();

    public static final String SHOPPING_KEY = "com.gueg.recipes.mainactivity.shopping_key";
    public static final String SHOPPING_ITEM = "com.gueg.recipes.mainactivity.shopping_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SQLUtility.init(this);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);


        _shoppingList = findViewById(R.id.activity_main_drawer_recyclerview);
        _shoppingList.setHasFixedSize(true);
        _shoppingList.setLayoutManager(new LinearLayoutManager(this));
        _shoppingList.addItemDecoration(new VerticalSpaceItemDecoration(3));

        _shoppingAdapter = new ShoppingListAdapter(_shoppingListItems, new ShoppingListAdapter.QuantityListener() {
            @Override
            public void onQuantityChanged(int pos, int changed) {
                if(changed>0)
                    _shoppingListItems.get(pos).addShoppingQuantity();
                else
                    _shoppingListItems.get(pos).dimShoppingQuantity();

                if(_shoppingListItems.get(pos).getShoppingQuantity()<0)
                    _shoppingListItems.remove(pos);

                _shoppingAdapter.notifyDataSetChanged();
            }
        });
        _shoppingList.setAdapter(_shoppingAdapter);


        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                _shoppingListItems.add((Ingredient)intent.getSerializableExtra(SHOPPING_ITEM));
                _shoppingAdapter.notifyDataSetChanged();
            }
        }, new IntentFilter(SHOPPING_KEY));
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
