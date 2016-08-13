package com.wingjay.jianshi.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wingjay.jianshi.R;

/**
 * Created by wingjay on 10/1/15.
 */
public class RedPointView extends FrameLayout {

    private View containerView;
    private TextView textView;

    public RedPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_red_point, this);
        textView = (TextView) findViewById(R.id.pointer);
        containerView = findViewById(R.id.red_point_container);
        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.RedPointView, 0, 0);
        try {
            String text = typedArray.getString(R.styleable.RedPointView_text);
            setText(text);
            int colorRes = typedArray.getInt(R.styleable.RedPointView_redPointViewBgColor,
                    R.color.bright_red);
            setContainerBackgroundColor(colorRes);
        } finally {
            typedArray.recycle();
        }
    }

    public void setText(String text) {
        if (text.length() > 1) {
            return;
        }
        textView.setText(text);
    }

    public void setContainerBackgroundColor(int colorRes) {
        GradientDrawable drawable = (GradientDrawable) containerView.getBackground();
        drawable.setColor(getResources().getColor(colorRes));
    }

}
