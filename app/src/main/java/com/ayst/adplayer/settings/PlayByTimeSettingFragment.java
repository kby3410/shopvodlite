package com.ayst.adplayer.settings;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.andexert.expandablelayout.library.ExpandableLayoutItem;
import com.andexert.expandablelayout.library.ExpandableLayoutListView;
import com.ayst.adplayer.R;
import com.ayst.adplayer.common.BaseFragment;
import com.ayst.adplayer.data.PlanInfo;
import com.ayst.adplayer.data.PlanListInfo;
import com.ayst.adplayer.data.PlayListInfo;
import com.ayst.adplayer.event.MessageEvent;
import com.ayst.adplayer.dialogs.PickerPlaylistDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PlayByTimeSettingFragment extends BaseFragment {
    private static final String TAG = "PlayByTimeSetting";

    @BindView(R.id.lv_planlist)
    ExpandableLayoutListView mPlanlistLv;
    @BindView(R.id.btn_add)
    Button mAddBtn;
    Unbinder unbinder;
    @BindView(R.id.planlist_container)
    LinearLayout mPlanlistContainer;
    @BindView(R.id.edt_title)
    EditText mTitleEdt;
    @BindView(R.id.lv_edit)
    ListView mEditLv;
    @BindView(R.id.btn_cancel)
    Button mCancelBtn;
    @BindView(R.id.btn_save)
    Button mSaveBtn;
    @BindView(R.id.edit_container)
    LinearLayout mEditContainer;
    @BindView(R.id.btn_edit_add)
    Button mEditAddBtn;

    private int mCurEditIndex = -1;
    private int mCurTimePickerIndex = -1;
    private ArrayList<PlanInfo> mEditList = new ArrayList<>();
    private EditPlayListAdapter mEditPlayListAdapter = null;

    private List<PlanListInfo> mList = new ArrayList<>();
    private PlanListAdapter mPlanListAdapter = null;

    private PickerPlaylistDialog mPickerPlaylistDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play_by_time_setting, container, false);
        unbinder = ButterKnife.bind(this, view);

        initView();
        return view;
    }

    private void initView() {
        List<PlanListInfo> list = Setting.get(mContext).getPlanList();
        if (null != list && !list.isEmpty()) {
            mList.clear();
            mList.addAll(list);
        }

        mPlanListAdapter = new PlanListAdapter(this.getActivity());
        mPlanlistLv.setAdapter(mPlanListAdapter);
        mPlanlistLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
        mPlanlistLv.setItemsCanFocus(true);

        mEditPlayListAdapter = new EditPlayListAdapter(this.getActivity());
        mEditLv.setAdapter(mEditPlayListAdapter);
        mEditLv.setItemsCanFocus(true);
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
            mPlanListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_cancel, R.id.btn_save, R.id.btn_add, R.id.btn_edit_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                mPlanlistContainer.setVisibility(View.VISIBLE);
                mEditContainer.setVisibility(View.INVISIBLE);
                mAddBtn.requestFocus();
                break;
            case R.id.btn_save:
                PlanListInfo info = new PlanListInfo();
                info.setTitle(mTitleEdt.getText().toString());
                info.setPlanInfoList(mEditList);
                if (mCurEditIndex >= 0 && mCurEditIndex < mList.size()) {
                    mList.set(mCurEditIndex, info);
                } else {
                    mList.add(info);
                }
                mPlanListAdapter.notifyDataSetChanged();
                mPlanlistContainer.setVisibility(View.VISIBLE);
                mEditContainer.setVisibility(View.INVISIBLE);
                mAddBtn.requestFocus();
                Setting.get(mContext).savePlanList(mList);
                break;
            case R.id.btn_add:
                mCurEditIndex = -1;

                mEditList.clear();
                mEditPlayListAdapter.notifyDataSetChanged();
                mPlanlistContainer.setVisibility(View.INVISIBLE);
                mEditContainer.setVisibility(View.VISIBLE);
                mTitleEdt.requestFocus();
                break;

            case R.id.btn_edit_add:
                showPickerFilesDialog();
                break;
        }
    }

    private void showPickerFilesDialog() {
        if (null == mPickerPlaylistDialog) {
            mPickerPlaylistDialog = new PickerPlaylistDialog(mContext);
            mPickerPlaylistDialog.setOnPickerListener(new PickerPlaylistDialog.OnPickerListener() {
                @Override
                public void onPicker(List<PlayListInfo> playListInfos) {
                    for (PlayListInfo playList : playListInfos) {
                        PlanInfo planInfo = new PlanInfo();
                        planInfo.setPlaylistId(playList.getId());
                        mEditList.add(planInfo);
                    }
                    mEditPlayListAdapter.notifyDataSetChanged();
                    mCurTimePickerIndex = mEditList.size()-1;
                    showTimePikerDialog();
                }
            });
        }
        mPickerPlaylistDialog.show();
    }

    private void showTimePikerDialog() {
        TimePickerDialog tpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                mEditList.get(mCurTimePickerIndex).setHour(hourOfDay);
                mEditList.get(mCurTimePickerIndex).setMin(minute);
                mEditPlayListAdapter.notifyDataSetChanged();
            }
        }, true);
        tpd.show(getFragmentManager(), "timepickerdialog");
    }

    private class PlanListAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;
        private int mSelectedItem = -1;
        private Context mContext = null;

        public PlanListAdapter(Context context) {
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

        private boolean isSelected(int index) {
            if (Setting.PLAY_MODE_PLAN == Setting.get(mContext).getPlayMode()) {
                int selectedIndex = Setting.get(mContext).getSelectedPlanListIndex();
                if (selectedIndex == index) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.setting_playlist_item, null);
                holder = new ViewHolder();
                holder.mExpandableMenu = (ExpandableLayoutItem) view.findViewById(R.id.expanded_menu);
                holder.mTitleTv = (TextView) view.findViewById(R.id.tv_title);
                holder.mCheckbox = (CheckBox) view.findViewById(R.id.checkbox);
                holder.mFileList = (ListView) view.findViewById(R.id.lv_playlist);
                holder.mEditBtn = (Button) view.findViewById(R.id.btn_edit);
                holder.mDeleteBtn = (Button) view.findViewById(R.id.btn_delete);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.mTitleTv.setText(mList.get(i).getTitle());
            holder.mTitleTv.setSelected(isSelected(i));
            holder.mTitleTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(holder.mExpandableMenu.isOpened()) {
                        holder.mExpandableMenu.hide();
                    } else {
                        holder.mExpandableMenu.show();
                    }
                }
            });
            holder.mCheckbox.setChecked(isSelected(i));
            holder.mCheckbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((CheckBox) view).isChecked()) {
                        Setting.get(mContext).savePlayMode(Setting.PLAY_MODE_PLAN, i, "");
                        notifyDataSetChanged();
                    } else {
                        Setting.get(mContext).savePlayMode(Setting.PLAY_MODE_NONE,
                                0, "");
                    }
                }
            });
            holder.mEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mEditList.clear();
                    mEditList.addAll(mList.get(i).getPlanInfoList());
                    mEditPlayListAdapter.notifyDataSetChanged();
                    mPlanlistContainer.setVisibility(View.INVISIBLE);
                    mEditContainer.setVisibility(View.VISIBLE);
                    mTitleEdt.setText(mList.get(i).getTitle());
                    mTitleEdt.requestFocus();
                    mCurEditIndex = i;
                }
            });
            holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mList.remove(i);
                    notifyDataSetChanged();
                    Setting.get(mContext).savePlanList(mList);
                }
            });

            holder.mFileList.setAdapter(new PlayListAdapter(mContext, mList.get(i).getPlanInfoList()));
            holder.mFileList.setFocusable(false);
            setListViewHeightBasedOnChildren(holder.mFileList);
            return view;
        }

        private final class ViewHolder {
            private ExpandableLayoutItem mExpandableMenu = null;
            private TextView mTitleTv = null;
            private CheckBox mCheckbox = null;
            private ListView mFileList = null;
            private Button mEditBtn = null;
            private Button mDeleteBtn = null;
        }

        private class PlayListAdapter extends BaseAdapter {
            private LayoutInflater mInflater = null;
            private int mSelectedItem = -1;
            private ArrayList<PlanInfo> mPlanList = new ArrayList<>();

            public PlayListAdapter(Context context, ArrayList<PlanInfo> list) {
                mInflater = LayoutInflater.from(context);
                mPlanList.addAll(list);
            }

            @Override
            public int getCount() {
                return mPlanList.size();
            }

            @Override
            public Object getItem(int i) {
                return mPlanList.get(i);
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
                    view = mInflater.inflate(R.layout.file_list_item, null);
                    holder = new ViewHolder();
                    holder.mTitleTv = (TextView) view.findViewById(R.id.tv_title);
                    view.setTag(holder);
                } else {
                    holder = (ViewHolder) view.getTag();
                }

                PlayListInfo playListInfo = Setting.get(mContext).getPlayListById(mPlanList.get(i).getPlaylistId());
                holder.mTitleTv.setText(null != playListInfo ? playListInfo.getTitle() : "unknow");
                holder.mTitleTv.setSelected(i == mSelectedItem);

                return view;
            }

            private final class ViewHolder {
                private TextView mTitleTv = null;

            }
        }
    }

    private class EditPlayListAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;
        private int mSelectedItem = -1;
        private Context mContext = null;

        public EditPlayListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mContext = context;
        }

        @Override
        public int getCount() {
            return mEditList.size();
        }

        @Override
        public Object getItem(int i) {
            if (i < mEditList.size()) {
                return mEditList.get(i);
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
                view = mInflater.inflate(R.layout.plan_edit_item, null);
                holder = new ViewHolder();
                holder.mTimeTv = (TextView) view.findViewById(R.id.tv_time);
                holder.mTitleTv = (TextView) view.findViewById(R.id.tv_title);
                holder.mMoveUpBtn = (ImageButton) view.findViewById(R.id.btn_up);
                holder.mMoveDownBtn = (ImageButton) view.findViewById(R.id.btn_down);
                holder.mDeleteBtn = (ImageButton) view.findViewById(R.id.btn_delete);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.mTimeTv.setText(mEditList.get(i).getTime());
            holder.mTimeTv.setSelected(i == mSelectedItem);
            holder.mTimeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCurTimePickerIndex = i;
                    showTimePikerDialog();
                }
            });

            PlayListInfo playListInfo = Setting.get(mContext).getPlayListById(mEditList.get(i).getPlaylistId());
            holder.mTitleTv.setText(null != playListInfo ? playListInfo.getTitle() : "unknow");
            holder.mTitleTv.setSelected(i == mSelectedItem);

            holder.mMoveUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (i > 0 && mEditList.size() > 1) {
                        PlanInfo planInfo = mEditList.get(i);
                        mEditList.set(i, mEditList.get(i-1));
                        mEditList.set(i-1, planInfo);
                        notifyDataSetChanged();
                    }
                }
            });
            holder.mMoveDownBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (i < mEditList.size()-1 && mEditList.size() > 1) {
                        PlanInfo planInfo = mEditList.get(i);
                        mEditList.set(i, mEditList.get(i+1));
                        mEditList.set(i+1, planInfo);
                        notifyDataSetChanged();
                    }
                }
            });

            holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mEditList.remove(i);
                    notifyDataSetChanged();
                }
            });

            return view;
        }

        private final class ViewHolder {
            private TextView mTimeTv = null;
            private TextView mTitleTv = null;
            private ImageButton mMoveUpBtn = null;
            private ImageButton mMoveDownBtn = null;
            private ImageButton mDeleteBtn = null;

        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
