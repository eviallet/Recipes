package com.gueg.recipes;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Tag extends RelativeLayout {

    TextView _text;
    ImageView _image;

    public Tag(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.item_tag, this);
        _text = findViewById(R.id.item_tag_text);
        _image = findViewById(R.id.item_tag_bkg);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Tag,
                0, 0);

        try {
            _text.setText(a.getString(R.styleable.Tag_tag_text));
            _text.setTextColor(a.getColor(R.styleable.Tag_tag_text_color, 0xFFFFFFFF));
            _image.setColorFilter(a.getColor(R.styleable.Tag_tag_bkg_color, 0xFFFFFFFF));
        } finally {
            a.recycle();
        }
    }
}
