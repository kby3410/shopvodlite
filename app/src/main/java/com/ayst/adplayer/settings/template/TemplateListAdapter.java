package com.ayst.adplayer.settings.template;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.ayst.adplayer.R;
import com.ayst.adplayer.data.FileInfo;
import com.ayst.adplayer.data.HomeTemplate;
import com.ayst.adplayer.dialogs.editimagebox.ItemImageView;
import com.ayst.adplayer.event.MessageEvent;
import com.ayst.adplayer.settings.Setting;
import com.ayst.adplayer.utils.AppUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenhb on 18/10/27.
 */
public class TemplateListAdapter extends RecyclerView.Adapter<TemplateListAdapter.Holder> {

    private Context mContext;
    private static int sItemWidth, sItemHeight;
    private List<HomeTemplate> mTemplates = new ArrayList<>();
    private HomeTemplate mSelectedTemplate = null;

    /**
     * @param mContext Context
     */
    public TemplateListAdapter(Context mContext) {
        this.mContext = mContext;
        if (AppUtils.isLandscape()) {
            sItemWidth = (int) mContext.getResources().getDimension(R.dimen.setting_content_width) / 2;
            sItemHeight = sItemWidth * 10 / 13;
        } else {
            sItemWidth = (int) mContext.getResources().getDimension(R.dimen.setting_content_width) / 3;
            sItemHeight = sItemWidth * 15 / 10;
        }
    }

    public void addData(List<HomeTemplate> templates) {
        this.mTemplates.addAll(templates);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.mTemplates.clear();
        notifyDataSetChanged();
    }

    public void setSelectedTemplate(HomeTemplate template) {
        mSelectedTemplate = template;
        notifyDataSetChanged();
    }

    public HomeTemplate getSelectedTemplate() {
        return mSelectedTemplate;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(new TemplatePreviewView(mContext));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.setData(getItem(position));
    }

    @Override
    public int getItemCount() {
        return this.mTemplates.size();
    }

    public HomeTemplate getItem(int position) {
        return this.mTemplates.get(position);
    }

    class Holder extends RecyclerView.ViewHolder {
        private TemplatePreviewView mTemplatePreviewView;

        public Holder(View itemView) {
            super(itemView);
            mTemplatePreviewView = (TemplatePreviewView) itemView;
        }

        public void setData(final HomeTemplate template) {
            mTemplatePreviewView.setLayoutParams(new FrameLayout.LayoutParams(sItemWidth, sItemHeight));
            mTemplatePreviewView.loadData(template, sItemWidth, sItemHeight-AppUtils.dp2px(mContext, 50));

            HomeTemplate selectedTemplate = getSelectedTemplate();
            if (null != selectedTemplate && selectedTemplate.getId() == template.getId()) {
                mTemplatePreviewView.setSelected(true);
            } else {
                mTemplatePreviewView.setSelected(false);
            }

            mTemplatePreviewView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSelectedTemplate(template);
                    Setting.get(mContext).saveCurHomeTemplate(template);
                    MessageEvent msg = new MessageEvent(MessageEvent.MSG_TEMPLATE_CHANGED);
                    EventBus.getDefault().post(msg);
                }
            });
        }
    }
}
