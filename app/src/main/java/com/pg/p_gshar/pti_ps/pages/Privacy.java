package com.pg.p_gshar.pti_ps.pages;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.pg.p_gshar.pti_ps.R;
import com.pg.p_gshar.pti_ps.data.DataManager;

import java.util.Arrays;
import java.util.List;

public class Privacy extends AppCompatActivity {
    private DataManager dataManager;

    WebView webView;
    public String fileName = "privacy.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_privacy);

        dataManager = DataManager.getInstance(this);
        String displayName = dataManager.getSharedPrefs().getString(DataManager.USER_DISPLAY_NAME, null);

        TextView tv = findViewById(R.id.userName);
        tv.setText(displayName);
        ImageView imageView = findViewById(R.id.selectedAvatar);
        setAvatar(imageView);

        // init webView
        webView = findViewById(R.id.privacy);
        // displaying content in WebView from html file that stored in assets folder
        webView.loadUrl("file:///android_asset/" + fileName);
    }

    public void back(View view) {
        onBackPressed();
    }

    private void setAvatar(ImageView imageView) {
        int avRsc = dataManager.getSharedPrefs().getInt(DataManager.USER_AVATAR, -1);
        List<Integer> imageViews = Arrays.asList(
                R.drawable.av1,
                R.drawable.av2,
                R.drawable.av3,
                R.drawable.av4,
                R.drawable.av5,
                R.drawable.av6);
        if (avRsc != -1) {
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), imageViews.get(avRsc), null));
        }
    }
}