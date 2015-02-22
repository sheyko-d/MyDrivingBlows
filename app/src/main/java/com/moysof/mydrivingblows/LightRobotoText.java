package com.moysof.mydrivingblows;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class LightRobotoText extends TextView {

	public LightRobotoText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public LightRobotoText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LightRobotoText(Context context) {
		super(context);
		init();
	}

	private void init() {
		Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
				"Roboto-Light.ttf");
		setTypeface(tf);
	}

}