package com.ayst.adplayer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ayst.adplayer.R;


/**
 * Created by Administrator on 2018/3/28.
 */

public class PickerUrlDialog {
    private Dialog mDialog = null;
    private OnPickerListener mOnPickerListener = null;

    private EditText mUrlEdt;

    public PickerUrlDialog(final Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_picker_url_dialog, null);
        mUrlEdt = view.findViewById(R.id.edt_url);

        mDialog = new CustomDialog.Builder(context)
                .setTitle(R.string.url_picker_title)
                .setContentView(view)
                .setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = mUrlEdt.getText().toString();
                        if (!TextUtils.isEmpty(url) && url.contains("://")) {
                            if (null != mOnPickerListener) {
                                mOnPickerListener.onPicker(url);
                            }
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, R.string.url_invalid, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).create();
    }

    public void show() {
        mDialog.show();
    }

    public void setOnPickerListener(OnPickerListener listener) {
        mOnPickerListener = listener;
    }

    public interface OnPickerListener {
        public void onPicker(String url);
    }
}
