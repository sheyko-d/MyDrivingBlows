package app.moysof.mydrivingblows;

import android.app.Application;

import com.facebook.FacebookSdk;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());
    }
}
