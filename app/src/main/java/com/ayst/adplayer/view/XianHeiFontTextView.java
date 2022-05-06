package com.ayst.adplayer.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.ayst.adplayer.AdPlayerApplication;

/**
 * Created by shenhaibo on 2017/1/2.
 */
public class XianHeiFontTextView extends AppCompatTextView {
    public XianHeiFontTextView(Context context) {
        super(context);
        setTypeface();
    }

    public XianHeiFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface();
    }

    public XianHeiFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface();
    }

    private void setTypeface() {
        // 如果自定义typeface初始化失败，就用原生的typeface
        if (AdPlayerApplication.sXianHeiTextType == null) {
            setTypeface(getTypeface());
        } else {
            setTypeface(AdPlayerApplication.sXianHeiTextType);
        }
    }
}
