package com.example.microvpn;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.sdk.HydraTransportConfig;
import com.anchorfree.sdk.NotificationConfig;
import com.anchorfree.sdk.TransportConfig;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.northghost.caketube.OpenVpnTransportConfig;
import com.pixplicity.easyprefs.library.Prefs;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainApplication extends Application {
    private static Context context;
    private static final String CHANNEL_ID = "vpn";
    AdmobInterstitial admobInterstitialmain;
    public static String AdmobNativeStr,AdmobInterstitialStr,AdmobLowCTRStr;
    public static FirebaseRemoteConfig fbRemoteConfig, Interstitial_admob,Banner_Admob;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
        initHydraSdk();
        FirebaseApp.initializeApp(this);
        fbRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings.Builder configBuilder = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0);
        if (BuildConfig.DEBUG) {
            long cacheInterval = 0;
            configBuilder.setMinimumFetchIntervalInSeconds(cacheInterval);
        }
        fbRemoteConfig.setConfigSettingsAsync(configBuilder.build());
        admobInterstitialmain = new AdmobInterstitial(context);
        getdata();
    }

    private void getdata() {
        fbRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    AdmobNativeStr = fbRemoteConfig.getString("ADMOB_NATIVE_56");
                    Log.i("TAG", "run: vl" + AdmobNativeStr);
                    AdmobInterstitialStr = fbRemoteConfig.getString("ADMOB_INTERSTITAL_56");
                    Log.i("TAG", "run: vl" + AdmobInterstitialStr);
                    AdmobLowCTRStr = fbRemoteConfig.getString("ADMOB_LOWCTR_56");

                }
            }
        });
    }

    public static Context getApplication() {
        return context;
    }

    public SharedPreferences getPrefs() {
        return getSharedPreferences(config.SHARED_PREFS, Context.MODE_PRIVATE);
    }
    public static Context getStaticContext() {
        return getApplication().getApplicationContext();
    }
    public static UnifiedSDK unifiedSDK;
    public void initHydraSdk() {

        ClientInfo clientInfo = ClientInfo.newBuilder()
                .baseUrl(config.baseURL)
                .carrierId(config.carrierID)
                .build();
        unifiedSDK = UnifiedSDK.getInstance(clientInfo);
        UnifiedSDK.clearInstances();
        createNotificationChannel();
        SharedPreferences prefs = getPrefs();

        List<TransportConfig> transportConfigList = new ArrayList<>();
        transportConfigList.add(HydraTransportConfig.create());
        transportConfigList.add(OpenVpnTransportConfig.tcp());
        transportConfigList.add(OpenVpnTransportConfig.udp());
        UnifiedSDK.update(transportConfigList, CompletableCallback.EMPTY);

        NotificationConfig notificationConfig = NotificationConfig.newBuilder()
                .title(getResources().getString(R.string.app_name))
                .channelId(CHANNEL_ID)
                .build();
        UnifiedSDK.update(notificationConfig);

        UnifiedSDK.setLoggingLevel(Log.VERBOSE);


    }

    public void setNewHostAndCarrier(String hostUrl, String carrierId) {
        SharedPreferences prefs = getPrefs();
        if (TextUtils.isEmpty(hostUrl)) {
            prefs.edit().remove(config.STORED_HOST_URL_KEY).apply();
        } else {
            prefs.edit().putString(config.STORED_HOST_URL_KEY, hostUrl).apply();
        }

        if (TextUtils.isEmpty(carrierId)) {
            prefs.edit().remove(config.STORED_CARRIER_ID_KEY).apply();
        } else {
            prefs.edit().putString(config.STORED_CARRIER_ID_KEY, carrierId).apply();
        }
        initHydraSdk();
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Sample VPN";
            String description = "VPN notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
