package com.ayst.adplayer.utils;


import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2018/4/3.
 */

public class ColorUtil {
    private static HashMap<String, Integer> sColors = new HashMap<>();

    static {
        sColors.put("black", 0xff000000);
        sColors.put("white", 0xffffffff);
        sColors.put("red", 0xffff0000);
        sColors.put("orange", 0xffffa500);
        sColors.put("yellow", 0xffffff00);
        sColors.put("green", 0xff008000);
        sColors.put("darkblue", 0xff00008b);
        sColors.put("blue", 0xff0000ff);
        sColors.put("purple", 0xff800080);
    }

    public static ArrayList<String> getColors() {
        ArrayList<String> colors = new ArrayList<>();
        colors.addAll(sColors.keySet());
        return colors;
    }

    public static Integer getColor(String key) {
        return sColors.get(key);
    }
}
