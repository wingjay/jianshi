package com.wingjay.jianshi.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.wingjay.jianshi.R;
import com.wingjay.jianshi.ui.widget.font.CustomizeTextView;
import com.wingjay.jianshi.util.DisplayUtil;

/**
 * Created by wingjay on 9/30/15.
 */
public class VerticalTextView extends CustomizeTextView {

    public VerticalTextView(Context context) {
        super(context);
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.VerticalTextView, 0, 0);
        try {
            float textSizePixel = typedArray.getDimension(R.styleable.VerticalTextView_verticalTextSize,
                    getResources().getDimension(R.dimen.normal_text_size));
            int textSizeSp = DisplayUtil.px2sp(context, textSizePixel);
            setTextSize(textSizeSp);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (TextUtils.isEmpty(text)) {
            super.setText(text, type);
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        int length = text.length();
        for (int i=0; i<length; i++) {
            CharSequence sequence = text.toString().subSequence(i, i+1);
            stringBuffer.append(sequence);
            if (i < length - 1) {
                stringBuffer.append("\n");
            }
        }
        super.setText(stringBuffer, type);
    }
}
