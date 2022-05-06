package com.ayst.adplayer.dialogs.editimagebox;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.ayst.adplayer.R;
import com.ayst.adplayer.data.FileInfo;
import com.ayst.adplayer.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shenhb on 18/10/27.
 */
public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.Holder> {
    private Context mContext;
    private List<ImageBean> mImages = new ArrayList<>();
    private List<String> mSelectedImages = new ArrayList<>();

    private float mItemWidth = 0;
    private float mItemHeight = 0;

    /**
     * @param context Context
     */
    public ImagesAdapter(Context context) {
        this.mContext = context;
        mItemWidth = context.getResources().getDimension(R.dimen.image_item_width);
        mItemHeight = context.getResources().getDimension(R.dimen.image_item_height);
    }

    public void update(List<ImageBean> images) {
        this.mImages.clear();
        this.mImages.addAll(images);
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(new ItemImageView(mContext));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.setData(getItem(position));
    }

    @Override
    public int getItemCount() {
        return this.mImages.size();
    }

    public List<String> getSelectedImages() {
        return mSelectedImages;
    }

    public void setSelectedImages(List<String> images) {
        if (null != images) {
            mSelectedImages.clear();
            mSelectedImages.addAll(images);
        }
    }

    public ImageBean getItem(int position) {
        return this.mImages.get(position);
    }

    class Holder extends RecyclerView.ViewHolder {
        private ItemImageView mItemImageView;

        public Holder(View itemView) {
            super(itemView);
            mItemImageView = (ItemImageView) itemView;
        }

        public void setData(final ImageBean imageBean) {
            mItemImageView.setLayoutParams(new FrameLayout.LayoutParams((int)mItemWidth, (int)mItemHeight));
            mItemImageView.loadData(imageBean);

            if (mSelectedImages.contains(imageBean.getPath())) {
                mItemImageView.setSelected(true);
            } else {
                mItemImageView.setSelected(false);
            }

            final String path = imageBean.getPath();
            mItemImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mSelectedImages.contains(path)) {
                        mSelectedImages.remove(path);
                        mItemImageView.setSelected(false);
                    } else {
                        mSelectedImages.add(path);
                        mItemImageView.setSelected(true);
                    }
                }
            });
        }
    }
}
