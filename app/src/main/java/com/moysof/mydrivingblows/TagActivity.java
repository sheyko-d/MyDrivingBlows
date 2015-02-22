package com.moysof.mydrivingblows;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TagActivity extends ActionBarActivity {

	Button uploadButton;
	private String path = "";
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
	protected int drawerPosition = 4;
	private DrawerLayout drawer;
	private DrawerListViewAdapter adapter;
	private ListView drawerList;
	public static final String SPEECH = "com.moysof.mydrivingblows.SPEECH";
	private Float longitude;
	private Float latitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Configuration config = getResources().getConfiguration();
		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.activity_tag_land);
		} else {
			setContentView(R.layout.activity_tag);
		}

		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		adapter = new DrawerListViewAdapter(this, drawerPosition);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		drawerList.setAdapter(adapter);
		mDrawerToggle = new ActionBarDrawerToggle(this, drawer,
				R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {

			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(R.string.title_activity_feed);
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(R.string.app_name);
			}
		};
		drawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				drawerPosition = position;
				if (preferences.getString("username", "").equals("")) {
					if (drawerPosition == 0) {
						startActivity(new Intent(TagActivity.this,
								LoginActivity.class));
					} else if (drawerPosition == 6) {
						return;
					}
				}
				if (position == 2 || position == 6) {
					adapter = new DrawerListViewAdapter(TagActivity.this,
							drawerPosition);
					drawerList.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					drawer.closeDrawer(drawerList);

					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							if (drawerPosition == 6) {
								startActivity(new Intent(TagActivity.this,
										NewsActivity.class));
							} else if (drawerPosition == 2) {
								startActivity(new Intent(TagActivity.this,
										FeedActivity.class));
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

		refreshViews();

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		Intent searchActivity = getIntent();

		if (searchActivity.getStringExtra("Plate") != null) {
			plate = searchActivity.getStringExtra("Plate");
			plateEditText.setText(plate);
			postEditText.setFocusableInTouchMode(true);
		}

		if (searchActivity.getStringExtra("State") != null) {
			state = searchActivity.getStringExtra("State");
			stateButton.setText(state);
			stateButton
					.setCompoundDrawablesWithIntrinsicBounds(
							null,
							null,
							getResources().getDrawable(
									R.drawable.state_selected), null);
		}

		statesArray = getResources().getStringArray(R.array.states_value);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
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
				Toast.makeText(TagActivity.this, "You must log in first",
						Toast.LENGTH_LONG).show();
				startActivity(new Intent(TagActivity.this, LoginActivity.class));
			} else {
				startActivity(new Intent(TagActivity.this, FeedActivity.class));
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
			startActivity(new Intent(TagActivity.this, LoginActivity.class));
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
		if (!(path.equals(""))) {
			new loadPhotoTask().execute();
		}

		registerForContextMenu(photoImageView);
		try {
			plateEditText.setText(oldPlate);
		} catch (Exception e) {

		}
		try {
			postEditText.setText(oldPost);
		} catch (Exception e) {

		}
		photoImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openContextMenu(photoImageView);
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
			stateButton.setText(state);
			stateButton
					.setCompoundDrawablesWithIntrinsicBounds(
							null,
							null,
							getResources().getDrawable(
									R.drawable.state_selected), null);
		}
	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return (cm.getActiveNetworkInfo() != null);
	}

	public void state(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = getLayoutInflater().inflate(R.layout.dialog_state, null);
		builder.setView(view);
		((ListView) view.findViewById(R.id.statesList))
				.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
						android.R.layout.simple_list_item_single_choice,
						getResources().getStringArray(R.array.states_title)));
		((ListView) view.findViewById(R.id.statesList))
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> view, View parent,
							int position, long id) {
						state = statesArray[position];
						stateButton.setText(state);
						stateButton.setCompoundDrawablesWithIntrinsicBounds(
								null,
								null,
								getResources().getDrawable(
										R.drawable.state_selected), null);
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0:
				path = "";
				if (data != null) {
					data_image = data;
					new loadPhotoTask().execute();
				}
				break;
			case 1:
				path = Environment.getExternalStorageDirectory()
						+ "/MyDrivingBlows/pic.jpg";
				imgFile = new File(path);
				if (imgFile.exists()) {
					new loadPhotoTask().execute();
				}
				break;
			}
		}
	}

	class loadPhotoTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			imageProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (path.equals("")) {
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(
						data_image.getData(), proj, null, null, null);
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				path = cursor.getString(column_index);
			}
			try {
				ExifInterface exif = new ExifInterface(path);
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
			myBitmap = BitmapFactory.decodeFile(path, bmpFactoryOptions);

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
			myBitmap = BitmapFactory.decodeFile(path, bmpFactoryOptions);
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

	};

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
				if (!(path.equals(""))) {
					multipartEntity.addPart("image", new FileBody(
							new File(path)));
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
		case R.id.gallery:
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, 0);
			return true;
		case R.id.camera:
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
		}
		return false;
	}

	private void launchCamera() {
		File newD = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "MyDrivingBlows");
		if (!newD.exists()) {
			newD.mkdirs();
		}
		path = newD + "/pic.jpg";
		imageFileUri = Uri.parse("file:///" + path);
		Intent cameraIntent = new Intent(
				MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
				imageFileUri);
		startActivityForResult(cameraIntent, 1);
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("GPS is disabled");
		View dialogView = new View(this);
		dialogView = getLayoutInflater().inflate(R.layout.dialog_geotagging,
				null);
		builder.setView(dialogView);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setCancelable(true);
		builder.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent gpsOptionsIntent = new Intent(
								android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						launchCamera();
						startActivity(gpsOptionsIntent);
					}
				});
		builder.setNegativeButton("No, skip",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						launchCamera();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}
}