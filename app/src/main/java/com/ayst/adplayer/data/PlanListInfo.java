package com.ayst.adplayer.data;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/3/29.
 */

public class PlanListInfo extends BaseInfo {
    private String title = "";
    private ArrayList<PlanInfo> planInfoList = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<PlanInfo> getPlanInfoList() {
        return planInfoList;
    }

    public void setPlanInfoList(ArrayList<PlanInfo> planInfoList) {
        this.planInfoList.addAll(planInfoList);
    }
}
