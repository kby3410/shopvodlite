package com.ayst.adplayer.settings;

import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ayst.adplayer.R;
import com.ayst.adplayer.common.BaseActivity;
import com.ayst.adplayer.common.BaseFragment;
import com.ayst.adplayer.data.SubMenu;
import com.ayst.adplayer.event.MessageEvent;
import com.ayst.adplayer.settings.template.TemplateSettingFragment;
import com.ayst.adplayer.view.SubMenuView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity implements
        View.OnClickListener, View.OnFocusChangeListener,
        SubMenuView.OnSelectedListener {
    private static final String TAG = "SettingActivity";



    private static final int MENU_PLAY = 0;
    private static final int MENU_IMAGE = 1;
    private static final int MENU_TEMPLATE = 2;
    private static final int MENU_TIME = 3;
    private static final int MENU_COPY = 4;
    private static final int MENU_ABOUT = 5;

    @BindView(R.id.btn_close)
    ImageButton mCloseBtn;
    @BindView(R.id.menu_play)
    LinearLayout mMenuPlay;
    @BindView(R.id.menu_image)
    LinearLayout mMenuImage;
    @BindView(R.id.menu_time)
    LinearLayout mMenuTime;
    @BindView(R.id.menu_copy)
    LinearLayout mMenuCopy;
    @BindView(R.id.menu_about)
    LinearLayout mMenuAbout;
    @BindView(R.id.submenu)
    SubMenuView mSubmenu;
    @BindView(R.id.content_container)
    FrameLayout mContentContainer;
    @BindView(R.id.menu_template)
    LinearLayout mMenuTemplate;
    @BindView(R.id.root_container)
    FrameLayout mRootContainer;
    @BindView(R.id.blank_view)
    View mBlankView;

    private int mMenuIndex = -1;
    private ArrayList<LinearLayout> mMenuViewList = new ArrayList<>();
    private ArrayList<SubMenu> mPlaySubMenuList = new ArrayList<>();

    private FragmentManager mFragmentManager;
    private ArrayList<BaseFragment> mSettingFragments = new ArrayList<>();
    private BaseFragment mCurFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        mFragmentManager = getFragmentManager();

        initView();
        addFocusView();
    }

    private void initView() {
        mSettingFragments.add(MENU_PLAY, new PlaySettingFragment());
        mSettingFragments.add(MENU_IMAGE, new ImageSettingFragment());
        mSettingFragments.add(MENU_TEMPLATE, new TemplateSettingFragment());
        mSettingFragments.add(MENU_TIME, new TimeSettingFragment());
        mSettingFragments.add(MENU_COPY, new CopySettingFragment());
        mSettingFragments.add(MENU_ABOUT, new AboutFragment());

        mMenuViewList.add(mMenuPlay);
        mMenuViewList.add(mMenuImage);
        mMenuViewList.add(mMenuTemplate);
        mMenuViewList.add(mMenuTime);
        mMenuViewList.add(mMenuCopy);
        mMenuViewList.add(mMenuAbout);

        mPlaySubMenuList.add(new SubMenu(getString(R.string.dir_play_setting), new PlayByDirSettingFragment()));
        mPlaySubMenuList.add(new SubMenu(getString(R.string.list_play_setting), new PlayByListSettingFragment()));
        mPlaySubMenuList.add(new SubMenu(getString(R.string.plan_play_setting), new PlayByTimeSettingFragment()));
        mPlaySubMenuList.add(new SubMenu(getString(R.string.share_play_setting), new PlayByShareSettingFragment()));
        //mPlaySubMenuList.add(new SubMenu(getString(R.string.http_play_setting), new PlayByHttpSettingFragment()));

        mMenuPlay.setOnClickListener(this);
        mMenuImage.setOnClickListener(this);
        mMenuTemplate.setOnClickListener(this);
        mMenuTime.setOnClickListener(this);
        mMenuCopy.setOnClickListener(this);
        mMenuAbout.setOnClickListener(this);
        mCloseBtn.setOnClickListener(this);

        mMenuPlay.setOnFocusChangeListener(this);
        mMenuImage.setOnFocusChangeListener(this);
        mMenuTemplate.setOnFocusChangeListener(this);
        mMenuTime.setOnFocusChangeListener(this);
        mMenuCopy.setOnFocusChangeListener(this);
        mMenuAbout.setOnFocusChangeListener(this);

        mBlankView.setOnClickListener(this);

        mSubmenu.setOnSelectedListener(this);

        setMenuSelected(MENU_PLAY, mPlaySubMenuList);
    }

    private void addFocusView() {
        // Focus view
        mFocusLayout.allowHide(false);
        mRootContainer.addView(mFocusLayout,
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        ViewTreeObserver viewTreeObserver = this.getWindow().getDecorView().getViewTreeObserver();
        viewTreeObserver.addOnGlobalFocusChangeListener(mFocusLayout);
    }

    private void restoreDefaultFocus() {
        mMenuPlay.requestFocus();
    }

    @Override
    public void onStart() {
        super.onStart();
        restoreDefaultFocus();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (MessageEvent.MSG_PLAY_MODE_CHANGE.equals(event.getMessage())) {
            mSubmenu.notifyDataSetChanged();
        } else if (MessageEvent.MSG_TEMPLATE_CHANGED.equals(event.getMessage())) {
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_play:
                setMenuSelected(MENU_PLAY, mPlaySubMenuList);
                break;
            case R.id.menu_image:
                setMenuSelected(MENU_IMAGE);
                break;
            case R.id.menu_template:
                setMenuSelected(MENU_TEMPLATE);
                break;
            case R.id.menu_time:
                startActivity();
                setMenuSelected(MENU_TIME);
                break;
            case R.id.menu_copy:
                setMenuSelected(MENU_COPY);
                break;
            case R.id.menu_about:
                setMenuSelected(MENU_ABOUT);
                break;
            case R.id.blank_view:
            case R.id.btn_close:
                finish();
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean isFocused) {
        if (isFocused) {
            switch (view.getId()) {
                case R.id.menu_play:
                    setMenuSelected(MENU_PLAY, mPlaySubMenuList);
                    break;
                case R.id.menu_image:
                    setMenuSelected(MENU_IMAGE);
                    break;
                case R.id.menu_template:
                    setMenuSelected(MENU_TEMPLATE);
                    break;
                case R.id.menu_time:
                    setMenuSelected(MENU_TIME);
                    break;
                case R.id.menu_copy:
                    setMenuSelected(MENU_COPY);
                    break;
                case R.id.menu_about:
                    setMenuSelected(MENU_ABOUT);
                    break;
            }
        }
    }

    @Override
    public void onSelected(int index, SubMenu menu) {
        switchContent(mCurFragment, menu.fragment);
    }

    private void setMenuSelected(int index) {
        setMenuSelected(index, null);
    }

    private void setMenuSelected(int index, ArrayList<SubMenu> subMenu) {
        if (index != mMenuIndex && index < mMenuViewList.size()) {
            for (LinearLayout menu : mMenuViewList) {
                menu.setSelected(false);
            }
            switchContent(mCurFragment, mSettingFragments.get(index));
            mMenuViewList.get(index).setSelected(true);
            mMenuIndex = index;

            if (subMenu != null && subMenu.size() > 0) {
                mSubmenu.show(subMenu);
                mSubmenu.setVisibility(View.VISIBLE);
                switchContent(mCurFragment, subMenu.get(0).fragment);
            } else {
                mSubmenu.setVisibility(View.GONE);
            }
        }
    }

    public void switchContent(BaseFragment from, BaseFragment to) {
        if (from != null) {
            if (!to.isAdded()) {
                mFragmentManager.beginTransaction().hide(from).add(R.id.content_container, to).commit();
            } else {
                mFragmentManager.beginTransaction().hide(from).show(to).commit();
            }
        } else {
            mFragmentManager.beginTransaction().add(R.id.content_container, to).commit();
        }
        mCurFragment = to;
    }

    private boolean isAvilible(Context context, String packageName ){
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for ( int i = 0; i < pinfo.size(); i++ )
        {
            if(pinfo.get(i).packageName.equalsIgnoreCase(packageName)){
                return true;
            }
        }
        return false;
    }
    public void startActivity(){
        if( isAvilible(this,"com.example.shutdown")){
        ComponentName componetName = new ComponentName(
                "com.example.shutdown",
                "com.example.shutdown.MainActivity");
        Intent intent = new Intent();
        intent.setComponent(componetName);
        startActivity(intent);
        }else{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$PowerOnOffSettingsActivity");
            intent.setComponent(comp);
            startActivity(intent);
        }
    }
}
