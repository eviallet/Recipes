package com.gueg.recipes;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder>{

    private ArrayList<String> _list;
    private int _size;


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView _content;
        TextView _number;
        ImageView _top;
        ImageView _bottom;
        ViewHolder(View v) {
            super(v);
            _content = v.findViewById(R.id.row_steps_text);
            _number = v.findViewById(R.id.row_steps_count_text);
            _top = v.findViewById(R.id.row_steps_count_top_link);
            _bottom = v.findViewById(R.id.row_steps_count_bottom_link);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public StepsAdapter(ArrayList<String> list, int size) {
        _list = list;
        _size = size -1;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public StepsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_steps, parent, false);
        return new StepsAdapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final StepsAdapter.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder._content.setText(_list.get(position));
        holder._number.setText(Integer.toString(position+1));
        if(position==0)
            holder._top.setVisibility(View.INVISIBLE);
        else if(position==_size)
            holder._bottom.setVisibility(View.INVISIBLE);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return _list.size();
    }



}