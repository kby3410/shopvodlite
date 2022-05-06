package com.ayst.adplayer.data;

import com.ayst.adplayer.common.BaseFragment;

/**
 * Created by Administrator on 2018/3/27.
 */

public class SubMenu {
    public String title = "";
    public BaseFragment fragment = null;

    public SubMenu(String title, BaseFragment fragment) {
        this.title = title;
        this.fragment = fragment;
    }
}
