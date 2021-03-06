package app.moysof.mydrivingblows;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class PlateText extends TextView {

	public PlateText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PlateText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PlateText(Context context) {
		super(context);
		init();
	}

	private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                    "fonts/LicensePlate.ttf");
            setTypeface(tf);
        }
	}

}