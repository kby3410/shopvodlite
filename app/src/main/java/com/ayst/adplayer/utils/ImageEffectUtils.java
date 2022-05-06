package com.ayst.adplayer.utils;

import androidx.viewpager.widget.ViewPager;

import com.youth.banner.Transformer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2018/4/3.
 */

public class ImageEffectUtils {
    private static HashMap<String, Class<? extends ViewPager.PageTransformer>> sEffects = new HashMap<>();

    static {
        sEffects.put("기본", Transformer.Default);
        sEffects.put("아코디언", Transformer.Accordion);
        sEffects.put("BackToFore", Transformer.BackgroundToForeground);
        sEffects.put("ForeToBack", Transformer.ForegroundToBackground);
        sEffects.put("CubeIn", Transformer.CubeIn);
        sEffects.put("CubeOut", Transformer.CubeOut);
        sEffects.put("DepthPage", Transformer.DepthPage);
        sEffects.put("FlipHorizontal", Transformer.FlipHorizontal);
        sEffects.put("FlipVertical", Transformer.FlipVertical);
        sEffects.put("RotateDown", Transformer.RotateDown);
        sEffects.put("RotateUp", Transformer.RotateUp);
        sEffects.put("ScaleInOut", Transformer.ScaleInOut);
        sEffects.put("Stack", Transformer.Stack);
        sEffects.put("Tablet", Transformer.Tablet);
        sEffects.put("ZoomIn", Transformer.ZoomIn);
        sEffects.put("ZoomOut", Transformer.ZoomOut);
        sEffects.put("ZoomOutSlide", Transformer.ZoomOutSlide);
    }

    public static ArrayList<String> getEffects() {
        ArrayList<String> effects = new ArrayList<>();
        effects.addAll(sEffects.keySet());
        return effects;
    }

    public static Class<? extends ViewPager.PageTransformer> getEffect(String key) {
        return sEffects.get(key);
    }
}
