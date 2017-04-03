package app.moysof.mydrivingblows;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

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
import java.util.Arrays;
import java.util.List;

import static app.moysof.mydrivingblows.LoginActivity.Log;

public class LoginFragment extends Fragment {

    private SharedPreferences.Editor editor;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    private EditText usernameEditTextLogin;
    private Button joinButton;
    private EditText passwordEditTextLogin;
    private SharedPreferences preferences;
    private String loginURL = "http://mydrivingblows.com/app/login.php";
    private Button mFacebookBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login,
                container, false);

        mFacebookBtn = (Button) rootView.findViewById(R.id.authButton);
        mFacebookBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.facebook_btn_txtsize));

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Medium.ttf");
        mFacebookBtn.setTypeface(tf);
        mFacebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(getActivity(),
                        Arrays.asList("public_profile", "email"));
            }
        });

        usernameEditTextLogin = (EditText) rootView
                .findViewById(R.id.usernameEditTextLogin);

        joinButton = (Button) rootView.findViewById(R.id.joinButton);
        joinButton.setOnClickListener(new View.OnClickListener() {

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
        private String username;
        private String password;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            username = usernameEditTextLogin.getText().toString();
            password = passwordEditTextLogin.getText().toString();

            serverProgressDialogLogin = new ProgressDialog(getActivity());
            serverProgressDialogLogin.setMessage("Loading...");
            serverProgressDialogLogin.setCancelable(false);
            serverProgressDialogLogin.show();
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(
                    passwordEditTextLogin.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(
                    usernameEditTextLogin.getWindowToken(), 0);
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("password", password));

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