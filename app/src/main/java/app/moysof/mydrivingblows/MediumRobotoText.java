package app.moysof.mydrivingblows;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MediumRobotoText extends TextView {

	public MediumRobotoText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MediumRobotoText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MediumRobotoText(Context context) {
		super(context);
		init();
	}

	private void init() {
        if (!isInEditMode()){
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/Roboto-Medium.ttf");
		    setTypeface(tf);
        }
	}

}