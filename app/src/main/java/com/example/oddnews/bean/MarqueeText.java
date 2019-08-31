package com.example.oddnews.bean;

import android.content.Context;
import android.util.AttributeSet;

public class MarqueeText extends android.support.v7.widget.AppCompatTextView {
    public MarqueeText(Context context) {
        super(context);
    }
    public MarqueeText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MarqueeText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
