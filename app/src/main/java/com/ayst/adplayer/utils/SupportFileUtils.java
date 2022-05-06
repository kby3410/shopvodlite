package com.ayst.adplayer.utils;

import com.ayst.adplayer.data.FileInfo;

import java.util.List;

/**
 * Created by shenhaibo on 2018/4/1.
 */

public class SupportFileUtils {
    public static final String[] SUPPORT_MEDIA_FILE_SUFFIX = {"mp4", "avi", "mkv", "vob","mov",
            "asf", "ts", "m2t", "rmvb", "flv", "webm", "wmv", "png", "jpg", "jpeg", "bmp",
            "tiff", "webp", "gif"};
    public static final String[] SUPPORT_VIDEO_FILE_SUFFIX = {"mp4", "avi", "mkv", "vob",
            "asf", "ts", "m2t", "rmvb", "flv", "webm", "wmv","mov"};
    public static final String[] SUPPORT_PICTURE_FILE_SUFFIX = {"png", "jpg", "jpeg", "bmp", "tiff", "webp", "gif"};
    public static final String[] SUPPORT_MUSIC_FILE_SUFFIX = {"mp3", "wav", "wma", "ogg", "mp2", "flac", "ape"};

    public static boolean isPicture(String path) {
        String fileSuffix = GetFilesUtil.getFileSuffix(path);
        for (String suffix : SUPPORT_PICTURE_FILE_SUFFIX) {
            if (suffix.equalsIgnoreCase(fileSuffix)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAudio(String path) {
        String fileSuffix = GetFilesUtil.getFileSuffix(path);
        for (String suffix : SUPPORT_MUSIC_FILE_SUFFIX) {
            if (suffix.equalsIgnoreCase(fileSuffix)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasMediaFile(String path) {
        List<FileInfo> list = GetFilesUtil.getSonNode(path, GetFilesUtil.TYPE_FILE,
                SupportFileUtils.SUPPORT_MEDIA_FILE_SUFFIX);
        return list != null && !list.isEmpty();
    }
}
