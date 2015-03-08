package app.moysof.mydrivingblows;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities {

    public static final int CODE_TAKE_PHOTO = 0;
    public static final int CODE_PICK_PHOTO = 1;
    public static final int CODE_TAKE_VIDEO = 2;
    public static final int CODE_PICK_VIDEO = 3;

    public static final int TYPE_PHOTO = 0;
    public static final int TYPE_VIDEO = 1;

	public static int convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		int px = (int) (dp * (metrics.densityDpi / 160f));
		return px;
	}

	public static Boolean isLollipop() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
	}

}
