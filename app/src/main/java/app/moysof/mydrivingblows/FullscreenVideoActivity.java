package app.moysof.mydrivingblows;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class FullscreenVideoActivity extends AppCompatActivity {

	private VideoView mVideoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_fullscreen_video);
		
		mVideoView = (VideoView) findViewById(R.id.videoView);
		mVideoView.setVideoURI(Uri.parse(getIntent().getStringExtra("video")));
		mVideoView.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				findViewById(R.id.progressBar).setVisibility(View.GONE);
				MediaController mediaController = new MediaController(
						FullscreenVideoActivity.this);
				mVideoView.setMediaController(mediaController);
				mediaController.show();
			}
		});

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
	}

}
