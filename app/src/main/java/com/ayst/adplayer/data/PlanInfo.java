package com.ayst.adplayer.data;

/**
 * Created by Administrator on 2018/3/29.
 */

public class PlanInfo extends BaseInfo{
    private int hour = 12;
    private int min = 0;
    private long playlistId = 0;

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getTime() {
        return String.format("%02d", hour) + ":" + String.format("%02d", min);
    }

    public long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }
}
