package com.ayst.adplayer.data;

/**
 * Created by shenhaibo on 2018/5/6.
 */

public class ShareFileInfo extends FileInfo {
    private ShareHostInfo hostInfo;

    public ShareHostInfo getHostInfo() {
        return hostInfo;
    }

    public void setHostInfo(ShareHostInfo hostInfo) {
        this.hostInfo = hostInfo;
    }
}
