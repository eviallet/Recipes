package com.gueg.recipes;

import android.arch.persistence.room.Room;
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
import android.widget.EditText;

import com.gueg.recipes.recipes_database.RecipeDatabase;
import com.gueg.recipes.shopping_database.ShoppingDatabase;
import com.gueg.recipes.tools.VerticalSpaceItemDecoration;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView _shoppingList;
    ShoppingListAdapter _shoppingAdapter;
    ArrayList<Ingredient> _shoppingListItems = new ArrayList<>();

    EditText _ingredientText;

    public static final String SHOPPING_KEY = "com.gueg.recipes.mainactivity.shopping_key";
    public static final String SHOPPING_ITEM = "com.gueg.recipes.mainactivity.shopping_item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Room.databaseBuilder(getApplicationContext(),
                RecipeDatabase.class, "recipes").build();

        Room.databaseBuilder(getApplicationContext(),
                ShoppingDatabase.class, "shopping").build();

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).defaultDisplayImageOptions(defaultOptions).build();
        ImageLoader.getInstance().init(config);

        _ingredientText = findViewById(R.id.activity_main_add_user_text);


        _shoppingList = findViewById(R.id.activity_main_drawer_recyclerview);
        _shoppingList.setHasFixedSize(true);
        _shoppingList.setLayoutManager(new LinearLayoutManager(this));
        _shoppingList.addItemDecoration(new VerticalSpaceItemDecoration(3));

        _shoppingAdapter = new ShoppingListAdapter(_shoppingListItems, new ShoppingListAdapter.QuantityListener() {
            @Override
            public void onQuantityChanged(final int pos, int changed) {
                if(changed>0)
                    _shoppingListItems.get(pos).addShoppingQuantity();
                else
                    _shoppingListItems.get(pos).dimShoppingQuantity();

                if(_shoppingListItems.get(pos).getShoppingQuantity()<1) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ShoppingDatabase.getDatabase(getApplicationContext()).shoppingDao().delete(_shoppingListItems.get(pos));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    _shoppingListItems.remove(pos);
                                    _shoppingAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ShoppingDatabase.getDatabase(getApplicationContext()).shoppingDao().updateIngredients(_shoppingListItems.get(pos));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    _shoppingAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();
                }
            }
        });
        _shoppingList.setAdapter(_shoppingAdapter);


        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                final Ingredient ing = (Ingredient) intent.getSerializableExtra(SHOPPING_ITEM);
                if(ShoppingDatabase.getDatabase(getApplicationContext()).shoppingDao().findByTitle(ing.getName())==null) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ShoppingDatabase.getDatabase(getApplicationContext()).shoppingDao().insertAll(ing);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    _shoppingListItems.add(ing);
                                    _shoppingAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();
                }
            }
        }, new IntentFilter(SHOPPING_KEY));


        loadShoppingFromDatabase();
    }

    private void loadShoppingFromDatabase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                _shoppingListItems.clear();
                _shoppingListItems.addAll(ShoppingDatabase.getDatabase(getApplicationContext()).shoppingDao().getAll());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _shoppingAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.activity_main_entrees:
                Intent intentEntrees = new Intent(MainActivity.this, RecipesListActivity.class);
                intentEntrees.putExtra(RecipesListActivity.KEY_TYPE, Recipe.CATEGORY_ENTREE);
                startActivity(intentEntrees);
                break;
            case R.id.activity_main_plats:
                Intent intentPlats = new Intent(MainActivity.this, RecipesListActivity.class);
                intentPlats.putExtra(RecipesListActivity.KEY_TYPE, Recipe.CATEGORY_PLAT);
                startActivity(intentPlats);
                break;
            case R.id.activity_main_desserts:
                Intent intentDesserts = new Intent(MainActivity.this, RecipesListActivity.class);
                intentDesserts.putExtra(RecipesListActivity.KEY_TYPE, Recipe.CATEGORY_DESSERT);
                startActivity(intentDesserts);
                break;
            case R.id.activity_main_ajouter:
                startActivity(new Intent(MainActivity.this, RecipesSearchActivity.class));
                break;
            case R.id.activity_main_add_button:
                if(_ingredientText.getText().toString().isEmpty())
                    return;
                final Ingredient ing = new Ingredient(_ingredientText.getText().toString(), "", "");
                if(ShoppingDatabase.getDatabase(getApplicationContext()).shoppingDao().findByTitle(ing.getName())==null) {
                    _ingredientText.setText("");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ShoppingDatabase.getDatabase(getApplicationContext()).shoppingDao().insertAll(ing);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    _shoppingListItems.add(ing);
                                    _shoppingAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();
                }
                break;
        }
    }



}
