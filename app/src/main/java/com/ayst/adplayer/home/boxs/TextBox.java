package com.ayst.adplayer.home.boxs;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.ayst.adplayer.R;
import com.ayst.adplayer.data.BoxInfo;
import com.ayst.adplayer.data.TextBoxData;
import com.ayst.adplayer.view.AlwaysMarqueeTextView;

import anylife.scrolltextview.ScrollTextView;

/**
 * Created by shenhaibo on 2018/10/19.
 */

public class TextBox extends BaseBox {

    private Context mContext;
    private ScrollTextView mTextView;
    private TextBoxData mTextData;

    public TextBox(Context context, BoxInfo data) {
        super(context, data);

        mContext = context;
        if (null == mData.getData()) {
            mTextData = new TextBoxData();
        } else {
            mTextData = (TextBoxData) mData.getData();
        }

        Log.i("TextBox", "mTextData: " + mTextData.toString());

        FrameLayout view = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.layout_text_box, this, true);

        initView(view);
    }

    protected void initView(View view) {
        super.initView(view);
        mTextView  = (ScrollTextView) view.findViewById(R.id.text);
        update(mTextData);
    }

    public void update(TextBoxData textData) {
        super.update(textData);
        if (null != textData) {
            this.mTextData = textData;
            mTextView.setBackgroundColor((int)mTextData.getBgColor());
            mTextView.setTextSize(mTextData.getSize());
            mTextView.setTextColor((int)mTextData.getTextColor());
            mTextView.setSpeed(mTextData.getScrollSpeed());
            mTextView.setText(mTextData.getText());
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        mTextView.setVisibility(visibility);
    }
}
