package com.wingjay.jianshi.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wingjay.jianshi.R;

/**
 * Created by wingjay on 10/1/15.
 */
public class RedPointView extends FrameLayout {

    private TextView textView;

    public RedPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_red_point, this);
        textView = (TextView) findViewById(R.id.pointer);
        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.RedPointView, 0, 0);
        try {
            String text = typedArray.getString(R.styleable.RedPointView_text);
            setText(text);
        } finally {
            typedArray.recycle();
        }
    }

    public void setText(String text) {
        if (text.length() != 1) {
            return;
        }
        textView.setText(text);
    }
}
