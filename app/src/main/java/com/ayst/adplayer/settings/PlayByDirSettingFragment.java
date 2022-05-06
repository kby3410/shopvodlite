package com.ayst.adplayer.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ayst.adplayer.R;
import com.ayst.adplayer.common.BaseFragment;
import com.ayst.adplayer.data.FileInfo;
import com.ayst.adplayer.event.MessageEvent;
import com.ayst.adplayer.utils.GetFilesUtil;
import com.ayst.adplayer.utils.SupportFileUtils;
import com.ayst.adplayer.dialogs.CustomDialog;
import com.ayst.adplayer.view.XianHeiFontTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PlayByDirSettingFragment extends BaseFragment {
    private static final String TAG = "PlayByDirSetting";

    @BindView(R.id.lv_dir)
    ListView mDirLv;
    @BindView(R.id.btn_back)
    ImageButton mBackBtn;
    @BindView(R.id.tv_cur_dir)
    XianHeiFontTextView mCurDirTv;

    private Unbinder unbinder;
    private List<FileInfo> mList = new ArrayList<>();
    private DirListAdapter mDirListAdapter = null;
    private List<String> mRootPathList = new ArrayList<>();
    private String mCurSelectedPath = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play_by_dir_setting, container, false);
        unbinder = ButterKnife.bind(this, view);

        initView();
        initData();
        return view;
    }

    private void initView() {
        mDirListAdapter = new DirListAdapter(this.getActivity());
        mDirLv.setAdapter(mDirListAdapter);
        mDirLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        mDirLv.setItemsCanFocus(true);
    }

    private void initData() {
        try {
            loadFolderList(GetFilesUtil.ROOT_DIR);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (MessageEvent.MSG_PLAY_MODE_CHANGE.equals(event.getMessage())) {
            mDirListAdapter.notifyDataSetChanged();
        } else if (MessageEvent.MSG_USB_MOUNTED.equals(event.getMessage())
                || MessageEvent.MSG_USB_UNMOUNTED.equals(event.getMessage())) {
            try {
                loadFolderList(mCurDirTv.getText().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_back)
    public void onViewClicked() {
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

    private void loadFolderList(String folder) throws IOException {
        if (!TextUtils.isEmpty(folder)) {
            if (folder.equals(GetFilesUtil.ROOT_DIR)) {
                mList.clear();
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
                List<FileInfo> list = GetFilesUtil.getSonNode(folder, GetFilesUtil.TYPE_ALL, SupportFileUtils.SUPPORT_MEDIA_FILE_SUFFIX);
                if (list != null) {
                    Collections.sort(list, GetFilesUtil.defaultOrder());
                    mList.clear();
                    mList.addAll(list);
                } else {
                    mList.clear();
                }
            }
            if (mDirListAdapter != null) {
                mDirListAdapter.notifyDataSetChanged();
            }
            mCurDirTv.setText(folder);
            if (folder.equals(GetFilesUtil.ROOT_DIR)) {
                mBackBtn.setVisibility(View.GONE);
                mDirLv.requestFocus();
            } else {
                mBackBtn.setVisibility(View.VISIBLE);
            }
            if (mList.isEmpty()) {
                mBackBtn.requestFocus();
            }
        }
    }

    private void showNoFileDialog() {
        new CustomDialog.Builder(mContext).setMessage(R.string.no_video_confirm)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Setting.get(mContext).savePlayMode(Setting.PLAY_MODE_DIR,
                                0, mCurSelectedPath);
                        mDirListAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDirListAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        }).create().show();
    }

    private class DirListAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;
        private int mSelectedItem = -1;

        public DirListAdapter(Context context) {
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

        private boolean isSelected(FileInfo info) {
            if (Setting.PLAY_MODE_DIR == Setting.get(mContext).getPlayMode()) {
                String path = Setting.get(mContext).getSelectedPlayPath();
                if (info.getPath().equals(path)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.setting_dir_item, null);
                holder = new ViewHolder();
                holder.mTitleTv = (TextView) view.findViewById(R.id.tv_title);
                holder.mCheckbox = (CheckBox) view.findViewById(R.id.checkbox);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.mTitleTv.setText(mList.get(i).getName());
            holder.mTitleTv.setSelected(isSelected(mList.get(i)));
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
                holder.mCheckbox.setVisibility(View.VISIBLE);
            } else {
                holder.mCheckbox.setVisibility(View.GONE);
            }
            holder.mCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((CheckBox) view).isChecked()) {
                        mCurSelectedPath = mList.get(i).getPath();
                        if (SupportFileUtils.hasMediaFile(mCurSelectedPath)) {
                            Setting.get(mContext).savePlayMode(Setting.PLAY_MODE_DIR,
                                    0, mCurSelectedPath);
                            notifyDataSetChanged();
                        } else {
                            showNoFileDialog();
                        }
                    } else {
                        Setting.get(mContext).savePlayMode(Setting.PLAY_MODE_NONE,
                                0, mCurSelectedPath);
                    }
                }
            });
            holder.mCheckbox.setChecked(isSelected(mList.get(i)));

            return view;
        }

        private final class ViewHolder {
            private TextView mTitleTv = null;
            private CheckBox mCheckbox = null;

        }
    }
}
