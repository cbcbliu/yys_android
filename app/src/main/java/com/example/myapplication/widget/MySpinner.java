package com.example.myapplication.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;


/**
 * 自定义spinner，使得重复点击时能够再次触发监听点击事件
 */
@SuppressLint("AppCompatCustomView")
public class MySpinner extends Spinner {
//
    public MySpinner(Context context) {
        super(context);
    }

    public MySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public void setSelection(int position, boolean animate){
        boolean sameSelected = (position == getSelectedItemPosition());
        super.setSelection(position, animate);
        if (sameSelected){
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    @Override
    public void setSelection(int position){
        boolean sameSelected = (position == getSelectedItemPosition());
        super.setSelection(position);
        if (sameSelected){
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }


}
