package com.ayst.adplayer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.ayst.adplayer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/28.
 */

public class PickerPicturePlayDurationDialog {
    private static final int[] DURATIONS = {3, 5, 10, 20, 30, 40, 50, 60};

    private Context mContext = null;
    private Dialog mDialog = null;
    private ListView mPlaylistLv = null;

    private List<String> mList = new ArrayList<>();
    private PlayListAdapter mPlayListAdapter = null;
    private OnPickerListener mOnPickerListener = null;

    public PickerPicturePlayDurationDialog(Context context) {
        mContext = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_picker_playlist_dialog, null);

        mDialog = new CustomDialog.Builder(context)
                .setTitle(R.string.duration_dialog_title)
                .setContentView(view)
                .setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedItem = mPlayListAdapter.getSelectedItem();
                        if (selectedItem >= 0 && selectedItem < DURATIONS.length) {
                            if (null != mOnPickerListener) {
                                mOnPickerListener.onPicker(DURATIONS[selectedItem]);
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

        mPlaylistLv = (ListView) view.findViewById(R.id.lv_playlist);
        mPlaylistLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPlayListAdapter.setSelectedItem(i);
            }
        });

        loadPlayList();
        mPlayListAdapter = new PlayListAdapter(context);
        mPlaylistLv.setAdapter(mPlayListAdapter);
        mPlaylistLv.setItemsCanFocus(true);
    }

    public void show() {
        mDialog.show();
    }

    private void loadPlayList() {
        mList.clear();
        for (int duration : DURATIONS) {
            mList.add(duration+mContext.getString(R.string.sec));
        }
    }

    private class PlayListAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;
        private int mSelectedItem = -1;

        public PlayListAdapter(Context context) {
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
            mSelectedItem = i;
            notifyDataSetChanged();
        }

        public int getSelectedItem() {
            return mSelectedItem;
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

            holder.mTitleTv.setText(mList.get(i));
            holder.mTitleTv.setSelected(i == mSelectedItem);
            holder.mCheckbox.setChecked(i == mSelectedItem);
            holder.mCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((CheckBox) view).isChecked()) {
                        setSelectedItem(i);
                    } else {
                        setSelectedItem(-1);
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
        public void onPicker(int duration);
    }
}
