package com.ayst.adplayer.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ayst.adplayer.R;
import com.ayst.adplayer.settings.Setting;
import com.ayst.adplayer.data.SubMenu;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/3/27.
 */

public class SubMenuAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<SubMenu> mList = null;
    private LayoutInflater mInflater = null;
    private int mSelectedItem = 0;
    private SubMenuView.OnSelectedListener mOnSelectedListener = null;

    public SubMenuAdapter(Context context, ArrayList<SubMenu> list, SubMenuView.OnSelectedListener listener) {
        mContext = context;
        mOnSelectedListener = listener;
        if (list == null) {
            mList = new ArrayList<SubMenu>();
        } else {
            mList = list;
        }
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void setSelectedItem(int i) {
        if (i >=0 && i< mList.size()) {
            mSelectedItem = i;
            notifyDataSetChanged();
        }
    }

    private boolean isSelected(int index) {
        return (Setting.get(mContext).getPlayMode()-1 == index);
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.sub_menu_item, null);
            holder = new ViewHolder();
            holder.mMenuTv = (TextView) view.findViewById(R.id.tv_title);
            holder.mContainer = (LinearLayout) view.findViewById(R.id.container);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.mMenuTv.setText(mList.get(i).title);
        holder.mMenuTv.setSelected(isSelected(i));

        holder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnSelectedListener != null) {
                    mOnSelectedListener.onSelected(i, mList.get(i));
                }
            }
        });

        return view;
    }

    private final class ViewHolder {
        private TextView mMenuTv = null;
        private LinearLayout mContainer = null;

    }
}
