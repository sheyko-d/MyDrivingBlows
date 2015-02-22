package com.moysof.mydrivingblows;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Profile;

public class LoginActivity extends FragmentActivity implements
		ActionBar.TabListener {

	AppSectionsPagerAdapter mAppSectionsPagerAdapter;

	ViewPager mViewPager;

	private static SimpleFacebook mSimpleFacebook;

	private static Context context;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		/*TODO:Permissions[] permissions = new Permissions[] { Permissions.BASIC_INFO,
				Permissions.EMAIL, };
		SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
				.setAppId(getResources().getString(R.string.app_id))
				.setPermissions(permissions).build();
		SimpleFacebook.setConfiguration(configuration);
		mSimpleFacebook.logout(new OnLogoutListener() {

			@Override
			public void onFail(String reason) {
			}

			@Override
			public void onException(Throwable throwable) {
			}

			@Override
			public void onThinking() {
			}

			@Override
			public void onLogout() {
			}
		});*/

		context = getApplicationContext();

		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(
				getSupportFragmentManager());

		/*TODO: final ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mAppSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}*/
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i) {
			case 0:
				return new LoginFragment();
			default:
				Fragment fragment = new SignupFragment();
				return fragment;
			}
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (position == 0) {
				return context.getResources().getString(R.string.login);
			} else {
				return context.getResources().getString(R.string.sign_up);
			}
		}
	}

	public static class LoginFragment extends Fragment {

		private EditText usernameEditTextLogin;
		private Button joinButton;
		private EditText passwordEditTextLogin;
		private CheckBox rememberCheckBox;
		private SharedPreferences preferences;
		private Editor editor;
		private String loginURL = "http://mydrivingblows.com/app/login.php";
		private String username = "";
		private String email = "";
		private String city = "";
		private String state = "";
		private String fname = "";
		private String lname = "";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_login,
					container, false);

			usernameEditTextLogin = (EditText) rootView
					.findViewById(R.id.usernameEditTextLogin);

			rootView.findViewById(R.id.authButton).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							//TODO:mSimpleFacebook.login(onLoginListener);
						}
					});

			joinButton = (Button) rootView.findViewById(R.id.joinButton);
			joinButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					logIn();
				}
			});

			passwordEditTextLogin = (EditText) rootView
					.findViewById(R.id.passwordEditTextLogin);

			passwordEditTextLogin
					.setOnEditorActionListener(new EditText.OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if (actionId == EditorInfo.IME_ACTION_DONE) {
								logIn();
								return true;
							}
							return false;
						}
					});

			rememberCheckBox = (CheckBox) rootView
					.findViewById(R.id.rememberCheckBox);

			preferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());

			editor = preferences.edit();

			rememberCheckBox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							editor = preferences.edit();
							if (isChecked) {
								editor.putBoolean("remember", true);
							} else {
								editor.putBoolean("remember", false);
							}
							editor.commit();
						}

					});

			return rootView;
		}

		/*TODO: OnLoginListener onLoginListener = new SimpleFacebook.OnLoginListener() {

			@Override
			public void onFail(String reason) {
				Log(reason);
			}

			@Override
			public void onException(Throwable throwable) {
				Log("Bad thing happened: " + throwable);
			}

			@Override
			public void onThinking() {
				Log("In progress");
			}

			@Override
			public void onLogin() {
				Properties properties = new Properties.Builder()
						.add(Properties.FIRST_NAME).add(Properties.LAST_NAME)
						.add(Properties.EMAIL).add(Properties.PICTURE)
						.add(Properties.LOCATION).build();

				mSimpleFacebook.getProfile(properties,
						new OnProfileRequestListener() {

							@Override
							public void onFail(String reason) {
							}

							@Override
							public void onException(Throwable throwable) {
							}

							@Override
							public void onThinking() {
							}

							@Override
							public void onComplete(Profile profile) {
								username = profile.getFirstName() + " "
										+ profile.getLastName();
								editor.putString("username", username);
								email = profile.getEmail();
								editor.putString("email", email);
								editor.putString("picture",
										profile.getPicture());
								editor.putBoolean("from_facebook", true);
								editor.commit();
								String location = profile.getLocation()
										.getName();
								fname = profile.getFirstName();
								lname = profile.getLastName();
								if (location != null) {
									try {
										String[] locationArray = location
												.split(", ");
										ArrayList<String> states_title = new ArrayList<String>(
												Arrays.asList(getResources()
														.getStringArray(
																R.array.states_title)));
										state = getResources().getStringArray(
												R.array.states_value)[states_title
												.indexOf(locationArray[1])];
										city = locationArray[0];

									} catch (Exception e) {

									}
								}
								if (isOnline()) {
									new connectServerSignupTask().execute();
								} else {
									Toast.makeText(getActivity(),
											"Error: Network problem",
											Toast.LENGTH_SHORT).show();
								}

							}
						});
			}

			@Override
			public void onNotAcceptingPermissions() {
				Log("User didn't accept read permissions");
			}

		};*/

		class connectServerSignupTask extends AsyncTask<Void, Void, Void> {

			private ProgressDialog serverProgressDialog;
			private String signupURL = "http://mydrivingblows.com/app/signup.php";
			private String error;
			private String fd_username;
			private String fd_password;
			private String fd_email;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				serverProgressDialog = new ProgressDialog(getActivity());
				serverProgressDialog.setMessage("Loading...");
				serverProgressDialog.setCancelable(false);
				serverProgressDialog.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs
						.add(new BasicNameValuePair("username", username));
				nameValuePairs.add(new BasicNameValuePair("email", email));
				if (state != null) {
					nameValuePairs.add(new BasicNameValuePair("state", state));
				}
				if (city != null) {
					nameValuePairs.add(new BasicNameValuePair("city", city));
				}
				nameValuePairs.add(new BasicNameValuePair("fname", fname));
				nameValuePairs.add(new BasicNameValuePair("lname", lname));
				nameValuePairs
						.add(new BasicNameValuePair("from_facebook", "1"));
				nameValuePairs.add(new BasicNameValuePair("date", ""
						+ new SimpleDateFormat("yyyy-MM-dd").format(Calendar
								.getInstance().getTime())));

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

					JSONObject jObject = new JSONObject(result);
					Log(jObject);
					String server_result = jObject.getString("result");
					if (server_result.equals("success")) {
						error = "";
						fd_username = jObject.getString("username");
						fd_email = jObject.getString("email");
						if (jObject.has("password")) {
							fd_password = jObject.getString("password");
						}
					} else {
						error = server_result;
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
					Intent feedActivity = new Intent(getActivity(),
							FeedActivity.class);
					feedActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					Toast.makeText(getActivity(), "Welcome, " + fname + "!",
							Toast.LENGTH_SHORT).show();
					startActivity(feedActivity);

					if (!preferences.getBoolean("username_changed", false)
							& fd_username.contains(" ")) {// it means user is
															// from Facebook
						NotificationCompat.Builder builder = new NotificationCompat.Builder(
								getActivity())
								.setSmallIcon(R.drawable.ic_launcher)
								.setTicker(
										"Don't want to show your Facebook name?")
								.setContentTitle("Tap to create a username")
								.setContentText("And use app incognito")
								.setAutoCancel(true);

						Intent notificationIntent = new Intent(getActivity(),
								UsernameActivity.class);
						notificationIntent
								.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						PendingIntent contentIntent = PendingIntent
								.getActivity(getActivity(), 0,
										notificationIntent,
										PendingIntent.FLAG_UPDATE_CURRENT);
						builder.setContentIntent(contentIntent);
						NotificationManager manager = (NotificationManager) getActivity()
								.getSystemService(Context.NOTIFICATION_SERVICE);
						manager.notify(0, builder.build());
						editor.putBoolean("username_changed", true).commit();
					}

					getActivity().finish();

				} else {
					boolean usernameChanged = preferences.getBoolean("username_changed", false);
					editor.clear().commit();
					editor.putBoolean("agree", true).commit();
					editor.putBoolean("username_changed", usernameChanged).commit();
					Toast.makeText(getActivity(), "Error: " + error,
							Toast.LENGTH_LONG).show();
					getActivity().finish();
				}
			}

			public void savePreferences() {
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(getActivity());

				Editor editor = preferences.edit();
				editor = preferences.edit();
				editor.putString("username", fd_username);
				editor.putString("password", fd_password);
				editor.putString("email", fd_email);
				editor.commit();
			}
		}

		public void logIn() {
			if (isOnline()) {
				if (usernameEditTextLogin.getText().toString().equals("")
						|| passwordEditTextLogin.getText().toString()
								.equals("")) {
					Toast.makeText(getActivity(),
							"Error: Some fields are empty", Toast.LENGTH_LONG)
							.show();
				} else {
					if (isOnline()) {
						new connectServerLoginTask().execute();
					} else {
						Toast.makeText(getActivity(), "Error: Network problem",
								Toast.LENGTH_SHORT).show();
					}
					if (!(rememberCheckBox.isChecked())) {
						editor.putBoolean("remember", false).commit();
					}
				}
			} else {
				Toast.makeText(getActivity(),
						"Error: Check your internet connection",
						Toast.LENGTH_LONG).show();
			}
		}

		public boolean isOnline() {
			ConnectivityManager cm = (ConnectivityManager) getActivity()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting()) {
				return true;
			}
			return false;
		}

		class connectServerLoginTask extends AsyncTask<Void, Void, Void> {

			private String error;
			private String fd_fname;
			private String fd_username;
			private String fd_password;
			private String fd_email;
			private ProgressDialog serverProgressDialogLogin;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				serverProgressDialogLogin = new ProgressDialog(getActivity());
				serverProgressDialogLogin.setMessage("Loading...");
				serverProgressDialogLogin.setCancelable(false);
				serverProgressDialogLogin.show();
				InputMethodManager imm = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						passwordEditTextLogin.getWindowToken(), 0);
				imm.hideSoftInputFromWindow(
						usernameEditTextLogin.getWindowToken(), 0);
			}

			@Override
			protected Void doInBackground(Void... params) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs.add(new BasicNameValuePair("username",
						usernameEditTextLogin.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("password",
						passwordEditTextLogin.getText().toString()));

				String result = null;
				try {
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(loginURL);
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

					JSONArray jArray = new JSONArray(result);
					JSONObject json_data = null;
					String server_result = jArray.getJSONObject(0).getString(
							"result");
					if (server_result.equals("success")) {
						error = "";
						for (int i = 0; i < jArray.length(); i++) {
							json_data = jArray.getJSONObject(i);
							fd_fname = json_data.getString("fname");
							fd_username = json_data.getString("username");
							fd_email = json_data.getString("email");
							fd_password = json_data.getString("password");
						}
					} else {
						error = server_result;
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
				serverProgressDialogLogin.dismiss();
				if (error.equals("")) {
					savePreferences();
					Intent feedActivity = new Intent(getActivity(),
							FeedActivity.class);
					feedActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(feedActivity);
					Toast.makeText(getActivity(),
							"Welcome back, " + fd_fname + "!",
							Toast.LENGTH_SHORT).show();
					getActivity().finish();
				} else {
					usernameEditTextLogin.setEnabled(true);
					passwordEditTextLogin.setEnabled(true);
					joinButton.setEnabled(true);
					rememberCheckBox.setEnabled(true);
					Toast.makeText(getActivity(), "Error: " + error,
							Toast.LENGTH_LONG).show();
				}
			}

			public void savePreferences() {
				editor = preferences.edit();
				editor.putString("username", fd_username);
				editor.putString("password", fd_password);
				editor.putString("email", fd_email);
				editor.commit();
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
		/*TODO:Permissions[] permissions = new Permissions[] { Permissions.BASIC_INFO,
				Permissions.USER_LOCATION, Permissions.EMAIL,
				Permissions.PUBLISH_ACTION };
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

	public static class SignupFragment extends Fragment {
		private EditText usernameEditTextSignup;
		private EditText passwordEditTextSignup;
		private EditText repeatPasswordEditText;
		private EditText emailEditTextSignup;
		private EditText fnameEditText;
		private EditText lnameEditText;
		private EditText cityEditText;
		private EditText stateEditText;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_signup,
					container, false);
			usernameEditTextSignup = (EditText) rootView
					.findViewById(R.id.usernameEditTextSignup);
			emailEditTextSignup = (EditText) rootView
					.findViewById(R.id.emailEditTextSignup);
			passwordEditTextSignup = (EditText) rootView
					.findViewById(R.id.passwordEditTextSignup);
			repeatPasswordEditText = (EditText) rootView
					.findViewById(R.id.repeatPasswordEditText);
			fnameEditText = (EditText) rootView
					.findViewById(R.id.fnameEditText);
			lnameEditText = (EditText) rootView
					.findViewById(R.id.lnameEditText);
			cityEditText = (EditText) rootView.findViewById(R.id.cityEditText);
			stateEditText = (EditText) rootView
					.findViewById(R.id.stateEditText);
			(rootView.findViewById(R.id.getStartedButton))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							signUp();
						}
					});

			return rootView;
		}

		public boolean isOnline() {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting()) {
				return true;
			}
			return false;
		}

		class connectServerSignupTask extends AsyncTask<Void, Void, Void> {

			private ProgressDialog serverProgressDialog;
			private String signupURL = "http://mydrivingblows.com/app/signup.php";
			private String error;
			private String fd_username;
			private String fd_email;
			private String fd_password;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				serverProgressDialog = new ProgressDialog(getActivity());
				serverProgressDialog.setMessage("Loading...");
				serverProgressDialog.setCancelable(false);
				serverProgressDialog.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

				nameValuePairs.add(new BasicNameValuePair("username",
						usernameEditTextSignup.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("email",
						emailEditTextSignup.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("password",
						passwordEditTextSignup.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("fname",
						fnameEditText.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("lname",
						lnameEditText.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("city", cityEditText
						.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("state",
						stateEditText.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("date", ""
						+ new SimpleDateFormat("yyyy-MM-dd").format(Calendar
								.getInstance().getTime())));

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

					JSONObject jObject = new JSONObject(result);
					String server_result = jObject.getString("result");
					if (server_result.equals("success")) {
						error = "";
						fd_username = jObject.getString("username");
						fd_email = jObject.getString("email");
						fd_password = jObject.getString("password");
					} else {
						error = server_result;
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
					savePreferences();
					Intent feedActivity = new Intent(getActivity(),
							FeedActivity.class);
					feedActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(feedActivity);
					Toast.makeText(getActivity(), "Registration complete",
							Toast.LENGTH_SHORT).show();

					getActivity().finish();

				} else {
					Toast.makeText(getActivity(), "Error: " + error,
							Toast.LENGTH_LONG).show();
				}
			}

			public void savePreferences() {
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(getActivity());

				Editor editor = preferences.edit();
				editor = preferences.edit();
				editor.putString("username", fd_username);
				editor.putString("email", fd_email);
				editor.putString("password", fd_password);
				editor.commit();
			}
		}

		public void signUp() {
			if (isOnline()) {
				if (!(passwordEditTextSignup.getText().toString()
						.equals(repeatPasswordEditText.getText().toString()))) {
					Toast.makeText(getActivity(), "Passwords do not match",
							Toast.LENGTH_SHORT).show();
				} else if (empty(usernameEditTextSignup)) {
					Toast.makeText(getActivity(),
							"Error: Username field is empty",
							Toast.LENGTH_SHORT).show();
				} else if (empty(emailEditTextSignup)) {
					Toast.makeText(getActivity(),
							"Error: Email field is empty", Toast.LENGTH_SHORT)
							.show();
				} else if (empty(passwordEditTextSignup)) {
					Toast.makeText(getActivity(),
							"Error: Password field is empty",
							Toast.LENGTH_SHORT).show();
				} else if (empty(repeatPasswordEditText)) {
					Toast.makeText(getActivity(),
							"Error: Repeat password field is empty",
							Toast.LENGTH_SHORT).show();
				} else if (empty(fnameEditText)) {
					Toast.makeText(getActivity(),
							"Error: First name field is empty",
							Toast.LENGTH_SHORT).show();
				} else if (empty(lnameEditText)) {
					Toast.makeText(getActivity(),
							"Error: Last name field is empty",
							Toast.LENGTH_SHORT).show();
				} else if (passwordEditTextSignup.getText().length() < 4) {
					Toast.makeText(
							getActivity(),
							"Error: Enter at least 4 characters in password field",
							Toast.LENGTH_SHORT).show();
				} else {
					if (isOnline()) {
						new connectServerSignupTask().execute();
					} else {
						Toast.makeText(getActivity(), "Error: Network problem",
								Toast.LENGTH_SHORT).show();
					}
				}
			} else {
				Toast.makeText(getActivity(),
						"Error: Check your internet connection",
						Toast.LENGTH_LONG).show();
			}
		}

		private boolean empty(EditText editText) {
			if (editText.getText().toString().equals("")) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return false;
		case R.id.action_terms:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Terms of Service");
			View dialogView = new View(this);
			dialogView = getLayoutInflater().inflate(R.layout.dialog_terms,
					null);
			builder.setView(dialogView);
			builder.setNeutralButton("Close", null);
			builder.create().show();
			return false;
		}
		return true;
	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return (cm.getActiveNetworkInfo() != null);
	}

	public static void Log(Object text) {
		Log.d("Log", "" + text);
	}
}
