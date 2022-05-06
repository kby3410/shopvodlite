package com.ayst.adplayer.data;

import java.io.Serializable;

/**
 * Created by shenhaibo on 2018/5/6.
 */

public class ShareHostInfo implements Serializable {
    private String address = "";
    private String username = "";
    private String password = "";
    private Boolean needPassword = true;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getNeedPassword() {
        return needPassword;
    }

    public void setNeedPassword(Boolean needPassword) {
        this.needPassword = needPassword;
    }
}
