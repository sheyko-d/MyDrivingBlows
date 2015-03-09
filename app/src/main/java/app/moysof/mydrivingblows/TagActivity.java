package app.moysof.mydrivingblows;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static app.moysof.mydrivingblows.CommonUtilities.convertDpToPixel;

public class TagActivity extends ActionBarActivity {

    Button uploadButton;
    private String imagePath = "";
    private Bitmap myBitmap;
    private File imgFile;
    private EditText plateEditText;
    private ImageView photoImageView;
    private Button postButton;
    private Dialog stateDialog;
    private String state = "";
    private Button stateButton;
    private EditText postEditText;
    private String plate = "";
    private String post = "";
    private InputStream is;
    private String result = null;
    private String error = "";
    private String server_result;
    private SharedPreferences preferences;
    private String email;
    private Uri imageFileUri;
    public ProgressDialog serverProgressDialog;
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    private String uploadURL = "http://mydrivingblows.com/app/upload.php";
    private ProgressBar imageProgressBar;
    InputStream imageStream;
    private Intent data_image;
    public Matrix matrix;
    public float rotation = 0;
    private String oldPlate = "";
    private String oldPost = "";
    private String[] statesArray;
    private ActionBarDrawerToggle mDrawerToggle;
    protected int drawerPosition = 2;
    private DrawerLayout drawer;
    private DrawerListViewAdapter adapter;
    private ListView drawerList;
    public static final String SPEECH = "app.moysof.mydrivingblows.SPEECH";
    private Float longitude;
    private Float latitude;
    private View mImageLayout;
    private ImageView mTagStateImg;
    private int mStatePos = -1;
    private String mVideoPath = "";
    private VideoView mVideoView;
    private boolean mClearIsVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration config = getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_tag_land);
        } else {
            setContentView(R.layout.activity_tag);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        toolbar.setContentInsetsAbsolute(
                convertDpToPixel(72, this), 0);
        setSupportActionBar(toolbar);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        mTagStateImg = (ImageView) findViewById(R.id.tagStateImg);
        mVideoView = (VideoView) findViewById(R.id.videoView);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        adapter = new DrawerListViewAdapter(this, drawerPosition);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(adapter);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer,
                R.string.app_name, R.string.app_name) {

            public void onDrawerClosed(View view) {
            }

            public void onDrawerOpened(View drawerView) {
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

                drawer.closeDrawer(drawerList);

                if (position == 1 || position == 3 || position == 5 || position == 6 || position == 7 || position == 8) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            if (position == 1) {
                                startActivity(new Intent(TagActivity.this,
                                        FeedActivity.class));
                                finish();
                            } else if (position == 3) {
                                startActivity(new Intent(TagActivity.this,
                                        NewsActivity.class));
                                finish();
                            } else if (position == 5) {
                                startActivity(new Intent(TagActivity.this,
                                        SettingsActivity.class));
                            } else if (position == 6) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(TagActivity.this);
                                builder = new AlertDialog.Builder(TagActivity.this);
                                builder.setTitle("Terms of Service");
                                View dialogView = new View(TagActivity.this);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(TagActivity.this);
                                builder.setTitle("About");
                                View dialogView = new View(TagActivity.this);
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
                                } catch (PackageManager.NameNotFoundException e) {
                                    ((TextView) dialogView.findViewById(R.id.dialogVersionText))
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

        refreshViews();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Intent searchActivity = getIntent();

        if (searchActivity.getStringExtra("Plate") != null) {
            plate = searchActivity.getStringExtra("Plate");
            plateEditText.setText(plate);
            postEditText.setFocusableInTouchMode(true);
        }

        if (searchActivity.getStringExtra("State") != null) {
            findViewById(R.id.tagStateLayout).setVisibility(View.VISIBLE);
            state = searchActivity.getStringExtra("State");
            setImageSmall(mTagStateImg, state);
            stateButton.setVisibility(View.GONE);
        }

        statesArray = getResources().getStringArray(R.array.states_value);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void refreshViews() {
        if (plateEditText != null) {
            oldPlate = plateEditText.getText().toString();
        }
        if (postEditText != null) {
            oldPost = postEditText.getText().toString();
        }
        photoImageView = (ImageView) findViewById(R.id.photoImageView);
        postButton = (Button) findViewById(R.id.postButton);
        stateButton = (Button) findViewById(R.id.stateButton);
        plateEditText = (EditText) findViewById(R.id.plateEditText);
        postEditText = (EditText) findViewById(R.id.postEditText);
        imageProgressBar = (ProgressBar) findViewById(R.id.imageProgressBar);

        mImageLayout = findViewById(R.id.imageLayout);
        if (!(imagePath.equals(""))) {
            new loadPhotoTask().execute();
        }

        try {
            plateEditText.setText(oldPlate);
        } catch (Exception e) {

        }
        try {
            postEditText.setText(oldPost);
        } catch (Exception e) {

        }
        mImageLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                registerForContextMenu(mImageLayout);
                openContextMenu(mImageLayout);
                unregisterForContextMenu(mImageLayout);
            }
        });

        plateEditText.addTextChangedListener(new TextWatcher() {
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
                    plateEditText.setText(s);
                    plateEditText.setSelection(s.length());
                }
            }
        });

        postButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOnline()) {
                    Toast.makeText(TagActivity.this, "Error: Network problem",
                            Toast.LENGTH_SHORT).show();
                } else if (state.equals("")) {
                    Toast.makeText(TagActivity.this, "Error: Choose state",
                            Toast.LENGTH_SHORT).show();
                } else if (plateEditText.getText().toString().equals("")) {
                    Toast.makeText(TagActivity.this,
                            "Error: Enter plate number", Toast.LENGTH_SHORT)
                            .show();
                } else if (postEditText.getText().toString().equals("")) {
                    Toast.makeText(TagActivity.this,
                            "Error: Enter your comment", Toast.LENGTH_SHORT)
                            .show();

                } else {
                    new uploadServerTask().execute();

                }
            }
        });
        if (!state.equals("")) {
            setImageSmall(mTagStateImg, state);
            findViewById(R.id.tagStateLayout).setVisibility(View.VISIBLE);
            stateButton.setVisibility(View.GONE);
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null);
    }

    public void state(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_single_choice,
                getResources().getStringArray(R.array.states_title));
        builder.setSingleChoiceItems(arrayAdapter, mStatePos,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mStatePos = which;
                        state = statesArray[which];
                        setImageSmall(mTagStateImg, state);
                        findViewById(R.id.tagStateLayout).setVisibility(View.VISIBLE);
                        stateButton.setVisibility(View.GONE);
                        /*stateButton.setCompoundDrawablesWithIntrinsicBounds(
                                null,
								null,
								getResources().getDrawable(
										R.drawable.state_selected), null);*/
                        stateDialog.dismiss();
                        Log(state);
                    }
                });
        builder.setTitle("Select state:");
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        stateDialog.dismiss();
                    }
                });
        stateDialog = builder.create();
        stateDialog.show();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mImageLayout.setClickable(false);
            ((FrameLayout) mImageLayout).setForeground(null);
            mImageLayout.setOnClickListener(null);
            mClearIsVisible = true;
            supportInvalidateOptionsMenu();

            switch (requestCode) {
                case CommonUtilities.CODE_PICK_PHOTO:
                    imagePath = "";
                    if (data != null) {
                        data_image = data;
                        new loadPhotoTask().execute();
                    }
                    break;
                case CommonUtilities.CODE_TAKE_PHOTO:
                    imagePath = Environment.getExternalStorageDirectory()
                            + "/MyDrivingBlows/pic.jpg";
                    imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        new loadPhotoTask().execute();
                    }
                    break;
                case CommonUtilities.CODE_PICK_VIDEO:
                    Uri selectedVideoURI = data.getData();

                    mVideoPath = getPath(getApplicationContext(), selectedVideoURI);

                    findViewById(R.id.videoViewLayout).setVisibility(View.VISIBLE);
                    mVideoView.setVideoPath(mVideoPath);
                    findViewById(R.id.imageProgressBar).setVisibility(View.VISIBLE);
                    mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            findViewById(R.id.imageProgressBar).setVisibility(View.GONE);
                            MediaController mediaController = new MediaController(
                                    TagActivity.this);
                            mVideoView.setMediaController(mediaController);
                            mediaController.show();
                        }
                    });

                    Bitmap thumbnailBitmap =
                            ThumbnailUtils.createVideoThumbnail(mVideoPath,
                                    MediaStore.Video.Thumbnails.MINI_KIND);

                    String root = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(root + "/MyDrivingBlows/Thumbnails/");
                    if (!myDir.exists())
                        myDir.mkdirs();
                    String fname = "Thumbnail" + System.currentTimeMillis() + ".jpg";
                    File file = new File(myDir, fname);
                    if (file.exists()) file.delete();
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        thumbnailBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                        out.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    imagePath = root + "/MyDrivingBlows/Thumbnails/" + fname;
                    Log("imagePath = " + imagePath);
                    break;
                case CommonUtilities.CODE_TAKE_VIDEO:
                    Uri videoURI = data.getData();
                    mVideoPath = getPath(getApplicationContext(), videoURI);

                    findViewById(R.id.videoViewLayout).setVisibility(View.VISIBLE);
                    mVideoView.setVideoPath(mVideoPath);
                    findViewById(R.id.imageProgressBar).setVisibility(View.VISIBLE);
                    mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            findViewById(R.id.imageProgressBar).setVisibility(View.GONE);
                            MediaController mediaController = new MediaController(
                                    TagActivity.this);
                            mVideoView.setMediaController(mediaController);
                            mediaController.show();
                        }
                    });
                    break;
            }
        }
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    class loadPhotoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (imagePath.equals("")) {
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(
                        data_image.getData(), proj, null, null, null);
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                imagePath = cursor.getString(column_index);
            }
            try {
                ExifInterface exif = new ExifInterface(imagePath);
                String LATITUDE = exif
                        .getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String LATITUDE_REF = exif
                        .getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                String LONGITUDE = exif
                        .getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                String LONGITUDE_REF = exif
                        .getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

                if ((LATITUDE != null) && (LATITUDE_REF != null)
                        && (LONGITUDE != null) && (LONGITUDE_REF != null)) {

                    if (LATITUDE_REF.equals("N")) {
                        latitude = convertToDegree(LATITUDE);
                    } else {
                        latitude = 0 - convertToDegree(LATITUDE);
                    }

                    if (LONGITUDE_REF.equals("E")) {
                        longitude = convertToDegree(LONGITUDE);
                    } else {
                        longitude = 0 - convertToDegree(LONGITUDE);
                    }

                    Log(latitude + ", " + longitude);

                } else {
                    Log("Empty coordinates");
                }

            } catch (Exception e) {
                Log(e);
            }
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inJustDecodeBounds = true;
            myBitmap = BitmapFactory.decodeFile(imagePath, bmpFactoryOptions);

            int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight
                    / (float) photoImageView.getHeight());
            int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
                    / (float) photoImageView.getWidth());

            if (heightRatio > 1 || widthRatio > 1) {
                if (heightRatio > widthRatio) {
                    bmpFactoryOptions.inSampleSize = heightRatio;
                } else {
                    bmpFactoryOptions.inSampleSize = widthRatio;
                }
            }

            bmpFactoryOptions.inJustDecodeBounds = false;
            myBitmap = BitmapFactory.decodeFile(imagePath, bmpFactoryOptions);
            matrix = new Matrix();
            matrix.postRotate(rotation);
            myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(),
                    myBitmap.getHeight(), matrix, true);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            photoImageView.setImageBitmap(myBitmap);
            photoImageView.setOnTouchListener(new OnTouchListener() {

                private int PointerCount;
                private int x;
                private int y;
                private int x2;
                private int y2;
                private int first_angle;
                private int current_rotation;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    PointerCount = event.getPointerCount();
                    x = (int) event.getX(0);
                    y = (int) event.getY(0);
                    if (PointerCount >= 2) {
                        x2 = (int) event.getX(1);
                        y2 = (int) event.getY(1);
                        int angle = (int) Math.toDegrees(Math.atan2(x2 - x, y2
                                - y));

                        if (angle < 0) {
                            angle += 360;
                        }
                        if (first_angle == 0) {
                            first_angle = angle;
                        }
                        photoImageView.setRotation(-angle + first_angle
                                + current_rotation);
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        first_angle = 0;
                        current_rotation = 0;
                    }
                    return false;
                }
            });
            imageProgressBar.setVisibility(View.INVISIBLE);
            findViewById(R.id.rotate1).setVisibility(View.VISIBLE);
            findViewById(R.id.rotate2).setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("UseValueOf")
    private Float convertToDegree(String stringDMS) {
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;

    }

    ;

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return (String.valueOf(latitude) + ", " + String.valueOf(longitude));
    }

    public int getlatitudeE6() {
        return (int) (latitude * 1000000);
    }

    public int getlongitudeE6() {
        return (int) (longitude * 1000000);
    }

    public void rotate1(View v) {
        rotation += 90;
        new loadPhotoTask().execute();
    }

    public void rotate2(View v) {
        rotation -= 90;
        new loadPhotoTask().execute();
    }

    class uploadServerTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            serverProgressDialog = new ProgressDialog(TagActivity.this);
            serverProgressDialog.setMessage("Loading...");
            serverProgressDialog.setCancelable(false);
            serverProgressDialog.show();
            plate = plateEditText.getText().toString();
            post = postEditText.getText().toString();
            Log("User: " + preferences.getString("username", ""));
            email = preferences.getString("email", "");
        }

        @SuppressWarnings("deprecation")
        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(uploadURL);
                MultipartEntityBuilder multipartEntity = MultipartEntityBuilder
                        .create();
                if (!(imagePath.equals(""))) {
                    multipartEntity.addPart("image", new FileBody(
                            new File(imagePath)));
                    Log("imagePath2 = " + imagePath);
                }

                if (!(mVideoPath.equals(""))) {
                    multipartEntity.addPart("video", new FileBody(
                            new File(mVideoPath)));
                }
                try {
                    multipartEntity.addPart("plate", new StringBody(plate));
                    multipartEntity.addPart("post", new StringBody(post));
                    multipartEntity.addPart("state", new StringBody(state));
                    multipartEntity.addPart("email", new StringBody(email));
                    multipartEntity.addPart("lat",
                            new StringBody(latitude + ""));
                    multipartEntity.addPart("lng", new StringBody(longitude
                            + ""));
                } catch (UnsupportedEncodingException e) {
                    Log(e);
                }

                httpPost.setEntity(multipartEntity.build());

                try {
                    HttpResponse response = client.execute(httpPost);
                    String responseString = EntityUtils.toString(response
                            .getEntity());
                    Log("Response:" + responseString);
                    JSONObject JObject = new JSONObject(responseString);
                    server_result = JObject.getString("result");
                } catch (ClientProtocolException e) {
                    Log("ClientProtocolException: " + e);
                } catch (IOException e) {
                    Log("IOException: " + e);
                }
                error = null;
            } catch (Exception e) {
                error = e + "";
                Log(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            serverProgressDialog.dismiss();
            if (error == null) {
                if (server_result.equals("success")) {
                    Toast.makeText(TagActivity.this, "Comment posted!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    Intent searchActivity = new Intent(TagActivity.this,
                            SearchActivity.class);
                    searchActivity.putExtra("Tag", plate);
                    searchActivity.putExtra("State", state);
                    startActivity(searchActivity);
                } else {
                    Toast.makeText(TagActivity.this, "Error: " + server_result,
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(TagActivity.this, "Error: Upload Fail" + error,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void Log(Object Object) {
        Log.d("Log", "" + Object);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_tag);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_tag_land);
        }
        refreshViews();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        adapter = new DrawerListViewAdapter(this, drawerPosition);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(adapter);
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.image_gallery:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, CommonUtilities.CODE_PICK_PHOTO);
                return true;
            case R.id.image_camera:
                final LocationManager manager = (LocationManager) TagActivity.this
                        .getSystemService(Context.LOCATION_SERVICE);

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        & !manager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    buildAlertMessageNoGps();
                } else {
                    Log(manager.isProviderEnabled(LocationManager.GPS_PROVIDER));
                    launchCamera();
                }
                return true;
            case R.id.video_gallery:
                Intent videoPickerIntent = new Intent(Intent.ACTION_PICK);
                videoPickerIntent.setType("video/*");
                startActivityForResult(videoPickerIntent, CommonUtilities.CODE_PICK_VIDEO);
                return true;
            case R.id.video_camera:
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent,
                            CommonUtilities.CODE_TAKE_VIDEO);
                }
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tag, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_clear).setVisible(mClearIsVisible);
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
            case R.id.action_clear:
                clearPhoto();

                clearVideo();

                mClearIsVisible = false;
                invalidateOptionsMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logIn(View v) {
        if (preferences.getString("username", "").equals("")) {
            startActivity(new Intent(TagActivity.this,
                    LoginActivity.class));
        } else {
            SharedPreferences.Editor editor = preferences.edit();
            boolean usernameChanged = preferences.getBoolean(
                    "username_changed", false);
            editor.clear().commit();
            editor.putBoolean("username_changed", usernameChanged).commit();
            editor.putBoolean("agree", true).commit();
            editor.commit();
            startActivity(new Intent(TagActivity.this, LoginActivity.class));
        }
    }

    private void clearPhoto() {

        mImageLayout.setClickable(true);
        mImageLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                registerForContextMenu(mImageLayout);
                openContextMenu(mImageLayout);
                unregisterForContextMenu(mImageLayout);
            }
        });

        int[] attrs = new int[]{android.R.attr.selectableItemBackground};
        TypedArray ta = obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = ta.getDrawable(0);
        ta.recycle();
        ((FrameLayout) mImageLayout).setForeground(drawableFromTheme);

        photoImageView.setImageBitmap(null);
        imagePath = "";

        findViewById(R.id.rotate1).setVisibility(View.GONE);
        findViewById(R.id.rotate2).setVisibility(View.GONE);
    }

    private void clearVideo() {
        mVideoView.setMediaController(null);
        findViewById(R.id.videoViewLayout).setVisibility(View.GONE);
        mVideoPath = "";
    }

    private void launchCamera() {
        File newD = new File(Environment.getExternalStorageDirectory()
                + File.separator + "MyDrivingBlows");
        if (!newD.exists()) {
            newD.mkdirs();
        }
        imagePath = newD + "/pic.jpg";
        imageFileUri = Uri.parse("file:///" + imagePath);
        Intent cameraIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                imageFileUri);
        startActivityForResult(cameraIntent, CommonUtilities.CODE_TAKE_PHOTO);
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS is disabled");
        View dialogView = new View(this);
        dialogView = getLayoutInflater().inflate(R.layout.dialog_geotagging,
                null);
        builder.setView(dialogView);
        builder.setCancelable(true);
        builder.setPositiveButton("Enable",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent gpsOptionsIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        launchCamera();
                        startActivity(gpsOptionsIntent);
                    }
                });
        builder.setNegativeButton("Skip",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        launchCamera();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(drawerList)) {
            drawer.closeDrawer(drawerList);
        } else {
            startActivity(new Intent(TagActivity.this, FeedActivity.class));
            super.onBackPressed();
        }
    }
}