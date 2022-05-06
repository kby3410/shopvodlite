package com.ayst.adplayer.player;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ayst.adplayer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shenhaibo on 2018/11/2.
 */

public class MediaController extends RelativeLayout {

    @BindView(R.id.iv_setting)
    ImageView mSettingIv;
    @BindView(R.id.iv_play)
    ImageView mPlayIv;
    @BindView(R.id.iv_previous)
    ImageView mPreviousIv;
    @BindView(R.id.iv_next)
    ImageView mNextIv;
    @BindView(R.id.iv_full_screen)
    ImageView mFullScreenIv;
    @BindView(R.id.media_controller)
    RelativeLayout mMediaController;

    private Context mContext;
    private Handler mHandler;
    private MediaPlayerControl mMediaPlayerControl;

    private Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    public MediaController(Context context) {
        this(context, null);
    }

    public MediaController(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MediaController(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
        View view = inflate(context, R.layout.layout_media_controller, this);
        ButterKnife.bind(this);

        initView(view);
    }

    private void initView(View view) {
        show(5000);
    }

    public void update() {
        if (null != mMediaPlayerControl) {
            if (mMediaPlayerControl.isPlaying()) {
                mPlayIv.setImageResource(R.drawable.ic_pause);
            } else {
                mPlayIv.setImageResource(R.drawable.ic_play);
            }

            if (mMediaPlayerControl.isFullscreen()) {
                mFullScreenIv.setImageResource(R.drawable.ic_cancel_full_screen);
            } else {
                mFullScreenIv.setImageResource(R.drawable.ic_full_screen);
            }
        }
    }

    @OnClick({R.id.iv_setting, R.id.iv_play, R.id.iv_previous, R.id.iv_next, R.id.iv_full_screen, R.id.iv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_setting:
                if (null != mMediaPlayerControl) {
                    mMediaPlayerControl.setting();
                }
                break;
            case R.id.iv_save:
                if (null != mMediaPlayerControl) {
                    mMediaPlayerControl.save();
                }
                break;
            case R.id.iv_play:
                if (null != mMediaPlayerControl) {
                    if (mMediaPlayerControl.isPlaying()) {
                        mMediaPlayerControl.pause();
                        mPlayIv.setImageResource(R.drawable.ic_play);
                    } else {
                        mMediaPlayerControl.start();
                        mPlayIv.setImageResource(R.drawable.ic_pause);
                    }
                }
                break;
            case R.id.iv_previous:
                if (null != mMediaPlayerControl) {
                    if (mMediaPlayerControl.isPlaying()) {
                        mMediaPlayerControl.previous();
                    }
                }
                break;
            case R.id.iv_next:
                if (null != mMediaPlayerControl) {
                    if (mMediaPlayerControl.isPlaying()) {
                        mMediaPlayerControl.next();
                    }
                }
                break;
            case R.id.iv_full_screen:
                if (null != mMediaPlayerControl) {
                    if (mMediaPlayerControl.isFullscreen()) {
                        mMediaPlayerControl.cancelFullscreen();
                        mFullScreenIv.setImageResource(R.drawable.ic_full_screen);
                    } else {
                        mMediaPlayerControl.fullscreen();
                        mFullScreenIv.setImageResource(R.drawable.ic_cancel_full_screen);
                    }
                }
                break;
        }
    }

    public void show(int duration) {
        mMediaController.setVisibility(VISIBLE);

        mHandler.removeCallbacks(mHideRunnable);
        if (duration > 0) {
            mHandler.postDelayed(mHideRunnable, duration);
        }
    }

    public void hide() {
        mMediaController.setVisibility(GONE);
    }

    public boolean isHidden() {
        return mMediaController.getVisibility() != VISIBLE;
    }

    public void setControlCallback(MediaPlayerControl callback) {
        this.mMediaPlayerControl = callback;
    }

    public interface MediaPlayerControl {
        void start();

        void pause();

        void previous();

        void next();

        void setting();

        void save();

        void fullscreen();

        void cancelFullscreen();

        boolean isPlaying();

        boolean isFullscreen();
    }
}
