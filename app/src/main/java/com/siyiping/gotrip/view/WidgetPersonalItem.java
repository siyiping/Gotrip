package com.siyiping.gotrip.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.siyiping.gotrip.R;


/**
 * Created by siyiping on 15/8/2.
 */
public class WidgetPersonalItem extends LinearLayout {

    private TextView mTextView;
    private ImageView mImageView;
    private TextView mHintTextView=null;
    private Resources mRes;

    private String mString;
    private String mHintString;

    public WidgetPersonalItem(Context context) {
        this(context, null);
    }


    public WidgetPersonalItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRes=context.getResources();
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.WidgetPersonalItem);
        mString=typedArray.getString(R.styleable.WidgetPersonalItem_text);
        mHintString=typedArray.getString(R.styleable.WidgetPersonalItem_text_hint);

        this.setPadding(25, 0, 25, 0);
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);
        this.setClickable(true);

        mTextView=new TextView(context,attrs);
        LinearLayout.LayoutParams textViewParams=new LinearLayout.LayoutParams(context,attrs);
        textViewParams.leftMargin = 100;
        mTextView.setLayoutParams(textViewParams);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setText(typedArray.getText(R.styleable.WidgetPersonalItem_text));
        mTextView.setClickable(false);


        mImageView=new ImageView(context,attrs);
        LinearLayout.LayoutParams imageViewParams=new LinearLayout.LayoutParams(context,attrs);
        imageViewParams.setMargins(0,0,0,0);
        mImageView.setLayoutParams(imageViewParams);
        mImageView.setImageDrawable(typedArray.getDrawable(R.styleable.WidgetPersonalItem_image_res));
        mImageView.setClickable(false);

        this.addView(mImageView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        this.addView(mTextView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        typedArray.recycle();

    }



}
