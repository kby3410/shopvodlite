package com.ayst.adplayer.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ayst.adplayer.R;
import com.ayst.adplayer.config.UsbConfigManager;
import com.ayst.adplayer.data.BoxInfo;
import com.ayst.adplayer.data.HomeTemplate;
import com.ayst.adplayer.data.TextBoxData;
import com.ayst.adplayer.data.TimingInfo;
import com.ayst.adplayer.template.BoxType;
import com.ayst.adplayer.settings.Setting;
import com.ayst.adplayer.data.FileInfo;
import com.ayst.adplayer.data.PlayListInfo;
import com.ayst.adplayer.event.MessageEvent;
import com.ayst.adplayer.upgrade.AppUpgradeManager;
import com.ayst.adplayer.utils.GetFilesUtil;
import com.ayst.adplayer.utils.SupportFileUtils;
import com.ayst.adplayer.dialogs.CustomDialog;

import org.apache.commons.lang.ArrayUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.trinea.android.common.util.FileUtils;

/**
 * Created by Administrator on 2018/5/21.
 */

public class HandleService extends Service {
    private final static String TAG = "HandleService";

    public static final int COMMAND_NULL = 0;
    public static final int COMMAND_CHECK_CONFIG = 1;
    public static final int COMMAND_CHECK_UPDATE = 2;
    public static final int COMMAND_SET_ALARM = 3;

    public static final String ACTION_TIMED_POWEROFF = "com.ayst.adplayer.timed_power_off";
    public static final String ACTION_TIMED_POWERON = "com.ayst.adplayer.timed_power_on";
    public static final String ACTION_TIMED_REBOOT = "com.ayst.adplayer.timed_reboot";

    private static final String REBOOT_ACTION = "rk.android.reboottime.action";
    private static final String POWER_OFF_ACTION = "rk.android.turnofftime.action";
    private static final String SET_POWER_ON_ACTION = "rk.android.turnontime.action";
    private static final String INSTALL_REBOOT_ACTION = "ads.android.installslient.action";

    public static final long MILLIS_OF_DAY = (24 * 60 * 60 * 1000);

    private static final int CUSTOM_TEMPLATE_ID_OFFSET = 100;
    private static final String CONFIG_FILE_NAME = "config.ini";
    private static final String TEMPLATE_FILE_NAME = "template.ini";
    public static final String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();

    private Handler mMainHandler;
    private WorkHandler mWorkHandler;
    private AppUpgradeManager mUpgradeManager;
    private AlarmManager mAlarmManager;
    private int todayWeek,nowHour,nowMin,nowSec;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mMainHandler = new Handler(getMainLooper());

        HandlerThread workThread = new HandlerThread("HandleService: workThread");
        workThread.start();
        mWorkHandler = new WorkHandler(workThread.getLooper());

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_TIMED_POWEROFF);
        filter.addAction(ACTION_TIMED_POWERON);
        filter.addAction(ACTION_TIMED_REBOOT);
        registerReceiver(mAlarmReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_NOT_STICKY;
        }

        int command = intent.getIntExtra("command", COMMAND_NULL);
        int delayTime = intent.getIntExtra("delay", 1000);
        Bundle extra = intent.getBundleExtra("extra");

        Log.d(TAG, "onStartCommand, command=" + command + " delayTime=" + delayTime);
        if (command == COMMAND_NULL) {
            return Service.START_NOT_STICKY;
        }

        Message msg = new Message();
        msg.what = command;
        msg.setData(extra);
        mWorkHandler.sendMessageDelayed(msg, delayTime);
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mAlarmReceiver);
    }

    /**
     * WorkHandler
     */
    private class WorkHandler extends Handler {
        WorkHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            String path = "";

            switch (msg.what) {
                case COMMAND_CHECK_CONFIG:
                    Log.d(TAG, "WorkHandler, COMMAND_CHECK_CONFIG");

                    // 检测配置文件
                    path = getConfigFile();
                    if (!TextUtils.isEmpty(path)) {
                        File configFile = new File(path);
                        if (configFile.exists()) {
                            showToast(getString(R.string.found_config_file_tips));

                            // 开始配置
                            setupConfig(configFile);

                            // 通知播放器更新数据
                            MessageEvent event = new MessageEvent(MessageEvent.MSG_PLAY_MODE_CHANGE);
                            EventBus.getDefault().post(event);

                            showToast(getString(R.string.config_complete));
                        }
                    }

                    // 检测模板配置文件
                    path = getTemplateFile();
                    if (!TextUtils.isEmpty(path)) {
                        File templateFile = new File(path);
                        if (templateFile.exists()) {
                            showToast(getString(R.string.found_template_file_tips));

                            // 开始配置
                            if (setupTemplate(templateFile)) {
                                showToast(getString(R.string.config_template_complete));
                            } else {
                                showToast(getString(R.string.config_template_failed));
                            }
                        }
                    }
                    break;

                case COMMAND_CHECK_UPDATE:
                    Log.d(TAG, "WorkHandler, COMMAND_CHECK_UPDATE");
                    if (null == mUpgradeManager) {
                        mUpgradeManager = new AppUpgradeManager(HandleService.this);
                    }
                    mUpgradeManager.check(new AppUpgradeManager.OnFoundNewVersionInterface() {
                        @Override
                        public void onFoundNewVersion(String version, String introduction, String url) {
                            showUpgradeDialog();
                            mUpgradeManager.download();
                        }

                        @Override
                        public void onNotFoundNewVersion() {
                        }
                    });
                    break;

                case COMMAND_SET_ALARM:
                    Bundle extra = msg.getData();
                    if (null != extra) {
                       // setAlarm(extra.getString("action"));
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private void setupConfig(File configFile) {
        UsbConfigManager configManager = new UsbConfigManager(this, configFile);

        // 删除sdcard/Movies目录中所有文件
        if (configManager.isDel()) {
            File file = new File(SDCARD + "/Movies");
            if (file.exists() && file.isDirectory()) {
                for (File f : file.listFiles()) {
                    Log.i(TAG, "setupConfig, delete: " + f.getPath());
                    if (f.isFile()) {
                        f.delete();
                    } else if (f.isDirectory()) {
                        FileUtils.deleteFile(f.getAbsolutePath());
                    }
                }
            }
        }

        // 复制U盘中媒体文件
        if (configManager.isCopy()) {
            List<FileInfo> fileList = GetFilesUtil.getSonNode(configFile.getParent(),
                    GetFilesUtil.TYPE_FILE,
                    (String[]) ArrayUtils.addAll(SupportFileUtils.SUPPORT_MEDIA_FILE_SUFFIX,
                            SupportFileUtils.SUPPORT_MUSIC_FILE_SUFFIX));
            for (FileInfo fileInfo : fileList) {
                Log.i(TAG, "setupConfig, copy: " + fileInfo.getPath());
                FileUtils.copyFile(fileInfo.getPath(), SDCARD + "/Movies/" + fileInfo.getName());
            }
        }

        // 配置在线播放列表
        ArrayList<FileInfo> playlist = new ArrayList<>();
        for (String url : configManager.getUrls()) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setUrl(url);
            fileInfo.setType(FileInfo.FILE_TYPE_HTTP);
            playlist.add(fileInfo);
        }

        PlayListInfo info = new PlayListInfo();
        info.setTitle(getString(R.string.http_playlist_name_by_config));
        info.setPlaylist(playlist);

        List<PlayListInfo> httpPlayList = Setting.get(this).getHttpPlayList();
        if (null == httpPlayList) {
            httpPlayList = new ArrayList<>();
        }
        httpPlayList.add(info);
        Setting.get(this).saveHttpPlayList(httpPlayList);

        // 配置文本广告
        HomeTemplate curHomeTemplate = Setting.get(this).getCurHomeTemplate();
        if (null != curHomeTemplate) {
            List<BoxInfo> items = curHomeTemplate.getItems();
            if (null != items) {
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getType() == BoxType.TEXT.ordinal()) {
                        String text = configManager.getText(items.get(i).getId());
                        if (!TextUtils.isEmpty(text)) {
                            try {
                                String newText = new String(text.getBytes(), "utf-8");
                                TextBoxData data = (TextBoxData) items.get(i).getData();
                                if (null != data) {
                                    data.setText(newText);
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                Setting.get(this).saveCurHomeTemplate(curHomeTemplate);
                MessageEvent msg = new MessageEvent(MessageEvent.MSG_TEMPLATE_CHANGED);
                EventBus.getDefault().post(msg);
            }
        }


        // 重命令config.ini->config.ini_backup，防止再次检查到重复文件
        //configFile.renameTo(new File(configFile.getPath() + "_backup"));
    }

    private boolean setupTemplate(File templateFile) {
        String templateStr = readFileByLines(templateFile);
        HomeTemplate homeTemplate = Setting.get(this).parseHomeTemplate(templateStr);
        if (null != templateFile) {
            homeTemplate.setId(homeTemplate.getId() + CUSTOM_TEMPLATE_ID_OFFSET);
            Setting.get(this).saveCurHomeTemplate(homeTemplate);
            MessageEvent msg = new MessageEvent(MessageEvent.MSG_TEMPLATE_CHANGED);
            EventBus.getDefault().post(msg);
            return true;
        }

        return false;
    }

    private static String readFileByLines(File file) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                builder.append(tempString);
            }
            reader.close();
            return builder.toString();
        } catch (IOException e) {
            Log.e(TAG, "readFileByLines, " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return "";
    }

    private String getConfigFile() {
        List<String> searchPaths = GetFilesUtil.getStorageList(this);
        for (String dirPath : searchPaths) {
            Log.d(TAG, "getConfigFile, search: " + dirPath);

            File dir = new File(dirPath);
            if(dir.listFiles() != null && dir.listFiles().length != 0 ) {
                for (File file : dir.listFiles()) {
                    if (file.isDirectory()) {
                        File[] files = file.listFiles(new FileFilter() {

                            @Override
                            public boolean accept(File tmpFile) {
                                return !tmpFile.isDirectory() && tmpFile.getName().equals(CONFIG_FILE_NAME);
                            }
                        });

                        if (files != null && files.length > 0) {
                            String filePath = files[0].getAbsolutePath();
                            Log.d(TAG, "getConfigFile, find config file: " + filePath);
                            return filePath;
                        }
                    } else {
                        if (file.getName().equals(CONFIG_FILE_NAME)) {
                            Log.d(TAG, "getConfigFile, find config file: " + file.getPath());
                            return file.getPath();
                        }
                    }
                }
            }
        }

        return "";
    }

    private String getTemplateFile() {
        List<String> searchPaths = GetFilesUtil.getStorageList(this);
        for (String dirPath : searchPaths) {
            Log.d(TAG, "getTemplateFile, search: " + dirPath);

            File dir = new File(dirPath);
            if( dir.listFiles() != null && dir.listFiles().length != 0) {
                for (File file : dir.listFiles()) {
                    if (file.isDirectory()) {
                        File[] files = file.listFiles(new FileFilter() {

                            @Override
                            public boolean accept(File tmpFile) {
                                return !tmpFile.isDirectory() && tmpFile.getName().equals(TEMPLATE_FILE_NAME);
                            }
                        });

                        if (files != null && files.length > 0) {
                            String filePath = files[0].getAbsolutePath();
                            Log.d(TAG, "getTemplateFile, find config file: " + filePath);
                            return filePath;
                        }
                    } else {
                        if (file.getName().equals(TEMPLATE_FILE_NAME)) {
                            Log.d(TAG, "getTemplateFile, find config file: " + file.getPath());
                            return file.getPath();
                        }
                    }
                }
            }
        }

        return "";
    }

    private void setAlarm(String action) {
        if (TextUtils.isEmpty(action)) {
            return;
        }

        TimingInfo timingInfo = null;
        if (TextUtils.equals(ACTION_TIMED_POWERON, action)) {
            timingInfo = Setting.get(this).getPowerOnTime();
            setPowerOnTimer(timingInfo);
            return;
        } else if (TextUtils.equals(ACTION_TIMED_POWEROFF, action)) {
            timingInfo = Setting.get(this).getPowerOffTime();
            setPowerOffTimer(timingInfo);
        } else if (TextUtils.equals(ACTION_TIMED_REBOOT, action)) {
            timingInfo = Setting.get(this).getRebootTime();
            setPowerRebootTimer(timingInfo);
        }

        if (null != timingInfo) {
            if (timingInfo.isEnable()) {
                Calendar calendar = Calendar.getInstance();
                nowHour = calendar.getTime().getHours();
                nowMin = calendar.getTime().getMinutes();
                nowSec = calendar.getTime().getSeconds();
                todayWeek = calendar.get(Calendar.DAY_OF_WEEK);
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int min = cal.get(Calendar.MINUTE);

                long curMillis = ((hour * 60) + min) * 60 * 1000;
                long timingMillis = ((timingInfo.getHour() * 60) + timingInfo.getMin()) * 60 * 1000;
                long differMillis = timingMillis - curMillis;
                if (differMillis > 0) {
                    Log.i(TAG, "setAlarm, action: " + action + ", " +
                            "today->" +
                            String.format("%02d", timingInfo.getHour()) + ":"
                            + String.format("%02d", timingInfo.getMin()));

                    setAlarm(this, action, System.currentTimeMillis() + differMillis);
                } else {
                    Log.i(TAG, "setAlarm, action: " + action + ", " +
                            "tomorrow->" +
                            String.format("%02d", timingInfo.getHour()) + ":"
                            + String.format("%02d", timingInfo.getMin()));

                   // setAlarm(this, action, System.currentTimeMillis() + differMillis + MILLIS_OF_DAY);//by Lily
                }
            } else {
                stopAlarm(this, action);
            }
        }
    }

    private void setAlarm(Context context, String action, long time) {
        stopAlarm(context, action);
        if (null == mAlarmManager) {
            mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        PendingIntent intent = PendingIntent.getBroadcast(context, 0, new Intent(action), 0);

        mAlarmManager.setExact(AlarmManager.RTC, time, intent);
    }

    private void stopAlarm(Context context, String action) {
        PendingIntent intent = PendingIntent.getBroadcast(context, 0, new Intent(action), 0);

        if (null == mAlarmManager) {
            mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        mAlarmManager.cancel(intent);
    }

    private void setPowerOnTimer(TimingInfo timingInfo) {
        Log.i(TAG, "setPowerOnTimer, "
                + String.format("%02d", timingInfo.getWeek()) + ":"
                + String.format("%02d", timingInfo.getHour()) + ":"
                + String.format("%02d", timingInfo.getMin())
                + " enable:" + timingInfo.isEnable());
        Calendar calendar = Calendar.getInstance();
        nowHour = calendar.getTime().getHours();
        nowMin = calendar.getTime().getMinutes();
        nowSec = calendar.getTime().getSeconds();
        todayWeek = calendar.get(Calendar.DAY_OF_WEEK);
        Intent i = new Intent(SET_POWER_ON_ACTION);
        i.putExtra("onHour", timingInfo.getHour() + "");
        i.putExtra("onMin",  timingInfo.getMin() + "");
        i.putExtra("onWeek", todayWeek + "");
        i.putExtra("enable", timingInfo.isEnable());
        sendBroadcast(i);
    }
    private void setPowerOffTimer(TimingInfo timingInfo) {
        Log.i(TAG, "setPowerOffTimer, "
                + String.format("%02d", timingInfo.getWeek()) + ":"
                + String.format("%02d", timingInfo.getHour()) + ":"
                + String.format("%02d", timingInfo.getMin())
                + " enable:" + timingInfo.isEnable());
        Calendar calendar = Calendar.getInstance();
        nowHour = calendar.getTime().getHours();
        nowMin = calendar.getTime().getMinutes();
        nowSec = calendar.getTime().getSeconds();
        todayWeek = calendar.get(Calendar.DAY_OF_WEEK);
    
        Intent i = new Intent(POWER_OFF_ACTION);
        i.putExtra("offHour", timingInfo.getHour() + "");
        i.putExtra("offMin",  timingInfo.getMin() + "");
        i.putExtra("offWeek", todayWeek + "");
        i.putExtra("enable", timingInfo.isEnable());
        sendBroadcast(i);
    }
    private void setPowerRebootTimer(TimingInfo timingInfo) {
        Log.i(TAG, "setPowerOffTimer, "
                + String.format("%02d", timingInfo.getHour()) + ":"
                + String.format("%02d", timingInfo.getMin())
                + " enable:" + timingInfo.isEnable());
        Calendar calendar = Calendar.getInstance();
        nowHour = calendar.getTime().getHours();
        nowMin = calendar.getTime().getMinutes();
        nowSec = calendar.getTime().getSeconds();
        todayWeek = calendar.get(Calendar.DAY_OF_WEEK);
        Intent i = new Intent(REBOOT_ACTION);
        i.putExtra("rebootHour", timingInfo.getHour() + "");
        i.putExtra("rebootMin",  timingInfo.getMin() + "");
        i.putExtra("rebootEnable", timingInfo.isEnable());
        sendBroadcast(i);
    }

    private BroadcastReceiver mAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "Alarm receiver action: " + action);
            if (TextUtils.equals(ACTION_TIMED_POWEROFF, action)) {
                sendBroadcast(new Intent(POWER_OFF_ACTION));
            } else if (TextUtils.equals(ACTION_TIMED_REBOOT, action)) {
                sendBroadcast(new Intent(REBOOT_ACTION));
            }
        }
    };

    private void showToast(final String text) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showUpgradeDialog() {
        new CustomDialog.Builder(this).setMessage(R.string.upgrade_tips)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }
}
