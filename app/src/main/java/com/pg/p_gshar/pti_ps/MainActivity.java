package com.pg.p_gshar.pti_ps;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.ads.MobileAds;
import com.pg.p_gshar.pti_ps.data.DataManager;

import java.util.Collections;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Handle the splash screen transition.
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        MobileAds.initialize(this, initializationStatus -> {});
        AppLovinSdk.getInstance(this).setMediationProvider("max");
        AppLovinSdk.initializeSdk(this, configuration -> {});
        // remove before publishing
        AppLovinSdk.getInstance(this).getSettings().setTestDeviceAdvertisingIds(Collections.singletonList("524639d6-f9ee-4bf6-a391-c0cbf759e361"));

        dataManager = DataManager.getInstance(this);
        String serverURL = getResources().getString(R.string.server_url);
        String jsonName = getResources().getString(R.string.json_file);
        OkHttpHandler okHttpHandler= new OkHttpHandler();
        okHttpHandler.execute(serverURL + (serverURL.endsWith("/") ? "" : "/") + jsonName);

        Intent intent = new Intent(MainActivity.this, AvatarActivity.class);
        startActivity(intent);
        finish();
    }

    class OkHttpHandler extends AsyncTask<String, String, String> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String...params) {
            Request.Builder builder = new Request.Builder();
            builder.url(params[0]);
            Request request = builder.build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().string();
                } else {
                    dataManager.setStatus(DataManager.NETWORK_ERROR);
                }
            }catch (Exception e){
                System.err.println(e.getMessage());
                dataManager.setStatus(DataManager.NETWORK_ERROR);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dataManager.setAdsData(s);
        }
    }
}