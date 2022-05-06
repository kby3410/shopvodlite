package com.ayst.adplayer.dialogs.editimagebox;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayst.adplayer.R;
import com.ayst.adplayer.data.ImageBoxData;
import com.ayst.adplayer.utils.ImageEffectUtils;
import com.ayst.adplayer.dialogs.CustomWideDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/28.
 */

public class EditImageBoxDialog {
    private static final String TAG = "EditImageBoxDialog";

    private Context mContext = null;
    private Dialog mDialog = null;
    private RecyclerView mImagesRecyclerView = null;
    private Spinner mImageIntervalSpn = null;
    private Spinner mImageEffectSpn = null;

    private ImagesAdapter mImagesAdapter = null;
    private OnPickerListener mOnPickerListener = null;
    private ImageBoxData mImageData = null;
    private int[] mImageIntervalArr = null;

    public EditImageBoxDialog(Context context, ImageBoxData imageData) {
        mContext = context;
        mImageData = imageData;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View mainView = layoutInflater.inflate(R.layout.layout_pick_images, null);
        View subView = layoutInflater.inflate(R.layout.layout_configuration_image_show, null);

        mDialog = new CustomWideDialog.Builder(context)
                .setMainContentView(mainView)
                .setSubContentView(subView)
                .setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (null != mOnPickerListener) {
                            List<String> selectedPictures = mImagesAdapter.getSelectedImages();
                            mImageData.setImages(selectedPictures);
                            mImageData.setInterval(mImageIntervalArr[mImageIntervalSpn.getSelectedItemPosition()]);
                            mImageData.setEffect((String) mImageEffectSpn.getSelectedItem());

                            mOnPickerListener.onPicker(mImageData);
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
        mDialog.setCancelable(false);

        mImageIntervalArr = mContext.getResources().getIntArray(R.array.image_interval);

        mImageIntervalSpn = (Spinner) subView.findViewById(R.id.spn_interval);
        mImageIntervalSpn.setAdapter(new ArrayAdapter<String>(mContext, R.layout.spinner_item,
                mContext.getResources().getStringArray(R.array.image_interval_label)));
        int position = 0;
        for(int i=0; i<mImageIntervalArr.length; i++ ){
            if(mImageIntervalArr[i] == mImageData.getInterval()){
                position = i;
                break;
            }
        }
        mImageIntervalSpn.setSelection(position);

        ArrayList<String> effects = ImageEffectUtils.getEffects();
        mImageEffectSpn = (Spinner) subView.findViewById(R.id.spn_effect);
        mImageEffectSpn.setAdapter(new ArrayAdapter<String>(mContext, R.layout.spinner_item,
                effects));
        position = 0;
        for(int i=0; i<effects.size(); i++ ){
            if(TextUtils.equals(effects.get(i), mImageData.getEffect())){
                position = i;
                break;
            }
        }
        mImageEffectSpn.setSelection(position);

        mImagesRecyclerView = (RecyclerView) mainView.findViewById(R.id.recycler_view);
        mImagesRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        mImagesAdapter = new ImagesAdapter(context);
        mImagesAdapter.setSelectedImages(mImageData.getImages());
        mImagesRecyclerView.setAdapter(mImagesAdapter);

        loadPictures();
    }

    public void show() {
        mDialog.show();
    }

    private void loadPictures() {
//        List<String> paths = GetFilesUtil.getStorageList(mContext);
//        for (String path : paths) {
//            List<FileInfo> files = GetFilesUtil.getSonNode(new File(path), GetFilesUtil.TYPE_FILE,
//                    SupportFileUtils.SUPPORT_PICTURE_FILE_SUFFIX, false);
//            if (null != files) {
//                Collections.sort(files, GetFilesUtil.defaultOrder());
//                mImagesAdapter.update(files);
//            }
//        }

        String[] projection = {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,// 路径
                MediaStore.Images.ImageColumns.WIDTH,// 宽
                MediaStore.Images.ImageColumns.HEIGHT,// 高
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,// 文件名
        };

        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, "", null,
                MediaStore.MediaColumns.DATE_ADDED + " DESC");

        List<ImageBean> imageBeans = new ArrayList<>();
        if (null != cursor && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                ImageBean bean = new ImageBean();
                bean.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)));
                bean.setWidth(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.WIDTH)));
                bean.setHeight(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.HEIGHT)));
                bean.setFileName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)));
                imageBeans.add(bean);
            } while (cursor.moveToNext());
        }
        mImagesAdapter.update(imageBeans);
    }

    public void setOnPickerListener(OnPickerListener listener) {
        mOnPickerListener = listener;
    }

    public interface OnPickerListener {
        public void onPicker(ImageBoxData imageData);
    }
}
