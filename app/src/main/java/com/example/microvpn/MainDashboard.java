package com.example.microvpn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.partner.api.response.RemainingTraffic;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.pixplicity.easyprefs.library.Prefs;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.example.microvpn.VPNActivity.selectedCountry;

public abstract class MainDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected static final String TAG = VPNActivity.class.getSimpleName();
    ImageView ivbtnconnect;
    ImageView ivbtndiscon,ivcontflag;
    TextView tv_notcon,tvnameregip,trafficsts;
    public static String selectedCountryName;
    TextView tvcontname,tv_ipadress,tv_dwnldbytes,tv_upldbytes;
    String trafficUsed,trafficLimit;
    String urll, carrierr;
    UnifiedSDK unifiedSDK;
    ImageView iv_flagip,navmenu;
    private DrawerLayout drawer;
    AdmobInterstitial admobInterstitialmain;
    AdmobNativeDashboard admobNativemain;
    FrameLayout frameLayoutmain;

    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    final Runnable mUIUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
            checkRemainingTraffic();
            mUIHandler.postDelayed(mUIUpdateRunnable, 10000);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);
        ivbtnconnect = findViewById(R.id.id_iv_serverconnect);
        tv_notcon = findViewById(R.id.statusreport);
        tvcontname  = findViewById(R.id.id_tv_servername);
        ivcontflag  = findViewById(R.id.iv_serverflag);
        ivbtndiscon = findViewById(R.id.iddisconectbtn);
       // iv_flagip = findViewById(R.id.imageView);
       // tvnameregip = findViewById(R.id.textView4);
        //tv_ipadress = findViewById(R.id.textView3);
        tv_dwnldbytes = findViewById(R.id.iddwnldbytes);
        tv_upldbytes=findViewById(R.id.idupldbytes);
        navmenu = findViewById(R.id.idmenu);
        admobInterstitialmain = new AdmobInterstitial(getApplicationContext());
        admobInterstitialmain.Interstital();
        admobNativemain = new AdmobNativeDashboard(getApplicationContext());
        frameLayoutmain = findViewById(R.id.framelayout_main);
        initauto();
        navmenu.setOnClickListener(view -> drawer.openDrawer(GravityCompat.START));
        setupDrawer();
        admobNativemain.loadnative(frameLayoutmain);
    }

    private void setupDrawer() {
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                MainDashboard.this, drawer, null, 0, 0);//R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainDashboard.this);
    }

    private void initauto() {
        SharedPreferences prefs = getPrefs();
        prefs.edit()
                .putString(config.STORED_HOST_URL_KEY, "https://backend.northghost.com")
                .putString(config.STORED_CARRIER_ID_KEY, "Mic_microvpn")
                .apply();
        initSDK();
        loginToVpn();
    }

    private void initSDK() {
        final SharedPreferences prefs = getPrefs();
        urll = prefs.getString(BuildConfig.STORED_HOST_URL_KEY, config.BASE_HOST);
        carrierr = prefs.getString(BuildConfig.STORED_CARRIER_ID_KEY, "");
        if (!TextUtils.isEmpty(urll) && !TextUtils.isEmpty(carrierr)) {
            ClientInfo clientInfo = ClientInfo.newBuilder()
                    .baseUrl(urll)
                    .carrierId(carrierr)
                    .build();
            UnifiedSDK.clearInstances();
            unifiedSDK = UnifiedSDK.getInstance(clientInfo);

        } else {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    startUIUpdateTask();
                }
            }
            @Override
            public void failure(@NonNull VpnException e) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUIUpdateTask(false);
    }

    public SharedPreferences getPrefs() {
        return getSharedPreferences(config.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public void Regionlistopen(View view) {
        if (unifiedSDK == null) {
            Toast.makeText(this, "VPN Initializing Please Wait!", Toast.LENGTH_LONG).show();
            return;
        }
        if (isConnected()) {
            chooseServer();
        } else {
            Toast.makeText(MainDashboard.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    protected void showMessage(String msg) {
        Toast.makeText(MainDashboard.this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void updateUI() {
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {
                switch (vpnState) {
                    case IDLE: {
                        ivbtnconnect.setEnabled(true);
                        tv_notcon.setText("Not connected");
                        ivbtndiscon.setBackground(getResources().getDrawable(R.drawable.ic_on_off_button));
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTED: {
                        ivbtnconnect.setEnabled(true);
                        ivbtndiscon.setBackground(getResources().getDrawable(R.drawable.ic_off_button));
                        tv_notcon.setText("Connected");
                        hideConnectProgress();

                        SharedPreferences app_preferences = getSharedPreferences("checkvalue", Context.MODE_PRIVATE);
                        int updatevale = app_preferences.getInt("valueMB", 0);
                        break;
                    }
                    case CONNECTING_VPN:
                    case CONNECTING_CREDENTIALS:
                    case CONNECTING_PERMISSIONS: {
                        tv_notcon.setText("Connecting");
                        ivbtnconnect.setEnabled(false);
                        break;
                    }
                    case PAUSED: {
                        ivbtnconnect.setEnabled(false);
                        ivbtndiscon.setBackground(getResources().getDrawable(R.drawable.ic_off_button));
                        break;
                    }
                }
            }
            @Override
            public void failure(@NonNull VpnException e) {
            }
        });
        UnifiedSDK.getInstance().getBackend().isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean isLoggedIn) {
            }
            @Override
            public void failure(@NonNull VpnException e) {
            }
        });
        getCurrentServer(new Callback<String>() {
            @Override
            public void success(@NonNull final String currentServer) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Locale locale = new Locale("", Prefs.getString("sname", "ca"));
                        selectedCountryName = locale.getDisplayCountry();
                        tvcontname.setText(selectedCountryName != null ? selectedCountryName : "UNKNOWN");
                        tvnameregip.setText(selectedCountryName!=null ? selectedCountryName : "UNKNOWN");
                        if (selectedCountryName=="Japan"){
                            tv_ipadress.setText("12345678");
                        }
                        ivcontflag.setImageResource(MainDashboard.this.getResources().getIdentifier("drawable/" + selectedCountry, "drawable", MainDashboard.this.getPackageName()));
                       // iv_flagip.setImageResource(MainDashboard.this.getResources().getIdentifier("drawable/" + selectedCountry, "drawable", MainDashboard.this.getPackageName()));
                    }
                });
            }
            @Override
            public void failure(@NonNull VpnException e) {
                tvcontname.setText("UNKNOWN");
                ivcontflag.setImageDrawable(getResources().getDrawable(R.drawable.select_flag_image));
            }
        });

        runOnUiThread(() -> {
            tvcontname.setText(selectedCountryName != null ? selectedCountryName : "UNKNOWN");
            ivcontflag.setImageResource(MainDashboard.this.getResources().getIdentifier("drawable/" + selectedCountry, "drawable", MainDashboard.this.getPackageName()));

        });
    }

    protected abstract void getCurrentServer(Callback<String> callback);

    protected void hideConnectProgress() {
        tv_notcon.setVisibility(View.VISIBLE);
    }

    protected void downloadupload(long outBytes, long inBytes) {
        String outString = Converter.humanReadableByteCountOld(outBytes, false);
        String inString = Converter.humanReadableByteCountOld(inBytes, false);
       // trafficsts.setText(getResources().getString(R.string.downlaodupload, outString, inString));
        tv_dwnldbytes.setText(outString);
        tv_upldbytes.setText(inString);
    }
   protected void startUIUpdateTask() {
       stopUIUpdateTask(true);
       mUIHandler.post(mUIUpdateRunnable);
   }

    protected void stopUIUpdateTask(boolean b) {
        mUIHandler.removeCallbacks(mUIUpdateRunnable);
        if (b) {
            updateUI();
        }
    }

    protected abstract void checkRemainingTraffic();

    @SuppressLint("StringFormatMatches")
    protected void updateRemainingTraffic(RemainingTraffic remainingTrafficResponse) {
        if (remainingTrafficResponse.isUnlimited()) {
        } else {
            trafficUsed = Converter.megabyteCount(remainingTrafficResponse.getTrafficUsed()) + "Mb";
            trafficLimit = Converter.megabyteCount(remainingTrafficResponse.getTrafficLimit()) + "Mb";
        }
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission") NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    protected abstract void isLoggedIn(Callback<Boolean> callback);

    protected abstract void loginToVpn();

    protected abstract void logOutFromVnp();

    protected abstract void isConnected(Callback<Boolean> callback);

    protected abstract void connectToVpn();

    protected abstract void disconnectFromVnp();

    protected abstract void chooseServer();

    public void connectovpn(View view) {
        if (unifiedSDK == null) {
            // Toast.makeText(this, "SDK is not configured", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "VPN Initializing Please Wait!", Toast.LENGTH_LONG).show();
            return;
        }
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    disconnectFromVnp();
                    ivbtndiscon.setBackground(getResources().getDrawable(R.drawable.ic_on_off_button));
                } else  {
                connectToVpn();
                }
            }
            @Override
            public void failure(@NonNull VpnException e) {
            }

        });
        if (admobInterstitialmain.tInterstitialAd != null) {
            admobInterstitialmain.tInterstitialAd.show(MainDashboard.this);
            admobInterstitialmain.Interstital();
        } else {
            //  startActivity(new Intent(getApplicationContext(),PremiumActivity.class));
        }
    }

    public void gotopreiumActivity(View view) {
        startActivity(new Intent(MainDashboard.this,PremiumScreen.class));
    }
    public void gotopreiumActivity2(View view){
        startActivity(new Intent(MainDashboard.this,PremiumScreen.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
    public  void customExitDialog()
    {
        /*// creating custom dialog
        final Dialog dialog = new Dialog(MainDashboard.this);

        // setting content view to dialog
        dialog.setContentView(R.layout.exit_dialog);

        // getting reference of TextView
        Button dialogButtonExit =  dialog.findViewById(R.id.extbtn);
        Button dialogButtonBack =  dialog.findViewById(R.id.extback);
        Button dialogButtonYes =  dialog.findViewById(R.id.extyes);



        // show the exit dialog
        dialog.show();*/

    }
}