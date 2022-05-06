package com.ayst.adplayer.config;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Created by shenhaibo on 2018/5/21.
 */
public class UsbConfigManager {
    private final static String TAG = "UsbConfigManager";
    public final static String KEY_DEL = "DEL";
    public final static String KEY_COPY = "COPY";
    public final static String KEY_URL = "URL_";
    public final static String KEY_TEXT = "TEXT_";

    private final static int URLS_MAX = 100;

    private Context mContext;
    private File mConfigFile;

    private Properties mCfgProperties = null;
    private String mDelValue = "";
    private String mCopyValue = "";
    private List<String> mUrlsValue = new ArrayList<>();
    private HashMap<Integer, String> mTextValues = new HashMap<>();

    public UsbConfigManager(Context context, File configFile) {
        mContext = context;
        mConfigFile = configFile;
        if (mConfigFile.exists()) {
            getProperties();
        }
    }

    private Properties getProperties() {
        if (mCfgProperties != null) {
            return mCfgProperties;
        }
        mCfgProperties = new Properties();
        try {
//            InputStream is = context.getAssets().open("config.ini");
            InputStream is = new FileInputStream(mConfigFile);
            mCfgProperties.load(new InputStreamReader(is, "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCfgProperties;
    }

    public boolean isDel() {
        if (TextUtils.isEmpty(mDelValue)) {
            Properties properties = getProperties();
            mDelValue = properties.getProperty(KEY_DEL, "0");
            Log.i(TAG, "isDel, value:" + mDelValue);
        }
        return mDelValue.equals("1");
    }

    public boolean isCopy() {
        if (TextUtils.isEmpty(mCopyValue)) {
            Properties properties = getProperties();
            mCopyValue = properties.getProperty(KEY_COPY, "0");
            Log.i(TAG, "isCopy, value:" + mCopyValue);
        }
        return mCopyValue.equals("1");
    }

    public List<String> getUrls() {
        if (mUrlsValue.isEmpty()) {
            Properties properties = getProperties();
            for (int i=1; i<URLS_MAX; i++) {
                String url = properties.getProperty(KEY_URL + i, "");
                if (!TextUtils.isEmpty(url)) {
                    mUrlsValue.add(url);
                    Log.i(TAG, "getUrls, add url:" + url);
                } else {
                    break;
                }
            }
        }
        return mUrlsValue;
    }

    public String getText(int id) {
        if (TextUtils.isEmpty(mTextValues.get(id))) {
            Properties properties = getProperties();
            String value = properties.getProperty(KEY_TEXT + id, "");
            mTextValues.put(id, value);
            Log.i(TAG, "getText, id: " + id + "value:" + value);
        }
        return mTextValues.get(id);
    }
}
