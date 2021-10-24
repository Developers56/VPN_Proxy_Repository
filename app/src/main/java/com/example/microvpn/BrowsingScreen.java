package com.example.microvpn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BrowsingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing_screen);

    }

    public void Next(View view) {
        startActivity(new Intent(getApplicationContext(),StreamingPoli.class));
    }
}