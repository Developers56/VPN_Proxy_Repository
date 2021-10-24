package com.example.microvpn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import static com.example.microvpn.MainApplication.fbRemoteConfig;

public class PreSplash extends AppCompatActivity {
Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_splash);
        fbRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings.Builder configBuilder = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0);
        if (BuildConfig.DEBUG) {
            long cacheInterval = 0;
            configBuilder.setMinimumFetchIntervalInSeconds(cacheInterval);
        }
        fbRemoteConfig.setConfigSettingsAsync(configBuilder.build());
        handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
              startActivity(new Intent(PreSplash.this,MainActivity.class));
            }
        };
        handler.postDelayed(r, 3000);
    }
}