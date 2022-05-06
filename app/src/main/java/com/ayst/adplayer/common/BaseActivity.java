package com.ayst.adplayer.common;

import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ayst.adplayer.home.FocusLayout;

/**
 * Created by Administrator on 2018/11/27.
 */

public class BaseActivity extends AppCompatActivity {
private  String TAG="Adplayer";
    protected FocusLayout mFocusLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFocusLayout = new FocusLayout(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        mFocusLayout.enable(true);
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mFocusLayout.hide();
        mFocusLayout.enable(false);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean hasWindowFocus() {
        return super.hasWindowFocus();
    }


}
