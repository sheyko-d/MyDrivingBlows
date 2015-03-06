package com.moysof.mydrivingblows;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class MediumRobotoButton extends Button {

    public MediumRobotoButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MediumRobotoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediumRobotoButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/Roboto-Medium.ttf");
            setTypeface(tf);
        }
    }

}