package com.example.microvpn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class PrivacyPolicy extends AppCompatActivity {
CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        checkBox = findViewById(R.id.idhkbox);
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", false);
        if (isFirstRun) {
            startActivity(new Intent(PrivacyPolicy.this, PolicyViewPager.class));
            finish();
        }
    }

    public void Continue(View view) {
        if (checkBox.isChecked()) {
            startActivity(new Intent(getApplicationContext(), PolicyViewPager.class));
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", true).commit();
        }
    }

    public void exitfromapp(View view) {
       this.finishAffinity();
    }
}