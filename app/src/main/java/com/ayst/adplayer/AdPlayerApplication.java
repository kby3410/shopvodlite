package com.ayst.adplayer;

import android.app.Application;
import android.graphics.Typeface;

import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.ayst.adplayer.utils.AppUtils;

import java.io.File;

/**
 * Created by shenhaibo on 2017/11/3.
 */
public class AdPlayerApplication extends MultiDexApplication {
    private static final String TAG = "AdPlayerApplication";

    public static Typeface sXianHeiTextType = null;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();

        // 加载自定义字体
        try{
            sXianHeiTextType = Typeface.createFromAsset(getAssets(), "fonts/flfbls.ttf");
        }catch(Exception e){
            Log.e(TAG, "onCreate, loading fonts/flfbls.ttf failed!") ;
        }

        if (!AppUtils.isDebug()) {
            File file = new File("system/xbin/libant.so");
            if (!file.exists()) {
                Log.w(TAG, "Unauthorized and exit!");
                System.exit(0);
            }
        }
    }

}
