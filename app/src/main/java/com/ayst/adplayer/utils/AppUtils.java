package com.ayst.adplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/4/6.
 */
public class AppUtils {
    private final static String TAG = "AppUtils";

    private static final boolean DEBUG = false;

    // APP版本号
    private static String sVersionName = "";
    private static int sVersionCode = -1;

    // MAC地址获取
    private static String sEth0Mac = "";
    private static String sWifiMac = "";

    // 屏幕宽高
    private static int sScreenWidth = -1;
    private static int sScreenHeight = -1;
    private static boolean isLandscape = true;
    public static boolean isStartByTimeout=false;
    // 目录
    private static String sRootDir = "";

    public static boolean isDebug() {
        return DEBUG;
    }

    public static String getVersionName(Context context) {
        if (TextUtils.isEmpty(sVersionName)) {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                sVersionName = info.versionName;
                sVersionCode = info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return sVersionName;
    }

    public static int getVersionCode(Context context) {
        if (-1 == sVersionCode) {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                sVersionName = info.versionName;
                sVersionCode = info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return sVersionCode;
    }

    public static boolean isConnNetWork(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        return ((networkInfo != null) && networkInfo.isConnected());
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return ((wifiNetworkInfo != null) && wifiNetworkInfo.isConnected());
    }

    public static String getEth0MacAddress(Context context) {
        if (TextUtils.isEmpty(sEth0Mac)) {
            try {
                int numRead = 0;
                char[] buf = new char[1024];
                StringBuffer strBuf = new StringBuffer(1000);
                BufferedReader reader = new BufferedReader(new FileReader("/sys/class/net/eth0/address"));
                while ((numRead = reader.read(buf)) != -1) {
                    String readData = String.valueOf(buf, 0, numRead);
                    strBuf.append(readData);
                }
                sEth0Mac = strBuf.toString();
                reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        Log.d(TAG, "getEth0MacAddress, mac=" + sEth0Mac);
        return sEth0Mac;
    }

    public static String getWifiMacAddr(Context context) {
        if (TextUtils.isEmpty(sWifiMac)) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            sWifiMac = wifiInfo.getMacAddress();
        }
        Log.d(TAG, "getWifiMacAddr, mac=" + sWifiMac);
        return sWifiMac;
    }

    public static int getScreenWidth(Activity context) {
        if (-1 == sScreenWidth) {
            sScreenWidth = context.getWindowManager().getDefaultDisplay().getWidth();
        }
        return sScreenWidth;
    }

    public static int getScreenHeight(Activity context) {
        if (-1 == sScreenHeight) {
            sScreenHeight = context.getWindowManager().getDefaultDisplay().getHeight();
        }
        return sScreenHeight;
    }

    public static void initScreenOrientation(Activity context) {
        isLandscape = getScreenWidth(context) > getScreenHeight(context);
    }

    public static boolean isLandscape() {
        return isLandscape;
    }

    public static boolean isExternalStorageMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static String getRootDir(Context context) {
        if (sRootDir.isEmpty()) {
            File sdcardDir = null;
            try {
                if (isExternalStorageMounted()) {
                    sdcardDir = Environment.getExternalStorageDirectory();
                    Log.i(TAG, "Environment.MEDIA_MOUNTED :" + sdcardDir.getAbsolutePath() + " R:" + sdcardDir.canRead() + " W:" + sdcardDir.canWrite());
                    if (sdcardDir.canWrite()) {
                        String dir = sdcardDir.getAbsolutePath() + "/com.ayst.adplayer";
                        File file = new File(dir);
                        if (!file.exists()) {
                            Log.i(TAG, "getRootDir, dir not exist and make dir");
                            file.mkdirs();
                        }
                        sRootDir = dir;
                        return sRootDir;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            sRootDir = context.getFilesDir().getAbsolutePath();
        }
        return sRootDir;
    }

    public static String getDir(Context context, String dirName) {
        String dir = getRootDir(context) + File.separator + dirName;
        File file = new File(dir);
        if (!file.exists()) {
            Log.i(TAG, "getDir, dir not exist and make dir");
            file.mkdirs();
        }
        return dir;
    }

    public static int getAttrColor(Context context, int attr, int defValue) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        TypedArray typedArray = context.obtainStyledAttributes(typedValue.data, new int[]{attr});
        int color = typedArray.getColor(0, defValue);
        typedArray.recycle();
        return color;
    }

    public static Drawable getAttrDrawable(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        TypedArray typedArray = context.obtainStyledAttributes(typedValue.data, new int[]{attr});
        Drawable drawable = typedArray.getDrawable(0);
        typedArray.recycle();
        return drawable;
    }

    public static String getProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, "unknown"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return value;
        }
    }

    public static void setProperty(String key, String value) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);
            set.invoke(c, key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
