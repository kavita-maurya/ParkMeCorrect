package com.parkmecorrect;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;



public class CustomSpinner extends Spinner {
    public CustomSpinner(Context context) {
        super(context);
    }

    OnItemSelectedListener listener;
    int prevPos = -1;


    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (position == getSelectedItemPosition() && prevPos == position) {
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemPosition());
        }
        prevPos = position;
    }
}
