
package com.ayst.adplayer.data;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BoxData implements Serializable {

    @SerializedName("text_data")
    @Expose
    private TextBoxData textData;
    @SerializedName("image_data")
    @Expose
    private ImageBoxData imageData;
    private final static long serialVersionUID = -7312520507976050465L;

    public TextBoxData getTextData() {
        return textData;
    }

    public void setTextData(TextBoxData textData) {
        this.textData = textData;
    }

    public BoxData withTextData(TextBoxData textData) {
        this.textData = textData;
        return this;
    }

    public ImageBoxData getImageData() {
        return imageData;
    }

    public void setImageData(ImageBoxData imageData) {
        this.imageData = imageData;
    }

    public BoxData withImageData(ImageBoxData imageData) {
        this.imageData = imageData;
        return this;
    }

    @Override
    public String toString() {
        return "BoxData{" +
                "textData=" + textData +
                ", imageData=" + imageData +
                '}';
    }
}
