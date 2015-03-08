package app.moysof.mydrivingblows;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class BoldRobotoText extends TextView {

	public BoldRobotoText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public BoldRobotoText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BoldRobotoText(Context context) {
		super(context);
		init();
	}

	private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/Roboto-Bold.ttf");
            setTypeface(tf);
        }
	}

}