package com.ayst.adplayer.data;

import java.util.Calendar;
/**
 * Created by Administrator on 2018/10/23.
 */

public class TimingInfo {
    private int week=7;
    private int hour = 12;
    private int min = 0;
    private boolean enable = false;
    private int todayWeek,nowHour,nowMin,nowSec;
    public TimingInfo(int week,int hour, int min, boolean enable) {
        this.week = week;
        this.hour = hour;
        this.min = min;
        this.enable = enable;
    }
    public int getWeek() {
        Calendar calendar = Calendar.getInstance();
        nowHour = calendar.getTime().getHours();
        nowMin = calendar.getTime().getMinutes();
        nowSec = calendar.getTime().getSeconds();
        week = calendar.get(Calendar.DAY_OF_WEEK);
        return week;
    }
    public void setWeek(int week){this.week = week;}
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

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "TimingInfo{" +
                "week=" + week +
                "hour=" + hour +
                ", min=" + min +
                ", enable=" + enable +
                '}';
    }
}
