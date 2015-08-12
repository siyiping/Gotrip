package com.siyiping.gotrip.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.siyiping.gotrip.R;

/**
 * Created by siyiping on 15/8/2.
 */
public class WidgetPersonalItem extends RelativeLayout {

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

        this.setPadding(20,0,15,0);
        this.setVerticalGravity(Gravity.CENTER_VERTICAL);

        mTextView=new TextView(context,attrs);
        LinearLayout.LayoutParams textViewParams=new LinearLayout.LayoutParams(context,attrs);
        textViewParams.gravity=Gravity.LEFT|Gravity.CENTER_VERTICAL;
        textViewParams.setMargins(30,0,0,0);
        mTextView.setLayoutParams(textViewParams);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setText(mRes.getString(R.string.personal_offline_map_manager));


        mImageView=new ImageView(context,attrs);
        LinearLayout.LayoutParams imageViewParams=new LinearLayout.LayoutParams(context,attrs);
        imageViewParams.setMargins(0,0,(int)mRes.getDimension(R.dimen.personal_fragment_item_image_margin_right),0);
        mImageView.setLayoutParams(imageViewParams);
        //mImageView.setImageDrawable(mRes.getDrawable(R.drawable.));

        this.addView(mTextView, LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        this.addView(mImageView,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        typedArray.recycle();

    }



}
