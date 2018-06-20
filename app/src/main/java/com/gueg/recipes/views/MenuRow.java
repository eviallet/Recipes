package com.gueg.recipes.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gueg.recipes.R;

public class MenuRow extends RelativeLayout {

    TextView _text;
    ImageView _image;

    public MenuRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.row_main_menu, this);
        _text = findViewById(R.id.row_main_menu_text);
        _image = findViewById(R.id.row_main_menu_image);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MenuRow,
                0, 0);

        try {
            _text.setText(a.getString(R.styleable.MenuRow_row_menu_text));
            _image.setImageDrawable(a.getDrawable(R.styleable.MenuRow_row_menu_image));
        } finally {
            a.recycle();
        }
    }
}
