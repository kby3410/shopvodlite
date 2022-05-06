package com.ayst.adplayer.settings;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.expandablelayout.library.ExpandableLayoutItem;
import com.andexert.expandablelayout.library.ExpandableLayoutListView;
import com.ayst.adplayer.R;
import com.ayst.adplayer.common.BaseFragment;
import com.ayst.adplayer.data.ShareHostInfo;
import com.ayst.adplayer.event.MessageEvent;
import com.ayst.adplayer.utils.IPAddressValidator;
import com.ayst.adplayer.view.XianHeiFontTextView;
import com.kyleduo.switchbutton.SwitchButton;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class PlayByShareSettingFragment extends BaseFragment {
    private static final String TAG = "PlayByShareSetting";

    private static final int MSG_CONNECT_SUCCESS = 1000;
    private static final int MSG_CONNECT_FAILED = 1001;
    private static final int MSG_LOAD_COMPLETE = 1003;

    @BindView(R.id.lv_host)
    ExpandableLayoutListView mHostLv;
    @BindView(R.id.btn_add)
    Button mAddBtn;
    @BindView(R.id.host_container)
    LinearLayout mHostContainer;
    @BindView(R.id.edt_host_address)
    EditText mHostAddressEdt;
    @BindView(R.id.btn_need_password)
    SwitchButton mNeedPasswordBtn;
    @BindView(R.id.edt_user_name)
    EditText mUserNameEdt;
    @BindView(R.id.edt_password)
    EditText mPasswordEdt;
    @BindView(R.id.btn_cancel)
    Button mCancelBtn;
    @BindView(R.id.btn_save)
    Button mSaveBtn;
    @BindView(R.id.edit_container)
    LinearLayout mEditContainer;
    Unbinder unbinder;
    @BindView(R.id.lv_dir)
    ListView mDirLv;
    @BindView(R.id.btn_back)
    ImageButton mBackBtn;
    @BindView(R.id.tv_cur_dir)
    XianHeiFontTextView mCurDirTv;

    private int mCurEditIndex = -1;
    private List<ShareHostInfo> mList = new ArrayList<>();
    private HostListAdapter mHostListAdapter = null;

    private SmbFile mCurSmbFile;
    private List<SmbFile> mFileList = new ArrayList<>();
    private DirListAdapter mFileListAdapter = null;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CONNECT_SUCCESS: {
                    Bundle bundle = msg.getData();
                    ShareHostInfo host = (ShareHostInfo) bundle.getSerializable("host");
                    if (mCurEditIndex >= 0 && mCurEditIndex < mList.size()) {
                        mList.set(mCurEditIndex, host);
                    } else {
                        mList.add(host);
                    }
                    mHostListAdapter.notifyDataSetChanged();
                    mHostContainer.setVisibility(View.VISIBLE);
                    mEditContainer.setVisibility(View.INVISIBLE);
                    mAddBtn.requestFocus();
                    Setting.get(mContext).saveHostList(mList);
                    break;
                }

                case MSG_CONNECT_FAILED: {
                    Bundle bundle = msg.getData();
                    String errorMsg = bundle.getString("error_msg");
                    Toast.makeText(mContext, mContext.getString(R.string.host_connect_failed) + errorMsg, Toast.LENGTH_SHORT).show();
                    break;
                }

                case MSG_LOAD_COMPLETE:
                    if (mFileListAdapter != null) {
                        mFileListAdapter.notifyDataSetChanged();
                    }
                    mCurDirTv.setText(mCurSmbFile.getUncPath());
                    mBackBtn.setVisibility(View.VISIBLE);
                    if (mFileList.isEmpty()) {
                        mBackBtn.requestFocus();
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play_by_share_setting, container, false);
        unbinder = ButterKnife.bind(this, view);

        initView();
        return view;
    }

    private void initView() {
        List<ShareHostInfo> list = Setting.get(mContext).getHostList();
        if (null != list && !list.isEmpty()) {
            mList.clear();
            mList.addAll(list);
        }

        mHostListAdapter = new HostListAdapter(this.getActivity());
        mHostLv.setAdapter(mHostListAdapter);
        mHostLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
        mHostLv.setItemsCanFocus(true);

        mFileListAdapter = new DirListAdapter(this.getActivity());
        mDirLv.setAdapter(mFileListAdapter);
        mDirLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    SmbFile file = mFileList.get(i);
                    if (file.isDirectory()) {
                        loadFolderList(file);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mDirLv.setItemsCanFocus(true);

        mNeedPasswordBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUserNameEdt.setEnabled(isChecked);
                mUserNameEdt.setFocusable(isChecked);
                mPasswordEdt.setEnabled(isChecked);
                mPasswordEdt.setFocusable(isChecked);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (MessageEvent.MSG_PLAY_MODE_CHANGE.equals(event.getMessage())) {
            mFileListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void loadFolderList(ShareHostInfo host) {
        if (host.getNeedPassword()) {
            loadFolderList("smb://" + host.getUsername() + ":" + host.getPassword() + "@" + host.getAddress() + "/");
        } else {
            loadFolderList("smb://guest:@" + host.getAddress() + "/");
        }
    }

    private void loadFolderList(String path) {
        try {
            loadFolderList(new SmbFile(path));
        } catch (IOException e) {
            Message msg = Message.obtain(mHandler, MSG_CONNECT_FAILED);
            Bundle bundle = new Bundle();
            bundle.putSerializable("error_msg", e.getMessage());
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    private void loadFolderList(final SmbFile smbFile) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SmbFile[] files = smbFile.listFiles();
                    mCurSmbFile = smbFile;
                    mFileList.clear();
                    for (SmbFile file : files) {
                        if (!file.getPath().endsWith("$/")) {
                            mFileList.add(file);
                        }
                    }
                    Message msg = Message.obtain(mHandler, MSG_LOAD_COMPLETE);
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    Message msg = Message.obtain(mHandler, MSG_CONNECT_FAILED);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("error_msg", e.getMessage());
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    private void cleanEditView() {
        mHostAddressEdt.setText("");
        mNeedPasswordBtn.setChecked(true);
        mUserNameEdt.setText("");
        mPasswordEdt.setText("");
        mUserNameEdt.setEnabled(true);
        mPasswordEdt.setEnabled(true);
    }

    @OnClick({R.id.btn_add, R.id.btn_cancel, R.id.btn_save, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                mCurEditIndex = -1;
                cleanEditView();
                mHostContainer.setVisibility(View.INVISIBLE);
                mEditContainer.setVisibility(View.VISIBLE);
                mHostAddressEdt.requestFocus();
                break;
            case R.id.btn_cancel:
                mHostContainer.setVisibility(View.VISIBLE);
                mEditContainer.setVisibility(View.INVISIBLE);
                mAddBtn.requestFocus();
                break;
            case R.id.btn_save:
                ShareHostInfo info = new ShareHostInfo();
                info.setAddress(mHostAddressEdt.getText().toString());
                info.setUsername(mUserNameEdt.getText().toString());
                info.setPassword(mPasswordEdt.getText().toString());
                info.setNeedPassword(mNeedPasswordBtn.isChecked());

                if (!IPAddressValidator.validate(info.getAddress())) {
                    Toast.makeText(mContext, R.string.ip_invalid, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (info.getNeedPassword()) {
                    if (TextUtils.isEmpty(info.getUsername())) {
                        Toast.makeText(mContext, R.string.username_hint, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(info.getPassword())) {
                        Toast.makeText(mContext, R.string.password_hint, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                connectHost(info);
                break;

            case R.id.btn_back:
                if(mCurSmbFile == null ){
                    return;
                }
                String parent = mCurSmbFile.getParent();
                Log.i(TAG, "onViewClicked, back button click parent: " + parent);
                if (parent.equals("smb://")) {
                    mBackBtn.setVisibility(View.GONE);
                    mCurDirTv.setText(parent);
                    mDirLv.setVisibility(View.INVISIBLE);
                    mHostContainer.setVisibility(View.VISIBLE);
                    mHostLv.requestFocus();
                } else {
                    loadFolderList(parent);
                }
                break;

        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP
                && event.getAction() == KeyEvent.ACTION_DOWN
                && !mNeedPasswordBtn.isChecked()) {
            View focusedView = mEditContainer.findFocus();
            if (focusedView.getId() == R.id.btn_cancel
                    || focusedView.getId() == R.id.btn_save) {
                mNeedPasswordBtn.requestFocus();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void connectHost(final ShareHostInfo host) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SmbFile smbfile;
                    if (host.getNeedPassword()) {
                        smbfile = new SmbFile("smb://" + host.getUsername() + ":" + host.getPassword() + "@" + host.getAddress() + "/");
                    } else {
                        smbfile = new SmbFile("smb://guest:@" + host.getAddress() + "/");
                    }
                    smbfile.list();
                    Message msg = Message.obtain(mHandler, MSG_CONNECT_SUCCESS);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("host", host);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    Log.e(TAG, "connect smb:" + host.getAddress() + " exception->" + e.getMessage());
                    Message msg = Message.obtain(mHandler, MSG_CONNECT_FAILED);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("error_msg", e.getMessage());
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                }
            }
        }).start();

    }

    private class HostListAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;
        private Context mContext = null;

        public HostListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mContext = context;
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

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.setting_host_item, null);
                holder = new ViewHolder();
                holder.mExpandableMenu = (ExpandableLayoutItem) view.findViewById(R.id.expanded_menu);
                holder.mTitleTv = (TextView) view.findViewById(R.id.tv_title);
                holder.mEnterBtn = (ImageButton) view.findViewById(R.id.btn_enter);
                holder.mEditBtn = (Button) view.findViewById(R.id.btn_edit);
                holder.mDeleteBtn = (Button) view.findViewById(R.id.btn_delete);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.mTitleTv.setText(mList.get(i).getAddress());
            holder.mTitleTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.mExpandableMenu.isOpened()) {
                        holder.mExpandableMenu.hide();
                    } else {
                        holder.mExpandableMenu.show();
                    }
                }
            });
            holder.mEnterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadFolderList(mList.get(i));
                    mHostContainer.setVisibility(View.INVISIBLE);
                    mDirLv.setVisibility(View.VISIBLE);
                    mBackBtn.setVisibility(View.VISIBLE);
                    mBackBtn.requestFocus();
                }
            });
            holder.mEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mHostAddressEdt.setText(mList.get(i).getAddress());
                    mNeedPasswordBtn.setChecked(mList.get(i).getNeedPassword());
                    if (mList.get(i).getNeedPassword()) {
                        mUserNameEdt.setEnabled(true);
                        mPasswordEdt.setEnabled(true);
                        mUserNameEdt.setText(mList.get(i).getUsername());
                        mPasswordEdt.setText(mList.get(i).getPassword());
                    } else {
                        mUserNameEdt.setEnabled(false);
                        mPasswordEdt.setEnabled(false);
                    }
                    mHostContainer.setVisibility(View.INVISIBLE);
                    mEditContainer.setVisibility(View.VISIBLE);
                    mHostAddressEdt.requestFocus();
                    mCurEditIndex = i;
                }
            });
            holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mList.remove(i);
                    notifyDataSetChanged();
                    Setting.get(mContext).saveHostList(mList);
                }
            });

            return view;
        }

        private final class ViewHolder {
            private ExpandableLayoutItem mExpandableMenu = null;
            private TextView mTitleTv = null;
            private ImageButton mEnterBtn = null;
            private Button mEditBtn = null;
            private Button mDeleteBtn = null;

        }
    }

    private class DirListAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;
        private int mSelectedItem = -1;

        public DirListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mFileList.size();
        }

        @Override
        public Object getItem(int i) {
            return mFileList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        private boolean isSelected(SmbFile file) {
            if (Setting.PLAY_MODE_SHARE == Setting.get(mContext).getPlayMode()) {
                String path = Setting.get(mContext).getSelectedPlayPathShare();
                if (file.getPath().equals(path)) {
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

            holder.mTitleTv.setText(mFileList.get(i).getName());
            holder.mTitleTv.setSelected(isSelected(mFileList.get(i)));
            holder.mTitleTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                SmbFile file = mFileList.get(i);
                                if (file.isDirectory()) {
                                    loadFolderList(file);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mFileList.get(i).isDirectory()) {
                            getActivity().runOnUiThread(new Runnable(){

                                @Override
                                public void run() {
                                    //更新UI
                                    holder.mCheckbox.setVisibility(View.VISIBLE);

                                }

                            });

                        } else {
                            getActivity().runOnUiThread(new Runnable(){

                                @Override
                                public void run() {
                                    //更新UI
                                    holder.mCheckbox.setVisibility(View.GONE);

                                }

                            });

                        }
                    } catch (SmbException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


            holder.mCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((CheckBox) view).isChecked()) {
                        Setting.get(mContext).savePlayMode(Setting.PLAY_MODE_SHARE,
                                0, mFileList.get(i).getPath());
                        notifyDataSetChanged();
                    } else {
                        Setting.get(mContext).savePlayMode(Setting.PLAY_MODE_NONE,
                                0, "");
                    }
                }
            });
            holder.mCheckbox.setChecked(isSelected(mFileList.get(i)));

            return view;
        }

        private final class ViewHolder {
            private TextView mTitleTv = null;
            private CheckBox mCheckbox = null;

        }
    }
}
