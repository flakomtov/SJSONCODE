package com.pg.p_gshar.pti_ps.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pg.p_gshar.pti_ps.AppOpenerActivity;
import com.pg.p_gshar.pti_ps.R;

public class Contact extends AppCompatActivity {
    // Email input //// Email input //// Email input //
    EditText etMail;
    Button bValidate;
    // Email input //// Email input //// Email input //// Email input //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_contact);

// Email input //// Email input //// Email input //// Email input ////// Email input //// Email input //// Email input //// Email input //
        etMail = findViewById(R.id.email);
        bValidate = findViewById(R.id.send);

        bValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailValidator(etMail);
            }
        });
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
// Email input //// Email input //// Email input //// Email input //// Email input //// Email input //// Email input ////// Email input //// Email input //// Email input //// Email input //

    }
    public void back(View view) {
        onBackPressed();
    }
}