package com.gueg.recipes.search;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gueg.recipes.R;
import com.gueg.recipes.Recipe;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class RecipesSearchAdapter extends RecyclerView.Adapter<RecipesSearchAdapter.ViewHolder>{

    private ArrayList<Recipe> _list;


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView _image;
        TextView _title;
        ViewHolder(View v) {
            super(v);
            _image = v.findViewById(R.id.row_recipe_image);
            _title = v.findViewById(R.id.row_recipe_title);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecipesSearchAdapter(ArrayList<Recipe> list) {
        _list = list;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecipesSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recipe, parent, false);
        return new RecipesSearchAdapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final RecipesSearchAdapter.ViewHolder holder, final int position) {
        holder._title.setText(_list.get(position).getName());
        ImageLoader.getInstance().displayImage(_list.get(position).getImageUrl(), holder._image);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return _list.size();
    }



}