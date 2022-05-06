package com.ayst.adplayer.dialogs.editimagebox;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ayst.adplayer.R;
import com.ayst.adplayer.data.FileInfo;
import com.bumptech.glide.Glide;

import java.io.File;

public class ItemImageView extends RelativeLayout {

    private ImageView mImageView;
    private CheckBox mCheckbox;
    private TextView mTitle;
    private TextView mSize;
    private RelativeLayout mParent;

    public ItemImageView(Context context) {
        this(context, null);
    }

    public ItemImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = inflate(context, R.layout.item_pick_image, this);

        initView(view);
    }

    private void initView(View view) {
        mImageView = (ImageView) view.findViewById(R.id.image);
        mCheckbox = (CheckBox) view.findViewById(R.id.checkbox);
        mTitle = (TextView) view.findViewById(R.id.title);
        mSize = (TextView) view.findViewById(R.id.size);

        mParent = (RelativeLayout) view.findViewById(R.id.parent);
        mParent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemImageView.this.callOnClick();
            }
        });
    }

    public void loadData(ImageBean imageBean) {
        if (null != imageBean && !TextUtils.isEmpty(imageBean.getPath())) {
            Glide.with(getContext()).load(new File(imageBean.getPath())).into(mImageView);
            mTitle.setText(imageBean.getPath());
            mSize.setText(imageBean.getWidth() + "x" + imageBean.getHeight());
        }
    }

    public void setSelected(boolean isSelected) {
        mCheckbox.setChecked(isSelected);
    }
}
