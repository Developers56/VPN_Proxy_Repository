package com.example.microvpn;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import static com.example.microvpn.MainApplication.AdmobInterstitialStr;

public class AdmobInterstitial {
    Context context;

    public static com.google.android.gms.ads.interstitial.InterstitialAd tInterstitialAd;


    public AdmobInterstitial(Context context) {
        this.context = context;
    }

    public void Interstital() {

        AdRequest adRequest = new AdRequest.Builder().build();
        try {
            com.google.android.gms.ads.interstitial.InterstitialAd.load(context, AdmobInterstitialStr, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                    tInterstitialAd = interstitialAd;
                    Log.i("TAG", "onAdLoaded");
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    // Handle the error
                    Log.i("TAG", loadAdError.getMessage());
                    tInterstitialAd = null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
