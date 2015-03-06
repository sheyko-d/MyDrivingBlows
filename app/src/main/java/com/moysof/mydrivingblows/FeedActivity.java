package com.moysof.mydrivingblows;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Outline;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FeedActivity extends ActionBarActivity {

    private SwipeRefreshLayout mPullToRefreshLayout;
    private String feedURL = "http://mydrivingblows.com/app/feed.php";
    private String suggestionsURL = "http://mydrivingblows.com/app/suggestions.php";
    private ImageLoader imgLoader;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout drawer;
    private ListView drawerList;
    private int drawerPosition = 1;
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
    private RecyclerView mRecyclerView;
    private FeedRecyclerAdapter mAdapter;
    private GridLayoutManager mLayoutManager;
    private ImageButton mFAB;
    private JSONObject feedJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_feed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        toolbar.setContentInsetsAbsolute(
                CommonUtilities.convertDpToPixel(72, this), 0);
        setSupportActionBar(toolbar);

        if (!FirstLaunch.isTrue) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        adapter = new DrawerListViewAdapter(this, drawerPosition);

        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(adapter);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.app_name, R.string.app_name) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int position, long id) {
                if (position < 4) {
                    drawerPosition = position;
                    adapter.notifyDataSetChanged();
                }
                if (preferences.getString("username", "").equals("")) {
                    if (drawerPosition == 2) {
                        Toast.makeText(FeedActivity.this,
                                "You must log in first", Toast.LENGTH_LONG)
                                .show();
                        startActivity(new Intent(FeedActivity.this,
                                LoginActivity.class));
                        finish();
                        return;
                    }
                }

                drawer.closeDrawer(drawerList);

                if (position == 2 || position == 3 || position == 5 || position == 6 || position == 7 || position == 8) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            if (position == 2) {
                                startActivity(new Intent(FeedActivity.this,
                                        TagActivity.class));
                                finish();
                            } else if (position == 3) {
                                startActivity(new Intent(FeedActivity.this,
                                        NewsActivity.class));
                                finish();
                            } else if (position == 5) {
                                startActivity(new Intent(FeedActivity.this,
                                        SettingsActivity.class));
                            } else if (position == 6) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(FeedActivity.this);
                                builder = new AlertDialog.Builder(FeedActivity.this);
                                builder.setTitle("Terms of Service");
                                View dialogView = new View(FeedActivity.this);
                                dialogView = getLayoutInflater().inflate(R.layout.dialog_terms,
                                        null);
                                builder.setView(dialogView);
                                builder.setNeutralButton("Close", null);
                                builder.create().show();

                            } else if (position == 7) {
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

                            } else if (position == 8) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(FeedActivity.this);
                                builder.setTitle("About");
                                View dialogView = new View(FeedActivity.this);
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
                                } catch (NameNotFoundException e) {((TextView) dialogView.findViewById(R.id.dialogVersionText))
                                        .setText("Version: n\\/a");
                                }
                                builder.setView(dialogView);
                                builder.setNeutralButton("Close", null);
                                builder.create().show();
                            }

                        }
                    }, 300);
                }

            }
        });
        drawer.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mPullToRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.ptr_layout);
        mPullToRefreshLayout.setColorSchemeResources(new int[]{R.color.accent});
        worstImage = (ImageView) findViewById(R.id.worstImage);
        worstImage2 = (ImageView) findViewById(R.id.worstImage2);

        FirstLaunch.isTrue = false;

        mRecyclerView = (RecyclerView) findViewById(R.id.feedRecycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (!preferences.getBoolean("agree", false))
            showAgreeDialog();

        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new updateTask().execute();
            }
        });

        new updateTask().execute();


        mFAB = (ImageButton) findViewById(R.id.fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (preferences.getString("username", "").equals("")) {
                    Toast.makeText(FeedActivity.this, "You must log in first",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(FeedActivity.this, LoginActivity.class));
                } else {
                    startActivity(new Intent(FeedActivity.this, TagActivity.class));
                }
            }
        });
        initFAB();
    }

    public void logIn(View v) {
        if (preferences.getString("username", "").equals("")) {
            startActivity(new Intent(FeedActivity.this,
                    LoginActivity.class));
        } else {
            Editor editor = preferences.edit();
            boolean usernameChanged = preferences.getBoolean(
                    "username_changed", false);
            editor.clear().commit();
            editor.putBoolean("username_changed", usernameChanged).commit();
            editor.putBoolean("agree", true).commit();
            editor.commit();
            startActivity(new Intent(FeedActivity.this, LoginActivity.class));
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initFAB() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    int shapeSize = getResources().getDimensionPixelSize(
                            R.dimen.fab_size);
                    outline.setRoundRect(0, 0, shapeSize, shapeSize,
                            shapeSize / 2);
                }
            };
            mFAB.setOutlineProvider(viewOutlineProvider);
            mFAB.setClipToOutline(true);
        }
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
        /*menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        menu.findItem(R.id.action_tag).setVisible(!drawerOpen);
        if (preferences.getString("username", "").equals("")) {
            menu.findItem(R.id.action_logout).setVisible(false);
        } else {
            menu.findItem(R.id.action_logout).setVisible(true);
        }
        if (preferences.getBoolean("onload", false)) {
            menu.findItem(R.id.action_load).setChecked(true);
        }*/
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
        }
        return super.onOptionsItemSelected(item);
    }

    class updateTask extends AsyncTask<Void, Void, Void> {

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
                    commentsArray = new JSONArray(
                            feedJSON.getString("comments"));
                    mAdapter = new FeedRecyclerAdapter(FeedActivity.this, mRecyclerView, feedJSON);
                    mRecyclerView.setAdapter(mAdapter);
                    Log("size feed = " + commentsArray.length());
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
        try {
            searchActivity.putExtra("Tag", feedJSON.getString("worst_num"));
            searchActivity.putExtra("State", feedJSON.getString("worst_state"));
        } catch (JSONException e) {
        }
        startActivity(searchActivity);
    }

    public void details(View v) {
        try {
            int number = Integer.parseInt(v.getTag() + "");
            number--;
            Intent searchActivity = new Intent(FeedActivity.this,
                    SearchActivity.class);

            searchActivity.putExtra("Tag", commentsArray.getJSONObject(number)
                    .getString("comment_plate_num"));

            searchActivity.putExtra("State", commentsArray
                    .getJSONObject(number).getString("comment_plate_state"));
            startActivity(searchActivity);
        } catch (JSONException e) {
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
