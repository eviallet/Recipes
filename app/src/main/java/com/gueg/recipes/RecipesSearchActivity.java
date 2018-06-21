package com.gueg.recipes;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.gueg.recipes.tools.RecyclerItemClickListener;
import com.gueg.recipes.tools.RecyclerViewAnimator;
import com.gueg.recipes.tools.VerticalSpaceItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class RecipesSearchActivity extends AppCompatActivity {
    RecyclerView _searchRecyclerView;
    RecipesAdapter _searchAdapter;
    EditText _search;
    SpinKitView _loading;
    ArrayList<Recipe> _recipes = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_search);

        _search = findViewById(R.id.activity_recipes_search_search_box);
        _search.setImeOptions(EditorInfo.IME_ACTION_DONE);
        _search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard();
                    searchFromBox();
                }
                return false;
            }
        });

        _loading = findViewById(R.id.activity_recipes_search_loading);


        _searchRecyclerView = findViewById(R.id.activity_recipes_search_recyclerview);
        _searchRecyclerView.setHasFixedSize(true);
        _searchRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        _searchRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(15));

        _searchAdapter = new RecipesAdapter(this, _recipes);


        _searchRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, _searchRecyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        RecipeViewDialog dialog = new RecipeViewDialog();
                        dialog.setRecipe(_recipes.get(position), false);
                        dialog.show(getSupportFragmentManager(), "com.gueg.recipes.recipessearchactivity.dialog");
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
        }));

        _searchRecyclerView.setAdapter(_searchAdapter);

        try {
            search(new String[]{new RandomRecipe().execute().get(), "6"});
        } catch (InterruptedException|ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_recipes_search_search_image:
                hideKeyboard();
                searchFromBox();
                break;
            default:
                break;
        }
    }

    public void searchFromBox() {
        if(!_search.getText().toString().isEmpty())
            search(new String[]{_search.getText().toString()});
    }

    public synchronized void search(final String[] txt) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                _recipes.clear();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _searchAdapter.notifyDataSetChanged();
                        showLoading();
                    }
                });
                try {
                    _recipes.addAll(new NetworkThread().execute(txt).get());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerViewAnimator.runAnimation(_searchRecyclerView);
                            _searchAdapter.notifyDataSetChanged();
                            hideLoading();
                        }
                    });
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static class NetworkThread extends AsyncTask<String, Void, ArrayList<Recipe>> {

        @Override
        protected ArrayList<Recipe> doInBackground(String... strs) {
            try {
                if(strs.length>1)
                    return Recipe.search(strs[0], strs[1]);
                else
                    return Recipe.search(strs[0], null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void showLoading() {
        _loading.animate().alpha(0).scaleX(0.5f).scaleY(0.5f).setDuration(0).withEndAction(new Runnable() {
            @Override
            public void run() {
                _loading.setVisibility(View.VISIBLE);
                _loading.animate().alpha(1).scaleX(1).scaleY(1).setDuration(150).start();
            }
        }).start();
    }

    private void hideLoading() {
        _loading.animate().alpha(0).scaleX(0.5f).scaleY(0.5f).setDuration(150).withEndAction(new Runnable() {
            @Override
            public void run() {
                _loading.setVisibility(View.GONE);
            }
        }).start();
    }

    private static class RandomRecipe extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return Recipe.getRandomRecipe();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm!=null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
