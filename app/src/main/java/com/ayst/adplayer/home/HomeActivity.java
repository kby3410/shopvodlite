package com.ayst.adplayer.home;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.ayst.adplayer.R;
import com.ayst.adplayer.common.BaseActivity;
import com.ayst.adplayer.common.IDispatchKeyEventListener;
import com.ayst.adplayer.data.ImageBoxData;
import com.ayst.adplayer.data.BoxInfo;
import com.ayst.adplayer.data.TextBoxData;
import com.ayst.adplayer.data.HomeTemplate;
import com.ayst.adplayer.dialogs.editimagebox.EditImageBoxDialog;
import com.ayst.adplayer.dialogs.edittextbox.EditTextBoxDialog;
import com.ayst.adplayer.event.MessageEvent;
import com.ayst.adplayer.home.boxs.BaseBox;
import com.ayst.adplayer.home.boxs.ImageBox;
import com.ayst.adplayer.home.boxs.TextBox;
import com.ayst.adplayer.player.PlayerFragment;
import com.ayst.adplayer.service.HandleService;
import com.ayst.adplayer.settings.Setting;
import com.ayst.adplayer.settings.SettingActivity;
import com.ayst.adplayer.dialogs.CustomDialog;
import com.ayst.adplayer.template.BoxType;
import com.ayst.adplayer.utils.AppUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity implements View.OnClickListener, PlayerFragment.OnFullscreenListener {
//    private static final String TAG = "HomeActivity";
    private  String TAG="Adplayer";
    @BindView(R.id.root_container)
    FrameLayout mRootContainer;
    @BindView(R.id.player_container)
    FrameLayout mPlayerContainer;
    @BindView(R.id.home_container)
    RelativeLayout mHomeContainer;

    private int mScreenWidth = 0;
    private int mScreenHeight = 0;
    private HomeTemplate mCurHomeTemplate;
    private FragmentManager mFragmentManager;
    private PlayerFragment mPlayerFragment;
    private IDispatchKeyEventListener mDispatchKeyEventListener = null;

    private boolean isVideoFullscreen = false;
    private BoxInfo mVideoBoxInfo;
    private Handler mHandler;
    private List<View> mAddedViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate...");
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        AppUtils.initScreenOrientation(this);

        mHandler = new Handler(getMainLooper());
        mPlayerFragment = new PlayerFragment();
        mPlayerFragment.setOnFullscreenListener(this);
        mFragmentManager = getFragmentManager();
        mFragmentManager.beginTransaction().add(R.id.player_container, mPlayerFragment).commit();
        Boolean isStartByTimeout= getIntent().getBooleanExtra("isStartByTimeout",false);
        Log.v(TAG,"isStartByTimeout:"+isStartByTimeout);
        AppUtils.isStartByTimeout=isStartByTimeout;
        initView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v(TAG,"onTouchEvent");
       if(AppUtils.isStartByTimeout) {
            finish();
            System.exit(0);
        }
        return super.onTouchEvent(event);
    }


    private void initView() {
        getScreenSize();
        loadTemplate();
        addFocusView();
    }

    public void loadTemplate() {
        getScreenSize();
        mCurHomeTemplate = Setting.get(this).getCurHomeTemplate();
        Log.i(TAG, "loadTemplate, template: " + mCurHomeTemplate.toString());

        if (null != mCurHomeTemplate) {
            mHomeContainer.removeAllViews();
            mAddedViews.clear();
            List<BoxInfo> items = mCurHomeTemplate.getItems();
            if (null != items) {
                for (BoxInfo item : items) {
                    ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(0, 0);
                    margin.leftMargin = (int) (item.getPosX() * mScreenWidth);
                    margin.topMargin = (int) (item.getPosY() * mScreenHeight);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
                    layoutParams.width = (int) (item.getWidth() * mScreenWidth);
                    layoutParams.height = (int) (item.getHeight() * mScreenHeight);

                    if (item.getType() == BoxType.IMAGE.ordinal()) {
                        Log.i(TAG, "loadTemplate, Add a ImageBox with id of " + item.getId());
                        ImageBox imageBox = new ImageBox(this, item);
                        imageBox.setOnClickListener(this);
                        mHomeContainer.addView(imageBox, layoutParams);
                        mAddedViews.add(imageBox);

                    } else if (item.getType() == BoxType.TEXT.ordinal()) {
                        Log.i(TAG, "loadTemplate, Add a TextBox with id of " + item.getId());
                        TextBox textBox = new TextBox(this, item);
                        textBox.setOnClickListener(this);
                        mHomeContainer.addView(textBox, layoutParams);
                        mAddedViews.add(textBox);

                    } else if (item.getType() == BoxType.VIDEO.ordinal()) {
                        Log.i(TAG, "loadTemplate, Add a VideoBox with id of " + item.getId());
                        mPlayerContainer.setLayoutParams(layoutParams);
                        mVideoBoxInfo = item;
                    }
                }
            }
        }
    }

    private void addFocusView() {
        // Focus view
        mFocusLayout.allowHide(true);
        mRootContainer.addView(mFocusLayout,
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        ViewTreeObserver viewTreeObserver = this.getWindow().getDecorView().getViewTreeObserver();
        viewTreeObserver.addOnGlobalFocusChangeListener(mFocusLayout);
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart...");
        super.onStart();
        EventBus.getDefault().register(this);

        Intent configIntent = new Intent(this, HandleService.class);
        configIntent.putExtra("command", HandleService.COMMAND_CHECK_CONFIG);
        configIntent.putExtra("delay", 5000);
        startService(configIntent);

        Intent updateIntent = new Intent(this, HandleService.class);
        updateIntent.putExtra("command", HandleService.COMMAND_CHECK_UPDATE);
        updateIntent.putExtra("delay", 10000);
        startService(updateIntent);

        startAlarm(HandleService.ACTION_TIMED_POWERON, 1000);
        startAlarm(HandleService.ACTION_TIMED_POWEROFF, 2000);
        startAlarm(HandleService.ACTION_TIMED_REBOOT, 3000);
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop...");
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (MessageEvent.MSG_TEMPLATE_CHANGED.equals(event.getMessage())) {
            loadTemplate();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPlayerFragment.changeVideoSize();
                }
            }, 200);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(TAG, "onConfigurationChanged...");
        loadTemplate();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    public void setDispatchKeyEventListener(IDispatchKeyEventListener listener) {
        mDispatchKeyEventListener = listener;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean ret = false;
        Log.i(TAG, "dispatchKeyEvent, key code: " + event.getKeyCode() + ", action: " + event.getAction());

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            if (KeyEvent.KEYCODE_MENU == keyCode) {
                startActivity(new Intent(this, SettingActivity.class));
                ret = true;
            }
        }

        if (!ret && null != mDispatchKeyEventListener) {
            ret = mDispatchKeyEventListener.dispatchKeyEvent(event);
        }

        if (!ret) {
            ret = super.dispatchKeyEvent(event);
        }
        return ret;
    }

    private void showExitDialog() {
        new CustomDialog.Builder(this)
                .setMessage(R.string.exit_dialog_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        //finish();
                      //  finishAffinity();
                      //  System.runFinalization();
                       // System.exit(0);
                        // 종료
                        finishAffinity();
                        System.runFinalization();
                        System.exit(0);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    private void getScreenSize() {
        WindowManager windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        if (Build.VERSION.SDK_INT >= 19) {
            display.getRealSize(outPoint);
        } else {
            display.getSize(outPoint);
        }
        mScreenWidth = outPoint.x;
        mScreenHeight = outPoint.y;
    }

    @Override
    public void onClick(View view) {
        Log.i(TAG, "onClick");
        if (view instanceof BaseBox) {
            final BaseBox box = (BaseBox) view;
            int type = box.getData().getType();
            if (BoxType.IMAGE.ordinal() == type) {
                EditImageBoxDialog dialog = new EditImageBoxDialog(this, (ImageBoxData) box.getData().getData());
                dialog.setOnPickerListener(new EditImageBoxDialog.OnPickerListener() {
                    @Override
                    public void onPicker(ImageBoxData imageData) {
                        ((ImageBox)box).update(imageData);
                        saveCurTemplate(box.getData());
                    }
                });
                dialog.show();
            } else if (BoxType.TEXT.ordinal() == type) {
                EditTextBoxDialog dialog = new EditTextBoxDialog(this, (TextBoxData) box.getData().getData());
                dialog.setOnPickerListener(new EditTextBoxDialog.OnPickerListener() {
                    @Override
                    public void onPicker(TextBoxData textData) {
                        ((TextBox)box).update(textData);
                        saveCurTemplate(box.getData());
                    }
                });
                dialog.show();
            }
        }
    }

    private void saveCurTemplate(BoxInfo boxInfo) {
        List<BoxInfo> boxInfos = mCurHomeTemplate.getItems();
        for (int i=0; i<boxInfos.size(); i++) {
            if (boxInfo.getId() == boxInfos.get(i).getId()) {
                boxInfos.set(i, boxInfo);
                mCurHomeTemplate.setItems(boxInfos);
                Setting.get(this).saveCurHomeTemplate(mCurHomeTemplate);
                Log.i(TAG, "saveCurTemplate, save success!");
            }
        }
    }

    @Override
    public void fullscreen() {
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(0, 0);
        margin.leftMargin = 0;
        margin.topMargin = 0;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        layoutParams.width = mScreenWidth;
        layoutParams.height = mScreenHeight;
        mPlayerContainer.setLayoutParams(layoutParams);
        setBoxFocusable(false);
        showBox(false);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFocusLayout.redraw();
            }
        }, 100);

        isVideoFullscreen = true;
    }

    @Override
    public void cancelFullscreen() {
        mFocusLayout.hide();
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(0, 0);
        margin.leftMargin = (int) (mVideoBoxInfo.getPosX() * mScreenWidth);
        margin.topMargin = (int) (mVideoBoxInfo.getPosY() * mScreenHeight);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        layoutParams.width = (int) (mVideoBoxInfo.getWidth() * mScreenWidth);
        layoutParams.height = (int) (mVideoBoxInfo.getHeight() * mScreenHeight);
        mPlayerContainer.setLayoutParams(layoutParams);
        setBoxFocusable(true);
        showBox(true);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFocusLayout.redraw();
            }
        }, 500);

        isVideoFullscreen = false;
    }

    @Override
    public boolean isFullscreen() {
        return isVideoFullscreen;
    }

    private void setBoxFocusable(boolean focusable) {
        for (View view : mAddedViews) {
            view.setFocusable(focusable);
        }
    }

    private void showBox(boolean show) {
        for (View view : mAddedViews) {
            view.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void startAlarm(String action, int delay) {
        Intent intent = new Intent(this, HandleService.class);
        intent.putExtra("command", HandleService.COMMAND_SET_ALARM);
        intent.putExtra("delay", delay);
        Bundle bundle = new Bundle();
        bundle.putString("action", action);
        intent.putExtra("extra", bundle);
        startService(intent);
    }
}
