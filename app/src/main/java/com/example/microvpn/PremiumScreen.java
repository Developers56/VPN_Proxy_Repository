package com.example.microvpn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PremiumScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_screen);
    }

    public void cancel(View view) {
        startActivity(new Intent(PremiumScreen.this,VPNActivity.class));
    }
}