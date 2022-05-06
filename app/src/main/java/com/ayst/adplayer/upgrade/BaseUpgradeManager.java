package com.ayst.adplayer.upgrade;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.ayst.adplayer.R;
import com.ayst.adplayer.http.Download;
import com.ayst.adplayer.http.RequestTask;
import com.ayst.adplayer.http.RequestTaskInterface;
import com.ayst.adplayer.utils.AppUtils;

import java.io.File;

/**
 * Created by Administrator on 2016/3/9.
 */
public class BaseUpgradeManager implements RequestTaskInterface {
    private final static String TAG = "BaseUpgradeManager";
    private final static int REQUEST_DESCRIPTOR = 1;
    private final static int REQUEST_DOWNLOAD = 2;

    protected Context mContext = null;
    private int mRequestType = REQUEST_DESCRIPTOR;
    private int mIgnoreVersion = 0;
    private String mDownloadUrl = "";
    private OnParseDescriptionInterface mOnParseDescriptionInterface = null;
    private OnDownloadCompleteInterface mOnDownloadCompleteInterface = null;
    private OnFoundNewVersionInterface mOnFoundNewVersionInterface = null;

    public BaseUpgradeManager(Context context) {
        mContext = context;
    }

    public void check(String url, OnParseDescriptionInterface onParseDescriptionInterface, OnFoundNewVersionInterface onFoundNewVersionInterface) {
        if (AppUtils.isConnNetWork(mContext) && !TextUtils.isEmpty(url)) {
            Log.i(TAG, "check, url=" + url);
            mOnParseDescriptionInterface = onParseDescriptionInterface;
            mOnFoundNewVersionInterface = onFoundNewVersionInterface;

            mRequestType = REQUEST_DESCRIPTOR;
            new RequestTask(mContext, url, "POST", null, this).execute(new String[]{url});
        } else {
            Log.e(TAG, "check, network disconnect or url is null");
        }
    }

    public void download(String fileName, OnDownloadCompleteInterface onDownloadCompleteInterface) {
        if (AppUtils.isConnNetWork(mContext) && !TextUtils.isEmpty(mDownloadUrl)) {
            Log.i(TAG, "download, url=" + mDownloadUrl);
            mOnDownloadCompleteInterface = onDownloadCompleteInterface;

            mRequestType = REQUEST_DOWNLOAD;
            new Download(mContext, mDownloadUrl, getUpgradeFilePath() + fileName, this, null).execute(new String[]{mDownloadUrl});
        } else {
            Log.e(TAG, "download, network disconnect or url is null");
        }
    }

    private String getUpgradeFilePath() {
        String dir = getUpgradeDir();
        File file = new File(dir);
        if (!file.exists())
        {
            Log.i(TAG, "getUpgradeFilePath, dir not exist and make dir");
            file.mkdirs();
        }
        return getUpgradeDir() + "/";
    }

    private String getUpgradeDir() {
        String state = Environment.getExternalStorageState();
        File sdcardDir = null;
        try {
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                sdcardDir = Environment.getExternalStorageDirectory();
                Log.i(TAG, "Environment.MEDIA_MOUNTED :" + sdcardDir.getAbsolutePath() + " R:" + sdcardDir.canRead() + " W:" + sdcardDir.canWrite());
                if (sdcardDir.canWrite()) {
                    return sdcardDir.getAbsolutePath() + "/com.ayst.adplayer/upgrade";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mContext.getFilesDir().getAbsolutePath() + "/upgrade";
    }

    @Override
    public void postExecute(String result) {
        Log.i(TAG, "postExecute, result=" + result);
        if (mRequestType == REQUEST_DESCRIPTOR) {
            if (!TextUtils.isEmpty(result)) {
                if (mOnParseDescriptionInterface != null) {
                    Log.i(TAG, "onParse");
                    DescriptionData data = mOnParseDescriptionInterface.onParse(result);
                    if (mContext.getPackageName().equals(data.packageName)) {
                        if (TextUtils.isEmpty(data.subDescriptionUrl)) {
                            if (data.newVersionCode > data.curVersionCode
                                    && data.newVersionCode != mIgnoreVersion) {
                                mDownloadUrl = data.downloadUrl;
                                if (mOnFoundNewVersionInterface != null) {
                                    Log.i(TAG, "onFoundNewVersion");
                                    mOnFoundNewVersionInterface.onFoundNewVersion(
                                            data.versionName,
                                            data.releaseNotes,
                                            mDownloadUrl);
                                    return;
                                }
                            }
                        } else {
                            Log.i(TAG, "postExecute, request subDescriptionUrl...");
                            check(data.subDescriptionUrl, mOnParseDescriptionInterface, mOnFoundNewVersionInterface);
                            return;
                        }
                    }
                }
            }
            if (mOnFoundNewVersionInterface != null) {
                mOnFoundNewVersionInterface.onNotFoundNewVersion();
            }
        } else if (mRequestType == REQUEST_DOWNLOAD) {
            if (!TextUtils.isEmpty(result)) {
                File file = new File(result);
                if (!file.exists()) {
                    Log.e(TAG, "download complete file is not exist");
                    return;
                }
                file.getParentFile().setExecutable(true, false);
                file.getParentFile().setReadable(true, false);
                file.getParentFile().setWritable(true, false);
                file.setExecutable(true, false);
                file.setReadable(true, false);
                file.setWritable(true, false);
                if (mOnDownloadCompleteInterface != null) {
                    Log.i(TAG, "onDownloadComplete, path=" + result);
                    mOnDownloadCompleteInterface.onDownloadComplete(result);
                }
            } else {
                Toast.makeText(mContext, "Download update files failed!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public interface OnParseDescriptionInterface {
        public abstract DescriptionData onParse(String response);
    }

    public interface OnFoundNewVersionInterface {
        public abstract void onFoundNewVersion(String version, String introduction, String url);
        public abstract void onNotFoundNewVersion();
    }

    public interface OnDownloadCompleteInterface {
        public abstract void onDownloadComplete(String path);
    }

    class DescriptionData {
        String appName = "";
        String packageName = "";
        String channel = "";
        int curVersionCode = 0;
        int newVersionCode = 0;
        String versionName = "";
        int licensed = 1;
        int updateType = 1;
        int fileSize = 0;
        String md5 = "";
        String releaseDate = "";
        String releaseNotes = "";
        String downloadUrl = "";
        String subDescriptionUrl = "";
        int debug = 0;
    }
}
