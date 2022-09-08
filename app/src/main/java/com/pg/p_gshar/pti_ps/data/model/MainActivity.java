package com.pg.p_gshar.pti_ps.data.model;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.ads.MobileAds;
import com.pg.p_gshar.pti_ps.R;
import com.pg.p_gshar.pti_ps.data.model.data.DataManager;
import com.skydoves.colorpickerview.ActionMode;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

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
        setContentView(R.layout.activity_main);

        // FirstTime
         //*   Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                  //  .getBoolean("isFirstRun", true);

    //        if (!isFirstRun) {
           //     Intent intent = new Intent(MainActivity.this, AvatarActivity.class);
            //    startActivity(intent);
            //    finish();

         //   }
     //   getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun",
        //        false).apply();
    // FirstTime//*

        MobileAds.initialize(this, initializationStatus -> {});
        AppLovinSdk.getInstance(this).setMediationProvider("max");
        AppLovinSdk.initializeSdk(this, configuration -> {});
        // remove before publishing
      //  AppLovinSdk.getInstance(this).getSettings().setTestDeviceAdvertisingIds(Collections.singletonList("524639d6-f9ee-4bf6-a391-c0cbf759e361"));

        dataManager = DataManager.getInstance(this);
        String serverURL = getResources().getString(R.string.server_url);
        String jsonName = getResources().getString(R.string.json_file);
        OkHttpHandler okHttpHandler= new OkHttpHandler();
        okHttpHandler.execute(serverURL + (serverURL.endsWith("/") ? "" : "/") + jsonName);

        TextView textView = findViewById(R.id.colorSelected);

        int savedColor = dataManager.getSharedPrefs().getInt(DataManager.COLOR_PICKER, -434869283);

        ColorPickerView colorPickerView = findViewById(R.id.colorPickerView);
        colorPickerView.setActionMode(ActionMode.LAST);
        if  (savedColor != 500) {
            colorPickerView.setInitialColor(savedColor);
        }

        colorPickerView.setColorListener((ColorEnvelopeListener) (envelope, fromUser) -> {
            dataManager.getSharedPrefs().edit()
                    .putInt(DataManager.COLOR_PICKER, envelope.getColor()).apply();
            textView.setText("Your choice is: #" + envelope.getHexCode());
        });

        Button button = findViewById(R.id.colorConfirmBtn);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, AvatarActivity.class);
            startActivity(intent);
        });
    }

    public class OkHttpHandler extends AsyncTask<String, String, String> {
        OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String...params) {
            Request.Builder builder = new Request.Builder();
            builder.url(params[0]);
            Request request = builder.build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            }catch (Exception e){
                e.printStackTrace();
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