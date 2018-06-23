package com.gueg.recipes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gueg.recipes.recipes_database.RecipeDatabase;
import com.gueg.recipes.tools.RecyclerItemClickListener;
import com.gueg.recipes.tools.VerticalSpaceItemDecoration;

import java.util.ArrayList;

public class RecipesListActivity extends AppCompatActivity {

    public static final String KEY_TYPE = "com.gueg.recipes.recipeslistactivity.type";

    ArrayList<Recipe> _recipes = new ArrayList<>();
    RecyclerView _recyclerView;
    RecipesAdapter _adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);

        _recyclerView = findViewById(R.id.activity_recipes_recyclerview);
        _recyclerView.setHasFixedSize(true);
        _recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        _recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(15));

        _adapter = new RecipesAdapter(this, _recipes);


        _recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, _recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final RecipeViewDialog dialog = new RecipeViewDialog();
                        dialog.setRecipe(_recipes.get(position), true);
                        dialog.setListener(new RecipeViewDialog.RecipeUpdate() {
                            @Override
                            public void onRecipeRemoved() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        _adapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                        updateRecipesFromDatabase();
                                    }
                                });
                            }
                        });
                        dialog.show(getSupportFragmentManager(), "com.gueg.recipes.recipessearchactivity.dialog");
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }));

        _recyclerView.setAdapter(_adapter);

        updateRecipesFromDatabase();

    }

    private void updateRecipesFromDatabase() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                _recipes.clear();
                _recipes.addAll(RecipeDatabase.getDatabase(getApplicationContext()).recipeDao().loadByCategories(getIntent().getIntExtra(KEY_TYPE,0)));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

}