
package com.ayst.adplayer.data;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BoxInfo implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("box_type")
    @Expose
    private Integer boxType;
    @SerializedName("pos_x")
    @Expose
    private Float posX;
    @SerializedName("pos_y")
    @Expose
    private Float posY;
    @SerializedName("width")
    @Expose
    private Float width;
    @SerializedName("height")
    @Expose
    private Float height;
    @SerializedName("data")
    @Expose
    private Object data;
    private final static long serialVersionUID = 1881243065840254751L;

    private static Gson sGson = null;

    public BoxInfo() {
        sGson = new Gson();
        id = -1;
        boxType = -1;
        posX = 0f;
        posY = 0f;
        width = 0f;
        height = 0f;
        data = null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BoxInfo withId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getType() {
        return boxType;
    }

    public void setType(Integer type) {
        this.boxType = type;
    }

    public BoxInfo withType(Integer type) {
        this.boxType = type;
        return this;
    }

    public Float getPosX() {
        return posX;
    }

    public void setPosX(Float posX) {
        this.posX = posX;
    }

    public BoxInfo withPosX(Float posX) {
        this.posX = posX;
        return this;
    }

    public Float getPosY() {
        return posY;
    }

    public void setPosY(Float posY) {
        this.posY = posY;
    }

    public BoxInfo withPosY(Float posY) {
        this.posY = posY;
        return this;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public BoxInfo withWidth(Float width) {
        this.width = width;
        return this;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public BoxInfo withHeight(Float height) {
        this.height = height;
        return this;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public BoxInfo withData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "BoxInfo{" +
                "id=" + id +
                ", type=" + boxType +
                ", posX=" + posX +
                ", posY=" + posY +
                ", width=" + width +
                ", height=" + height +
                ", data=" + data +
                '}';
    }
}
