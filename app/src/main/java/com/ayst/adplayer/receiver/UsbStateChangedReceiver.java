/* 
**
** Copyright 2007, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License"); 
** you may not use this file except in compliance with the License. 
** You may obtain a copy of the License at 
**
**     http://www.apache.org/licenses/LICENSE-2.0 
**
** Unless required by applicable law or agreed to in writing, software 
** distributed under the License is distributed on an "AS IS" BASIS, 
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and 
** limitations under the License.
*/

package com.ayst.adplayer.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.util.Log;

import com.ayst.adplayer.event.MessageEvent;
import com.ayst.adplayer.service.HandleService;

import org.greenrobot.eventbus.EventBus;

public class UsbStateChangedReceiver extends BroadcastReceiver {
    private final static String TAG = "UsbStateChangedReceiver";

    private static int USB_STATE_NONE = 0;
    private static int USB_STATE_MOUNTED = 1;
    private static int USB_STATE_UNMOUNTED = 2;

    private static int mVolumeState = -1;

    @Override
    public void onReceive(final Context context, Intent intent) {
        int usbState = USB_STATE_NONE;
        String action = intent.getAction();

        Log.d(TAG, "onReceive, action = " + action);
        if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
            usbState = USB_STATE_MOUNTED;
        } else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(action)) {
            usbState = USB_STATE_UNMOUNTED;
        } else if ("android.hardware.usb.action.USB_STATE".equals(action)) {
            Bundle extras = intent.getExtras();
            boolean connected = extras.getBoolean("connected");
            boolean configured = extras.getBoolean("configured");
            boolean mtpEnabled = extras.getBoolean("mtp");
            boolean ptpEnabled = extras.getBoolean("ptp");
            if (!connected && mtpEnabled && !configured) {
                usbState = USB_STATE_MOUNTED;
            }
        } else if (action.equals("android.os.storage.action.VOLUME_STATE_CHANGED")) {
            int state = intent.getIntExtra("android.os.storage.extra.VOLUME_STATE", 0);
            /**
             * 0: STATE_UNMOUNTED
             * 2: STATE_MOUNTED
             */
            if (mVolumeState == 0 && state == 2) {
                usbState = USB_STATE_MOUNTED;
            } else if (mVolumeState == 2 && state == 0) {
                usbState = USB_STATE_UNMOUNTED;
            }
            mVolumeState = state;
        }

        if (USB_STATE_MOUNTED == usbState) {
            Log.i(TAG, "onReceive, USB_STATE_MOUNTED");
            MessageEvent msg = new MessageEvent(MessageEvent.MSG_USB_MOUNTED);
            EventBus.getDefault().post(msg);

            Intent serviceIntent = new Intent(context, HandleService.class);
            serviceIntent.putExtra("command", HandleService.COMMAND_CHECK_CONFIG);
            serviceIntent.putExtra("delay", 5000);
            context.startService(serviceIntent);
        } else if (USB_STATE_UNMOUNTED == usbState) {
            Log.i(TAG, "onReceive, USB_STATE_UNMOUNTED");
            MessageEvent msg = new MessageEvent(MessageEvent.MSG_USB_UNMOUNTED);
            EventBus.getDefault().post(msg);
        }
    }
}


