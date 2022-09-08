package com.pg.p_gshar.pti_ps.data.model;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.pg.p_gshar.pti_ps.R;
import com.pg.p_gshar.pti_ps.data.model.data.DataManager;
import com.pg.p_gshar.pti_ps.data.model.pages.About;
import com.pg.p_gshar.pti_ps.data.model.pages.Contact;
import com.pg.p_gshar.pti_ps.data.model.pages.Privacy;

import java.util.Arrays;
import java.util.List;

public class ParametresActivity extends AppCompatActivity {
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_parametres);

        dataManager = DataManager.getInstance(this);
        String displayName = dataManager.getSharedPrefs().getString(DataManager.USER_DISPLAY_NAME, null);

        TextView tv = findViewById(R.id.userName);
        tv.setText(displayName);
        ImageView imageView = findViewById(R.id.selectedAvatar);
        setAvatar(imageView);

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

    public void back(View view) {
        onBackPressed();
    }

    public void style(View view) {
        Intent intent = new Intent(ParametresActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void profil(View view) {
        Intent intent = new Intent(ParametresActivity.this, AvatarActivity.class);
        startActivity(intent);
    }

    public void privacy(View view) {
        Intent intent = new Intent(ParametresActivity.this, Privacy.class);
        startActivity(intent);
    }

    public void support(View view) {
        Intent intent = new Intent(ParametresActivity.this, Contact.class);
        startActivity(intent);
    }

    public void about(View view) {
        Intent intent = new Intent(ParametresActivity.this, About.class);
        startActivity(intent);
    }
}