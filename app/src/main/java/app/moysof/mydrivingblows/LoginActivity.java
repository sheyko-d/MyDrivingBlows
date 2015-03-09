package app.moysof.mydrivingblows;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.sromku.simple.fb.SimpleFacebook;

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
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class LoginActivity extends ActionBarActivity implements
        ActionBar.TabListener {

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    ViewPager mViewPager;

    private static SimpleFacebook mSimpleFacebook;

    private static Context context;
    private PagerSlidingTabStrip mTabs;
    private UiLifecycleHelper uiHelper;
    private Session mSession;
    private static EditText usernameEditTextSignup;
    private static EditText passwordEditTextSignup;
    private static EditText repeatPasswordEditText;
    private static EditText emailEditTextSignup;
    private static EditText fnameEditText;
    private static EditText lnameEditText;
    private static EditText cityEditText;
    private static EditText stateEditText;
    private static String username = "";
    private static String email = "";
    private static String city = "";
    private static String state = "";
    private static String fname = "";
    private static String lname = "";
    private static Editor editor;
    private static LoginActivity activity;
    private static SharedPreferences preferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activity = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        toolbar.setContentInsetsAbsolute(
                CommonUtilities.convertDpToPixel(72, this), 0);
        setSupportActionBar(toolbar);

        // Log out from Facebook
        mSession = Session.getActiveSession();
        if (mSession != null) {
            mSession.closeAndClearTokenInformation();
            mSession.close();
            Session.setActiveSession(null);
        }

        context = getApplicationContext();

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(
                getSupportFragmentManager());

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);

        mTabs.setViewPager(mViewPager);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        try {

            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("Log", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

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

    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

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

    @SuppressLint("ValidFragment")
    public class LoginFragment extends Fragment {

        private EditText usernameEditTextLogin;
        private Button joinButton;
        private EditText passwordEditTextLogin;
        private SharedPreferences preferences;
        private String loginURL = "http://mydrivingblows.com/app/login.php";
        private LoginButton mFacebookBtn;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login,
                    container, false);

            mFacebookBtn = (LoginButton) rootView.findViewById(R.id.authButton);
            mFacebookBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.facebook_btn_txtsize));

            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                    "fonts/Roboto-Medium.ttf");
            mFacebookBtn.setTypeface(tf);

            usernameEditTextLogin = (EditText) rootView
                    .findViewById(R.id.usernameEditTextLogin);

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

            preferences = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());

            editor = preferences.edit();

            return rootView;
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

    static class connectServerSignupTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog serverProgressDialog;
        private String signupURL = "http://mydrivingblows.com/app/signup.php";
        private String error;
        private String fd_username;
        private String fd_password;
        private String fd_email;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            serverProgressDialog = new ProgressDialog(activity);
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
            try {
                serverProgressDialog.dismiss();
            } catch (Exception e) {
            }
            if (error.equals("")) {
                Intent feedActivity = new Intent(activity,
                        FeedActivity.class);
                feedActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                Toast.makeText(activity, "Welcome, " + fname + "!",
                        Toast.LENGTH_SHORT).show();
                activity.startActivity(feedActivity);

                if (!preferences.getBoolean("username_changed", false)
                        & fd_username.contains(" ")) {// it means user is
                    // from Facebook
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(
                            activity)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setTicker(
                                    "Don't want to show your Facebook name?")
                            .setContentTitle("Tap to create a username")
                            .setContentText("And use app incognito")
                            .setAutoCancel(true);

                    Intent notificationIntent = new Intent(activity,
                            UsernameActivity.class);
                    notificationIntent
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent contentIntent = PendingIntent
                            .getActivity(activity, 0,
                                    notificationIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(contentIntent);
                    NotificationManager manager = (NotificationManager) activity
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(0, builder.build());
                    editor.putBoolean("username_changed", true).commit();
                }

                activity.finish();

            } else {
                boolean usernameChanged = preferences.getBoolean("username_changed", false);
                editor.clear().commit();
                editor.putBoolean("agree", true).commit();
                editor.putBoolean("username_changed", usernameChanged).commit();
                Toast.makeText(activity, "Error: " + error,
                        Toast.LENGTH_LONG).show();
                activity.finish();
            }
        }

        public void savePreferences() {
            SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(activity);

            Editor editor = preferences.edit();
            editor = preferences.edit();
            editor.putString("username", fd_username);
            editor.putString("password", fd_password);
            editor.putString("email", fd_email);
            editor.commit();
        }
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            // Respond to session state changes, ex: updating the view
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }
        uiHelper.onResume();
    }

    private void onSessionStateChange(final Session session, SessionState sessionState, Exception exception) {
        if (sessionState.isOpened()) {
            if (!session.getPermissions().contains("email")) {
                session.requestNewReadPermissions(new Session.NewPermissionsRequest(activity, Arrays.asList("email")));
            } else if (!session.getPermissions().contains("publish_actions")) {
                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(activity, Arrays.asList("publish_actions")));
            } else {
                Request request = Request.newMeRequest(session,
                        new Request.GraphUserCallback() {
                            @Override
                            public void onCompleted(GraphUser user, Response response) {
                                // If the response is successful
                                if (session == Session.getActiveSession()) {
                                    if (user != null) {
                                        username = user.getFirstName() + " "
                                                + user.getLastName();
                                        editor.putString("username", username);
                                        email = (String) response.getGraphObject().getProperty("email");
                                        Log("email = " + email);
                                        editor.putString("email", email);
                                        editor.putString("picture",
                                                "https://graph.facebook.com/" + user.getId() + "/picture?type=large");
                                        editor.putBoolean("from_facebook", true);
                                        editor.commit();
                                        String location = "";
                                        if (user.getLocation() != null) {
                                            location = user.getLocation()
                                                    .getName();
                                        }
                                        fname = user.getFirstName();
                                        lname = user.getLastName();
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
                                        Log("username(local) = " + username);
                                        if (isOnline()) {
                                            new connectServerSignupTask().execute();
                                        } else {
                                            Toast.makeText(LoginActivity.this,
                                                    "Error: Network problem",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                if (response.getError() != null) {
                                    // Handle errors, will do so later.
                                }
                            }
                        });

                request.executeAsync();
            }
        } else if (sessionState.isClosed()) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    public static class SignupFragment extends Fragment {

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


        public void signUp() {
            username = usernameEditTextSignup.getText().toString();

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
