package app.moysof.mydrivingblows;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CondensedRegularRobotoText extends TextView {

	public CondensedRegularRobotoText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public CondensedRegularRobotoText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CondensedRegularRobotoText(Context context) {
		super(context);
		init();
	}

	private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/RobotoCondensed-Regular.ttf");
            setTypeface(tf);
        }
	}

}