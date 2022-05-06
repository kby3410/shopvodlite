
package com.ayst.adplayer.data;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TextBoxData implements Serializable {

    @SerializedName("size")
    @Expose
    private Integer size;
    @SerializedName("style")
    @Expose
    private String style;
    @SerializedName("text_color")
    @Expose
    private long textColor;
    @SerializedName("bg_color")
    @Expose
    private long bgColor;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("scroll_speed")
    @Expose
    private int scrollSpeed;
    private final static long serialVersionUID = 2669398067560793709L;

    public TextBoxData() {
        this.size = 30;
        this.textColor = 0xff1a1a1a;
        this.bgColor = 0xffffffff;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public TextBoxData withSize(Integer size) {
        this.size = size;
        return this;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public TextBoxData withStyle(String style) {
        this.style = style;
        return this;
    }

    public long getTextColor() {
        return textColor;
    }

    public void setTextColor(long color) {
        this.textColor = color;
    }

    public TextBoxData withTextColor(long color) {
        this.textColor = color;
        return this;
    }

    public long getBgColor() {
        return bgColor;
    }

    public void setBgColor(long bgColor) {
        this.bgColor = bgColor;
    }

    public TextBoxData withBgColor(long bg) {
        this.bgColor = bg;
        return this;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TextBoxData withText(String text) {
        this.text = text;
        return this;
    }

    public int getScrollSpeed() {
        return scrollSpeed;
    }

    public void setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    @Override
    public String toString() {
        return "TextBoxData{" +
                "size=" + size +
                ", style='" + style + '\'' +
                ", textColor=" + textColor +
                ", bgColor=" + bgColor +
                ", scrollSpeed='" + scrollSpeed + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
