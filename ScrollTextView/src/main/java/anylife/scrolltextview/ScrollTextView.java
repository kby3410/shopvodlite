package anylife.scrolltextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScrollTextView extends SurfaceView implements SurfaceHolder.Callback {
    private final String TAG = "ScrollTextView";

    private static final float DEF_TEXT_SIZE = 14.0f;
    private static final String SCROLL_ORIENTATION_H = "h";
    private static final String SCROLL_ORIENTATION_V = "v";

    // surface Handle onto a raw buffer that is being managed by the screen compositor.
    private SurfaceHolder mSurfaceHolder;

    private Paint mPaint = null;
    private boolean isStop = false;

    //Default value
    public String mOrientation = SCROLL_ORIENTATION_H;
    private int mSpeed = 1;
    private String mText = "";
    private float mTextSize = DEF_TEXT_SIZE;
    private int mTextColor = Color.BLACK;

    private int mViewWidth = 0;
    private int mViewHeight = 0;
    private float mTextWidth = 0f;
    private float mTextHeight = 0f;
    private float mTextX = 0f;
    private float mTextY = 0f;
    private float mScrollStep = 0.0f;

    private ScheduledExecutorService mScheduledExecutorService;

    public ScrollTextView(Context context) {
        this(context, null);
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mSurfaceHolder = this.getHolder();  //get The surface holder
        mSurfaceHolder.addCallback(this);
        mPaint = new Paint();
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.ScrollText);
        mOrientation = arr.getString(R.styleable.ScrollText_st_orientation);
        mSpeed = arr.getInteger(R.styleable.ScrollText_st_speed, mSpeed);
        mText = arr.getString(R.styleable.ScrollText_st_text);
        mTextColor = arr.getColor(R.styleable.ScrollText_st_textColor, mTextColor);
        mTextSize = arr.getDimension(R.styleable.ScrollText_st_textSize, mTextSize);

        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);

        mTextWidth = getTextWidth();
        mTextHeight = getTextHeight();

        setZOrderOnTop(true);  //Control whether the surface view's surface is placed on top of its window.
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    /**
     * measure text height width
     *
     * @param widthMeasureSpec  widthMeasureSpec
     * @param heightMeasureSpec heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getTextHeight();
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        Log.i(TAG, "onMeasure, mViewWidth=" + mViewWidth + " mViewHeight=" + mViewHeight);

        // When layout width or height is wrap_content, should init ScrollTextView Width/Height
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mViewWidth, height);
            mViewHeight = height;
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mViewWidth, mViewHeight);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mViewWidth, height);
            mViewHeight = height;
        }
    }

    /**
     * surfaceChanged
     *
     * @param arg0 arg0
     * @param arg1 arg1
     * @param arg2 arg1
     * @param arg3 arg1
     */
    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        Log.d(TAG, "surfaceChanged, arg0:" + arg0.toString() + "  arg1:" + arg1 + "  arg2:" + arg2 + "  arg3:" + arg3);
    }

    /**
     * surfaceCreated,init a new scroll thread.
     * lockCanvas
     * Draw somthing
     * unlockCanvasAndPost
     *
     * @param holder holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isStop = false;
        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        mScheduledExecutorService.scheduleAtFixedRate(new ScrollTextThread(), 1000, 1000, TimeUnit.MILLISECONDS);
        Log.d(TAG, "surfaceCreated");
    }

    /**
     * surfaceDestroyed
     *
     * @param arg0 SurfaceHolder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        isStop = true;
        mScheduledExecutorService.shutdownNow();
        Log.d(TAG, "surfaceDestroyed");
    }

    /**
     * Get text width
     *
     * @return text width
     */
    public float getTextWidth() {
        return mPaint.measureText(mText);
    }

    /**
     * Get text height by baseline
     *
     * @return text height
     */
    public int getTextHeight() {
        FontMetrics fm = mPaint.getFontMetrics();
        return (int) Math.ceil(fm.leading - fm.ascent);
    }

    /**
     * Set text scroll orientation
     *
     * @param orientation SCROLL_ORIENTATION_H or SCROLL_ORIENTATION_H
     */
    public void setHorizontal(String orientation) {
        mOrientation = orientation;
    }

    /**
     * Set text
     *
     * @param text scroll text
     */
    public void setText(String text) {
        this.mText = text;
        if (TextUtils.isEmpty(mText)) {
            this.mText = "";
        }
        mTextWidth = getTextWidth();
        mTextHeight = getTextHeight();
        mScrollStep = mViewWidth + mTextWidth;
    }

    /**
     * Set text size
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        this.mTextSize = dp2px(getContext(), textSize);
        mPaint.setTextSize(mTextSize <= 0 ? DEF_TEXT_SIZE : mTextSize);
        mTextWidth = getTextWidth();
        mTextHeight = getTextHeight();
        mScrollStep = mViewWidth + mTextWidth;
    }

    /**
     * Set text color
     *
     * @param textColor
     */
    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
        mPaint.setColor(mTextColor);
    }

    /**
     * Set scroll speed
     *
     * @param speed scroll speed [0,10]
     */
    public void setSpeed(int speed) {
        if (speed > 10 || speed < 0) {
            throw new IllegalArgumentException("Speed was invalid integer, it must between 0 and 10");
        } else {
            this.mSpeed = speed;
        }
    }

    /**
     * Draw text
     *
     * @param x
     * @param y
     */
    public synchronized void draw(float x, float y) {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
        canvas.drawText(mText, x, y, mPaint);
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    /**
     * Scroll text vertical
     */
    private void drawVerticalScroll() {
        List<String> strings = new ArrayList<>();
        int start = 0, end = 0;
        while (end < mText.length()) {
            while (mPaint.measureText(mText.substring(start, end)) < mViewWidth && end < mText.length()) {
                end++;
            }
            if (end == mText.length()) {
                strings.add(mText.substring(start, end));
                break;
            } else {
                end--;
                strings.add(mText.substring(start, end));
                start = end;
            }
        }

        float fontHeight = getTextHeight();
        int GPoint = ((int) fontHeight + mViewHeight) / 2;

        for (int n = 0; n < strings.size(); n++) {
            if (isStop) {
                return;
            }
            for (float i = mViewHeight + fontHeight; i > -fontHeight; i = i - 3) {
                Canvas canvas = mSurfaceHolder.lockCanvas();
                canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                canvas.drawText(strings.get(n), 0, i, mPaint);
                mSurfaceHolder.unlockCanvasAndPost(canvas);
                if (i - GPoint < 4 && i - GPoint > 0) {
                    if (isStop) {
                        return;
                    }
                    try {
                        Thread.sleep(mSpeed * 1000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        }
    }

    /**
     * Scroll thread
     */
    class ScrollTextThread implements Runnable {
        @Override
        public void run() {
            mTextWidth = getTextWidth();
            mTextHeight = getTextHeight();
            mScrollStep = mViewWidth + mTextWidth;
            mTextX = mViewWidth - mViewWidth / 5;

            while (true) {
                mTextY = mViewHeight/2 + mTextHeight/2;

                // NoNeed Scroll
                if (mTextWidth < getWidth()) {
                    draw((getWidth()-mTextWidth)/2, mTextY);
                    break;
                }

                if (TextUtils.equals(SCROLL_ORIENTATION_H, mOrientation)) {
                    draw(mViewWidth - mTextX, mTextY);
                    mTextX += mSpeed;
                    if (mTextX > mScrollStep) {
                        mTextX = 0;
                    }
                } else {
                    drawVerticalScroll();
                }
            }
        }
    }

    private static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
