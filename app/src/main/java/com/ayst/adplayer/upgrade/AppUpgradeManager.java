package com.ayst.adplayer.upgrade;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.ayst.adplayer.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by shenhaibo on 2017/4/29.
 */

public class AppUpgradeManager extends BaseUpgradeManager {

    private final static String FILE_NAME = "upgrade.apk";
    private final static String REQUEST_DESCRIPTOR_URL = "http://pbfq6rrho.bkt.clouddn.com/description.json";

    public AppUpgradeManager(Context context) {
        super(context);
    }

    public void check(OnFoundNewVersionInterface onFoundNewVersionInterface) {
        super.check(REQUEST_DESCRIPTOR_URL, new OnParseDescriptionInterface() {
            @Override
            public DescriptionData onParse(String response) {
                DescriptionData desc = new DescriptionData();
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject data = new JSONObject(response);
                        desc.appName = data.getString("appName");
                        desc.packageName = data.getString("package");
                        desc.channel = data.getString("channel");
                        desc.subDescriptionUrl = data.getString("subDescriptionUrl");
                        desc.newVersionCode = data.getInt("versionCode");
                        desc.curVersionCode = AppUtils.getVersionCode(mContext);
                        desc.versionName = data.getString("versionName");
                        desc.licensed = data.getInt("licensed");
                        desc.updateType = data.getInt("updateType");
                        desc.fileSize = data.getInt("fileSize");
                        desc.md5 = data.getString("md5");
                        desc.releaseDate = data.getString("releaseDate");
                        desc.releaseNotes = data.getString("releaseNotes");
                        desc.downloadUrl = data.getString("downloadUrl");
                        desc.debug = data.getInt("debug");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return desc;
            }
        }, onFoundNewVersionInterface);
    }

    public void download() {
        super.download(FILE_NAME, new OnDownloadCompleteInterface() {
            @Override
            public void onDownloadComplete(String path) {
                if (!SilentInstall.install(mContext, path)) {
                    Toast.makeText(mContext, "Silent install failed!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.fromFile(new File(path)),
                            "application/vnd.android.package-archive");
                    mContext.startActivity(intent);
                }
            }
        });
    }
}
