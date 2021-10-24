package com.example.microvpn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.example.microvpn.MainApplication.AdmobLowCTRStr;
import static com.example.microvpn.MainApplication.fbRemoteConfig;

public  class MainActivity extends AppCompatActivity {
Handler handler;
ProgressBar pgadload;
Button btncontinue;
LottieAnimationView lottieAnimationView;
AdmobNative admobNativeSpl;
    FrameLayout frameLayoutspl,frameLayoutspl1;
    MainApplication mainApplication;
    RelativeLayout relativeLayout,relativeLayout1;
    AdmobInterstitial admobInterstitialSpl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        btncontinue = findViewById(R.id.id_btnconti);
        frameLayoutspl = findViewById(R.id.framelayout_spl);
        relativeLayout = findViewById(R.id.id_ad);
        frameLayoutspl1 = findViewById(R.id.framelayout_spl1);
        relativeLayout1 = findViewById(R.id.id_ad1);
       admobNativeSpl = new AdmobNative(getApplicationContext());
       mainApplication = new MainApplication();
       pgadload = findViewById(R.id.idpgad);
       admobInterstitialSpl = new AdmobInterstitial(getApplicationContext());
//        fbRemoteConfig = FirebaseRemoteConfig.getInstance();
//        FirebaseRemoteConfigSettings.Builder configBuilder = new FirebaseRemoteConfigSettings.Builder()
//                .setMinimumFetchIntervalInSeconds(0);
//        if (BuildConfig.DEBUG) {
//            long cacheInterval = 0;
//            configBuilder.setMinimumFetchIntervalInSeconds(cacheInterval);
//        }
//        fbRemoteConfig.setConfigSettingsAsync(configBuilder.build());

        lottieAnimationView = findViewById(R.id.animationView);
       /* final Runnable r = new Runnable() {
            public void run() {
                btncontinue.setVisibility(View.VISIBLE);
                lottieAnimationView.setVisibility(View.GONE);
            }
        };
        handler.postDelayed(r, 5000);*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btncontinue.setVisibility(View.VISIBLE);
                lottieAnimationView.setVisibility(View.GONE);
            }
        },8000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                admobInterstitialSpl.Interstital();
                pgadload.setVisibility(View.GONE);
                if ("yes".equals(AdmobLowCTRStr)){
                    relativeLayout1.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.VISIBLE);
                    admobNativeSpl.loadnative(frameLayoutspl);
                }
                if ("no".equals(AdmobLowCTRStr)){
                    relativeLayout.setVisibility(View.GONE);
                    relativeLayout1.setVisibility(View.VISIBLE);
                    admobNativeSpl.loadnative(frameLayoutspl1);
                }
            }
        }, 3000);



        btncontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (admobInterstitialSpl.tInterstitialAd != null) {
                    admobInterstitialSpl.tInterstitialAd.show(MainActivity.this);
                    admobInterstitialSpl.Interstital();
                    startActivity(new Intent(getApplicationContext(),PrivacyPolicy.class));
                } else {
                      startActivity(new Intent(getApplicationContext(),PrivacyPolicy.class));
                }
                /*admobInterstitialSpl.tInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull @NotNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                              Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", false);
                            if (isFirstRun) {
                                startActivity(new Intent(MainActivity.this, SignupScreen.class));
                                finish();
                            }
                            else{
                                startActivity(new Intent(MainActivity.this, PrivacyPolicy.class));
                            }
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", false);
                        if (isFirstRun) {
                            startActivity(new Intent(MainActivity.this, SignupScreen.class));
                            finish();
                        }
                        else{
                            startActivity(new Intent(MainActivity.this, PrivacyPolicy.class));
                        }
                    }
                });*/
              // admobInterstitialSpl.tInterstitialAd.show(MainActivity.this);
              /*     Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", false);
                            if (isFirstRun) {
                                startActivity(new Intent(MainActivity.this, SignupScreen.class));
                                finish();
                            }
                            else{
                                startActivity(new Intent(MainActivity.this, PrivacyPolicy.class));
                            }*/
            }
        });
    }



}