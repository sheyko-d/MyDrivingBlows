package com.moysof.mydrivingblows;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class FeedActivity extends ActionBarActivity {

	private SwipeRefreshLayout mPullToRefreshLayout;
	private String feedURL = "http://mydrivingblows.com/app/feed.php";
	private String suggestionsURL = "http://mydrivingblows.com/app/suggestions.php";
	private ImageLoader imgLoader;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout drawer;
	private ListView drawerList;
	private int drawerPosition = 2;
	private ImageView worstImage;
	public String worstNum;
	public String worstState;
	private ImageView worstImage2;
	public String worstNum2;
	public String worstState2;
	private DrawerListViewAdapter adapter;
	private DisplayImageOptions options;
	public JSONArray commentsArray;
	public JSONArray suggestionsJSON;
	public GridViewSuggestionsAdapter suggestionsAdapter;
	public JSONArray originalSuggestionsJSON;
	private SharedPreferences preferences;
	public static FeedActivity feedActivity;
	public static String searchQuery = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		setContentView(R.layout.activity_feed);


		if (!FirstLaunch.isTrue) {
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}

		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		adapter = new DrawerListViewAdapter(this, drawerPosition);

		drawerList = (ListView) findViewById(R.id.left_drawer);
		drawerList.setAdapter(adapter);
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
		drawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				drawerPosition = position;
				if (preferences.getString("username", "").equals("")) {
					if (drawerPosition == 0) {
						startActivity(new Intent(FeedActivity.this,
								LoginActivity.class));
					} else if (drawerPosition == 4) {
						Toast.makeText(FeedActivity.this,
								"You must log in first", Toast.LENGTH_LONG)
								.show();
						startActivity(new Intent(FeedActivity.this,
								LoginActivity.class));
						finish();
						return;
					} else if (drawerPosition == 6) {
						return;
					}
				}
				if (position == 4 || position == 6) {
					adapter = new DrawerListViewAdapter(FeedActivity.this,
							drawerPosition);
					drawerList.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					drawer.closeDrawer(drawerList);
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							if (drawerPosition == 4) {
								startActivity(new Intent(FeedActivity.this,
										TagActivity.class));
							} else if (drawerPosition == 6) {
								startActivity(new Intent(FeedActivity.this,
										NewsActivity.class));
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

		mPullToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.ptr_layout);
		worstImage = (ImageView) findViewById(R.id.worstImage);
		worstImage2 = (ImageView) findViewById(R.id.worstImage2);

		FirstLaunch.isTrue = false;

		Configuration conf = getResources().getConfiguration();
		if (conf.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			((GridView) findViewById(R.id.suggestionsGrid)).setNumColumns(2);
		} else {
			((GridView) findViewById(R.id.suggestionsGrid)).setNumColumns(1);
		}

		if (!preferences.getBoolean("agree", false))
			showAgreeDialog();

        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new updateTask().execute();
            }
        });

		new updateTask().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
		feedActivity = this;
	}

	private void showAgreeDialog() {
		final Editor editor = preferences.edit();
		AlertDialog.Builder agreeDialogBuilder = new AlertDialog.Builder(this);
		agreeDialogBuilder.setTitle("Warning");
		View agreeView = getLayoutInflater().inflate(R.layout.dialog_agree,
				null);
		agreeDialogBuilder.setView(agreeView);
		agreeDialogBuilder.setCancelable(false);
		agreeDialogBuilder.setPositiveButton("Agree", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				editor.putBoolean("agree", true).commit();
			}
		});
		agreeDialogBuilder.setNegativeButton("Exit", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		AlertDialog agreeDialog = agreeDialogBuilder.create();
		agreeDialog.show();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = drawer.isDrawerOpen(drawerList);
		menu.findItem(R.id.action_search).setVisible(!drawerOpen);
		menu.findItem(R.id.action_tag).setVisible(!drawerOpen);
		if (preferences.getString("username", "").equals("")) {
			menu.findItem(R.id.action_logout).setVisible(false);
		} else {
			menu.findItem(R.id.action_logout).setVisible(true);
		}
		if (preferences.getBoolean("onload", false)) {
			menu.findItem(R.id.action_load).setChecked(true);
		}
		MenuItemCompat.setOnActionExpandListener(
				menu.findItem(R.id.action_search),
				new OnActionExpandListener() {
					@Override
					public boolean onMenuItemActionCollapse(MenuItem item) {
						findViewById(R.id.suggestionsLayout).setVisibility(
								View.GONE);
						return true;
					}

					@Override
					public boolean onMenuItemActionExpand(MenuItem item) {
						return true;
					}
				});
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
		case R.id.action_tag:
			if (preferences.getString("username", "").equals("")) {
				Toast.makeText(FeedActivity.this, "You must log in first",
						Toast.LENGTH_LONG).show();
				startActivity(new Intent(FeedActivity.this, LoginActivity.class));
			} else {
				startActivity(new Intent(FeedActivity.this, TagActivity.class));
			}
			break;
		case R.id.action_contact:
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
					Uri.fromParts("mailto", "support@mydrivingblows.com", null));
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MyDrivingBlows");
			try {
				emailIntent.putExtra(
						Intent.EXTRA_TEXT,
						"[MyDrivingBlows App Version] � "
								+ getPackageManager().getPackageInfo(
										getPackageName(), 0).versionName
								+ "\n[Android Version] � "
								+ Build.VERSION.RELEASE + "\n\n\n");
			} catch (NameNotFoundException e1) {
			}
			startActivity(Intent.createChooser(emailIntent,
					"Send Us Email Via:"));
			break;
		case R.id.action_logout:
			Editor editor = preferences.edit();
			boolean usernameChanged = preferences.getBoolean(
					"username_changed", false);
			editor.clear().commit();
			editor.putBoolean("username_changed", usernameChanged).commit();
			editor.putBoolean("agree", true).commit();
			startActivity(new Intent(FeedActivity.this, LoginActivity.class));
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

	class updateTask extends AsyncTask<Void, Void, Void> {

		private JSONObject feedJSON;

		@Override
		protected Void doInBackground(Void... params) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(feedURL);
				HttpResponse response = httpclient.execute(httppost);
				feedJSON = new JSONObject(EntityUtils.toString(response
						.getEntity()));
			} catch (Exception e) {
				Log("Error in http connection" + e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (imgLoader != null) {
				imgLoader.stop();
				imgLoader.destroy();
			}
			imgLoader = ImageLoader.getInstance();

			options = new DisplayImageOptions.Builder().cacheInMemory(false)
					.cacheOnDisc(true).build();
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					getApplicationContext())
					.defaultDisplayImageOptions(options)
					.build();
			imgLoader.init(config);
			if (feedJSON != null) {
				try {
					worstNum = feedJSON.getString("worst_num");
					((TextView) findViewById(R.id.worstText)).setText(worstNum);
					((TextView) findViewById(R.id.worstText)).setSelected(true);

					worstState = feedJSON.getString("worst_state");
					((TextView) findViewById(R.id.worstComment)).setText(Html
							.fromHtml(feedJSON.getString("worst_comment")));
					setImage(worstImage, worstState);
					Configuration configuration = getResources()
							.getConfiguration();
					if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
						worstNum2 = feedJSON.getString("worst_num2");
						((TextView) findViewById(R.id.worstText2))
								.setText(worstNum2);
						worstState2 = feedJSON.getString("worst_state2");
						((TextView) findViewById(R.id.worstComment2))
								.setText(Html.fromHtml(feedJSON
										.getString("worst_comment2")));
						setImage(worstImage2, worstState2);
					}
					LayoutInflater ltInflater = getLayoutInflater();
					LinearLayout feedLayout = (LinearLayout) findViewById(R.id.feedLayout);
					commentsArray = new JSONArray(
							feedJSON.getString("comments"));
					feedLayout.removeAllViews();
					for (int i = 0; i < commentsArray.length(); i++) {
						if (i > 0) {
							View divider = ltInflater.inflate(
									R.layout.feed_divider, feedLayout, false);
							feedLayout.addView(divider);
						}
						View item = ltInflater.inflate(R.layout.feed_item,
								feedLayout, false);
						item.findViewById(R.id.commentLayout).setTag(i);
						((TextView) item.findViewById(R.id.plateNum))
								.setText(commentsArray.getJSONObject(i)
										.getString("comment_plate_num"));
						((TextView) item.findViewById(R.id.commentInfo))
								.setText(Html.fromHtml(commentsArray
										.getJSONObject(i).getString(
												"comment_info")));
						((TextView) item.findViewById(R.id.comment))
								.setText(Html.fromHtml(commentsArray
										.getJSONObject(i).getString(
												"comment_text")));
						if (!commentsArray.getJSONObject(i)
								.getString("comment_photo").equals("")) {
							item.findViewById(R.id.photoLayout).setVisibility(
									View.VISIBLE);

							imgLoader.displayImage(
									commentsArray.getJSONObject(i).getString(
											"comment_photo"),
									(ImageView) item.findViewById(R.id.photo)
									);
						}
						setImageSmall(
								((ImageView) item.findViewById(R.id.plateImage)),
								commentsArray.getJSONObject(i).getString(
										"comment_plate_state"));
						feedLayout.addView(item);
					}

				} catch (Exception e) {
					Log(e);
				}
				mPullToRefreshLayout.setRefreshing(false);

				mPullToRefreshLayout.setVisibility(View.VISIBLE);
				findViewById(R.id.internetLayout).setVisibility(View.GONE);
				new suggestionsTask().execute();
			} else {
				if (isOnline()) {
					// unlown error
				} else {
					mPullToRefreshLayout.setVisibility(View.GONE);
					findViewById(R.id.internetLayout).setVisibility(
							View.VISIBLE);
				}
			}
			findViewById(R.id.progressBar).setVisibility(View.GONE);
		}
	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return (cm.getActiveNetworkInfo() != null);
	}

	class suggestionsTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(suggestionsURL);
				HttpResponse response = httpclient.execute(httppost);
				originalSuggestionsJSON = new JSONArray(
						EntityUtils.toString(response.getEntity()));
			} catch (Exception e) {
				Log("Error in http connection" + e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

	}

	private void setImage(ImageView image, String itemState) {
		if (itemState.equals("AL")) {
			image.setImageResource(R.drawable.al);
		} else if (itemState.equals("AK")) {
			image.setImageResource(R.drawable.ak);
		} else if (itemState.equals("AZ")) {
			image.setImageResource(R.drawable.az);
		} else if (itemState.equals("AR")) {
			image.setImageResource(R.drawable.ar);
		} else if (itemState.equals("CA")) {
			image.setImageResource(R.drawable.ca);
		} else if (itemState.equals("CO")) {
			image.setImageResource(R.drawable.co);
		} else if (itemState.equals("CT")) {
			image.setImageResource(R.drawable.ct);
		} else if (itemState.equals("DC")) {
			image.setImageResource(R.drawable.dc);
		} else if (itemState.equals("DE")) {
			image.setImageResource(R.drawable.de);
		} else if (itemState.equals("FL")) {
			image.setImageResource(R.drawable.fl);
		} else if (itemState.equals("GA")) {
			image.setImageResource(R.drawable.ga);
		} else if (itemState.equals("GOV")) {
			image.setImageResource(R.drawable.gov);
		} else if (itemState.equals("HI")) {
			image.setImageResource(R.drawable.hi);
		} else if (itemState.equals("ID")) {
			image.setImageResource(R.drawable.id);
		} else if (itemState.equals("IL")) {
			image.setImageResource(R.drawable.il);
		} else if (itemState.equals("IN")) {
			image.setImageResource(R.drawable.in);
		} else if (itemState.equals("IA")) {
			image.setImageResource(R.drawable.ia);
		} else if (itemState.equals("KS")) {
			image.setImageResource(R.drawable.ks);
		} else if (itemState.equals("KY")) {
			image.setImageResource(R.drawable.ky);
		} else if (itemState.equals("LA")) {
			image.setImageResource(R.drawable.la);
		} else if (itemState.equals("ME")) {
			image.setImageResource(R.drawable.me);
		} else if (itemState.equals("MD")) {
			image.setImageResource(R.drawable.md);
		} else if (itemState.equals("MA")) {
			image.setImageResource(R.drawable.ma);
		} else if (itemState.equals("MI")) {
			image.setImageResource(R.drawable.mi);
		} else if (itemState.equals("MN")) {
			image.setImageResource(R.drawable.mn);
		} else if (itemState.equals("MS")) {
			image.setImageResource(R.drawable.ms);
		} else if (itemState.equals("MO")) {
			image.setImageResource(R.drawable.mo);
		} else if (itemState.equals("MT")) {
			image.setImageResource(R.drawable.mt);
		} else if (itemState.equals("NE")) {
			image.setImageResource(R.drawable.ne);
		} else if (itemState.equals("NV")) {
			image.setImageResource(R.drawable.nv);
		} else if (itemState.equals("NH")) {
			image.setImageResource(R.drawable.nh);
		} else if (itemState.equals("NJ")) {
			image.setImageResource(R.drawable.nj);
		} else if (itemState.equals("NM")) {
			image.setImageResource(R.drawable.nm);
		} else if (itemState.equals("NY")) {
			image.setImageResource(R.drawable.ny);
		} else if (itemState.equals("NC")) {
			image.setImageResource(R.drawable.nc);
		} else if (itemState.equals("ND")) {
			image.setImageResource(R.drawable.nd);
		} else if (itemState.equals("OH")) {
			image.setImageResource(R.drawable.oh);
		} else if (itemState.equals("OK")) {
			image.setImageResource(R.drawable.ok);
		} else if (itemState.equals("OR")) {
			image.setImageResource(R.drawable.or);
		} else if (itemState.equals("PA")) {
			image.setImageResource(R.drawable.pa);
		} else if (itemState.equals("RI")) {
			image.setImageResource(R.drawable.ri);
		} else if (itemState.equals("SC")) {
			image.setImageResource(R.drawable.sc);
		} else if (itemState.equals("SD")) {
			image.setImageResource(R.drawable.sd);
		} else if (itemState.equals("TN")) {
			image.setImageResource(R.drawable.tn);
		} else if (itemState.equals("TX")) {
			image.setImageResource(R.drawable.tx);
		} else if (itemState.equals("UT")) {
			image.setImageResource(R.drawable.ut);
		} else if (itemState.equals("VT")) {
			image.setImageResource(R.drawable.vt);
		} else if (itemState.equals("VA")) {
			image.setImageResource(R.drawable.va);
		} else if (itemState.equals("WA")) {
			image.setImageResource(R.drawable.wa);
		} else if (itemState.equals("WV")) {
			image.setImageResource(R.drawable.wv);
		} else if (itemState.equals("WI")) {
			image.setImageResource(R.drawable.wi);
		} else if (itemState.equals("WY")) {
			image.setImageResource(R.drawable.wy);
		}
	}

	private void setImageSmall(ImageView image, String itemState) {
		if (itemState.equals("AL")) {
			image.setImageResource(R.drawable.al_sm);
		} else if (itemState.equals("AK")) {
			image.setImageResource(R.drawable.ak_sm);
		} else if (itemState.equals("AZ")) {
			image.setImageResource(R.drawable.az_sm);
		} else if (itemState.equals("AR")) {
			image.setImageResource(R.drawable.ar_sm);
		} else if (itemState.equals("CA")) {
			image.setImageResource(R.drawable.ca_sm);
		} else if (itemState.equals("CO")) {
			image.setImageResource(R.drawable.co_sm);
		} else if (itemState.equals("CT")) {
			image.setImageResource(R.drawable.ct_sm);
		} else if (itemState.equals("DC")) {
			image.setImageResource(R.drawable.dc_sm);
		} else if (itemState.equals("DE")) {
			image.setImageResource(R.drawable.de_sm);
		} else if (itemState.equals("FL")) {
			image.setImageResource(R.drawable.fl_sm);
		} else if (itemState.equals("GA")) {
			image.setImageResource(R.drawable.ga_sm);
		} else if (itemState.equals("GOV")) {
			image.setImageResource(R.drawable.gov_sm);
		} else if (itemState.equals("HI")) {
			image.setImageResource(R.drawable.hi_sm);
		} else if (itemState.equals("ID")) {
			image.setImageResource(R.drawable.id_sm);
		} else if (itemState.equals("IL")) {
			image.setImageResource(R.drawable.il_sm);
		} else if (itemState.equals("IN")) {
			image.setImageResource(R.drawable.in_sm);
		} else if (itemState.equals("IA")) {
			image.setImageResource(R.drawable.ia_sm);
		} else if (itemState.equals("KS")) {
			image.setImageResource(R.drawable.ks_sm);
		} else if (itemState.equals("KY")) {
			image.setImageResource(R.drawable.ky_sm);
		} else if (itemState.equals("LA")) {
			image.setImageResource(R.drawable.la_sm);
		} else if (itemState.equals("ME")) {
			image.setImageResource(R.drawable.me_sm);
		} else if (itemState.equals("MD")) {
			image.setImageResource(R.drawable.md_sm);
		} else if (itemState.equals("MA")) {
			image.setImageResource(R.drawable.ma_sm);
		} else if (itemState.equals("MI")) {
			image.setImageResource(R.drawable.mi_sm);
		} else if (itemState.equals("MN")) {
			image.setImageResource(R.drawable.mn_sm);
		} else if (itemState.equals("MS")) {
			image.setImageResource(R.drawable.ms_sm);
		} else if (itemState.equals("MO")) {
			image.setImageResource(R.drawable.mo_sm);
		} else if (itemState.equals("MT")) {
			image.setImageResource(R.drawable.mt_sm);
		} else if (itemState.equals("NE")) {
			image.setImageResource(R.drawable.ne_sm);
		} else if (itemState.equals("NV")) {
			image.setImageResource(R.drawable.nv_sm);
		} else if (itemState.equals("NH")) {
			image.setImageResource(R.drawable.nh_sm);
		} else if (itemState.equals("NJ")) {
			image.setImageResource(R.drawable.nj_sm);
		} else if (itemState.equals("NM")) {
			image.setImageResource(R.drawable.nm_sm);
		} else if (itemState.equals("NY")) {
			image.setImageResource(R.drawable.ny_sm);
		} else if (itemState.equals("NC")) {
			image.setImageResource(R.drawable.nc_sm);
		} else if (itemState.equals("ND")) {
			image.setImageResource(R.drawable.nd_sm);
		} else if (itemState.equals("OH")) {
			image.setImageResource(R.drawable.oh_sm);
		} else if (itemState.equals("OK")) {
			image.setImageResource(R.drawable.ok_sm);
		} else if (itemState.equals("OR")) {
			image.setImageResource(R.drawable.or_sm);
		} else if (itemState.equals("PA")) {
			image.setImageResource(R.drawable.pa_sm);
		} else if (itemState.equals("RI")) {
			image.setImageResource(R.drawable.ri_sm);
		} else if (itemState.equals("SC")) {
			image.setImageResource(R.drawable.sc_sm);
		} else if (itemState.equals("SD")) {
			image.setImageResource(R.drawable.sd_sm);
		} else if (itemState.equals("TN")) {
			image.setImageResource(R.drawable.tn_sm);
		} else if (itemState.equals("TX")) {
			image.setImageResource(R.drawable.tx_sm);
		} else if (itemState.equals("UT")) {
			image.setImageResource(R.drawable.ut_sm);
		} else if (itemState.equals("VT")) {
			image.setImageResource(R.drawable.vt_sm);
		} else if (itemState.equals("VA")) {
			image.setImageResource(R.drawable.va_sm);
		} else if (itemState.equals("WA")) {
			image.setImageResource(R.drawable.wa_sm);
		} else if (itemState.equals("WV")) {
			image.setImageResource(R.drawable.wv_sm);
		} else if (itemState.equals("WI")) {
			image.setImageResource(R.drawable.wi_sm);
		} else if (itemState.equals("WY")) {
			image.setImageResource(R.drawable.wy_sm);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.feed, menu);
		((SearchView) menu.findItem(R.id.action_search).getActionView())
				.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (originalSuggestionsJSON != null) {
                            suggestionsJSON = new JSONArray();
                            searchQuery = newText;
                            if (newText.length() > 0) {
                                findViewById(R.id.suggestionsLayout)
                                        .setVisibility(View.VISIBLE);
                            } else {
                                findViewById(R.id.suggestionsLayout)
                                        .setVisibility(View.GONE);
                            }
                            for (int i = 0; i < originalSuggestionsJSON
                                    .length(); i++) {
                                try {
                                    if (originalSuggestionsJSON
                                            .getJSONObject(i).getString("num")
                                            .toLowerCase()
                                            .contains(newText.toLowerCase())) {
                                        suggestionsJSON
                                                .put(originalSuggestionsJSON
                                                        .getJSONObject(i));
                                    }
                                } catch (Exception e) {
                                }
                            }
                            suggestionsAdapter = new GridViewSuggestionsAdapter(
                                    FeedActivity.this, suggestionsJSON);
                            ((GridView) findViewById(R.id.suggestionsGrid))
                                    .setAdapter(suggestionsAdapter);
                            if (suggestionsJSON.length() > 0) {
                                findViewById(R.id.noResults).setVisibility(
                                        View.GONE);
                            } else {
                                findViewById(R.id.noResults).setVisibility(
                                        View.VISIBLE);
                            }
                        }
                        return false;
                    }
                });
		return true;
	}

	public void worst(View v) {
		Intent searchActivity = new Intent(FeedActivity.this,
				SearchActivity.class);
		searchActivity.putExtra("Tag", worstNum);
		searchActivity.putExtra("State", worstState);
		startActivity(searchActivity);
	}

	public void worst2(View v) {
		Intent searchActivity = new Intent(FeedActivity.this,
				SearchActivity.class);
		searchActivity.putExtra("Tag", worstNum2);
		searchActivity.putExtra("State", worstState2);
		startActivity(searchActivity);
	}

	public void details(View v) {
		try {
			int number = Integer.parseInt(v.getTag() + "");
			Intent searchActivity = new Intent(FeedActivity.this,
					SearchActivity.class);

			searchActivity.putExtra("Tag", commentsArray.getJSONObject(number)
					.getString("comment_plate_num"));

			searchActivity.putExtra("State", commentsArray
					.getJSONObject(number).getString("comment_plate_state"));
			startActivity(searchActivity);
		} catch (Exception e) {
		}
	}

	public void suggestion(View v) {
		try {
			int number = Integer.parseInt(v.getTag() + "");
			Intent searchActivity = new Intent(FeedActivity.this,
					SearchActivity.class);

			searchActivity.putExtra("Tag", suggestionsJSON
					.getJSONObject(number).getString("num"));

			searchActivity.putExtra("State",
					suggestionsJSON.getJSONObject(number).getString("state"));
			startActivity(searchActivity);
		} catch (Exception e) {
		}
	}

	public static void Log(Object text) {
		Log.d("Log", "" + text);
	}

	public void retry(View v) {
		findViewById(R.id.internetLayout).setClickable(false);
		/*TODO: Put this ugly animation back(?)

		ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(
				this, R.anim.flipping);
		anim.setTarget(findViewById(R.id.errorImage));
		anim.setDuration(2500);
		anim.start();
		anim.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				((TextView) findViewById(R.id.retryText)).setText("Loading...");
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
					((Vibrator) FeedActivity.this
							.getSystemService(Context.VIBRATOR_SERVICE))
							.vibrate(400);
					((TextView) findViewById(R.id.retryText))
							.setText(R.string.retry);
				} else {
					((TextView) findViewById(R.id.retryText))
							.setText("Wait a Moment...");
				}
				findViewById(R.id.internetLayout).setClickable(true);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});*/
		new updateTask().execute();
	}

}
