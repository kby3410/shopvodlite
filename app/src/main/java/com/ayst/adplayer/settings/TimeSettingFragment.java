package com.ayst.adplayer.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.ayst.adplayer.R;
import com.ayst.adplayer.common.BaseFragment;
import com.ayst.adplayer.data.TimingInfo;
import com.ayst.adplayer.event.MessageEvent;
import com.ayst.adplayer.service.HandleService;
import com.ayst.adplayer.view.XianHeiFontTextView;
import com.kyleduo.switchbutton.SwitchButton;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TimeSettingFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.btn_power_on)
    SwitchButton mPowerOnBtn;
    @BindView(R.id.tv_power_on_time)
    XianHeiFontTextView mPowerOnTimeTv;
    @BindView(R.id.btn_power_off)
    SwitchButton mPowerOffBtn;
    @BindView(R.id.btn_power_manager)
    SwitchButton mPower;
    @BindView(R.id.tv_power_off_time)
    XianHeiFontTextView mPowerOffTimeTv;
    @BindView(R.id.btn_reboot)
    SwitchButton mRebootBtn;
    @BindView(R.id.tv_reboot_time)
    XianHeiFontTextView mRebootTimeTv;

    private static final int TYPE_POWER_OFF = 1;
    private static final int TYPE_POWER_ON = 2;
    private static final int TYPE_REBOOT = 3;
    private static  final int TYPE_POWRE = 4;
    private int mType = TYPE_POWER_OFF;

    Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_setting, container, false);
        unbinder = ButterKnife.bind(this, view);

        initView();
        return view;
    }

    private void initView() {

        TimingInfo timingInfo = Setting.get(mContext).getPowerOnTime();
        mPowerOnTimeTv.setText(String.format("%02d", timingInfo.getHour()) + ":" + String.format("%02d", timingInfo.getMin()));
        mPowerOnBtn.setChecked(timingInfo.isEnable());

        timingInfo = Setting.get(mContext).getPowerOffTime();
        mPowerOffTimeTv.setText(String.format("%02d", timingInfo.getHour()) + ":" + String.format("%02d", timingInfo.getMin()));
        mPowerOffBtn.setChecked(timingInfo.isEnable());

        timingInfo = Setting.get(mContext).getRebootTime();
        mRebootTimeTv.setText(String.format("%02d", timingInfo.getHour()) + ":" + String.format("%02d", timingInfo.getMin()));
        mRebootBtn.setChecked(timingInfo.isEnable());
        mPowerOnBtn.setOnCheckedChangeListener(this);
        mPowerOffBtn.setOnCheckedChangeListener(this);
        mRebootBtn.setOnCheckedChangeListener(this);
        timingInfo=Setting.get(mContext).getPower();
        mPower.setChecked(timingInfo.isEnable());
        mPower.setOnCheckedChangeListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_power_off_time, R.id.tv_power_on_time, R.id.tv_reboot_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_power_off_time:
                mType = TYPE_POWER_OFF;
                break;
            case R.id.tv_power_on_time:
                mType = TYPE_POWER_ON;
                break;
            case R.id.tv_reboot_time:
                mType = TYPE_REBOOT;
                break;
        }
        showTimePikerDialog();
    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        String action = "";
        switch (compoundButton.getId()) {
            case R.id.btn_power_manager:
                TimingInfo timingInfo;
                    timingInfo = Setting.get(mContext).getPower();
                    timingInfo.setEnable(isChecked);
                    Setting.get(mContext).savePower(timingInfo);
                    action = HandleService.ACTION_TIMED_POWERON;
                    timingInfo = Setting.get(mContext).getPowerOnTime();
                    timingInfo.setEnable(isChecked);
                    Setting.get(mContext).savePowerOnTime(timingInfo);
					startAlarm(action);

                    action = HandleService.ACTION_TIMED_POWEROFF;
                    timingInfo = Setting.get(mContext).getPowerOffTime();
                    timingInfo.setEnable(isChecked);
                    Setting.get(mContext).savePowerOffTime(timingInfo);
					startAlarm(action);

                break;
            case R.id.btn_power_on: {
               /* action = HandleService.ACTION_TIMED_POWERON;
                TimingInfo timingInfo2 = Setting.get(mContext).getPowerOnTime();
                timingInfo2.setEnable(isChecked);
                Setting.get(mContext).savePowerOnTime(timingInfo2);
                break;*/
            }
            case R.id.btn_power_off: {
              /*  action = HandleService.ACTION_TIMED_POWEROFF;
                TimingInfo timingInfo3 = Setting.get(mContext).getPowerOffTime();
                timingInfo3.setEnable(isChecked);
                Setting.get(mContext).savePowerOffTime(timingInfo3);
                break;*/
            }
            case R.id.btn_reboot: {
                TimingInfo timingInfo1=null;
                 action = HandleService.ACTION_TIMED_REBOOT;
                 timingInfo1 = Setting.get(mContext).getRebootTime();
                timingInfo1.setEnable(isChecked);
                Setting.get(mContext).saveRebootTime(timingInfo1);
				startAlarm(action);
                break;
            }
            default:
                return;
        }
        
    }

    private void showTimePikerDialog() {
        TimePickerDialog tpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                String action = "";
                if (TYPE_POWER_ON == mType) {
                    action = HandleService.ACTION_TIMED_POWERON;
                    mPowerOnTimeTv.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                    TimingInfo timingInfo = Setting.get(mContext).getPowerOnTime();
                    timingInfo.setHour(hourOfDay);
                    timingInfo.setMin(minute);
                    Setting.get(mContext).savePowerOnTime(timingInfo);
                } else if (TYPE_POWER_OFF == mType) {
                    action = HandleService.ACTION_TIMED_POWEROFF;
                    mPowerOffTimeTv.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                    TimingInfo timingInfo = Setting.get(mContext).getPowerOffTime();
                    timingInfo.setHour(hourOfDay);
                    timingInfo.setMin(minute);
                    Setting.get(mContext).savePowerOffTime(timingInfo);
                } else if (TYPE_REBOOT == mType) {
                    action = HandleService.ACTION_TIMED_REBOOT;
                    mRebootTimeTv.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                    TimingInfo timingInfo = Setting.get(mContext).getRebootTime();
                    timingInfo.setHour(hourOfDay);
                    timingInfo.setMin(minute);
                    Setting.get(mContext).saveRebootTime(timingInfo);
                }
                startAlarm(action);
            }
        }, true);
        tpd.show(getFragmentManager(), "timepickerdialog");
    }

    private void startAlarm(String action) {
        Intent intent = new Intent(mContext, HandleService.class);
        intent.putExtra("command", HandleService.COMMAND_SET_ALARM);
        intent.putExtra("delay", 0);
        Bundle bundle = new Bundle();
        bundle.putString("action", action);
        intent.putExtra("extra", bundle);
        mContext.startService(intent);
    }
}
