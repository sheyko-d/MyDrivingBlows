package app.moysof.mydrivingblows;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class RegularRobotoText extends TextView {

	public RegularRobotoText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RegularRobotoText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RegularRobotoText(Context context) {
		super(context);
		init();
	}

	private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/Roboto-Regular.ttf");
            setTypeface(tf);
        }
	}

}