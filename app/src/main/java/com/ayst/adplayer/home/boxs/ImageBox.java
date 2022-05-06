package com.ayst.adplayer.home.boxs;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.ayst.adplayer.R;
import com.ayst.adplayer.data.ImageBoxData;
import com.ayst.adplayer.data.BoxInfo;
import com.ayst.adplayer.utils.ImageEffectUtils;
import com.ayst.adplayer.view.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by shenhaibo on 2018/10/19.
 */

public class ImageBox extends BaseBox {

    private Context mContext;
    private Banner mBanner;
    private ImageBoxData mImageData;

    public ImageBox(Context context, BoxInfo data) {
        super(context, data);

        mContext = context;
        if (null == mData.getData()) {
            mImageData = new ImageBoxData();
        } else {
            mImageData = (ImageBoxData) mData.getData();
        }

        Log.i("BoxInfo", "mImageData: " + mImageData.toString());

        FrameLayout view = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.layout_image_box, this, true);

        initView(view);
    }

    protected void initView(View view) {
        super.initView(view);
        mBanner  = (Banner) view.findViewById(R.id.banner);
        mBanner.setBannerStyle(BannerConfig.NOT_INDICATOR);
        mBanner.setImageLoader(new GlideImageLoader());
        mBanner.setImages(getImages());
        mBanner.setBannerAnimation(ImageEffectUtils.getEffect(mImageData.getEffect()));
//        mBanner.setBannerTitles(titles);
        mBanner.isAutoPlay(true);
        mBanner.setDelayTime(mImageData.getInterval() * 1000);
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.start();
    }

    public void update(ImageBoxData imageData) {
        super.update(imageData);
        if (null != imageData) {
            this.mImageData = imageData;
            mBanner.update(getImages());
            mBanner.setDelayTime(mImageData.getInterval() * 1000);
            mBanner.setBannerAnimation(ImageEffectUtils.getEffect(mImageData.getEffect()));
        }
    }
    private List<Uri> getImages() {
        List<Uri> images = new ArrayList<>();
        for (String image : mImageData.getImages()) {
                images.add(Uri.fromFile(new File(image)));
        }
        return images;
    }
}
