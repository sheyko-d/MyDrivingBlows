package com.moysof.mydrivingblows;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class LauncherActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (!preferences.getBoolean("remember", true)){
			Editor editor = preferences.edit();
			editor.clear();
			editor.putBoolean("agree", true);
			editor.commit();
		}
		if (preferences.getBoolean("onload", false)) {
			startActivity(new Intent(LauncherActivity.this, TagActivity.class));
		} else {
			startActivity(new Intent(LauncherActivity.this, FeedActivity.class));
		}
		finish();
	}

}
