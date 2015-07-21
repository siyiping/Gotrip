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

    private TextView guide;
    private EditText input;

    public InputLayout(Context context){
        this(context,null);
    }

    public InputLayout(Context context,AttributeSet attrs){
        this(context,attrs,0);
    }

    public InputLayout(Context context,AttributeSet attrs,int defStyleAttr){
        super(context, attrs, defStyleAttr);

        this.setFocusable(true);
        this.requestFocus();

        TypedArray t=context.obtainStyledAttributes(attrs, R.styleable.InputLayout);

        guide=new TextView(context, attrs, defStyleAttr);
        input=new EditText(context, attrs, defStyleAttr);

        guide.setText(t.getString(R.styleable.InputLayout_text));
        input.setHint(t.getString(R.styleable.InputLayout_text_hint));

        this.addView(guide, (int)getResources().getDimension(R.dimen.widget_textview_width), ViewGroup.LayoutParams.MATCH_PARENT);
        input.setMaxLines(1);

        this.addView(input, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        input.setFocusable(true);
        input.requestFocus();
    }



}
