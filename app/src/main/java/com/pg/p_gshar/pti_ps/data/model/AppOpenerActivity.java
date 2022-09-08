package com.pg.p_gshar.pti_ps.data.model;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.pg.p_gshar.pti_ps.R;
import com.pg.p_gshar.pti_ps.data.model.data.DataManager;
import com.pg.p_gshar.pti_ps.data.model.utils.BlurBuilder;

import java.util.Arrays;
import java.util.List;

public class AppOpenerActivity extends AppCompatActivity {
    private DataManager dataManager;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_app_opener);

        dataManager = DataManager.getInstance(this);
        if (dataManager.getAdsData().isVerificationPage()) {
            setContentView(R.layout.activity_app_opener);
            setupVerificationPage();
        } else {
            setContentView(R.layout.activity_app_opener_no_verif);
            setupDescriptionPage();
        }
    }

    private void setupDescriptionPage() {
        TextView description = findViewById(R.id.description);
        description.setText(dataManager.getAdsData().getDisabledPageDescription());
    }

    private void setupVerificationPage() {
        code = dataManager.getAdsData().getCode();

        int savedColor = dataManager.getSharedPrefs().getInt(DataManager.COLOR_PICKER, -1);
        String displayName = dataManager.getSharedPrefs().getString(DataManager.USER_DISPLAY_NAME, null);

        EditText codeInput = findViewById(R.id.codeInput);
        Button errorMsg = findViewById(R.id.codeErrorBtn);
        errorMsg.setVisibility(View.GONE);

        // bind data with UI
        TextView requestCodeTxt = findViewById(R.id.requestCodeTxt);
        requestCodeTxt.setText(dataManager.getAdsData().getRequestCodeText());

        TextView tv = findViewById(R.id.userName);
        tv.setText(displayName);
        ImageView imageView = findViewById(R.id.selectedAvatar);
        setAvatar(imageView);
        Button accessBtn = findViewById(R.id.requestAccessBtn);
        accessBtn.setBackgroundColor(savedColor);

        accessBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(codeInput.getText())) {
                errorMsg.setVisibility(View.VISIBLE);
            } else if (!codeInput.getText().toString().equals(code)) {
                codeInput.setError("Code incorrect, try again !");
                errorMsg.setVisibility(View.VISIBLE);
            } else {
                errorMsg.setVisibility(View.GONE);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataManager.getAdsData().getAccessLink()));
                startActivity(browserIntent);
            }
        });

        Button codeBtn = findViewById(R.id.requestCodeBtn);
        codeBtn.setBackgroundColor(savedColor);

        codeBtn.setOnClickListener(v -> {
            errorMsg.setVisibility(View.GONE);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataManager.getAdsData().getCodeLink()));
            startActivity(browserIntent);
        });

        // set blurry background
        ImageView appBackground = findViewById(R.id.appBackground);
        Bitmap blurredBitmap = BlurBuilder.blur( this,
                BitmapFactory.decodeResource(getResources(), R.drawable.logo));
        appBackground.setImageDrawable(new BitmapDrawable(getResources(), blurredBitmap));
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