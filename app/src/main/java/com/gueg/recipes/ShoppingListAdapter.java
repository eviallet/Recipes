package com.gueg.recipes;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private ArrayList<Ingredient> _list;
    private QuantityListener _listener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView _title;
        TextView _quantity;
        ImageView _image;
        ImageView _add;
        ImageView _dim;
        ViewHolder(View v) {
            super(v);
            _title = v.findViewById(R.id.row_shopping_item_title);
            _image = v.findViewById(R.id.row_shopping_item_image);
            _quantity = v.findViewById(R.id.row_shopping_item_quantity);
            _add = v.findViewById(R.id.row_shopping_item_add);
            _dim = v.findViewById(R.id.row_shopping_item_dim);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ShoppingListAdapter(ArrayList<Ingredient> list, QuantityListener listener) {
        _list = list;
        _listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ShoppingListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_shopping_item, parent, false);
        return new ShoppingListAdapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ShoppingListAdapter.ViewHolder holder, final int position) {
        holder._title.setText(_list.get(position).getName());
        holder._quantity.setText(Integer.toString(_list.get(position).getShoppingQuantity()));
        ImageLoader.getInstance().displayImage(_list.get(position).getImageUrl(), holder._image);
        holder._add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _listener.onQuantityChanged(holder.getAdapterPosition(), 1);
            }
        });
        holder._dim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _listener.onQuantityChanged(holder.getAdapterPosition(), -1);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return _list.size();
    }


    public interface QuantityListener {
        void onQuantityChanged(int pos, int changed);
    }


}