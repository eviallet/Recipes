package com.gueg.recipes;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder>{

    private ArrayList<Recipe> _list;
    private Context c;


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView _image;
        TextView _title;
        ImageView _star1;
        ImageView _star2;
        ImageView _star3;
        ImageView _star4;
        ImageView _star5;
        TextView _tags;
        TextView _ppl;
        ViewHolder(View v) {
            super(v);
            _image = v.findViewById(R.id.row_recipe_image);
            _star1 = v.findViewById(R.id.row_recipe_star1);
            _star2 = v.findViewById(R.id.row_recipe_star2);
            _star3 = v.findViewById(R.id.row_recipe_star3);
            _star4 = v.findViewById(R.id.row_recipe_star4);
            _star5 = v.findViewById(R.id.row_recipe_star5);
            _title = v.findViewById(R.id.row_recipe_title);
            _ppl = v.findViewById(R.id.row_recipe_ppl);
            _tags = v.findViewById(R.id.row_recipe_tags);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecipesAdapter(Context c, ArrayList<Recipe> list) {
        this.c = c;
        _list = list;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecipesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recipe, parent, false);
        return new RecipesAdapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecipesAdapter.ViewHolder holder, final int position) {
        holder._title.setText(_list.get(position).getName());
        ImageLoader.getInstance().displayImage(_list.get(position).getImageUrl(), holder._image);
        holder._ppl.setText(Integer.toString(_list.get(position).getNumReviews()));
        holder._tags.setText(_list.get(position).getTag());

        switch (_list.get(position).getRating()) {
            case 5:
                holder._star5.setImageDrawable(c.getDrawable(R.drawable.ic_star));
            case 4:
                holder._star4.setImageDrawable(c.getDrawable(R.drawable.ic_star));
            case 3:
                holder._star3.setImageDrawable(c.getDrawable(R.drawable.ic_star));
            case 2:
                holder._star2.setImageDrawable(c.getDrawable(R.drawable.ic_star));
            case 1:
                holder._star1.setImageDrawable(c.getDrawable(R.drawable.ic_star));
                break;
            default:
                break;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return _list.size();
    }

}