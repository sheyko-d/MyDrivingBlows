package app.moysof.mydrivingblows;

import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Outline;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewOutlineProvider;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnPublishListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.co.senab.photoview.PhotoViewAttacher;

public class SearchActivity extends ActionBarActivity {

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private EditText searchEditText;
    private AlertDialog stateDialog;
    private Button stateButtonSearch;
    private ScrollView itemScrollView;
    private String state = "";
    private String search;
    ArrayList<String> infoArrayList = new ArrayList<String>();
    ArrayList<String> commentsArrayList = new ArrayList<String>();
    ArrayList<String> latArrayList = new ArrayList<String>();
    ArrayList<String> lonArrayList = new ArrayList<String>();
    ArrayList<String> photosArrayList = new ArrayList<String>();
    ArrayList<String> videosArrayList = new ArrayList<String>();
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    private LinearLayout commentsLayout;
    private boolean searchFromMenu = false;
    private String searchURL = "http://mydrivingblows.com/app/search.php";
    private TextView commentsTextView;
    private String plate = "Search";
    private String error = "";
    private String result = "";
    private ProgressBar itemProgressBar;
    private Intent TagActivity;
    private ImageView itemImage;
    public int tag = 0;
    private String itemState = "";
    private ImageView commentImage;
    private boolean showingImage = false;
    private boolean notFound = false;
    public ImageLoader imgLoader;
    private PhotoViewAttacher mAttacher;
    private SharedPreferences preferences;
    public boolean postingTwitter = false;
    public boolean postingFacebook = false;
    public int pos;
    private ListView mList;
    private ImageButton mFAB;
    private DisplayImageOptions mOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        toolbar.setContentInsetsAbsolute(
                CommonUtilities.convertDpToPixel(72, this), 0);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");

        mOptions = new DisplayImageOptions.Builder().cacheInMemory(false)
                .cacheOnDisk(true).showImageOnFail(R.drawable.photo_error)
                .displayer(new FadeInBitmapDisplayer(250, true, false, false))
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(mOptions).build();
        imgLoader = ImageLoader.getInstance();
        imgLoader.init(config);

        mList = (ListView) findViewById(R.id.commentsList);

        searchEditText = (EditText) findViewById(R.id.searchEditText);
        itemScrollView = (ScrollView) findViewById(R.id.itemScrollView);
        stateButtonSearch = (Button) findViewById(R.id.stateButtonSearch);
        commentsLayout = (LinearLayout) findViewById(R.id.commentsLayout);
        commentsTextView = (TextView) findViewById(R.id.commentsTextView);
        itemProgressBar = (ProgressBar) findViewById(R.id.itemProgressBar);
        commentImage = (ImageView) findViewById(R.id.image);
        itemImage = (ImageView) findViewById(R.id.itemImage);

        mAttacher = new PhotoViewAttacher(commentImage);
        mAttacher.setMaximumScale(20.0f);
        TagActivity = getIntent();

        if (TagActivity.getStringExtra("Tag") != null) {
            search = TagActivity.getStringExtra("Tag");
            state = TagActivity.getStringExtra("State");
            searchFromMenu = true;
        }

        new serverSearchTask().execute();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase(Locale.US))) {
                    s = s.toUpperCase(Locale.US);
                    searchEditText.setText(s);
                    searchEditText.setSelection(s.length());
                }
            }
        });


        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listItems);
        mList.setAdapter(adapter);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mFAB = (ImageButton) findViewById(R.id.fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (preferences.getString("username", "").equals("")) {
                    Toast.makeText(SearchActivity.this, "You must log in first",
                            Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SearchActivity.this,
                            LoginActivity.class));
                } else {
                    Intent tagActivity = new Intent(SearchActivity.this,
                            TagActivity.class);
                    tagActivity.putExtra("Plate", plate);
                    tagActivity.putExtra("State", itemState);
                    startActivity(tagActivity);
                    finish();
                }
            }
        });
        initFAB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
        Permission[] permissions = new Permission[]{Permission.PUBLIC_PROFILE
        };
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(getResources().getString(R.string.app_id))
                .setPermissions(permissions).build();
        SimpleFacebook.setConfiguration(configuration);
    }

    OnLoginListener onLoginListener = new OnLoginListener() {

        @Override
        public void onFail(String reason) {
            Toast.makeText(getApplicationContext(), "Login Fail: " + reason,
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onException(Throwable throwable) {
            Toast.makeText(getApplicationContext(),
                    "Login Exception: " + throwable, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onThinking() {
            Toast.makeText(getApplicationContext(), "Loading...",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLogin() {
            Builder dialog = new Builder(SearchActivity.this);
            View dialogView = new View(SearchActivity.this);
            dialogView = getLayoutInflater().inflate(R.layout.comment_edittext,
                    null);
            EditText editText = (EditText) dialogView
                    .findViewById(R.id.commentEditText);
            editText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    facebookMessage = s.toString();
                }
            });
            dialog.setView(dialogView);
            dialog.setPositiveButton("Post",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new generatePlateTask().execute();
                        }
                    });
            dialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.create().show();
        }

        @Override
        public void onNotAcceptingPermissions(Permission.Type type) {

        }
    };

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

    public void search(View v) {
        searchFromMenu = false;
        if (searchEditText.getText().toString().equals("")) {
            Toast.makeText(SearchActivity.this, "Error: Search field is empty",
                    Toast.LENGTH_LONG).show();
        } else if (state.equals("")) {
            Toast.makeText(SearchActivity.this, "Error: Please, select state",
                    Toast.LENGTH_LONG).show();
        } else {
            new serverSearchTask().execute();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        }
    }

    class serverSearchTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            itemProgressBar.setVisibility(View.VISIBLE);
            if (!searchFromMenu) {
                search = searchEditText.getText().toString();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            nameValuePairs.clear();

            nameValuePairs.add(new BasicNameValuePair("search", search));
            nameValuePairs.add(new BasicNameValuePair("state", state));
            nameValuePairs.add(new BasicNameValuePair("menu", searchFromMenu
                    + ""));

            postToServer(nameValuePairs, searchURL);

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            super.onPostExecute(param);
            itemProgressBar.setVisibility(View.INVISIBLE);
            if (error.equals("")) {
                if (notFound) {
                    mList.setVisibility(View.GONE);
                    plate = searchEditText.getText().toString();
                    itemState = stateButtonSearch.getText().toString();
                    ((TextView) findViewById(R.id.worstText)).setText(plate);
                    findViewById(R.id.worstText).setVisibility(View.VISIBLE);
                    findViewById(R.id.commentsTextView).setVisibility(
                            View.VISIBLE);
                    commentsLayout.removeAllViews();
                    setImage(itemState);
                } else {
                    mList.setVisibility(View.GONE);
                    ((TextView) findViewById(R.id.worstText)).setText(plate);
                    findViewById(R.id.worstText).setVisibility(View.VISIBLE);
                    String sizeText = commentsArrayList.size() + "";

                    if (commentsLayout.getChildCount() != 0) {
                        commentsLayout.removeAllViews();
                    }
                    if (commentsArrayList.isEmpty()) {
                        commentsTextView.setVisibility(View.VISIBLE);
                        findViewById(R.id.commentsText)
                                .setVisibility(View.GONE);
                    } else {
                        commentsTextView.setVisibility(View.GONE);
                        findViewById(R.id.commentsText).setVisibility(
                                View.VISIBLE);
                        if (sizeText.substring(sizeText.length() - 1).equals(
                                "1")) {
                            ((TextView) findViewById(R.id.commentsText))
                                    .setText(commentsArrayList.size()
                                            + " Comment");
                        } else {
                            ((TextView) findViewById(R.id.commentsText))
                                    .setText(commentsArrayList.size()
                                            + " Comments");
                        }
                    }
                    itemScrollView.setVisibility(View.VISIBLE);

                    LayoutInflater ltInflater = getLayoutInflater();

                    for (int i = 0; i < commentsArrayList.size(); i++) {
                        pos = i;
                        if (i > 0) {
                            View divider = ltInflater.inflate(R.layout.divider,
                                    commentsLayout, false);
                            commentsLayout.addView(divider);
                        }
                        View item = ltInflater.inflate(
                                R.layout.comment_listview_item, commentsLayout,
                                false);
                        TextView infoTextView = (TextView) item
                                .findViewById(R.id.infoTextView);
                        infoTextView
                                .setText(Html.fromHtml(infoArrayList.get(i)));
                        TextView locTextView = (TextView) item
                                .findViewById(R.id.locTextView);
                        if (!latArrayList.get(i).equals("null")
                                && !latArrayList.get(i).equals("")) {
                            locTextView.setTag(
                                    "l" + i);
                            locTextView
                                    .setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            int pos = Integer.parseInt((v
                                                    .getTag() + "").replace(
                                                    "l", ""));
                                            LatLng ITEM = new LatLng(Float
                                                    .parseFloat(latArrayList
                                                            .get(pos)), Float
                                                    .parseFloat(lonArrayList
                                                            .get(pos)));

                                            GoogleMap map = ((MapFragment) getFragmentManager()
                                                    .findFragmentById(R.id.map))
                                                    .getMap();
                                            findViewById(R.id.mapLayout)
                                                    .setVisibility(View.VISIBLE);
                                            map.moveCamera(CameraUpdateFactory
                                                    .newLatLngZoom(ITEM, 15));
                                            map.animateCamera(
                                                    CameraUpdateFactory
                                                            .zoomTo(13), 2000,
                                                    null);
                                            String[] infoArray = (Html.fromHtml(infoArrayList.get(pos).replace("Today", "today")) + "").split(" � ");
                                            map.addMarker(new MarkerOptions()
                                                    .position(ITEM)
                                                    .title(search)
                                                    .snippet("Tagged " + infoArray[1] + " by " + infoArray[0])
                                                    .icon(BitmapDescriptorFactory
                                                            .fromResource(R.drawable.ic_marker)));
                                            getSupportActionBar().setTitle("Location");
                                        }
                                    });

                            try {
                                Geocoder geo = new Geocoder(
                                        SearchActivity.this
                                                .getApplicationContext(),
                                        Locale.getDefault());
                                List<Address> addresses = geo
                                        .getFromLocation(Double
                                                .parseDouble(latArrayList
                                                        .get(i)), Double
                                                .parseDouble(lonArrayList
                                                        .get(i)), 1);
                                if (addresses.isEmpty()) {
                                    locTextView
                                            .setVisibility(View.GONE);
                                } else {
                                    if (addresses.size() > 0) {
                                        if (addresses.get(0).getLocality() != null) {
                                            locTextView.setText(addresses
                                                    .get(0).getLocality()
                                                    + ", ");
                                        } else if (addresses.get(0)
                                                .getFeatureName() != null) {
                                            locTextView.setText(addresses
                                                    .get(0).getFeatureName()
                                                    + ", ");
                                        }
                                        locTextView.setText(locTextView
                                                .getText()
                                                + addresses.get(0)
                                                .getAdminArea());
                                    }
                                }
                            } catch (Exception e) {
                                locTextView.setVisibility(View.GONE);
                                Log(e);
                            }
                        } else {
                            item.findViewById(R.id.locTextView)
                                    .setVisibility(View.GONE);
                        }
                        TextView commentTextView = (TextView) item
                                .findViewById(R.id.commentTextView);
                        commentTextView.setText(String
                                .valueOf(commentsArrayList.get(i)));
                        if (!TextUtils.isEmpty(photosArrayList.get(i))) {
                            item.findViewById(R.id.photoLayout).setVisibility(
                                    View.VISIBLE);
                            imgLoader
                                    .displayImage(
                                            photosArrayList.get(i),
                                            (ImageView) item
                                                    .findViewById(R.id.commentsImageView),
                                            mOptions);

                            Log("display image = " + photosArrayList.get(i));

                        }
                        if (!TextUtils.isEmpty(videosArrayList.get(i))) {
                            item.findViewById(R.id.photoLayout).setVisibility(
                                    View.VISIBLE);
                        }
                        if (!photosArrayList.get(i).equals("") & !videosArrayList.get(i).equals("")) {
                            item.findViewById(R.id.videoIcon).setVisibility(View.VISIBLE);
                            item.findViewById(R.id.commentClick)
                                    .setOnClickListener(commentVideoListener);
                        } else {
                            item.findViewById(R.id.videoIcon).setVisibility(View.GONE);
                            item.findViewById(R.id.commentClick)
                                    .setOnClickListener(commentImageListener);
                        }
                        item.findViewById(R.id.commentClick).setTag(i);
                        item.getLayoutParams().width = LayoutParams.MATCH_PARENT;
                        item.findViewById(R.id.commentsImageView).setTag(i);
                        commentsLayout.addView(item);
                    }
                    setImage(itemState);

                }
                invalidateOptionsMenu();
            } else {
                Toast.makeText(SearchActivity.this, "Error: " + error,
                        Toast.LENGTH_LONG).show();
            }
        }

        private void setImage(String itemState) {
            if (itemState.equals("AL")) {
                itemImage.setImageResource(R.drawable.al);
            } else if (itemState.equals("AK")) {
                itemImage.setImageResource(R.drawable.ak);
            } else if (itemState.equals("AZ")) {
                itemImage.setImageResource(R.drawable.az);
            } else if (itemState.equals("AR")) {
                itemImage.setImageResource(R.drawable.ar);
            } else if (itemState.equals("CA")) {
                itemImage.setImageResource(R.drawable.ca);
            } else if (itemState.equals("CO")) {
                itemImage.setImageResource(R.drawable.co);
            } else if (itemState.equals("CT")) {
                itemImage.setImageResource(R.drawable.ct);
            } else if (itemState.equals("DC")) {
                itemImage.setImageResource(R.drawable.dc);
            } else if (itemState.equals("DE")) {
                itemImage.setImageResource(R.drawable.de);
            } else if (itemState.equals("FL")) {
                itemImage.setImageResource(R.drawable.fl);
            } else if (itemState.equals("GA")) {
                itemImage.setImageResource(R.drawable.ga);
            } else if (itemState.equals("GOV")) {
                itemImage.setImageResource(R.drawable.gov);
            } else if (itemState.equals("HI")) {
                itemImage.setImageResource(R.drawable.hi);
            } else if (itemState.equals("ID")) {
                itemImage.setImageResource(R.drawable.id);
            } else if (itemState.equals("IL")) {
                itemImage.setImageResource(R.drawable.il);
            } else if (itemState.equals("IN")) {
                itemImage.setImageResource(R.drawable.in);
            } else if (itemState.equals("IA")) {
                itemImage.setImageResource(R.drawable.ia);
            } else if (itemState.equals("KS")) {
                itemImage.setImageResource(R.drawable.ks);
            } else if (itemState.equals("KY")) {
                itemImage.setImageResource(R.drawable.ky);
            } else if (itemState.equals("LA")) {
                itemImage.setImageResource(R.drawable.la);
            } else if (itemState.equals("ME")) {
                itemImage.setImageResource(R.drawable.me);
            } else if (itemState.equals("MD")) {
                itemImage.setImageResource(R.drawable.md);
            } else if (itemState.equals("MA")) {
                itemImage.setImageResource(R.drawable.ma);
            } else if (itemState.equals("MI")) {
                itemImage.setImageResource(R.drawable.mi);
            } else if (itemState.equals("MN")) {
                itemImage.setImageResource(R.drawable.mn);
            } else if (itemState.equals("MS")) {
                itemImage.setImageResource(R.drawable.ms);
            } else if (itemState.equals("MO")) {
                itemImage.setImageResource(R.drawable.mo);
            } else if (itemState.equals("MT")) {
                itemImage.setImageResource(R.drawable.mt);
            } else if (itemState.equals("NE")) {
                itemImage.setImageResource(R.drawable.ne);
            } else if (itemState.equals("NV")) {
                itemImage.setImageResource(R.drawable.nv);
            } else if (itemState.equals("NH")) {
                itemImage.setImageResource(R.drawable.nh);
            } else if (itemState.equals("NJ")) {
                itemImage.setImageResource(R.drawable.nj);
            } else if (itemState.equals("NM")) {
                itemImage.setImageResource(R.drawable.nm);
            } else if (itemState.equals("NY")) {
                itemImage.setImageResource(R.drawable.ny);
            } else if (itemState.equals("NC")) {
                itemImage.setImageResource(R.drawable.nc);
            } else if (itemState.equals("ND")) {
                itemImage.setImageResource(R.drawable.nd);
            } else if (itemState.equals("OH")) {
                itemImage.setImageResource(R.drawable.oh);
            } else if (itemState.equals("OK")) {
                itemImage.setImageResource(R.drawable.ok);
            } else if (itemState.equals("OR")) {
                itemImage.setImageResource(R.drawable.or);
            } else if (itemState.equals("PA")) {
                itemImage.setImageResource(R.drawable.pa);
            } else if (itemState.equals("RI")) {
                itemImage.setImageResource(R.drawable.ri);
            } else if (itemState.equals("SC")) {
                itemImage.setImageResource(R.drawable.sc);
            } else if (itemState.equals("SD")) {
                itemImage.setImageResource(R.drawable.sd);
            } else if (itemState.equals("TN")) {
                itemImage.setImageResource(R.drawable.tn);
            } else if (itemState.equals("TX")) {
                itemImage.setImageResource(R.drawable.tx);
            } else if (itemState.equals("UT")) {
                itemImage.setImageResource(R.drawable.ut);
            } else if (itemState.equals("VT")) {
                itemImage.setImageResource(R.drawable.vt);
            } else if (itemState.equals("VA")) {
                itemImage.setImageResource(R.drawable.va);
            } else if (itemState.equals("WA")) {
                itemImage.setImageResource(R.drawable.wa);
            } else if (itemState.equals("WV")) {
                itemImage.setImageResource(R.drawable.wv);
            } else if (itemState.equals("WI")) {
                itemImage.setImageResource(R.drawable.wi);
            } else if (itemState.equals("WY")) {
                itemImage.setImageResource(R.drawable.wy);
            }
        }
    }

    private InputStream is;

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
        } catch (Exception e) {
            Log("Error converting result " + e.toString());
        }

        try {
            notFound = false;
            JSONObject jObject = new JSONObject(result);
            if (jObject.getString("count").equals("0")) {
                error = "";
                infoArrayList.clear();
                commentsArrayList.clear();
                photosArrayList.clear();
                videosArrayList.clear();

                itemState = jObject.getString("state");

                plate = jObject.getString("plate");

                JSONArray comments = (JSONArray) jObject.get("comment");
                for (int num = 0; num < comments.length(); num++) {
                    JSONObject json_data = comments.getJSONObject(num);
                    infoArrayList.add(json_data.getString("comment_info"));
                    commentsArrayList.add(json_data.getString("comment_text"));
                    latArrayList.add(json_data.getString("comment_lat"));
                    lonArrayList.add(json_data.getString("comment_lng"));
                    photosArrayList.add(json_data.getString("comment_photo"));
                    videosArrayList.add(json_data.getString("comment_video"));
                }
            } else if (jObject.getString("count").equals("-1")) {
                error = "";
                listItems.clear();
                notFound = true;
            } else {
                error = "";
                listItems.clear();

                JSONArray plates = (JSONArray) jObject.get("plate");
                for (int num = 0; num < plates.length(); num++) {
                    listItems.add(plates.getString(num));
                }
            }

        } catch (final JSONException e1) {
            Log("JSON error: " + e1.getMessage());
            error = result;
        } catch (final ParseException e1) {
            Log("Parse error: " + e1.getMessage());
            error = result;
        }
        return;
    }

    OnClickListener commentImageListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            startActivity(new Intent(SearchActivity.this, FullscreenImageActivity.class)
                    .putExtra("image", photosArrayList.get((Integer) v.getTag())));
        }
    };

    OnClickListener commentVideoListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            startActivity(new Intent(SearchActivity.this, FullscreenVideoActivity.class)
                    .putExtra("video", videosArrayList.get((Integer) v.getTag())));
        }
    };


    private SimpleFacebook mSimpleFacebook;
    public String plateLink;
    protected String facebookMessage = "";
    protected String twitterMessage;

    @Override
    public void onBackPressed() {
        getSupportActionBar().setTitle("");
        if (showingImage) {
            findViewById(R.id.imageLayout).setVisibility(View.GONE);
            imgLoader.displayImage(null, commentImage);
            showingImage = false;
            return;
        } else if (findViewById(R.id.mapLayout).getVisibility() == View.VISIBLE) {
            findViewById(R.id.mapLayout).setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportActionBar().setTitle("");
                if (showingImage) {
                    findViewById(R.id.imageLayout).setVisibility(View.GONE);
                    imgLoader.displayImage(null, commentImage);
                } else if (findViewById(R.id.mapLayout).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.mapLayout).setVisibility(View.GONE);
                } else {
                    finish();
                }
                break;
            case R.id.share_twitter:
                Builder dialog = new Builder(this);
                View dialogView = new View(this);
                dialogView = getLayoutInflater().inflate(R.layout.comment_edittext,
                        null);
                EditText editText = (EditText) dialogView
                        .findViewById(R.id.commentEditText);
                editText.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        twitterMessage = s.toString();
                    }
                });
                dialog.setView(dialogView);
                dialog.setPositiveButton("Post",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postingTwitter = true;
                                new generatePlateTask().execute();
                            }
                        });
                dialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog.create().show();

                return false;
            case R.id.share_facebook:
                if (mSimpleFacebook.isLogin()) {
                    dialog = new Builder(this);
                    dialogView = new View(this);
                    dialogView = getLayoutInflater().inflate(
                            R.layout.comment_edittext, null);
                    editText = (EditText) dialogView
                            .findViewById(R.id.commentEditText);
                    editText.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence s, int start,
                                                  int before, int count) {
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start,
                                                      int count, int after) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            facebookMessage = s.toString();
                        }
                    });
                    dialog.setView(dialogView);
                    dialog.setPositiveButton("Post",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    postingFacebook = true;
                                    new generatePlateTask().execute();
                                }
                            });
                    dialog.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.create().show();
                } else {
                    mSimpleFacebook.login(onLoginListener);
                }
                return false;
        }
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        PackageManager pkManager = this.getPackageManager();
        try {
            PackageInfo pkgInfo = pkManager.getPackageInfo(
                    "com.twitter.android", 0);
            String getPkgInfo = pkgInfo.toString();
            if (getPkgInfo.equals("com.twitter.android")) {
                menu.findItem(R.id.share_twitter).setVisible(true);
            }
        } catch (NameNotFoundException e) {
            menu.findItem(R.id.share_twitter).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void showTwitterDialog() {
        Builder dialog = new Builder(SearchActivity.this);
        View dialogView = new View(SearchActivity.this);
        dialogView = getLayoutInflater().inflate(R.layout.comment_edittext,
                null);
        EditText editText = (EditText) dialogView
                .findViewById(R.id.commentEditText);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                facebookMessage = s.toString();
            }
        });
        dialog.setView(dialogView);
        dialog.setPositiveButton("Post",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new generatePlateTask().execute();
                    }
                });
        dialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.create().show();
    }

    class generatePlateTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog serverProgressDialog;
        private String signupURL = "http://mydrivingblows.com/app/generate_image.php";
        private String error;
        private String filePath;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            serverProgressDialog = new ProgressDialog(SearchActivity.this);
            serverProgressDialog.setMessage("Loading...");
            serverProgressDialog.setCancelable(false);
            serverProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("state", state));
            nameValuePairs.add(new BasicNameValuePair("plate", search));

            String result = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(signupURL);
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

                JSONObject plateJSON = new JSONObject(result);
                if (plateJSON.has("plate")) {
                    plateLink = plateJSON.getString("plate");
                    Log.d("Log", "plateLink = ");
                    error = "success";
                } else {
                    error = result;
                }
            } catch (Exception e) {
                Log("Server error: " + e.getMessage());
                error = result;
            }
            filePath = Environment.getExternalStorageDirectory()
                    + "/.SafeTracker/" + System.currentTimeMillis() + ".png";
            try {
                saveImage(plateLink, filePath);
            } catch (Exception e) {
                Log(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (error.equals("success")) {
                if (postingFacebook) {
                    Feed feed = new Feed.Builder()
                            .setMessage(facebookMessage)
                            .setName("MyDrivingBlows Android app")
                            .setCaption(
                                    "Tell drivers, that THEY SUCK AT DRIVING!")
                            .setDescription(
                                    "It's a service to let users post comments and photos of HORRIBLE drivers. Did that guy just cut you off in his mid-life crisis convertible? Did that that lady park in the middle of the two last remaining parking spaces in the nearest 20 mile radius of the grocery store? Is that kid driving in the left lane of the highway at half the speed of a turtle? Tell them THEY SUCK AT DRIVING!")
                            .setPicture(plateLink)
                            .setLink("http://mydrivingblows.com/").build();

                    mSimpleFacebook.publish(feed, new OnPublishListener() {

                        @Override
                        public void onFail(String reason) {

                            serverProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "Error, can't post" + reason,
                                    Toast.LENGTH_LONG).show();
                            facebookMessage = "";

                        }

                        @Override
                        public void onException(Throwable throwable) {

                            serverProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "Error, can't post" + throwable,
                                    Toast.LENGTH_LONG).show();
                            facebookMessage = "";
                        }

                        @Override
                        public void onThinking() {
                        }

                        @Override
                        public void onComplete(String id) {

                            serverProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Posted!",
                                    Toast.LENGTH_SHORT).show();
                            facebookMessage = "";
                        }
                    });

                } else if (postingTwitter) {
                    List<Intent> targetedShareIntents = new ArrayList<Intent>();
                    Intent share = new Intent(
                            Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    List<ResolveInfo> resInfo = getPackageManager()
                            .queryIntentActivities(share, 0);
                    if (!resInfo.isEmpty()) {
                        for (ResolveInfo info : resInfo) {
                            Intent targetedShare = new Intent(
                                    Intent.ACTION_SEND);
                            targetedShare.setType("image/jpeg");
                            if (info.activityInfo.packageName.toLowerCase()
                                    .contains("twi")
                                    || info.activityInfo.name.toLowerCase()
                                    .contains("twi")) {
                                if (twitterMessage != null) {
                                    targetedShare
                                            .putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    twitterMessage
                                                            + "\n\nGet MyDrivingBlows for Android: http://goo.gl/QtAvmd");
                                } else {
                                    targetedShare
                                            .putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    "Don't like this driver!"
                                                            + "\n\nGet MyDrivingBlows for Android: http://goo.gl/QtAvmd");
                                }

                                targetedShare.putExtra(Intent.EXTRA_STREAM,
                                        Uri.fromFile(new File(filePath)));
                                targetedShare
                                        .setPackage(info.activityInfo.packageName);
                                targetedShareIntents.add(targetedShare);

                            }
                        }
                        Intent chooserIntent = Intent.createChooser(
                                targetedShareIntents.remove(0),
                                "Select app to share");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                                targetedShareIntents
                                        .toArray(new Parcelable[]{}));
                        startActivity(chooserIntent);
                    }
                    serverProgressDialog.dismiss();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Error: " + error,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void saveImage(String imageUrl, String destinationFile)
            throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void Log(Object Object) {
        Log.d("Log", "" + Object);
    }
}
