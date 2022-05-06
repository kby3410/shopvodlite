package com.ayst.adplayer.data;

/**
 * Created by Administrator on 2018/5/21.
 */

public class BaseInfo {
    protected long id = System.currentTimeMillis();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
