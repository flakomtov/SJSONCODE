package com.pg.p_gshar.pti_ps.data.model;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.pg.p_gshar.pti_ps.R;
import com.pg.p_gshar.pti_ps.data.model.data.DataManager;

import java.util.Arrays;
import java.util.List;

public class AvatarActivity extends AppCompatActivity {
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_avatar);
        dataManager = DataManager.getInstance(this);

        String userName = dataManager.getSharedPrefs().getString(DataManager.USER_NAME, null);
        String displayName = dataManager.getSharedPrefs().getString(DataManager.USER_DISPLAY_NAME, null);

        EditText userNameInput = findViewById(R.id.userNameInput);
        EditText displayNameInput = findViewById(R.id.displayNameInput);

        if (!TextUtils.isEmpty(userName)) {
            userNameInput.setText(userName);
        }
        if (!TextUtils.isEmpty(displayName)) {
            displayNameInput.setText(displayName);
        }

        int avRsc = dataManager.getSharedPrefs().getInt(DataManager.USER_AVATAR, -1);

        ImageView im1 = findViewById(R.id.av1);
        ImageView im2 = findViewById(R.id.av2);
        ImageView im3 = findViewById(R.id.av3);
        ImageView im4 = findViewById(R.id.av4);
        ImageView im5 = findViewById(R.id.av5);
        ImageView im6 = findViewById(R.id.av6);

        List<ImageView> imageViews = Arrays.asList(im1, im2, im3, im4, im5, im6);

        ImageView selectedAv = findViewById(R.id.selectedAv);
        if (avRsc != -1) {
            selectedAv.setImageDrawable(imageViews.get(avRsc).getDrawable());
        }
        View.OnClickListener imageViewListener = view -> {
            ImageView imgView = (ImageView) view;
            selectedAv.setImageDrawable(imgView.getDrawable());
            dataManager.getSharedPrefs().edit()
                    .putInt(DataManager.USER_AVATAR, imageViews.indexOf(imgView)).apply();
        };
        for (ImageView iv : imageViews) {
            iv.setOnClickListener(imageViewListener);
        }

        Button button = findViewById(R.id.avatarConfirmBtn);
        button.setOnClickListener(v -> {
            boolean userNameInputEmpty = TextUtils.isEmpty(userNameInput.getText());
            boolean displayNameInputEmpty = TextUtils.isEmpty(displayNameInput.getText());
            boolean allGood = !userNameInputEmpty && !displayNameInputEmpty;
            if (allGood) {
                dataManager.getSharedPrefs().edit()
                        .putString(DataManager.USER_NAME,
                                userNameInput.getText().toString()).apply();
                dataManager.getSharedPrefs().edit()
                        .putString(DataManager.USER_DISPLAY_NAME,
                                displayNameInput.getText().toString()).apply();
                Intent intent = new Intent(this, AdScreenActivity.class);
                intent.putExtra("currentIndex", 1);
                startActivity(intent);
            }else {
                if (userNameInputEmpty) {
                    userNameInput.setError("Name is required");
                }
                if (displayNameInputEmpty) {
                    displayNameInput.setError("Display Name is required");
                }
            }
        });
    }
}