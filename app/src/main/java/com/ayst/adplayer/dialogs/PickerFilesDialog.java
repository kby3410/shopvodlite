package com.ayst.adplayer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ayst.adplayer.R;
import com.ayst.adplayer.data.FileInfo;
import com.ayst.adplayer.utils.GetFilesUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Administrator on 2018/3/28.
 */

public class PickerFilesDialog {
    private Dialog mDialog = null;
    private Context mContext = null;

    private ImageButton mBackBtn = null;
    private TextView mCurDirTv = null;
    private ListView mFilesLv = null;

    private ArrayList<FileInfo> mList = new ArrayList<>();
    private FileListAdapter mFileListAdapter = null;
    private List<String> mRootPathList = new ArrayList<>();
    private String[] mSuffixes;
    private OnPickerListener mOnPickerListener = null;

    public PickerFilesDialog(Context context, String[] suffixes) {
        mContext = context;
        mSuffixes = suffixes;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_picker_file_dialog, null);

        mDialog = new CustomDialog.Builder(context)
                .setTitle(R.string.file_picker_title)
                .setContentView(view)
                .setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<Integer, Boolean> selectedItems = mFileListAdapter.getSelectedItem();
                        for (int i=0; i < mList.size(); i++) {
                            if (selectedItems.get(i)) {
                                List<FileInfo> selectedFileInfo = new ArrayList<>();
                                selectedFileInfo.add(mList.get(i));
                                if (null != mOnPickerListener) {
                                    mOnPickerListener.onPicker(selectedFileInfo);
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

        mFilesLv = (ListView) view.findViewById(R.id.lv_files);
        mBackBtn = (ImageButton) view.findViewById(R.id.btn_back);
        mCurDirTv = (TextView) view.findViewById(R.id.tv_cur_dir);

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String folder = "";
                    String curPath = mCurDirTv.getText().toString();
                    for (String path : mRootPathList) {
                        if (path.equals(curPath)) {
                            folder = GetFilesUtil.ROOT_DIR;
                            break;
                        }
                    }
                    if (TextUtils.isEmpty(folder)) {
                        folder = GetFilesUtil.getParentPath(curPath);
                    }
                    loadFolderList(folder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mFilesLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    FileInfo fileInfo = mList.get(i);
                    if (fileInfo.isDir()) {
                        loadFolderList(fileInfo.getPath());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            loadFolderList(GetFilesUtil.ROOT_DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mFileListAdapter = new FileListAdapter(context);
        mFilesLv.setAdapter(mFileListAdapter);
        mFilesLv.setItemsCanFocus(true);
    }

    public void show() {
        if (mFileListAdapter != null) {
            mFileListAdapter.clearSelected();
            mFileListAdapter.notifyDataSetChanged();
        }
        mDialog.show();
    }

    private void loadFolderList(String file) throws IOException {
        if (!TextUtils.isEmpty(file)) {
            mList.clear();
            if (file.equals(GetFilesUtil.ROOT_DIR)) {
                List<String> pathList = GetFilesUtil.getStorageList(mContext);
                for (String path : pathList) {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setPath(path);
                    fileInfo.setName(new File(path).getName());
                    fileInfo.setDir(true);
                    mList.add(fileInfo);
                }
                mRootPathList.clear();
                mRootPathList.addAll(pathList);
            } else {
                List<FileInfo> list = GetFilesUtil.getSonNode(file, GetFilesUtil.TYPE_ALL, mSuffixes);
                if (list != null) {
                    Collections.sort(list, GetFilesUtil.defaultOrder());
                    mList.addAll(list);
                }
            }
            if (mFileListAdapter != null) {
                mFileListAdapter.clearSelected();
                mFileListAdapter.notifyDataSetChanged();
            }
            mCurDirTv.setText(file);
            if (file.equals(GetFilesUtil.ROOT_DIR)) {
                mBackBtn.setVisibility(View.GONE);
            } else {
                mBackBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    private class FileListAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;
        private HashMap<Integer, Boolean> mSelectedItems;

        public FileListAdapter(Context context) {
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
                view = mInflater.inflate(R.layout.setting_dir_item1, null);
                holder = new ViewHolder();
                holder.mTitleTv = (TextView) view.findViewById(R.id.tv_title);
                holder.mCheckbox = (CheckBox) view.findViewById(R.id.checkbox);
                holder.mImageView = (ImageView) view.findViewById(R.id.item_image);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.mTitleTv.setText(mList.get(i).getName());
            holder.mTitleTv.setSelected(mSelectedItems.get(i));
            holder.mImageView.setImageURI(mList.get(i).getUri());
            holder.mTitleTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        FileInfo fileInfo = mList.get(i);
                        if (fileInfo.isDir()) {
                            loadFolderList(fileInfo.getPath());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            if (mList.get(i).isDir()) {
                holder.mCheckbox.setVisibility(View.INVISIBLE);
            } else {
                holder.mCheckbox.setVisibility(View.VISIBLE);
            }
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
            private ImageView mImageView = null;

        }
    }

    public void setOnPickerListener(OnPickerListener listener) {
        mOnPickerListener = listener;
    }

    public interface OnPickerListener {
        public void onPicker(List<FileInfo> fileInfo);
    }
}
