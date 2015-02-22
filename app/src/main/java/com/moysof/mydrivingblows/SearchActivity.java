package com.moysof.mydrivingblows;

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

import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Feed;

public class SearchActivity extends ListActivity {

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
	public DisplayImageOptions options;
	private PhotoViewAttacher mAttacher;
	private SharedPreferences preferences;
	public boolean postingTwitter = false;
	public boolean postingFacebook = false;
	public int pos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		//TODO:getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
		setListAdapter(adapter);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);
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
					getListView().setVisibility(View.GONE);
					plate = searchEditText.getText().toString();
					itemState = stateButtonSearch.getText().toString();
					((TextView) findViewById(R.id.worstText)).setText(plate);
					findViewById(R.id.worstText).setVisibility(View.VISIBLE);
					findViewById(R.id.commentsTextView).setVisibility(
							View.VISIBLE);
					commentsLayout.removeAllViews();
					setImage(itemState);
				} else {
					getListView().setVisibility(View.GONE);
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
											+ " COMMENT");
						} else {
							((TextView) findViewById(R.id.commentsText))
									.setText(commentsArrayList.size()
											+ " COMMENTS");
						}
					}
					itemScrollView.setVisibility(View.VISIBLE);

					LayoutInflater ltInflater = getLayoutInflater();
					imgLoader = ImageLoader.getInstance();

					options = new DisplayImageOptions.Builder()
							.cacheInMemory(false).cacheOnDisc(true).build();
					ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
							SearchActivity.this)
							.defaultDisplayImageOptions(options)
							.threadPoolSize(1).build();
					imgLoader.init(config);
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
							item.findViewById(R.id.locationLayout).setTag(
									"l" + i);
							item.findViewById(R.id.locationLayout)
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
											String[] infoArray = (Html.fromHtml(infoArrayList.get(pos).replace("Today", "today"))+"").split(" � ");
											map.addMarker(new MarkerOptions()
													.position(ITEM)
													.title(search)
													.snippet("Tagged "+infoArray[1]+" by "+infoArray[0])
													.icon(BitmapDescriptorFactory
															.fromResource(R.drawable.ic_marker)));
											//TODO:getSupportActionBar().setTitle("Location");
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
									item.findViewById(R.id.locationLayout)
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
								item.findViewById(R.id.locationLayout)
								.setVisibility(View.GONE);
								Log(e);
							}
						} else {
							item.findViewById(R.id.locationLayout)
									.setVisibility(View.GONE);
						}
						TextView commentTextView = (TextView) item
								.findViewById(R.id.commentTextView);
						commentTextView.setText(String
								.valueOf(commentsArrayList.get(i)));
						if (!photosArrayList.get(i).equals("")) {
							item.findViewById(R.id.photoLayout).setVisibility(
									View.VISIBLE);
							imgLoader
									.displayImage(
											photosArrayList.get(i),
											(ImageView) item
													.findViewById(R.id.commentsImageView),
											options);
						}
						item.findViewById(R.id.commentClick).setTag(i);
						item.findViewById(R.id.commentClick)
								.setOnClickListener(commentImageListener);
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
			imgLoader = ImageLoader.getInstance();

			options = new DisplayImageOptions.Builder().cacheInMemory(false)
					.cacheOnDisc(true).build();
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					SearchActivity.this).defaultDisplayImageOptions(options)
					.threadPoolSize(1).build();
			imgLoader.init(config);
			findViewById(R.id.imageLayout).setVisibility(View.VISIBLE);
			imgLoader.displayImage(photosArrayList.get((Integer) v.getTag()),
					commentImage, options, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							findViewById(R.id.imageProgressBar).setVisibility(
									View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {
							findViewById(R.id.imageProgressBar).setVisibility(
									View.GONE);
							Toast.makeText(SearchActivity.this,
									"Can't upload image", Toast.LENGTH_LONG)
									.show();
							findViewById(R.id.imageLayout).setVisibility(
									View.GONE);
						}

						@Override
						public void onLoadingComplete(String arg0, View arg1,
								Bitmap bitmap) {
							if (bitmap != null) {
								Display display = getWindowManager()
										.getDefaultDisplay();
								Point size = new Point();
								display.getSize(size);
								int width = size.x;
								int height = size.y;
								if (bitmap.getHeight() < height
										& bitmap.getWidth() < width) {
									commentImage
											.setLayoutParams(new FrameLayout.LayoutParams(
													LayoutParams.MATCH_PARENT,
													LayoutParams.MATCH_PARENT));
								} else {
									commentImage
											.setLayoutParams(new FrameLayout.LayoutParams(
													LayoutParams.MATCH_PARENT,
													LayoutParams.WRAP_CONTENT));
								}
								findViewById(R.id.imageProgressBar)
										.setVisibility(View.GONE);
								mAttacher.update();
							}
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							findViewById(R.id.imageProgressBar).setVisibility(
									View.GONE);
							findViewById(R.id.imageLayout).setVisibility(
									View.GONE);
						}
					});
			showingImage = true;
			//TODO:getSupportActionBar().setTitle("Photo");
		}
	};
	private SimpleFacebook mSimpleFacebook;
	public String plateLink;
	protected String facebookMessage = "";
	protected String twitterMessage;

	public void state(View v) {
		Builder builder = new Builder(this);
		View view = getLayoutInflater().inflate(R.layout.dialog_state, null);
		builder.setView(view);
		builder.setTitle("Select State:");
		((ListView) view.findViewById(R.id.statesList))
				.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
						android.R.layout.simple_list_item_single_choice,
						getResources().getStringArray(R.array.states_title)));
		((ListView) view.findViewById(R.id.statesList))
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> view, View parent,
							int position, long id) {
						state = getResources().getStringArray(
								R.array.states_value)[position];
						stateButtonSearch.setText(state);
						stateButtonSearch
								.setCompoundDrawablesWithIntrinsicBounds(
										null,
										null,
										getResources().getDrawable(
												R.drawable.state_selected),
										null);
						stateDialog.dismiss();
					}
				});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						stateDialog.dismiss();
					}
				});
		builder.setNeutralButton("Reset",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						state = "";
						stateButtonSearch
								.setCompoundDrawablesWithIntrinsicBounds(0, 0,
										R.drawable.state, 0);
						stateButtonSearch.setText(getResources().getString(
								R.string.state_btn));
						stateDialog.dismiss();
					}
				});
		stateDialog = builder.create();
		stateDialog.show();
	}

	@Override
	public void onBackPressed() {
		//TODO:getSupportActionBar().setTitle(R.string.title_activity_search);
		if (showingImage) {
			findViewById(R.id.imageLayout).setVisibility(View.GONE);
			imgLoader.displayImage(null, commentImage);
			showingImage = false;
			imgLoader.destroy();
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
			//TODO:getSupportActionBar().setTitle(R.string.title_activity_search);
			if (showingImage) {
				findViewById(R.id.imageLayout).setVisibility(View.GONE);
				imgLoader.displayImage(null, commentImage);
				showingImage = false;
				imgLoader.destroy();
			} else if (findViewById(R.id.mapLayout).getVisibility() == View.VISIBLE) {
				findViewById(R.id.mapLayout).setVisibility(View.GONE);
			} else {
				finish();
			}
			break;
		case R.id.add:
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
			return false;
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
				//mSimpleFacebook.login(onLoginListener);
			}
			return false;
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
			editor.putString("username", "");
			editor.putString("password", "");
			editor.commit();
			startActivity(new Intent(SearchActivity.this, LoginActivity.class));
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
			Builder builder = new Builder(this);
			builder.setTitle("About");
			builder.setIcon(R.drawable.sm_logo);
			dialogView = new View(this);
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
			builder = new Builder(this);
			builder.setTitle("Terms of Service");
			dialogView = new View(this);
			dialogView = getLayoutInflater().inflate(R.layout.dialog_terms,
					null);
			builder.setView(dialogView);
			builder.setNeutralButton("Close", null);
			builder.create().show();
			break;
		}
		return true;
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

	/*OnLoginListener onLoginListener = new SimpleFacebook.OnLoginListener() {

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
		public void onNotAcceptingPermissions() {
			Toast.makeText(getApplicationContext(),
					"Error: You didn't accept permissions", Toast.LENGTH_LONG)
					.show();
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
	};*/

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

					/*mSimpleFacebook.publish(feed, new OnPublishListener() {

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
					});*/

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
										.toArray(new Parcelable[] {}));
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
	protected void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		/*Permissions[] permissions = new Permissions[] { Permissions.BASIC_INFO,
				Permissions.EMAIL, Permissions.PUBLISH_ACTION };
		SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
				.setAppId(getResources().getString(R.string.app_id))
				.setPermissions(permissions).build();
		SimpleFacebook.setConfiguration(configuration);*/
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
