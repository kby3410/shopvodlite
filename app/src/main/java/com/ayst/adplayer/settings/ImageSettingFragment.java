package com.ayst.adplayer.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.expandablelayout.library.ExpandableLayout;
import com.ayst.adplayer.R;
import com.ayst.adplayer.common.BaseFragment;
import com.ayst.adplayer.data.FileInfo;
import com.ayst.adplayer.utils.SupportFileUtils;
import com.ayst.adplayer.dialogs.PickerFilesDialog;
import com.ayst.adplayer.dialogs.PickerPicturePlayDurationDialog;
import com.ayst.adplayer.dialogs.PickerPicturePlayEffectDialog;
import com.ayst.adplayer.view.XianHeiFontTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ImageSettingFragment extends BaseFragment {

    @BindView(R.id.set_duration)
    RelativeLayout mSetDurationView;
    @BindView(R.id.set_effect)
    RelativeLayout mSetEffectView;
    @BindView(R.id.set_music)
    ExpandableLayout mSetMusicView;
    Unbinder unbinder;
    @BindView(R.id.tv_duration)
    XianHeiFontTextView mDurationTv;
    @BindView(R.id.tv_effect)
    XianHeiFontTextView mEffectTv;

    private RelativeLayout mMusicItemHeader;
    private Button mAddMusicBtn;
    private Button mSaveMusicBtn;

    private List<FileInfo> mMusicList = new ArrayList<>();
    private MusicListAdapter mMusicListAdapter = null;

    private PickerPicturePlayDurationDialog mPickerDurationDialog = null;
    private PickerPicturePlayEffectDialog mPickerEffectDialog = null;
    private PickerFilesDialog mPickerMusicFilesDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_setting, container, false);
        unbinder = ButterKnife.bind(this, view);

        initView();
        return view;
    }

    private void initView() {
        mDurationTv.setText(Setting.get(mContext).getPictureDuration() + getString(R.string.sec));
        mEffectTv.setText(Setting.get(mContext).getPictureEffect());

        List<FileInfo> music = Setting.get(mContext).getPictureMusic();
        if (null != music) {
            mMusicList.addAll(music);
        }
        mMusicListAdapter = new MusicListAdapter(mContext);
        ListView musicLv = (ListView) mSetMusicView.findViewById(R.id.lv_music);
        musicLv.setAdapter(mMusicListAdapter);
        musicLv.setItemsCanFocus(true);
        mMusicItemHeader = (RelativeLayout) mSetMusicView.findViewById(R.id.header);
        mMusicItemHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSetMusicView.isOpened()) {
                    mSetMusicView.hide();
                } else {
                    mSetMusicView.show();
                }
            }
        });
        mAddMusicBtn = (Button) mSetMusicView.findViewById(R.id.btn_add);
        mAddMusicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPickerMusicFileDialog();
            }
        });
        mSaveMusicBtn = (Button) mSetMusicView.findViewById(R.id.btn_save);
        mSaveMusicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Setting.get(mContext).savePictureMusic(mMusicList);
                mSetMusicView.hide();
                mMusicItemHeader.requestFocus();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.set_duration, R.id.set_effect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.set_duration:
                showPickerDurationDialog();
                break;
            case R.id.set_effect:
                showPickerEffectDialog();
                break;
        }
    }

    private void showPickerDurationDialog() {
        if (null == mPickerDurationDialog) {
            mPickerDurationDialog = new PickerPicturePlayDurationDialog(mContext);
            mPickerDurationDialog.setOnPickerListener(new PickerPicturePlayDurationDialog.OnPickerListener() {
                @Override
                public void onPicker(int duration) {
                    mDurationTv.setText(duration+getString(R.string.sec));
                    Setting.get(mContext).savePictureDuration(duration);
                }
            });
        }
        mPickerDurationDialog.show();
    }

    private void showPickerEffectDialog() {
        if (null == mPickerEffectDialog) {
            mPickerEffectDialog = new PickerPicturePlayEffectDialog(mContext);
            mPickerEffectDialog.setOnPickerListener(new PickerPicturePlayEffectDialog.OnPickerListener() {
                @Override
                public void onPicker(String  effect) {
                    mEffectTv.setText(effect);
                    Setting.get(mContext).savePictureEffect(effect);
                }
            });
        }
        mPickerEffectDialog.show();
    }

    private void showPickerMusicFileDialog() {
        if (null == mPickerMusicFilesDialog) {
            mPickerMusicFilesDialog = new PickerFilesDialog(mContext, SupportFileUtils.SUPPORT_MUSIC_FILE_SUFFIX);
            mPickerMusicFilesDialog.setOnPickerListener(new PickerFilesDialog.OnPickerListener() {
                @Override
                public void onPicker(List<FileInfo> fileInfos) {
                    mMusicList.addAll(fileInfos);
                    mMusicListAdapter.notifyDataSetChanged();
                }
            });
        }
        mPickerMusicFilesDialog.show();
    }

    private class MusicListAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;
        private int mSelectedItem = -1;
        private Context mContext = null;

        public MusicListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mContext = context;
        }

        @Override
        public int getCount() {
            return mMusicList.size();
        }

        @Override
        public Object getItem(int i) {
            if (i < mMusicList.size()) {
                return mMusicList.get(i);
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public void setSelectedItem(int i) {
            mSelectedItem = i;
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.playlist_edit_item, null);
                holder = new ViewHolder();
                holder.mTitleTv = (TextView) view.findViewById(R.id.tv_title);
                holder.mMoveUpBtn = (ImageButton) view.findViewById(R.id.btn_up);
                holder.mMoveDownBtn = (ImageButton) view.findViewById(R.id.btn_down);
                holder.mDeleteBtn = (ImageButton) view.findViewById(R.id.btn_delete);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.mTitleTv.setText(mMusicList.get(i).getPath());
            holder.mTitleTv.setSelected(i == mSelectedItem);
            holder.mMoveUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (i > 0 && mMusicList.size() > 1) {
                        FileInfo fileInfo = mMusicList.get(i);
                        mMusicList.set(i, mMusicList.get(i - 1));
                        mMusicList.set(i - 1, fileInfo);
                        notifyDataSetChanged();
                    }
                }
            });
            holder.mMoveDownBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (i < mMusicList.size() - 1 && mMusicList.size() > 1) {
                        FileInfo fileInfo = mMusicList.get(i);
                        mMusicList.set(i, mMusicList.get(i + 1));
                        mMusicList.set(i + 1, fileInfo);
                        notifyDataSetChanged();
                    }
                }
            });
            holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMusicList.remove(i);
                    if (mMusicList.isEmpty()) {
                        mAddMusicBtn.requestFocus();
                    }
                    notifyDataSetChanged();
                }
            });

            return view;
        }

        private final class ViewHolder {
            private TextView mTitleTv = null;
            private ImageButton mMoveUpBtn = null;
            private ImageButton mMoveDownBtn = null;
            private ImageButton mDeleteBtn = null;
        }
    }
}
