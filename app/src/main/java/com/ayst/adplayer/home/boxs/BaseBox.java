package com.ayst.adplayer.home.boxs;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ayst.adplayer.R;
import com.ayst.adplayer.data.ImageBoxData;
import com.ayst.adplayer.data.BoxInfo;
import com.ayst.adplayer.data.TextBoxData;
import com.ayst.adplayer.template.BoxType;

/**
 * Created by Administrator on 2018/10/23.
 */

public class BaseBox extends FrameLayout {

    protected RelativeLayout mNullLayout;
    protected TextView mSizeTv;
    protected BoxInfo mData;

    public BaseBox(Context context, BoxInfo boxInfo) {
        super(context);
        mData = boxInfo;

        this.setFocusable(true);
        this.setClickable(true);
        this.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }

    protected void initView(View view) {
        mNullLayout = (RelativeLayout) view.findViewById(R.id.layout_box_null);
        mSizeTv = mNullLayout.findViewById(R.id.size);
        mSizeTv.setVisibility(VISIBLE);
        checkData(null);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mSizeTv.setText("id:" + mData.getId() + " size:" + this.getWidth() + "x" + this.getHeight());
    }

    protected void update(Object data) {
        checkData(data);
    }

    public BoxInfo getData() {
        return mData;
    }

    public void setData(BoxInfo mData) {
        this.mData = mData;
    }

    protected void checkData(Object data) {
        if (null == data && (null == mData || null == mData.getData())) {
            mNullLayout.setVisibility(VISIBLE);
        } else {
            if (null == data) {
                data = mData.getData();
            }
            if (mData.getType() == BoxType.IMAGE.ordinal()) {
                ImageBoxData imageData = (ImageBoxData) data;
                if (null == imageData || imageData.getImages().isEmpty()) {
                    mNullLayout.setVisibility(VISIBLE);
                } else {
                    mNullLayout.setVisibility(GONE);
                }
            } else if (mData.getType() == BoxType.TEXT.ordinal()) {
                TextBoxData textData = (TextBoxData) data;
                if (null == textData || TextUtils.isEmpty(textData.getText())) {
                    mNullLayout.setVisibility(VISIBLE);
                } else {
                    mNullLayout.setVisibility(GONE);
                }
            }
        }
    }
}
