package com.ayst.adplayer.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ayst.adplayer.R;
import com.ayst.adplayer.common.BaseFragment;
import com.ayst.adplayer.upgrade.AppUpgradeManager;
import com.ayst.adplayer.utils.AppUtils;
import com.ayst.adplayer.view.XianHeiFontTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AboutFragment extends BaseFragment {

    @BindView(R.id.tv_module)
    XianHeiFontTextView mModuleTv;
    @BindView(R.id.tv_sn)
    XianHeiFontTextView mSnTv;
    @BindView(R.id.tv_app_version)
    XianHeiFontTextView mAppVersionTv;
    @BindView(R.id.tv_sys_version)
    XianHeiFontTextView mSysVersionTv;
    @BindView(R.id.tv_android_version)
    XianHeiFontTextView mAndroidVersionTv;
    @BindView(R.id.tv_mac)
    XianHeiFontTextView mMacTv;
    @BindView(R.id.btn_check_update)
    Button mCheckUpdateBtn;

    Unbinder unbinder;

    private boolean canUpgrade = false;
    private AppUpgradeManager mUpgradeManager = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        unbinder = ButterKnife.bind(this, view);

        initView();
        return view;
    }

    private void initView() {
        mModuleTv.setText(AppUtils.getProperty("ro.product.model", "adplayer"));
        mSnTv.setText(AppUtils.getProperty("ro.serialno", "AO6U44GBIM"));
        mSysVersionTv.setText(AppUtils.getProperty("ro.product.version", "1.0"));
        mAndroidVersionTv.setText(AppUtils.getProperty("ro.build.version.release", "5.1"));
        mAppVersionTv.setText(AppUtils.getVersionName(mContext));
        mMacTv.setText(AppUtils.getWifiMacAddr(mContext));

        mCheckUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (canUpgrade) {
                    mUpgradeManager.download();
                    mCheckUpdateBtn.setText(R.string.downloading);
                    mCheckUpdateBtn.setEnabled(false);
                } else {
                    Toast.makeText(mContext, R.string.newest, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mUpgradeManager = new AppUpgradeManager(mContext);
        mUpgradeManager.check(new AppUpgradeManager.OnFoundNewVersionInterface() {
            @Override
            public void onFoundNewVersion(String version, String introduction, String url) {
                mCheckUpdateBtn.setText(getString(R.string.upgrade_to) + version);
                canUpgrade = true;
            }

            @Override
            public void onNotFoundNewVersion() {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_check_update)
    public void onViewClicked() {

    }
}
