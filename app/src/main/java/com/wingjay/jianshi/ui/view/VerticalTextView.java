package com.wingjay.jianshi.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.wingjay.jianshi.ui.view.font.CustomizeTextView;

/**
 * Created by wingjay on 9/30/15.
 */
public class VerticalTextView extends CustomizeTextView {

    public VerticalTextView(Context context) {
        super(context);
    }

    public VerticalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (TextUtils.isEmpty(text)) {
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
