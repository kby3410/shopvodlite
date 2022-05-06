package com.ayst.adplayer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ayst.adplayer.R;
import com.ayst.adplayer.home.FocusLayout;

/**
 * Create custom Dialog windows for your application
 * Custom dialogs rely on custom layouts with allow you to
 * create and use your own look & feel.
 */
public class CustomWideDialog extends Dialog {
    private Context mContext;
    private FocusLayout mFocusLayout;
    private FrameLayout mRootContainer;

    public CustomWideDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public CustomWideDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void show() {
        super.show();
        if (null != mRootContainer && null != mFocusLayout) {
            ViewTreeObserver observer = mRootContainer.getViewTreeObserver();
            observer.addOnGlobalFocusChangeListener(mFocusLayout);
        }
    }

    private void addFocusView(FrameLayout root) {
        if (null != root) {
            mRootContainer = root;
            mFocusLayout = new FocusLayout(mContext);
            mRootContainer.addView(mFocusLayout,
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
            ViewTreeObserver observer = mRootContainer.getViewTreeObserver();
            observer.addOnGlobalFocusChangeListener(mFocusLayout);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        mFocusLayout.enable(true);
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mFocusLayout.hide();
        mFocusLayout.enable(false);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

    /**
     * Helper class for creating a custom layout_custom_default_dialog
     */
    public static class Builder {
        private Context context;
        private String title;
        private String positiveButtonText;
        private String negativeButtonText;
        private View mainContentView;
        private View subContentView;
        private FocusLayout mFocusLayout;
        private FrameLayout mRootContainer;

        private OnClickListener
                positiveButtonClickListener,
                negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set a custom main content view for the Dialog.
         * If a message is set, the mainContentView is not
         * added to the Dialog...
         *
         * @param v
         * @return
         */
        public Builder setMainContentView(View v) {
            this.mainContentView = v;
            return this;
        }

        /**
         * Set a custom sub content view for the Dialog.
         * If a message is set, the mainContentView is not
         * added to the Dialog...
         *
         * @param v
         * @return
         */
        public Builder setSubContentView(View v) {
            this.subContentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         *
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it's listener
         *
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(int negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button text and it's listener
         *
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Create the custom layout_custom_default_dialog
         */
        public CustomWideDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // instantiate the layout_custom_default_dialog with the custom Theme
            final CustomWideDialog dialog = new CustomWideDialog(context,
                    R.style.Dialog);
            View layout = inflater.inflate(R.layout.layout_custom_wide_dialog, null);

            // add focus layout
            FrameLayout root = layout.findViewById(R.id.root_container);
            dialog.addFocusView(root);

            // add content view
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

            // set the title
            if (TextUtils.isEmpty(title)) {
                layout.findViewById(R.id.layout_title).setVisibility(View.GONE);
            } else {
                ((TextView) layout.findViewById(R.id.title)).setText(title);
            }

            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(
                                            dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }

            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    negativeButtonClickListener.onClick(
                                            dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }

            // set the main content view
            if (mainContentView != null) {
                // if no message set
                // add the mainContentView to the layout_custom_default_dialog body
                ((LinearLayout) layout.findViewById(R.id.content_main))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content_main))
                        .addView(mainContentView,
                                new LayoutParams(
                                        LayoutParams.WRAP_CONTENT,
                                        LayoutParams.WRAP_CONTENT));
            }

            // set the sub content view
            if (subContentView != null) {
                // if no message set
                // add the subContentView to the layout_custom_default_dialog body
                ((LinearLayout) layout.findViewById(R.id.content_sub))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content_sub))
                        .addView(subContentView,
                                new LayoutParams(
                                        LayoutParams.WRAP_CONTENT,
                                        LayoutParams.WRAP_CONTENT));
            }
            dialog.setContentView(layout);

            return dialog;
        }
    }
}