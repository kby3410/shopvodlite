package com.ayst.adplayer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.ayst.adplayer.R;
import com.ayst.adplayer.settings.Setting;
import com.ayst.adplayer.data.PlayListInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/3/28.
 */

public class PickerPlaylistDialog {
    private Context mContext = null;
    private Dialog mDialog = null;
    private ListView mPlaylistLv = null;

    private List<PlayListInfo> mList = new ArrayList<>();
    private PlayListAdapter mPlayListAdapter = null;
    private OnPickerListener mOnPickerListener = null;

    public PickerPlaylistDialog(Context context) {
        mContext = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_picker_playlist_dialog, null);

        mDialog = new CustomDialog.Builder(context)
                .setTitle(R.string.playlist_picker_title)
                .setContentView(view)
                .setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<Integer, Boolean> selectedItems = mPlayListAdapter.getSelectedItem();
                        for (int i=0; i < mList.size(); i++) {
                            if (selectedItems.get(i)) {
                                List<PlayListInfo> selectedPlayListInfo = new ArrayList<>();
                                selectedPlayListInfo.add(mList.get(i));
                                if (null != mOnPickerListener) {
                                    mOnPickerListener.onPicker(selectedPlayListInfo);
                                }
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).create();

        loadPlayList();
        mPlayListAdapter = new PlayListAdapter(context);
        mPlaylistLv = (ListView) view.findViewById(R.id.lv_playlist);
        mPlaylistLv.setAdapter(mPlayListAdapter);
        mPlaylistLv.setItemsCanFocus(true);
    }

    public void show() {
        loadPlayList();
        mDialog.show();
    }

    private void loadPlayList() {
        List<PlayListInfo> list = Setting.get(mContext).getPlayList();
        if (null != list && !list.isEmpty()) {
            mList.clear();
            mList.addAll(list);
            if (mPlayListAdapter != null) {
                mPlayListAdapter.clearSelected();
                mPlayListAdapter.notifyDataSetChanged();
            }
        }
    }

    private class PlayListAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;
        private HashMap<Integer, Boolean> mSelectedItems;

        public PlayListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mSelectedItems = new HashMap<Integer, Boolean>();
            clearSelected();
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

        public void clearSelected() {
            for (int i = 0; i < mList.size(); i++) {
                mSelectedItems.put(i, false);
            }
        }

        public void setSelectedItem(int i, boolean isSelected) {
            mSelectedItems.put(i, isSelected);
//            notifyDataSetChanged();
        }

        public HashMap<Integer, Boolean> getSelectedItem() {
            return mSelectedItems;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.select_item, null);
                holder = new ViewHolder();
                holder.mTitleTv = (TextView) view.findViewById(R.id.tv_title);
                holder.mCheckbox = (CheckBox) view.findViewById(R.id.checkbox);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.mTitleTv.setText(mList.get(i).getTitle());
            holder.mTitleTv.setSelected(mSelectedItems.get(i));
            holder.mCheckbox.setChecked(mSelectedItems.get(i));
            holder.mCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((CheckBox) view).isChecked()) {
                        setSelectedItem(i, true);
                    } else {
                        setSelectedItem(i, false);
                    }
                }
            });

            return view;
        }

        private final class ViewHolder {
            private TextView mTitleTv = null;
            private CheckBox mCheckbox = null;

        }
    }

    public void setOnPickerListener(OnPickerListener listener) {
        mOnPickerListener = listener;
    }

    public interface OnPickerListener {
        public void onPicker(List<PlayListInfo> playListInfo);
    }
}
