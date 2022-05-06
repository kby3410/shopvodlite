package com.ayst.adplayer.dialogs.edittextbox;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.ayst.adplayer.R;
import com.ayst.adplayer.data.TextBoxData;
import com.ayst.adplayer.dialogs.CustomWideDialog;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/3/28.
 */

public class EditTextBoxDialog implements RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "EditTextBoxDialog";

    private Context mContext = null;
    private Dialog mDialog = null;
    private Spinner mTextSizeSpn = null;
    private Spinner mTextStyleSpn = null;
    private Spinner mScrollSpeedSpn = null;
    private RadioGroup mTextColorGroup = null;
    private RadioGroup mBgColorGroup = null;
    private EditText mEditText = null;

    private OnPickerListener mOnPickerListener = null;
    private TextBoxData mTextData = null;
    private int[] mTextSizeArr = null;
    private String[] mTextStyleArr = null;
    private int[] mScrollSpeedArr = null;

    private static HashMap<Integer, Integer> sColorsMap = new HashMap<>();
    private static HashMap<String, Typeface> sStyleMap = new HashMap<>();

    static {
        sColorsMap.put(R.id.rdo_color_11, 0xff09c3fd);
        sColorsMap.put(R.id.rdo_color_12, 0xff2167fc);
        sColorsMap.put(R.id.rdo_color_13, 0xff184fbf);
        sColorsMap.put(R.id.rdo_color_21, 0xff00e8ba);
        sColorsMap.put(R.id.rdo_color_22, 0xff00a684);
        sColorsMap.put(R.id.rdo_color_23, 0xff007a61);
        sColorsMap.put(R.id.rdo_color_31, 0xff00f744);
        sColorsMap.put(R.id.rdo_color_32, 0xff00bf33);
        sColorsMap.put(R.id.rdo_color_33, 0xff007c1e);
        sColorsMap.put(R.id.rdo_color_41, 0xfffdf942);
        sColorsMap.put(R.id.rdo_color_42, 0xfffdc42b);
        sColorsMap.put(R.id.rdo_color_43, 0xff90530f);
        sColorsMap.put(R.id.rdo_color_51, 0xffff427c);
        sColorsMap.put(R.id.rdo_color_52, 0xffc5315f);
        sColorsMap.put(R.id.rdo_color_53, 0xff972447);
        sColorsMap.put(R.id.rdo_color_61, 0xffff4cfb);
        sColorsMap.put(R.id.rdo_color_62, 0xffc73ac5);
        sColorsMap.put(R.id.rdo_color_63, 0xff922890);
        sColorsMap.put(R.id.rdo_color_71, 0xffffffff);
        sColorsMap.put(R.id.rdo_color_72, 0xffcbcbcb);
        sColorsMap.put(R.id.rdo_color_73, 0xff8d8d8d);
        sColorsMap.put(R.id.rdo_color_74, 0xff474747);
        sColorsMap.put(R.id.rdo_color_75, 0xff383838);
        sColorsMap.put(R.id.rdo_color_76, 0xff1a1a1a);

        sStyleMap.put("normal", Typeface.defaultFromStyle(Typeface.NORMAL));
        sStyleMap.put("bold", Typeface.defaultFromStyle(Typeface.BOLD));
        sStyleMap.put("italic", Typeface.defaultFromStyle(Typeface.ITALIC));
        sStyleMap.put("bold_italic", Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
    }

    public EditTextBoxDialog(Context context, TextBoxData textData) {
        mContext = context;
        mTextData = textData;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View mainView = layoutInflater.inflate(R.layout.layout_edit_text, null);
        View subView = layoutInflater.inflate(R.layout.layout_configuration_text_show, null);

        mDialog = new CustomWideDialog.Builder(context)
                .setMainContentView(mainView)
                .setSubContentView(subView)
                .setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (null != mOnPickerListener) {
                            mTextData.setText(mEditText.getText().toString());
                            mTextData.setSize(mTextSizeArr[mTextSizeSpn.getSelectedItemPosition()]);
                            mTextData.setStyle((String) mTextStyleSpn.getSelectedItem());
                            mTextData.setScrollSpeed(mScrollSpeedArr[mScrollSpeedSpn.getSelectedItemPosition()]);

                            mOnPickerListener.onPicker(mTextData);
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

        mTextSizeArr = mContext.getResources().getIntArray(R.array.text_size);
        mTextStyleArr = mContext.getResources().getStringArray(R.array.text_style_label);
        mScrollSpeedArr = mContext.getResources().getIntArray(R.array.scroll_speed);

        // Text Size
        mTextSizeSpn = (Spinner) subView.findViewById(R.id.spn_text_size);
        mTextSizeSpn.setAdapter(new ArrayAdapter<String>(mContext, R.layout.spinner_item,
                mContext.getResources().getStringArray(R.array.text_size_label)));
        int position = 0;
        for(int i=0; i<mTextSizeArr.length; i++ ){
            if(mTextSizeArr[i] == mTextData.getSize()){
                position = i;
                break;
            }
        }
        mTextSizeSpn.setSelection(position);
        mTextSizeSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mEditText.setTextSize(mTextSizeArr[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Text Style
        mTextStyleSpn = (Spinner) subView.findViewById(R.id.spn_text_style);
        mTextStyleSpn.setAdapter(new ArrayAdapter<String>(mContext, R.layout.spinner_item,
                mContext.getResources().getStringArray(R.array.text_style_label)));
        position = 0;
        for(int i=0; i<mTextStyleArr.length; i++ ){
            if(TextUtils.equals(mTextStyleArr[i], mTextData.getStyle())){
                position = i;
                break;
            }
        }
        mTextStyleSpn.setSelection(position);
        mTextStyleSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mEditText.setTypeface(sStyleMap.get(mTextStyleArr[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Text Scroll Speed
        mScrollSpeedSpn = (Spinner) subView.findViewById(R.id.spn_scroll_speed);
        mScrollSpeedSpn.setAdapter(new ArrayAdapter<String>(mContext, R.layout.spinner_item,
                mContext.getResources().getStringArray(R.array.scroll_speed_label)));
        position = 0;
        for(int i=0; i<mScrollSpeedArr.length; i++ ){
            if(mScrollSpeedArr[i] == mTextData.getScrollSpeed()){
                position = i;
                break;
            }
        }
        mScrollSpeedSpn.setSelection(position);
        mScrollSpeedSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Edit Text
        mEditText = (EditText) mainView.findViewById(R.id.edit_text);
        mEditText.setText(mTextData.getText());
        mEditText.setTextSize(mTextData.getSize());
        mEditText.setTextColor((int) mTextData.getTextColor());
        mEditText.setBackgroundColor((int) mTextData.getBgColor());

        // Text Color
        mTextColorGroup = (RadioGroup) subView.findViewById(R.id.group_text_color);
        mTextColorGroup.setOnCheckedChangeListener(this);
        if (sColorsMap.containsValue((int) mTextData.getTextColor())) {
            mTextColorGroup.check(getKey(sColorsMap, (int) mTextData.getTextColor()));
        }

        // Text Background Color
        mBgColorGroup = (RadioGroup) subView.findViewById(R.id.group_text_bg_color);
        mBgColorGroup.setOnCheckedChangeListener(this);
        if (sColorsMap.containsValue((int) mTextData.getBgColor())) {
            mBgColorGroup.check(getKey(sColorsMap, (int) mTextData.getBgColor()));
        }

    }

    public void show() {
        mDialog.show();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int color = sColorsMap.get(checkedId);
        switch (group.getId()) {
            case R.id.group_text_color:
                mTextData.setTextColor(color);
                mEditText.setTextColor(color);
                break;
            case R.id.group_text_bg_color:
                mTextData.setBgColor(color);
                mEditText.setBackgroundColor(color);
                break;

        }
    }

    private static Integer getKey(HashMap<Integer, Integer> map, Integer value) {
        Integer key = 0;
        for (Integer getKey : map.keySet()) {
            if (map.get(getKey).equals(value)) {
                key = getKey;
            }
        }
        return key;
    }

    public void setOnPickerListener(OnPickerListener listener) {
        mOnPickerListener = listener;
    }

    public interface OnPickerListener {
        public void onPicker(TextBoxData textData);
    }
}
