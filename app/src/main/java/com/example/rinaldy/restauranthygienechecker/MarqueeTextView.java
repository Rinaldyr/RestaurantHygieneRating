package com.example.rinaldy.restauranthygienechecker;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * Created by Rinaldy on 04/03/2018.
 */

public class MarqueeTextView extends android.support.v7.widget.AppCompatTextView {

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if(focused)
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public void onWindowFocusChanged(boolean focused) {
        if(focused)
            super.onWindowFocusChanged(focused);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
