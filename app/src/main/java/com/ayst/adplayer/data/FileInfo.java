package com.ayst.adplayer.data;

import android.net.Uri;
import com.ayst.adplayer.utils.SupportFileUtils;
import java.io.File;
import retrofit2.http.HTTP;

/**
 * Created by Administrator on 2018/3/27.
 */

public class FileInfo {
    public static final int FILE_TYPE_LOCAL = 0;
    public static final int FILE_TYPE_SMB = 1;
    public static final int FILE_TYPE_HTTP = 2;
    public static final int MEDIA_TYPE_IMAGE = 0;
    public static final int MEDIA_TYPE_VIDEO = 1;
    public static final int MEDIA_TYPE_AUDIO = 2;

    private String name = "";
    private String path = "";
    private boolean isDir = false;
    private String suffix = "";
    private String info = "";
    private int subDirs = 0;
    private int subFiles = 0;
    private Uri uri;
    private String url;
    private int type = FILE_TYPE_LOCAL;

    public FileInfo() {

    }

    public FileInfo(String name, String path, boolean isDir, String suffix, String info, int subDirs, int subFiles) {
        this.name = name;
        this.path = path;
        this.isDir = isDir;
        this.suffix = suffix;
        this.info = info;
        this.subDirs = subDirs;
        this.subFiles = subFiles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getSubDirs() {
        return subDirs;
    }

    public void setSubDirs(int subDirs) {
        this.subDirs = subDirs;
    }

    public int getSubFiles() {
        return subFiles;
    }

    public void setSubFiles(int subFiles) {
        this.subFiles = subFiles;
    }

    public Uri getUri() {
        if (getType() == FileInfo.FILE_TYPE_LOCAL) {
            return Uri.fromFile(new File(getPath()));
        }
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isHidden() {
        return this.name.startsWith(".");
    }
    public int getMediaType() {
        if (getType() == FileInfo.FILE_TYPE_HTTP) {
                return FileInfo.MEDIA_TYPE_VIDEO;
            } else if (SupportFileUtils.isPicture(getUri().toString())) {
                return MEDIA_TYPE_IMAGE;
            } else if (SupportFileUtils.isAudio(getUri().toString())) {
                return MEDIA_TYPE_AUDIO;
            } else {
                return MEDIA_TYPE_VIDEO;
            }
    }
}
