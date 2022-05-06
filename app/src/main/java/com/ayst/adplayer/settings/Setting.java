package com.ayst.adplayer.settings;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ayst.adplayer.data.BoxInfo;
import com.ayst.adplayer.data.FileInfo;
import com.ayst.adplayer.data.HomeTemplate;
import com.ayst.adplayer.data.PlanInfo;
import com.ayst.adplayer.data.PlanListInfo;
import com.ayst.adplayer.data.PlayListInfo;
import com.ayst.adplayer.data.ShareHostInfo;
import com.ayst.adplayer.data.TimingInfo;
import com.ayst.adplayer.event.MessageEvent;
import com.ayst.adplayer.template.TemplateListTypeAdapter;
import com.ayst.adplayer.template.TemplateType;
import com.ayst.adplayer.template.TemplateTypeAdapter;
import com.ayst.adplayer.utils.AppUtils;
import com.ayst.adplayer.utils.GetFilesUtil;
import com.ayst.adplayer.utils.SPUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sososeen09.multitypejsonparser.parse.MultiTypeJsonParser;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/29.
 */

public class Setting {
    private static final String KEY_PLAY_LIST = "play_list";
    private static final String KEY_PLAN_LIST = "plan_list";
    private static final String KEY_HOST_LIST = "host_list";
    private static final String KEY_HTTP_PLAY_LIST = "http_play_list";
    private static final String KEY_PLAY_PATH = "play_path";
    private static final String KEY_PLAY_LIST_INDEX = "play_list_index";
    private static final String KEY_PLAY_PLAN_INDEX = "play_plan_index";
    private static final String KEY_PLAY_PATH_SHARE = "play_path_share";
    private static final String KEY_PLAY_HTTP_INDEX = "play_http_index";
    private static final String KEY_PLAY_MODE = "play_mode";
    private static final String KEY_PICTURE_DURATION = "picture_duration";
    private static final String KEY_PICTURE_EFFECT = "picture_effect";
    private static final String KEY_PICTURE_MUSIC = "picture_music";

    private static final String KEY_POWER_ON_TIMING = "power_on_timing";
    private static final String KEY_POWER_OFF_TIMING = "power_off_timing";
    private static final String KEY_REBOOT_TIMING = "reboot_timing";
    private static final String KEY_POWER_TIMING="power_timing";

    private static final String KEY_CUR_HOME_TEMPLATE = "cur_home_template";
    private static final String KEY_HOME_TEMPLATES = "home_templates";

    public static final int PLAY_MODE_NONE = 0;
    public static final int PLAY_MODE_DIR = 1;
    public static final int PLAY_MODE_PLAYLIST = 2;
    public static final int PLAY_MODE_PLAN = 3;
    public static final int PLAY_MODE_SHARE = 4;
    public static final int PLAY_MODE_HTTP = 5;

    private static Context sContext = null;
    private static Setting sConfigManger = null;
    private static SPUtils sSP = null;
    private static Gson sGson = null;
    private static Gson sTemplateGson = null;
    private static Gson sTemplateListGson = null;
    private static String sDefaultPath = "";

    private Setting(Context context) {
        sContext = context;
        sSP = SPUtils.get(context);
        sGson = new Gson();

        final GsonBuilder templateGsonBuilder = new GsonBuilder();
        templateGsonBuilder.registerTypeAdapter(HomeTemplate.class, new TemplateTypeAdapter());
        sTemplateGson = templateGsonBuilder.create();

        final GsonBuilder templateListGsonBuilder = new GsonBuilder();
        templateListGsonBuilder.registerTypeAdapter(new TypeToken<List<HomeTemplate>>() {}.getType(), new TemplateListTypeAdapter());
        sTemplateListGson = templateListGsonBuilder.create();

        sDefaultPath = GetFilesUtil.getBasePath() + "/Movies";
    }

    public static Setting get(Context context) {
        if (null == sConfigManger) {
            sConfigManger = new Setting(context);
        }
        return sConfigManger;
    }

    public void savePlayList(List<PlayListInfo> playlist) {
        String json = sGson.toJson(playlist);
        sSP.saveData(KEY_PLAY_LIST, json);
    }

    public List<PlayListInfo> getPlayList() {
        String json = sSP.getData(KEY_PLAY_LIST, "");
        Type type = new TypeToken<List<PlayListInfo>>() {
        }.getType();

        return sGson.fromJson(json, type);
    }

    public PlayListInfo getPlayListById(long id) {
        List<PlayListInfo> list = getPlayList();
        if (null != list) {
            for (PlayListInfo info : list) {
                if (info.getId() == id) {
                    return info;
                }
            }
        }

        return null;
    }

    public boolean isPlayListUsed(PlayListInfo playListInfo) {
        if (null != playListInfo) {
            List<PlanListInfo> planList = getPlanList();
            if (null != planList) {
                for (PlanListInfo info : planList) {
                    for (PlanInfo planInfo : info.getPlanInfoList()) {
                        if (planInfo.getPlaylistId() == playListInfo.getId()) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public void savePlanList(List<PlanListInfo> planlist) {
        String json = sGson.toJson(planlist);
        sSP.saveData(KEY_PLAN_LIST, json);
    }

    public List<PlanListInfo> getPlanList() {
        String json = sSP.getData(KEY_PLAN_LIST, "");
        Type type = new TypeToken<List<PlanListInfo>>() {
        }.getType();

        return sGson.fromJson(json, type);
    }

    public void saveHostList(List<ShareHostInfo> hostlist) {
        String json = sGson.toJson(hostlist);
        sSP.saveData(KEY_HOST_LIST, json);
    }

    public List<ShareHostInfo> getHostList() {
        String json = sSP.getData(KEY_HOST_LIST, "");
        Type type = new TypeToken<List<ShareHostInfo>>() {
        }.getType();

        return sGson.fromJson(json, type);
    }

    public void saveHttpPlayList(List<PlayListInfo> playlist) {
        String json = sGson.toJson(playlist);
        sSP.saveData(KEY_HTTP_PLAY_LIST, json);
    }

    public List<PlayListInfo> getHttpPlayList() {
        String json = sSP.getData(KEY_HTTP_PLAY_LIST, "");
        Type type = new TypeToken<List<PlayListInfo>>() {
        }.getType();

        return sGson.fromJson(json, type);
    }

    public void savePlayMode(int mode, int index, String path) {
        switch (mode) {
            case PLAY_MODE_DIR:
                sSP.saveData(KEY_PLAY_PATH, path);
                break;
            case PLAY_MODE_PLAYLIST:
                sSP.saveData(KEY_PLAY_LIST_INDEX, index);
                break;
            case PLAY_MODE_PLAN:
                sSP.saveData(KEY_PLAY_PLAN_INDEX, index);
                break;
            case PLAY_MODE_SHARE:
                sSP.saveData(KEY_PLAY_PATH_SHARE, path);
                break;
            case PLAY_MODE_HTTP:
                sSP.saveData(KEY_PLAY_HTTP_INDEX, index);
                break;
        }
        sSP.saveData(KEY_PLAY_MODE, mode);
        MessageEvent msg = new MessageEvent(MessageEvent.MSG_PLAY_MODE_CHANGE);
        EventBus.getDefault().post(msg);
    }

    public int getPlayMode() {
        return sSP.getData(KEY_PLAY_MODE, PLAY_MODE_NONE);
    }

    public String getSelectedPlayPath() {
        return sSP.getData(KEY_PLAY_PATH, sDefaultPath);
    }

    public String getSelectedPlayPathShare() {
        return sSP.getData(KEY_PLAY_PATH_SHARE, sDefaultPath);
    }

    public int getSelectedPlayListIndex() {
        return sSP.getData(KEY_PLAY_LIST_INDEX, 0);
    }

    public PlayListInfo getSelectedPlayList() {
        int index = getSelectedPlayListIndex();
        List<PlayListInfo> list = getPlayList();
        if (null != list && index < list.size()) {
            return list.get(index);
        }
        return null;
    }

    public int getSelectedPlanListIndex() {
        return sSP.getData(KEY_PLAY_PLAN_INDEX, 0);
    }

    public PlanListInfo getSelectedPlanList() {
        int index = getSelectedPlanListIndex();
        List<PlanListInfo> list = getPlanList();
        if (null != list && index < list.size()) {
            return list.get(index);
        }
        return null;
    }

    public int getSelectedHttpPlayListIndex() {
        return sSP.getData(KEY_PLAY_HTTP_INDEX, 0);
    }

    public void savePictureDuration(int duration) {
        sSP.saveData(KEY_PICTURE_DURATION, duration);
        MessageEvent msg = new MessageEvent(MessageEvent.MSG_PICTURE_SETTING_CHANGE);
        EventBus.getDefault().post(msg);
    }

    public int getPictureDuration() {
        return sSP.getData(KEY_PICTURE_DURATION, 5);
    }

    public void savePictureEffect(String effect) {
        sSP.saveData(KEY_PICTURE_EFFECT, effect);
        MessageEvent msg = new MessageEvent(MessageEvent.MSG_PICTURE_SETTING_CHANGE);
        EventBus.getDefault().post(msg);
    }

    public String getPictureEffect() {
        return sSP.getData(KEY_PICTURE_EFFECT, "기본");
    }

    public void savePictureMusic(List<FileInfo> fileInfos) {
        String json = sGson.toJson(fileInfos);
        sSP.saveData(KEY_PICTURE_MUSIC, json);
        MessageEvent msg = new MessageEvent(MessageEvent.MSG_PICTURE_SETTING_CHANGE);
        EventBus.getDefault().post(msg);
    }

    public List<FileInfo> getPictureMusic() {
        String json = sSP.getData(KEY_PICTURE_MUSIC, "");
        Type type = new TypeToken<List<FileInfo>>() {
        }.getType();

        return sGson.fromJson(json, type);
    }

    public void savePowerOnTime(TimingInfo timingInfo) {
        String json = sGson.toJson(timingInfo);
        sSP.saveData(KEY_POWER_ON_TIMING, json);
    }

    public TimingInfo getPowerOnTime() {
        String json = sSP.getData(KEY_POWER_ON_TIMING, "");
        if (TextUtils.isEmpty(json)) {
            TimingInfo timingInfo = new TimingInfo(7,12,0 ,false);
            savePowerOnTime(timingInfo);
            return timingInfo;
        }
        Type type = new TypeToken<TimingInfo>() {
        }.getType();

        return sGson.fromJson(json, type);
    }

    public void savePowerOffTime(TimingInfo timingInfo) {
        String json = sGson.toJson(timingInfo);
        sSP.saveData(KEY_POWER_OFF_TIMING, json);
    }

    public TimingInfo getPowerOffTime() {
        String json = sSP.getData(KEY_POWER_OFF_TIMING, "");
        if (TextUtils.isEmpty(json)) {
            TimingInfo timingInfo = new TimingInfo(7,12,0, false);
            savePowerOffTime(timingInfo);
            return timingInfo;
        }
        Type type = new TypeToken<TimingInfo>() {
        }.getType();
        return sGson.fromJson(json, type);
    }
    public void savePower(TimingInfo timingInfo) {
        String json = sGson.toJson(timingInfo);
        sSP.saveData(KEY_POWER_TIMING, json);
    }
    public TimingInfo getPower() {
        String json = sSP.getData(KEY_POWER_TIMING, "");
        if (TextUtils.isEmpty(json)) {
            TimingInfo timingInfo = new TimingInfo(7,12,0, false);
            savePowerOffTime(timingInfo);
            return timingInfo;
        }
        Type type = new TypeToken<TimingInfo>() {
        }.getType();

        return sGson.fromJson(json, type);
    }

    public void saveRebootTime(TimingInfo timingInfo) {
        String json = sGson.toJson(timingInfo);
        sSP.saveData(KEY_REBOOT_TIMING, json);
    }

    public TimingInfo getRebootTime() {
        String json = sSP.getData(KEY_REBOOT_TIMING, "");
        if (TextUtils.isEmpty(json)) {
            TimingInfo timingInfo = new TimingInfo(7,12,0, false);
            saveRebootTime(timingInfo);
            return timingInfo;
        }
        Type type = new TypeToken<TimingInfo>() {
        }.getType();

        return sGson.fromJson(json, type);
    }

    public void saveCurHomeTemplate(HomeTemplate template) {
        String json = sTemplateGson.toJson(template);
        sSP.saveData(KEY_CUR_HOME_TEMPLATE, json);

        List<HomeTemplate> templates = getHomeTemplates();
        int i=0;
        for (i=0; i<templates.size(); i++) {
            if (template.equals(templates.get(i))) {
                templates.set(i, template);
                saveHomeTemplates(templates);
                break;
            }
        }
        if (i >= templates.size()) {
            templates.add(template);
            saveHomeTemplates(templates);
        }
    }

    public HomeTemplate getCurHomeTemplate() {
        String json = sSP.getData(KEY_CUR_HOME_TEMPLATE, "");
        if (!TextUtils.isEmpty(json)) {
            Type type = new TypeToken<HomeTemplate>() {
            }.getType();

            HomeTemplate template = sTemplateGson.fromJson(json, type);
            int typeFilter = AppUtils.isLandscape() ? TemplateType.LANDSCAPE.ordinal() : TemplateType.PORTRAIT.ordinal();
            if (template.getType() == TemplateType.BOTH.ordinal()
                    || template.getType() == typeFilter) {
                return template;
            }
        }

        List<HomeTemplate> templates = getHomeTemplates();
        if (null != templates && templates.size() > 0) {
            saveCurHomeTemplate(templates.get(0));
            return templates.get(0);
        }

        return null;
    }

    public void saveHomeTemplates(List<HomeTemplate> templates) {
        String json = sTemplateListGson.toJson(templates);
        sSP.saveData(KEY_HOME_TEMPLATES, json);
    }

    public List<HomeTemplate> getHomeTemplates() {
        String json = sSP.getData(KEY_HOME_TEMPLATES, "");
        if (TextUtils.isEmpty(json)) {
            json = readFromAssets("default_home_template.json");
            if (!TextUtils.isEmpty(json)) {
                sSP.saveData(KEY_HOME_TEMPLATES, json);
            }
        }
        Type type = new TypeToken<List<HomeTemplate>>() {
        }.getType();

        return sTemplateListGson.fromJson(json, type);
    }

    public List<HomeTemplate> getHomeTemplatesMatched() {
        int typeFilter = AppUtils.isLandscape() ? TemplateType.LANDSCAPE.ordinal() : TemplateType.PORTRAIT.ordinal();

        List<HomeTemplate> filterTemplates = new ArrayList<>();
        List<HomeTemplate> templates = getHomeTemplates();
        if (null != templates) {
            for (HomeTemplate template : templates) {
                if (template.getType() == TemplateType.BOTH.ordinal()
                        || template.getType() == typeFilter) {
                    filterTemplates.add(template);
                }
            }
        }
        return filterTemplates;
    }

    public HomeTemplate parseHomeTemplate(String json) {
        if (!TextUtils.isEmpty(json)) {
            Type type = new TypeToken<HomeTemplate>() {
            }.getType();

            return sTemplateGson.fromJson(json, type);
        }
        return null;
    }

    /**
     * Read assets file as a string
     *
     * @param fileName Assets File Name
     * @return String
     */
    private static String readFromAssets(String fileName) {
        String lastStr = "";
        BufferedReader reader = null;
        try {
            InputStream in = sContext.getResources().getAssets().open(fileName);
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                lastStr = lastStr + tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException el) {
                    el.printStackTrace();
                }
            }
        }
        return lastStr;
    }
}
