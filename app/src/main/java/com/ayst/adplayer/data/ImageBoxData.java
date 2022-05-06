
package com.ayst.adplayer.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageBoxData implements Serializable {

    @SerializedName("images")
    @Expose
    private List<String> images;
    @SerializedName("interval")
    @Expose
    private Integer interval;
    @SerializedName("effect")
    @Expose
    private String effect;
    private final static long serialVersionUID = 8427614961868614733L;

    public ImageBoxData() {
        this.images = new ArrayList<>();
        this.interval = 3;
        this.effect = "Default";
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public ImageBoxData withImages(List<String> images) {
        this.images = images;
        return this;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public ImageBoxData withInterval(Integer interval) {
        this.interval = interval;
        return this;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public ImageBoxData withEffect(String effect) {
        this.effect = effect;
        return this;
    }

    @Override
    public String toString() {
        return "ImageBoxData{" +
                "images=" + images +
                ", interval=" + interval +
                ", effect='" + effect + '\'' +
                '}';
    }
}
