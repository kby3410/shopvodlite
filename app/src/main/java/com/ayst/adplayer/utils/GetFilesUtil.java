package com.ayst.adplayer.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ayst.adplayer.data.FileInfo;


/**
 * 用于获取手机的文件夹及文件的工具类，如果权限允许，可以获取手机上任意路径的文件列表
 * GetFilesUtils使用的是懒汉式单例模式，线程安全
 *
 * @author wuwang
 * @since 2014.11
 */
public class GetFilesUtil {
    private static final String TAG = "GetFilesUtil";

    public static final String ROOT_DIR = "/";

    public static final int TYPE_DIR = 1;
    public static final int TYPE_FILE = 2;
    public static final int TYPE_ALL = 3;

    public static List<FileInfo> getSonNode(File path, int type, String[] suffixes) {
        return getSonNode(path, type, suffixes, true);
    }

    /**
     * 获取文件path文件夹下的文件列表
     *
     * @param path 手机上的文件夹
     * @return path文件夹下的文件列表的信息，信息存储在Map中，Map的key的列表如下：<br />
     * FILE_INFO_NAME : String 文件名称 <br />
     * FILE_INFO_ISFOLDER: boolean 是否为文件夹  <br />
     * FILE_INFO_TYPE: string 文件的后缀 <br />
     * FILE_INFO_NUM_SONDIRS : int 子文件夹个数  <br />
     * FILE_INFO_NUM_SONFILES: int 子文件个数  <br />
     * FILE_INFO_PATH : String 文件的绝对路径 <br />
     * @see #getSonNode(String)
     **/
    public static List<FileInfo> getSonNode(File path, int type, String[] suffixes, boolean showHiddenFile) {
        if (path.isDirectory()) {
            List<FileInfo> list = new ArrayList<FileInfo>();
            File[] files = path.listFiles();

            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setName(files[i].getName());
                    if (files[i].isDirectory()) {
                        fileInfo.setDir(true);
                        File[] bFiles = files[i].listFiles();
                        if (bFiles == null) {
                            fileInfo.setSubDirs(0);
                            fileInfo.setSubFiles(0);
                        } else {
                            int getNumOfDir = 0;
                            for (int j = 0; j < bFiles.length; j++) {
                                if (bFiles[j].isDirectory()) {
                                    getNumOfDir++;
                                }
                            }
                            fileInfo.setSubDirs(getNumOfDir);
                            fileInfo.setSubFiles(bFiles.length - getNumOfDir);
                        }
                        fileInfo.setSuffix("");
                    } else {
                        fileInfo.setDir(false);
                        fileInfo.setSubDirs(0);
                        fileInfo.setSubFiles(0);
                        fileInfo.setSuffix(getFileSuffix(files[i].getName()));
                    }
                    fileInfo.setPath(files[i].getAbsoluteFile().getPath());
                    if ((fileInfo.isDir() && (TYPE_DIR == type || TYPE_ALL == type)) // 目录
                        || (!fileInfo.isDir() && (TYPE_FILE == type || TYPE_ALL == type) // 文件
                            && filterSuffix(fileInfo.getSuffix(), suffixes) // 过滤文件名
                            && (showHiddenFile || !fileInfo.isHidden()))) { // 过滤隐藏文件
                        list.add(fileInfo);
                    }
                }
                return list;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 获取文件pathStr文件夹下的文件列表
     *
     * @param pathStr 手机上的文件夹的绝对路径
     * @return pathStr文件夹下的文件列表的信息，信息存储在Map中，Map的key的列表如下：<br />
     * FILE_INFO_NAME : String 文件名称 <br />
     * FILE_INFO_ISFOLDER: boolean 是否为文件夹  <br />
     * FILE_INFO_TYPE: string 文件的后缀 <br />
     * FILE_INFO_NUM_SONDIRS : int 子文件夹个数  <br />
     * FILE_INFO_NUM_SONFILES: int 子文件个数  <br />
     * FILE_INFO_PATH : String 文件的绝对路径 <br />
     **/
    public static List<FileInfo> getSonNode(String pathStr, int type, String[] suffixes) {
        File path = new File(pathStr);
        return getSonNode(path, type, suffixes);
    }

    /**
     * 获取文件pathStr文件夹下的文件列表
     *
     * @param pathStr 手机上的文件夹的绝对路径
     * @return pathStr文件夹下的文件列表的信息，信息存储在Map中，Map的key的列表如下：<br />
     * FILE_INFO_NAME : String 文件名称 <br />
     * FILE_INFO_ISFOLDER: boolean 是否为文件夹  <br />
     * FILE_INFO_TYPE: string 文件的后缀 <br />
     * FILE_INFO_NUM_SONDIRS : int 子文件夹个数  <br />
     * FILE_INFO_NUM_SONFILES: int 子文件个数  <br />
     * FILE_INFO_PATH : String 文件的绝对路径 <br />
     **/
    public static List<FileInfo> getSonNode(String pathStr, int type) {
        File path = new File(pathStr);
        return getSonNode(path, type, null);
    }

    /**
     * 获取文件pathStr文件夹下的文件列表
     *
     * @param pathStr 手机上的文件夹的绝对路径
     * @return pathStr文件夹下的文件列表的信息，信息存储在Map中，Map的key的列表如下：<br />
     * FILE_INFO_NAME : String 文件名称 <br />
     * FILE_INFO_ISFOLDER: boolean 是否为文件夹  <br />
     * FILE_INFO_TYPE: string 文件的后缀 <br />
     * FILE_INFO_NUM_SONDIRS : int 子文件夹个数  <br />
     * FILE_INFO_NUM_SONFILES: int 子文件个数  <br />
     * FILE_INFO_PATH : String 文件的绝对路径 <br />
     **/
    public static List<FileInfo> getSonNode(String pathStr) {
        File path = new File(pathStr);
        return getSonNode(path, TYPE_ALL, null);
    }

    /**
     * 获取文件path文件或文件夹的兄弟节点文件列表
     *
     * @param path 手机上的文件夹
     * @return path文件夹下的文件列表的信息，信息存储在Map中，Map的key的列表如下：<br />
     * FILE_INFO_NAME : String 文件名称 <br />
     * FILE_INFO_ISFOLDER: boolean 是否为文件夹  <br />
     * FILE_INFO_TYPE: string 文件的后缀 <br />
     * FILE_INFO_NUM_SONDIRS : int 子文件夹个数  <br />
     * FILE_INFO_NUM_SONFILES: int 子文件个数  <br />
     * FILE_INFO_PATH : String 文件的绝对路径 <br />
     * @see #getBrotherNode(String)
     **/
    public static List<FileInfo> getBrotherNode(File path) {
        if (path.getParentFile() != null) {
            return getSonNode(path.getParentFile(), TYPE_ALL, null);
        } else {
            return null;
        }
    }

    /**
     * 获取文件path文件或文件夹的兄弟节点文件列表
     *
     * @param pathStr 手机上的文件夹
     * @return path文件夹下的文件列表的信息，信息存储在Map中，Map的key的列表如下：<br />
     * FILE_INFO_NAME : String 文件名称 <br />
     * FILE_INFO_ISFOLDER: boolean 是否为文件夹  <br />
     * FILE_INFO_TYPE: string 文件的后缀 <br />
     * FILE_INFO_NUM_SONDIRS : int 子文件夹个数  <br />
     * FILE_INFO_NUM_SONFILES: int 子文件个数  <br />
     * FILE_INFO_PATH : String 文件的绝对路径 <br />
     * @see #getBrotherNode(File)
     **/
    public static List<FileInfo> getBrotherNode(String pathStr) {
        File path = new File(pathStr);
        return getBrotherNode(path);
    }

    /**
     * 获取文件或文件夹的父路径
     *
     * @param path 文件或者文件夹
     * @return String path的父路径
     **/
    public static String getParentPath(File path) {
        if (path.getParentFile() == null) {
            return null;
        } else {
            return path.getParent();
        }
    }

    /**
     * 获取文件或文件的父路径
     *
     * @param pathStr 文件或者文件夹路径
     * @return String pathStr的父路径
     **/
    public static String getParentPath(String pathStr) {
        File path = new File(pathStr);
        if (path.getParentFile() == null) {
            return null;
        } else {
            return path.getParent();
        }
    }

    /**
     * 获取sd卡的绝对路径
     *
     * @return String 如果sd卡存在，返回sd卡的绝对路径，否则返回null
     **/
    public static String getSDPath() {
        String sdcard = Environment.getExternalStorageState();
        if (sdcard.equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * 获取一个基本的路径，一般应用创建存放应用数据可以用到
     *
     * @return String 如果SD卡存在，返回SD卡的绝对路径，如果SD卡不存在，返回Android数据目录的绝对路径
     **/
    public static String getBasePath() {
        String basePath = getSDPath();
        if (basePath == null) {
            return Environment.getDataDirectory().getAbsolutePath();
        } else {
            return basePath;
        }
    }

    public static List<String> getStorageList(Context context) {
        List<String> pathList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pathList = getStorageVolumeList(context);
        } else {
            pathList = getMountPathList();
        }

        if (pathList.isEmpty()) {
            pathList.add(getSDPath());
        }
        return pathList;
    }

    /**
     * 获取所有存储卡挂载路径
     * @return
     */
    private static List<String> getMountPathList() {
        List<String> pathList = new ArrayList<String>();
        final String cmd = "cat /proc/mounts";
        Runtime run = Runtime.getRuntime();
        try {
            Process p = run.exec(cmd);
            BufferedInputStream inputStream = new BufferedInputStream(p.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.i(TAG, "getMountPathList, " + line);

                // /data/media /storage/emulated/0 sdcardfs rw,nosuid,nodev,relatime,uid=1023,gid=1023 0 0
                String[] temp = TextUtils.split(line, " ");
                // 第二个空格后面是路径
                String result = temp[1];
                File file = new File(result);
                // 类型为目录、可读、可写，视为一条挂载路径
                if (file.isDirectory() && file.canRead() && file.canWrite()) {
                    Log.d(TAG, "getMountPathList, add --> " + file.getAbsolutePath());
                    pathList.add(result);
                }

                // 检查命令是否执行失败 p.exitValue()==0：表示正常结束，1：非正常结束
                if (p.waitFor() != 0 && p.exitValue() == 1) {
                    Log.e(TAG, "getMountPathList, cmd execute failed!");
                }
            }
            bufferedReader.close();
            inputStream.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            pathList.add(Environment.getExternalStorageDirectory().getAbsolutePath());
        }
        return pathList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static List<String> getStorageVolumeList(Context context) {
        List<String> pathList = new ArrayList<String>();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> volumes = storageManager.getStorageVolumes();
        try {
            Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            for (int i = 0; i < volumes.size(); i++) {
                //获取每个挂载的StorageVolume
                StorageVolume storageVolume = volumes.get(i);

                //通过反射调用getPath、isRemovable
                String storagePath = (String) getPath.invoke(storageVolume); //获取路径
                boolean isRemovableResult = (boolean) isRemovable.invoke(storageVolume);//是否可移除
                String description = storageVolume.getDescription(context);
                Log.d(TAG, "getStorageList, storagePath=" + storagePath
                        + ", isRemovableResult=" + isRemovableResult +", description="+description);
                pathList.add(storagePath);
            }
        } catch (Exception e) {
            Log.d(TAG, "getStorageList, e:" + e);
        }
        return pathList;
    }

    /**
     * 获取文件path的大小
     *
     * @return String path的大小
     **/
    public static String getFileSize(File path) throws IOException {
        if (path.exists()) {
            DecimalFormat df = new DecimalFormat("#.00");
            String sizeStr = "";
            FileInputStream fis = new FileInputStream(path);
            long size = fis.available();
            fis.close();
            if (size < 1024) {
                sizeStr = size + "B";
            } else if (size < 1048576) {
                sizeStr = df.format(size / (double) 1024) + "KB";
            } else if (size < 1073741824) {
                sizeStr = df.format(size / (double) 1048576) + "MB";
            } else {
                sizeStr = df.format(size / (double) 1073741824) + "GB";
            }
            return sizeStr;
        } else {
            return null;
        }
    }

    /**
     * 获取文件fpath的大小
     *
     * @return String path的大小
     **/
    public static String getFileSize(String fpath) {
        File path = new File(fpath);
        if (path.exists()) {
            DecimalFormat df = new DecimalFormat("#.00");
            String sizeStr = "";
            long size = 0;
            try {
                FileInputStream fis = new FileInputStream(path);
                size = fis.available();
                fis.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "unknown";
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "unknown";
            }
            if (size < 1024) {
                sizeStr = size + "B";
            } else if (size < 1048576) {
                sizeStr = df.format(size / (double) 1024) + "KB";
            } else if (size < 1073741824) {
                sizeStr = df.format(size / (double) 1048576) + "MB";
            } else {
                sizeStr = df.format(size / (double) 1073741824) + "GB";
            }
            return sizeStr;
        } else {
            return "unknown";
        }
    }

    /**
     * 根据后缀获取文件fileName的类型
     *
     * @return String 文件的类型
     **/
    public static String getFileSuffix(String fileName) {
        if (!TextUtils.isEmpty(fileName) && fileName.length() > 3) {
            int dot = fileName.lastIndexOf(".");
            if (dot > 0) {
                return fileName.substring(dot + 1);
            } else {
                return "";
            }
        }
        return "";
    }

    public static Comparator<FileInfo> defaultOrder() {
        Comparator<FileInfo> order = new Comparator<FileInfo>() {
            @Override
            public int compare(FileInfo lhs, FileInfo rhs) {
                // TODO Auto-generated method stub
                int left0 = lhs.isDir() ? 0 : 1;
                int right0 = rhs.isDir() ? 0 : 1;
                if (left0 == right0) {
                    String left1 = lhs.getSuffix();
                    String right1 = rhs.getSuffix();
                    if (left1.compareTo(right1) == 0) {
                        String left2 = lhs.getName();
                        String right2 = rhs.getName();
                        return left2.compareTo(right2);
                    } else {
                        return left1.compareTo(right1);
                    }
                } else {
                    return left0 - right0;
                }
            }
        };

        return order;
    }

    private static boolean filterSuffix(String suffix, String[] suffixes) {
        if (null != suffixes && suffixes.length > 0) {
            for (String suffix1 : suffixes) {
                if (suffix.equalsIgnoreCase(suffix1)) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

}