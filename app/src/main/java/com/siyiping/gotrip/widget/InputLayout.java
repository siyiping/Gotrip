package com.siyiping.gotrip.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.siyiping.gotrip.R;

/**
 * Created by siyiping on 15/7/3.
 */
public class InputLayout extends LinearLayout {

    private String text;
    private String texthint;

    public InputLayout(Context context){
        this(context,null);
    }

    public InputLayout(Context context,AttributeSet attrs){
        this(context,attrs,0);
    }

    public InputLayout(Context context,AttributeSet attrs,int defStyleAttr){
        super(context, attrs, defStyleAttr);

        TypedArray t=context.obtainStyledAttributes(attrs, R.styleable.InputLayout);
        text=t.getString(R.styleable.InputLayout_text);
        texthint=t.getString(R.styleable.InputLayout_texthint);
        t.recycle();

        TextView guide=new TextView(context, attrs, defStyleAttr);
        EditText input=new EditText(context, attrs, defStyleAttr);

        guide.setText(text);
        this.addView(guide, (int) getResources().getDimension(R.dimen.widget_textview_width), ViewGroup.LayoutParams.MATCH_PARENT);

        input.setMaxLines(1);
        input.setHint(texthint);
        this.addView(input, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

    }



}
