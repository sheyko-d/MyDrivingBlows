package app.moysof.mydrivingblows;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class UsernameActivity extends ActionBarActivity {

	private SharedPreferences preferences;
	private String usernameURL = "http://mydrivingblows.com/app/username.php";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_username);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);
	}

	class connectServerUsernameTask extends AsyncTask<Void, Void, Void> {

		private String error;
		private ProgressDialog serverProgressDialogLogin;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			serverProgressDialogLogin = new ProgressDialog(
					UsernameActivity.this);
			serverProgressDialogLogin.setMessage("Loading...");
			serverProgressDialogLogin.setCancelable(false);
			serverProgressDialogLogin.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("email", preferences
					.getString("email", "")));
			nameValuePairs.add(new BasicNameValuePair("pwd", preferences
					.getString("password", "")));
			nameValuePairs.add(new BasicNameValuePair("username",
					((EditText) findViewById(R.id.usernameEditText)).getText()
							.toString()));

			String result = null;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(usernameURL);
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
				JSONObject json = new JSONObject(result);
				String server_result = json.getString(
						"result");
				if (server_result.equals("success")) {
					error = "";
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
				Intent feedActivity = new Intent(UsernameActivity.this,
						FeedActivity.class);
				feedActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(feedActivity);
				Toast.makeText(UsernameActivity.this, "Username changed!",
						Toast.LENGTH_SHORT).show();
				preferences.edit().putString("username", ((EditText) findViewById(R.id.usernameEditText)).getText()
							.toString()).commit();
				finish();
			} else {
				Toast.makeText(UsernameActivity.this, "Error: " + error,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}

	private void Log(Object Object) {
		Log.d("Log", "" + Object);
	}

	public void cancel(View v) {
		finish();
	}

	public void ok(View v) {
		if (!((EditText) findViewById(R.id.usernameEditText)).getText()
				.toString().equals("")){
		new connectServerUsernameTask().execute();
		} else {
			Toast.makeText(this, "Error: Username is empty",
					Toast.LENGTH_LONG).show();
		}
	}

}
