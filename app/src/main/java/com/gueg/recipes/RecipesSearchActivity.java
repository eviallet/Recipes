package com.gueg.recipes;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
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
    String _currentSearchTerm = "";
    String _lastpos = "0";

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

        randomSearch();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_recipes_search_search_image:
                findViewById(R.id.activity_recipes_search_search_image).animate().scaleX(0.8f).scaleY(0.8f).setDuration(150).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.activity_recipes_search_search_image).animate().scaleX(1f).scaleY(1f).setDuration(250).start();
                    }
                }).start();
                hideKeyboard();
                searchFromBox();
                break;
            case R.id.activity_recipes_search_next:
                findViewById(R.id.activity_recipes_search_next).animate().translationX(200f).alpha(0.2f).setDuration(500).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.activity_recipes_search_next).animate().translationX(-75f).alpha(0.7f).setDuration(0).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.activity_recipes_search_next).animate().translationX(0).alpha(1).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(600).start();
                            }
                        }).start();
                    }
                }).start();
                hideKeyboard();
                loadNextPage();
                break;
            case R.id.activity_recipes_search_random:
                findViewById(R.id.activity_recipes_search_random).animate().rotationBy(-20f).setDuration(250).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.activity_recipes_search_random).animate().rotationBy(200f).setDuration(500).start();
                    }
                }).start();
                hideKeyboard();
                randomSearch();
                break;
            default:
                break;
        }
    }




    private void randomSearch() {
        try {
            search(new String[]{
                    (_currentSearchTerm = new RandomRecipe().execute().get()),
                    Recipe.PARAM_LIMIT,
                    (_lastpos="6")
            });
        } catch (InterruptedException|ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void searchFromBox() {
        if(!_search.getText().toString().isEmpty()) {
            _currentSearchTerm = _search.getText().toString();
            search(new String[]{
                    _currentSearchTerm,
                    Recipe.PARAM_NONE
            });
        }
    }

    private void loadNextPage() {
        search(new String[]{
                _currentSearchTerm,
                Recipe.PARAM_CURPOS,
                _lastpos
        });
    }

    public synchronized void search(final String[] params) {
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
                    _recipes.addAll(new NetworkThread().execute(params).get());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerViewAnimator.runAnimation(_searchRecyclerView);
                            _searchAdapter.notifyDataSetChanged();
                            hideLoading();
                            if(params[1].equals(Recipe.PARAM_CURPOS))
                                _lastpos = Integer.toString(Integer.decode(_lastpos)+_recipes.size());
                            else
                                _lastpos = Integer.toString(_recipes.size());
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
                int param = -1;
                if(!strs[1].equals(Recipe.PARAM_NONE))
                    param = Integer.decode(strs[2]);
                return Recipe.search(strs[0], strs[1], param);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
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

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm!=null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
