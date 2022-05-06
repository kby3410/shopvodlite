package com.ayst.adplayer.settings.template;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ayst.adplayer.R;
import com.ayst.adplayer.data.BoxInfo;
import com.ayst.adplayer.data.HomeTemplate;
import com.ayst.adplayer.template.BoxType;

import java.util.List;

public class TemplatePreviewView extends RelativeLayout {

    private Context mContext;
    private CheckBox mCheckbox;
    private TextView mNameTv;
    private RelativeLayout mContainer;
    private RelativeLayout mParent;

    public TemplatePreviewView(Context context) {
        this(context, null);
    }

    public TemplatePreviewView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TemplatePreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View view = inflate(context, R.layout.item_template_preview, this);

        initView(view);
    }

    private void initView(View view) {
        mCheckbox = (CheckBox) view.findViewById(R.id.checkbox);
        mNameTv = (TextView) view.findViewById(R.id.name);
        mContainer = (RelativeLayout) view.findViewById(R.id.template_container);

        mParent = (RelativeLayout) view.findViewById(R.id.parent);
        mParent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TemplatePreviewView.this.callOnClick();
            }
        });
    }

    public void loadData(HomeTemplate template, int width, int height) {
        if (null != template) {
            mNameTv.setText(template.getName());
            List<BoxInfo> items = template.getItems();
            if (null != items) {
                for (BoxInfo item : items) {
                    ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(0, 0);
                    margin.leftMargin = (int) (item.getPosX() * width);
                    margin.topMargin = (int) (item.getPosY() * height);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
                    layoutParams.width = (int) (item.getWidth() * width);
                    layoutParams.height = (int) (item.getHeight() * height);

                    View previewView = inflate(mContext, R.layout.box_null_content, null);

                    ImageView iconView = previewView.findViewById(R.id.icon);
                    ViewGroup.LayoutParams params = iconView.getLayoutParams();
                    params.width = 20;
                    params.height = 20;
                    iconView.setLayoutParams(params);
                    if (item.getType() == BoxType.IMAGE.ordinal()) {
                        iconView.setImageResource(R.drawable.ic_image);
                        mContainer.addView(previewView, layoutParams);
                    } else if (item.getType() == BoxType.TEXT.ordinal()) {
                        iconView.setImageResource(R.drawable.ic_text);
                        mContainer.addView(previewView, layoutParams);
                    } else if (item.getType() == BoxType.VIDEO.ordinal()) {
                        iconView.setImageResource(R.drawable.ic_video);
                        mContainer.addView(previewView, layoutParams);
                    }
                }
            }
        }
    }

    public void setSelected(boolean isSelected) {
        mCheckbox.setChecked(isSelected);
    }
}
