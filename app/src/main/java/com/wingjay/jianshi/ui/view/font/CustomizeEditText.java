package com.wingjay.jianshi.ui.view.font;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.wingjay.jianshi.R;

/**
 * Created by wingjay on 10/3/15.
 */
public class CustomizeEditText extends EditText {

    private Context context;
    public CustomizeEditText(Context context) {
        super(context);
        this.context = context;
        initTypeFace();
    }

    public CustomizeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initTypeFace(attrs);
    }

    private void initTypeFace(AttributeSet attrs) {
        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.CustomizeEditText, 0, 0);
        try {
            String fontFamily = typedArray.getString(R.styleable.CustomizeEditText_myEditTextFontFamily);
            if (fontFamily != null) {
                setTypeFaceByPath("fonts/" + fontFamily);
                return;
            }
        } finally {
            typedArray.recycle();
        }
        initTypeFace();
    }

    private void initTypeFace() {
        String fontPath = "fonts/" + getFontName();
        setTypeFaceByPath(fontPath);
    }

    private void setTypeFaceByPath(String fontPath) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontPath);
        setTypeface(typeface);
    }

    protected String getFontName() {
        return FontFamilyFactory.getDefaultFontFamily();
    }
}
