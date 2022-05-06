package com.ayst.adplayer.home;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.ayst.adplayer.R;

public class FocusLayout extends RelativeLayout implements ViewTreeObserver.OnGlobalFocusChangeListener {
    private static final int CAL_FOCUS_RECT_CNT = 5;

    private View mFocusBorder;
    private View mCurFocusView;
    private Handler mHandler;
    private boolean allowHide = false;
    private boolean enable = false;
    private Rect mFocusRect = new Rect();
    private int mCalFocusRectCnt = CAL_FOCUS_RECT_CNT;

    private Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private Runnable mFocusRunnable = new Runnable() {
        @Override
        public void run() {
            if (null != mCurFocusView) {
                Rect focusRect = new Rect();
                mCurFocusView.getGlobalVisibleRect(focusRect);
                if (--mCalFocusRectCnt > 0
                        && (focusRect.left != mFocusRect.left
                        || focusRect.right != mFocusRect.right
                        || focusRect.top != mFocusRect.top
                        || focusRect.bottom != mFocusRect.bottom)) {
                    Log.i("FocusLayout", "focus rect change");
                    mFocusRect = focusRect;
                    mHandler.postDelayed(this, 200);
                } else {
                    mCalFocusRectCnt = CAL_FOCUS_RECT_CNT;
                    setFocus(mCurFocusView);
                }
            }
        }
    };

    public FocusLayout(Context context) {
        this(context, null);
    }

    public FocusLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mHandler = new Handler(Looper.getMainLooper());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(0, 0);
        this.mFocusBorder = new View(context);
        this.mFocusBorder.setBackgroundResource(R.drawable.ic_focus_border);
        this.addView(this.mFocusBorder, layoutParams);
    }

    public void allowHide(boolean allowHide) {
        this.allowHide = allowHide;
    }

    public void hide() {
        setFocusLocation(0, 0, 0, 0);
    }

    public void enable(boolean enable) {
        Log.i("FocusLayout", "enable: " + enable);
        this.enable = enable;
    }

    public void redraw() {
        setFocus(mCurFocusView);
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        if (enable) {
            mCurFocusView = newFocus;

            // Delay 10ms to avoid the loss of focus at the end of the ListView item
            mHandler.removeCallbacks(mFocusRunnable);
            mHandler.postDelayed(mFocusRunnable, 100);
        }
    }

    private void setFocus(View focusView) {
        if (null != focusView) {
            Rect focusRect = new Rect();
            focusView.getGlobalVisibleRect(focusRect);

            this.setFocusLocation(
                    focusRect.left - this.mFocusBorder.getPaddingLeft(),
                    focusRect.top - this.mFocusBorder.getPaddingTop(),
                    focusRect.right + this.mFocusBorder.getPaddingRight(),
                    focusRect.bottom + this.mFocusBorder.getPaddingBottom());

            if (allowHide) {
                mHandler.removeCallbacks(mHideRunnable);
                mHandler.postDelayed(mHideRunnable, 5000);
            }
        } else {
            hide();
        }
    }

    protected void setFocusLocation(int left, int top, int right, int bottom) {
        this.mFocusBorder.layout(left, top, right, bottom);
    }
}
