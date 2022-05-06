package com.ayst.adplayer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ayst.adplayer.R;
import com.ayst.adplayer.data.SubMenu;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by shenhaibo on 2017/1/2.
 */

public class SubMenuView extends RelativeLayout {
    private static final String TAG = "SubMenuView";

    private Context mContext = null;
    private AdaptiveWidthListView mMenuLv = null;
    private ArrayList<SubMenu> mList = null;
    private SubMenuAdapter mSubMenuAdapter = null;
    private OnSelectedListener mOnSelectedListener = null;

    public SubMenuView(Context context) {
        this(context, null);
    }

    public SubMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        RelativeLayout view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.layout_sub_menu, this, true);
        mMenuLv = (AdaptiveWidthListView) view.findViewById(R.id.lv_menu);
//        mMenuLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (mOnSelectedListener != null) {
//                    mOnSelectedListener.onSelected(i, mList.get(i));
//                }
//            }
//        });
        mMenuLv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mOnSelectedListener != null) {
                    mOnSelectedListener.onSelected(i, mList.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mMenuLv.setItemsCanFocus(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = mMenuLv.getWidth() + 1;
        super.onMeasure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.UNSPECIFIED), heightMeasureSpec);
    }

    public void notifyDataSetChanged() {
        mSubMenuAdapter.notifyDataSetChanged();
    }

    public void show(ArrayList<SubMenu> list) {
        mList = list;
        mSubMenuAdapter = new SubMenuAdapter(mContext, list, mOnSelectedListener);
        mMenuLv.setAdapter(mSubMenuAdapter);
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        mOnSelectedListener = listener;
    }

    public interface OnSelectedListener {
        public void onSelected(int index, SubMenu menu);
    }
}
