package com.ayst.adplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.youth.banner.Banner;

/**
 * Created by shenhaibo on 2018/10/21.
 */

public class IgnoreKeyEventBanner extends Banner {
    public IgnoreKeyEventBanner(Context context) {
        this(context, null);
    }

    public IgnoreKeyEventBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IgnoreKeyEventBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }
}
