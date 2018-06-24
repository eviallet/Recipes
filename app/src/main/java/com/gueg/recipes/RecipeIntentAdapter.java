package com.gueg.recipes;


import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class RecipeIntentAdapter extends RecyclerView.Adapter<RecipeIntentAdapter.ViewHolder>{

    private ArrayList<Recipe> _list;


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView _title;
        ImageView _image;
        RadioGroup _radios;
        ViewHolder(View v) {
            super(v);
            _title = v.findViewById(R.id.row_recipe_intent_title);
            _image = v.findViewById(R.id.row_recipe_intent_image);
            _radios = v.findViewById(R.id.row_recipe_intent_radios);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecipeIntentAdapter(ArrayList<Recipe> list) {
        _list = list;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecipeIntentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recipe_intent, parent, false);
        return new RecipeIntentAdapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecipeIntentAdapter.ViewHolder holder, final int position) {
        holder._title.setText(_list.get(position).getName());
        ImageLoader.getInstance().displayImage(_list.get(position).getImageUrl(), holder._image);
        holder._radios.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.row_recipe_intent_entree:
                        _list.get(holder.getAdapterPosition()).setCategory(Recipe.CATEGORY_ENTREE);
                        break;
                    case R.id.row_recipe_intent_plat:
                        _list.get(holder.getAdapterPosition()).setCategory(Recipe.CATEGORY_PLAT);
                        break;
                    case R.id.row_recipe_intent_dessert:
                        _list.get(holder.getAdapterPosition()).setCategory(Recipe.CATEGORY_DESSERT);
                        break;
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return _list.size();
    }

}