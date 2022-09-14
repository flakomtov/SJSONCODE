package com.pg.p_gshar.pti_ps.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.pg.p_gshar.pti_ps.AppOpenerActivity;
import com.pg.p_gshar.pti_ps.R;
import com.pg.p_gshar.pti_ps.data.DataManager;

import java.util.Arrays;
import java.util.List;

public class Contact extends AppCompatActivity {
    private DataManager dataManager;
    EditText etMail;
    Button bValidate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_contact);

        dataManager = DataManager.getInstance(this);
        String displayName = dataManager.getSharedPrefs().getString(DataManager.USER_DISPLAY_NAME, null);

        TextView tv = findViewById(R.id.userName);
        tv.setText(displayName);
        ImageView imageView = findViewById(R.id.selectedAvatar);
        setAvatar(imageView);

        etMail = findViewById(R.id.email);
        bValidate = findViewById(R.id.send);
        bValidate.setBackgroundColor(getResources().getColor(R.color.masterColor));

        bValidate.setOnClickListener(v -> emailValidator(etMail));
    }
    public void emailValidator(EditText etMail) {

        String emailToText = etMail.getText().toString();

        if (!emailToText.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailToText).matches()) {
            Toast.makeText(this, "Thank you for your message, We will contact you soon", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Contact.this, AppOpenerActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Check your info", Toast.LENGTH_SHORT).show();
        }
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