package com.moysof.mydrivingblows;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.moysof.mydrivingblows.FeedActivity.suggestionsTask;
import com.moysof.mydrivingblows.FeedActivity.updateTask;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class NewsActivity extends ActionBarActivity implements OnItemLongClickListener,
		ActionMode.Callback {

	private ProgressBar progressBar;
	private List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	private String newsURL = "http://mydrivingblows.com/app/news.php";
	private String result = "";
	private SharedPreferences preferences;
	private ListView list;
	private ArrayList<String> comment_ids = new ArrayList<String>();
	private ArrayList<String> comment_texts = new ArrayList<String>();
	private ArrayList<String> photos = new ArrayList<String>();
	private ArrayList<String> plate_nums = new ArrayList<String>();
	private ArrayList<String> plate_states = new ArrayList<String>();
	private NewsListViewAdapter adapter;
	private AdapterView<ListAdapter> drawerList;
	private DrawerLayout drawer;
	private int drawerPosition = 6;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerListViewAdapter drawerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		list = (ListView) findViewById(R.id.listview);
		adapter = new NewsListViewAdapter(this, getApplicationContext(),
				comment_texts, photos, plate_nums, plate_states);
		list.setAdapter(adapter);

		list.setOnItemClickListener(itemListener);

		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerAdapter = new DrawerListViewAdapter(this, drawerPosition);

		drawerList = (ListView) findViewById(R.id.left_drawer);
		drawerList.setAdapter(drawerAdapter);
		mDrawerToggle = new ActionBarDrawerToggle(this, drawer,
				R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getSupportActionBar().setTitle(R.string.title_activity_feed);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle(R.string.app_name);
				invalidateOptionsMenu();
			}
		};
		list.setOnItemLongClickListener(this);
		drawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				drawerPosition = position;
				if (preferences.getString("username", "").equals("")) {
					if (drawerPosition == 0) {
						startActivity(new Intent(NewsActivity.this,
								LoginActivity.class));
					}
				}
				if (position == 2 || position == 4) {
					drawerAdapter = new DrawerListViewAdapter(
							NewsActivity.this, drawerPosition);
					drawerList.setAdapter(adapter);
					drawerAdapter.notifyDataSetChanged();
					drawer.closeDrawer(drawerList);
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {

							if (drawerPosition == 2) {
								startActivity(new Intent(NewsActivity.this,
										FeedActivity.class));
							} else if (drawerPosition == 4) {
								startActivity(new Intent(NewsActivity.this,
										TagActivity.class));
							}
							finish();
						}
					}, 300);

				}
			}
		});
		drawer.setDrawerListener(mDrawerToggle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		new serverNewsTask().execute();
	}

	OnItemClickListener itemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
			Intent searchActivity = new Intent(NewsActivity.this,
					SearchActivity.class);
			searchActivity.putExtra("Tag", plate_nums.get(pos));
			searchActivity.putExtra("State", plate_states.get(pos));
			startActivity(searchActivity);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		if (preferences.getString("username", "").equals("")) {
			menu.findItem(R.id.action_logout).setVisible(false);
		} else {
			menu.findItem(R.id.action_logout).setVisible(true);
		}
		if (preferences.getBoolean("onload", false)) {
			menu.findItem(R.id.action_load).setChecked(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (drawer.isDrawerOpen(drawerList)) {
				drawer.closeDrawer(drawerList);
			} else {
				drawer.openDrawer(drawerList);
			}
			break;
		case R.id.action_contact:
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
					Uri.fromParts("mailto", "support@mydrivingblows.com", null));
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MyDrivingBlows");
			try {
				emailIntent.putExtra(
						Intent.EXTRA_TEXT,
						"[MyDrivingBlows App Version] - "
								+ getPackageManager().getPackageInfo(
										getPackageName(), 0).versionName
								+ "\n[Android Version] - "
								+ Build.VERSION.RELEASE + "\n\n\n");
			} catch (NameNotFoundException e1) {
			}
			startActivity(Intent.createChooser(emailIntent,
					"Send Us Email Via:"));
			break;
		case R.id.action_logout:
			Editor editor = preferences.edit();
			boolean usernameChanged = preferences.getBoolean("username_changed", false);
			editor.clear().commit();
			editor.putBoolean("username_changed", usernameChanged).commit();
			editor.putBoolean("agree", true).commit();
			startActivity(new Intent(NewsActivity.this, LoginActivity.class));
			break;
		case R.id.action_load:
			editor = preferences.edit();
			editor.putBoolean("onload", !item.isChecked());
			editor.commit();
			item.setChecked(!item.isChecked());
			if (item.isChecked()) {
				Toast.makeText(
						this,
						"\"Tag a Plate\" screen will be automatically launched next time",
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.action_about:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("About");
			builder.setIcon(R.drawable.sm_logo);
			View dialogView = new View(this);
			dialogView = getLayoutInflater().inflate(R.layout.dialog_about,
					null);
			((TextView) dialogView.findViewById(R.id.dialogWebText))
					.setText(Html.fromHtml("<a href=\""
							+ getResources().getString(R.string.website)
							+ "\">"
							+ getResources().getString(R.string.website)
							+ "</a>"));
			try {
				((TextView) dialogView.findViewById(R.id.dialogVersionText))
						.setText("Version: "
								+ getPackageManager().getPackageInfo(
										getPackageName(), 0).versionName);
			} catch (NameNotFoundException e) {
			}
			builder.setView(dialogView);
			builder.setNeutralButton("Close", null);
			builder.create().show();
			break;
		case R.id.action_terms:
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Terms of Service");
			dialogView = new View(this);
			dialogView = getLayoutInflater().inflate(R.layout.dialog_terms,
					null);
			builder.setView(dialogView);
			builder.setNeutralButton("Close", null);
			builder.create().show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	class serverNewsTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
			comment_texts.clear();
			comment_ids.clear();
			plate_states.clear();
			plate_nums.clear();
			photos.clear();
		}

		@Override
		protected Void doInBackground(Void... params) {
			nameValuePairs.clear();

			nameValuePairs.add(new BasicNameValuePair("email", preferences
					.getString("email", "")));
			nameValuePairs.add(new BasicNameValuePair("pwd", preferences
					.getString("password", "")));

			if (preferences.getBoolean("from_facebook", false)) {
				nameValuePairs
						.add(new BasicNameValuePair("from_facebook", "1"));
			}

			postToServer(nameValuePairs, newsURL);

			return null;
		}

		@Override
		protected void onPostExecute(Void param) {
			super.onPostExecute(param);
			progressBar.setVisibility(View.GONE);
			if (!isOnline()) {
				findViewById(R.id.internetLayout).setVisibility(View.VISIBLE);
			} else {
				findViewById(R.id.internetLayout).setVisibility(View.GONE);
				try {
					JSONObject jObject = new JSONObject(result);
					JSONArray comments = new JSONArray(
							jObject.getString("comments"));
					if (comments.length() > 0) {
						for (int i = 0; i < comments.length(); i++) {
							comment_texts.add(new JSONObject(""
									+ comments.get(i)).getString("comment"));
							comment_ids
									.add(new JSONObject("" + comments.get(i))
											.getString("comment_id"));
							plate_states
									.add(new JSONObject("" + comments.get(i))
											.getString("plate_state"));
							plate_nums.add(new JSONObject("" + comments.get(i))
									.getString("plate_num"));
							photos.add(new JSONObject("" + comments.get(i))
									.getString("photo"));
						}
						findViewById(R.id.noItemsText).setVisibility(View.GONE);
					} else {
						findViewById(R.id.noItemsText).setVisibility(
								View.VISIBLE);
					}
					adapter.notifyDataSetChanged();
				} catch (Exception e) {
					Log(e);
				}
			}
		}
	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return (cm.getActiveNetworkInfo() != null);
	}

	private InputStream is;
	private ActionMode mActionMode;
	private JSONArray removeArray;

	public void postToServer(List<? extends NameValuePair> nameValuePairs2,
			String Url) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Url);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs2));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {
			Log("Error in http connection: " + e.toString());
		}

		try {
			StringBuilder sb = null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			sb = new StringBuilder();
			sb.append(reader.readLine());
			String line = "0";
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			is.close();
			result = sb.toString();
			Log(result);
		} catch (Exception e) {
			Log("Error converting result " + e.toString());
		}
	}

	public void hideImage(View v) {
		v.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.delete, menu);
		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_delete:
			removeArray = new JSONArray();
			for (int i = 0; i < list.getChildCount(); i++) {
				if (((CheckBox) list.getChildAt(i).findViewById(R.id.checkBox))
						.isChecked()) {
					removeArray.put(comment_ids.get(i));
				}
			}
			if (removeArray.length() > 0) {
				new deletePlatesTask().execute();
			} else {
				mode.finish();
			}
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		adapter = new NewsListViewAdapter(NewsActivity.this,
				getApplicationContext(), comment_texts, photos, plate_nums,
				plate_states);
		list.setAdapter(adapter);
		list.setOnItemLongClickListener(this);
		list.setOnItemClickListener(itemListener);
		mActionMode = null;
	}

	private void Log(Object Object) {
		Log.d("Log", "" + Object);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (mActionMode != null) {
			return false;
		}
		mActionMode = this.startActionMode(this);
		list.setAdapter(new NewsListViewAdapterDelete(this,
				getApplicationContext(), comment_texts, photos, plate_nums,
				plate_states));
		list.setOnItemLongClickListener(null);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
				checkBox.setChecked(!checkBox.isChecked());
			}
		});
		return true;
	}

	class deletePlatesTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog serverProgressDialog;
		private String removeURL = "http://mydrivingblows.com/app/remove_comments.php";
		private String error;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			serverProgressDialog = new ProgressDialog(NewsActivity.this);
			serverProgressDialog.setMessage("Loading...");
			serverProgressDialog.setCancelable(false);
			serverProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("remove_array",
					removeArray.toString()));

			String result = null;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(removeURL);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();

				StringBuilder sb = null;
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				sb = new StringBuilder();
				sb.append(reader.readLine());
				String line = "0";
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				is.close();
				result = sb.toString();

				JSONObject jObject = new JSONObject(result);
				if (jObject.getString("result").equals("success")) {
					error = "";
				}
			} catch (Exception e) {
				Log("Server error: " + e.getMessage());
				error = result;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			serverProgressDialog.dismiss();
			if (error.equals("")) {
				mActionMode.finish();
				new serverNewsTask().execute();
				Toast.makeText(getApplicationContext(), "Removed!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "Error: " + error,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public void retry(View v) {
		findViewById(R.id.internetLayout).setClickable(false);
		ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(
				this, R.anim.flipping);
		anim.setTarget(findViewById(R.id.errorImage));
		anim.setDuration(2500);
		anim.start();
		anim.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if (!isOnline()) {
					findViewById(R.id.errorImage).startAnimation(
							AnimationUtils.loadAnimation(
									getApplicationContext(), R.anim.shake));
					((Vibrator) NewsActivity.this
							.getSystemService(Context.VIBRATOR_SERVICE))
							.vibrate(400);
				} else {
					findViewById(R.id.internetLayout).setVisibility(View.GONE);
					new serverNewsTask().execute();
				}
				findViewById(R.id.internetLayout).setClickable(true);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
	}

}
