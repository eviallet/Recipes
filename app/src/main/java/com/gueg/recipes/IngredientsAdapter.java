package com.gueg.recipes;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder>{

    private ArrayList<Ingredient> _list;


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView _image;
        TextView _title;
        ViewHolder(View v) {
            super(v);
            _image = v.findViewById(R.id.row_ingredient_image);
            _title = v.findViewById(R.id.row_ingredient_title);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public IngredientsAdapter(ArrayList<Ingredient> list) {
        _list = list;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ingredient, parent, false);
        return new IngredientsAdapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final IngredientsAdapter.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String qty = _list.get(position).getQuantity();
        holder._title.setText(qty+_list.get(position).getName());
        ImageLoader.getInstance().displayImage(_list.get(position).getImageUrl(), holder._image);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return _list.size();
    }



}