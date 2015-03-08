package app.moysof.mydrivingblows;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import uk.co.senab.photoview.PhotoViewAttacher;

public class FullscreenImageActivity extends ActionBarActivity {

	private ActionBar mActionBar;
	private Toolbar mToolbar;
	private String mImage;
	private ImageView mFullscreenImg;
	private PhotoViewAttacher mAttacher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_image);

		mToolbar = (Toolbar) findViewById(R.id.mytoolbar);
		mToolbar.setContentInsetsAbsolute(
				CommonUtilities.convertDpToPixel(72, this), 0);
		setSupportActionBar(mToolbar);

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setTitle("");

		mImage = getIntent().getStringExtra("image");

		// Work-around, while there are no multiple images

		mFullscreenImg = (ImageView) findViewById(R.id.productFullscreenImg);
		mAttacher = new PhotoViewAttacher(mFullscreenImg);
		mAttacher.setMaximumScale(20);

		// Create global configuration and initialize ImageLoader with this
		// config
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisk(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(defaultOptions).build();
		ImageLoader.getInstance().init(config);
		ImageLoader.getInstance().displayImage(mImage, mFullscreenImg,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap arg2) {
						findViewById(R.id.productFullscreenProgressBar)
								.setVisibility(View.GONE);
						mAttacher.update();
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
					}
				});

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == android.R.id.home) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
