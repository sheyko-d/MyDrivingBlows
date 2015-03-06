package com.moysof.mydrivingblows;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends ActionBarActivity implements OnItemLongClickListener {

    private ProgressBar progressBar;
    private List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    private String newsURL = "http://mydrivingblows.com/app/news.php";
    private String result = "";
    private SharedPreferences preferences;
    private ListView list;
    private ArrayList<String> comment_ids = new ArrayList<String>();
    private ArrayList<String> comment_texts = new ArrayList<String>();
    private ArrayList<String> photos = new ArrayList<String>();
    private ArrayList<String> videos = new ArrayList<String>();
    private ArrayList<String> plate_nums = new ArrayList<String>();
    private ArrayList<String> plate_states = new ArrayList<String>();
    private NewsListViewAdapter adapter;
    private AdapterView<ListAdapter> drawerList;
    private DrawerLayout drawer;
    private int drawerPosition = 3;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerListViewAdapter drawerAdapter;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        mToolbar = (Toolbar) findViewById(R.id.mytoolbar);
        mToolbar.setContentInsetsAbsolute(
                CommonUtilities.convertDpToPixel(72, this), 0);
        setSupportActionBar(mToolbar);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        list = (ListView) findViewById(R.id.listview);
        adapter = new NewsListViewAdapter(this, getApplicationContext(),
                comment_texts, photos, videos, plate_nums, plate_states);
        list.setAdapter(adapter);

        list.setOnItemClickListener(itemListener);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerAdapter = new DrawerListViewAdapter(this, drawerPosition);

        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(drawerAdapter);
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
        list.setOnItemLongClickListener(this);
        drawerList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int position, long id) {
                if (position < 4) {
                    drawerPosition = position;
                    adapter.notifyDataSetChanged();
                }

                drawer.closeDrawer(drawerList);

                if (position == 1 || position == 2 || position == 5 || position == 6 || position == 7 || position == 8) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (position == 1) {
                                startActivity(new Intent(NewsActivity.this,
                                        FeedActivity.class));
                                finish();
                            } else if (position == 2) {
                                startActivity(new Intent(NewsActivity.this,
                                        TagActivity.class));
                                finish();
                            } else if (position == 5) {
                                startActivity(new Intent(NewsActivity.this,
                                        SettingsActivity.class));
                            } else if (position == 6) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(NewsActivity.this);
                                builder = new AlertDialog.Builder(NewsActivity.this);
                                builder.setTitle("Terms of Service");
                                View dialogView = new View(NewsActivity.this);
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
                                } catch (PackageManager.NameNotFoundException e1) {
                                }
                                startActivity(Intent.createChooser(emailIntent,
                                        "Send Us Email Via:"));

                            } else if (position == 8) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(NewsActivity.this);
                                builder.setTitle("About");
                                View dialogView = new View(NewsActivity.this);
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
                                } catch (PackageManager.NameNotFoundException e) {((TextView) dialogView.findViewById(R.id.dialogVersionText))
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
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
            videos.clear();
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
                            videos.add(new JSONObject("" + comments.get(i))
                                    .getString("video"));
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



    private void Log(Object Object) {
        Log.d("Log", "" + Object);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
        if (mActionMode != null) {
            return false;
        }
        mToolbar.startActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mActionMode = mode;

                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.delete, menu);

                mode.setTitle("Select posts");
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
                        getApplicationContext(), comment_texts, photos, videos, plate_nums,
                        plate_states);
                list.setAdapter(adapter);
                list.setOnItemLongClickListener(NewsActivity.this);
                list.setOnItemClickListener(itemListener);
                mActionMode = null;
            }
        });
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
        /*TODO: Put back ugly animation (?)
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
        });*/
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(drawerList)) {
            drawer.closeDrawer(drawerList);
        } else {
            startActivity(new Intent(NewsActivity.this, FeedActivity.class));
            super.onBackPressed();
        }
    }

    public void tag(View v){
        startActivity(new Intent(NewsActivity.this, TagActivity.class));
        finish();
    }

}
