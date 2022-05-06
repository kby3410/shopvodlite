package com.ayst.adplayer.dialogs.editimagebox;


/**
 * Created by Administrator on 2019/1/2.
 */

public class ImageBean {
    private String fileName = "";
    private String path = "";
    private int width = 0;
    private int height = 0;

    public ImageBean() {

    }

    public ImageBean(String fileName, String path, int width, int height) {
        this.fileName = fileName;
        this.path = path;
        this.width = width;
        this.height = height;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "ImageBean{" +
                "fileName='" + fileName + '\'' +
                ", path='" + path + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
