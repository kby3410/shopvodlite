package com.ayst.adplayer.event;

import android.os.Bundle;

/**
 * Created by Administrator on 2018/3/30.
 */

public class MessageEvent {
    public static final String MSG_PLAY_MODE_CHANGE = "play_mode_change";
    public static final String MSG_PICTURE_SETTING_CHANGE = "picture_setting_change";
    public static final String MSG_USB_MOUNTED = "usb_mounted";
    public static final String MSG_USB_UNMOUNTED = "usb_unmounted";
    public static final String MSG_TEMPLATE_CHANGED = "template_changed";

    private String message = "";
    private Bundle bundle = null;

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }
}
