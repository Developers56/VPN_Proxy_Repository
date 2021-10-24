package com.example.microvpn;

import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.Preference;
//import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.data.Country;
import com.anchorfree.partner.api.response.RemainingTraffic;
import com.anchorfree.partner.api.response.User;
import com.anchorfree.reporting.TrackingConstants;
import com.anchorfree.sdk.SdkInfo;
import com.anchorfree.sdk.SessionConfig;
import com.anchorfree.sdk.SessionInfo;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.VpnPermissions;
import com.anchorfree.sdk.exceptions.CnlBlockedException;
import com.anchorfree.sdk.exceptions.InvalidTransportException;
import com.anchorfree.sdk.exceptions.PartnerApiException;
import com.anchorfree.sdk.fireshield.FireshieldCategory;
import com.anchorfree.sdk.fireshield.FireshieldConfig;
import com.anchorfree.sdk.rules.TrafficRule;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.anchorfree.vpnsdk.callbacks.TrafficListener;
import com.anchorfree.vpnsdk.callbacks.VpnCallback;
import com.anchorfree.vpnsdk.callbacks.VpnStateListener;
import com.anchorfree.vpnsdk.compat.CredentialsCompat;
import com.anchorfree.vpnsdk.exceptions.BrokenRemoteProcessException;
import com.anchorfree.vpnsdk.exceptions.ConnectionCancelledException;
import com.anchorfree.vpnsdk.exceptions.ConnectionTimeoutException;
import com.anchorfree.vpnsdk.exceptions.CorruptedConfigException;
import com.anchorfree.vpnsdk.exceptions.CredentialsLoadException;
import com.anchorfree.vpnsdk.exceptions.GenericPermissionException;
import com.anchorfree.vpnsdk.exceptions.InternalException;
import com.anchorfree.vpnsdk.exceptions.NetworkChangeVpnException;
import com.anchorfree.vpnsdk.exceptions.NetworkRelatedException;
import com.anchorfree.vpnsdk.exceptions.NoCredsSourceException;
import com.anchorfree.vpnsdk.exceptions.NoNetworkException;
import com.anchorfree.vpnsdk.exceptions.NoVpnTransportsException;
import com.anchorfree.vpnsdk.exceptions.ServiceBindFailedException;
import com.anchorfree.vpnsdk.exceptions.StopCancelledException;
import com.anchorfree.vpnsdk.exceptions.TrackableException;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionDeniedException;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionNotGrantedExeption;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionRevokedException;
import com.anchorfree.vpnsdk.exceptions.VpnTransportException;
import com.anchorfree.vpnsdk.exceptions.WrongStateException;
import com.anchorfree.vpnsdk.transporthydra.HydraTransport;
import com.anchorfree.vpnsdk.transporthydra.HydraVpnTransportException;
import com.anchorfree.vpnsdk.vpnservice.ConnectionStatus;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.anchorfree.vpnsdk.vpnservice.credentials.AppPolicy;
import com.anchorfree.vpnsdk.vpnservice.credentials.CaptivePortalException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.northghost.caketube.CaketubeTransport;
import com.northghost.caketube.exceptions.CaketubeTransportException;
import com.pixplicity.easyprefs.library.Prefs;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

public class VPNActivity extends MainDashboard implements TrafficListener, VpnStateListener,
        RegionsScreen.RegionChooserInterface,GoogleApiClient.OnConnectionFailedListener {
    boolean dialogShown;
    public static String selectedCountry = "";
    private NotificationManager notificationManager;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;

    @Override
    protected void onStart() {
        super.onStart();
        Locale locale = new Locale("", Prefs.getString("sname", "ca"));
        selectedCountryName = locale.getDisplayCountry();
        selectedCountry = locale.getCountry().toLowerCase();
        UnifiedSDK.addTrafficListener(this);
        UnifiedSDK.addVpnStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UnifiedSDK.removeVpnStateListener(this);
        UnifiedSDK.removeTrafficListener(this);
    }

    @Override
    public void onTrafficUpdate(long l, long l1) {
        updateUI();
       downloadupload(l, l1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }

    @Override
    public void vpnStateChanged(@NonNull @NotNull VPNState vpnState) {
        updateUI();
    }

    @Override
    public void vpnError(@NonNull @NotNull VpnException e) {
        updateUI();
        handleError(e);
    }

    @Override
    protected void isLoggedIn(Callback<Boolean> callback) {
        UnifiedSDK.getInstance().getBackend().isLoggedIn(callback);
    }

    @Override
    protected void loginToVpn() {
        AuthMethod authMethod = AuthMethod.anonymous();
        UnifiedSDK.getInstance().getBackend().login(authMethod, new Callback<User>() {
            @Override
            public void success(User user) {
                updateUI();
            }

            @Override
            public void failure(VpnException e) {
                updateUI();

                handleError(e);
            }
        });
    }

    @Override
    protected void logOutFromVnp() {

        UnifiedSDK.getInstance().getBackend().logout(new CompletableCallback() {
            @Override
            public void complete() {
                updateUI();
            }

            @Override
            public void error(VpnException e) {
                updateUI();
            }
        });
        selectedCountry = "";
    }

    @Override
    protected void isConnected(Callback<Boolean> callback) {
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@androidx.annotation.NonNull VPNState vpnState) {
                callback.success(vpnState == VPNState.CONNECTED);
            }

            @Override
            public void failure(@androidx.annotation.NonNull VpnException e) {
                callback.success(false);
            }
        });
    }

    @Override
    protected void connectToVpn() {
        MainApplication.unifiedSDK.getBackend().isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    List<String> fallbackOrder = new ArrayList<>();
                    fallbackOrder.add(HydraTransport.TRANSPORT_ID);
                    fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_TCP);
                    fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_UDP);
                    List<String> bypassDomains = new LinkedList<>();
                    bypassDomains.add("*domain1.com");
                    bypassDomains.add("*domain2.com");
                    UnifiedSDK.getInstance().getVPN().start(new SessionConfig.Builder()
                            .withReason(TrackingConstants.GprReasons.M_UI)
                            .withTransportFallback(fallbackOrder)
                            .withTransport(HydraTransport.TRANSPORT_ID)
                            .withVirtualLocation(selectedCountry)
                            .addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains))
                            .build(), new CompletableCallback() {
                        @Override
                        public void complete() {
                            hideConnectProgress();
                            startUIUpdateTask();
                        }

                        @Override
                        public void error(@androidx.annotation.NonNull VpnException e) {
                            hideConnectProgress();
                            updateUI();

                            handleError(e);
                        }
                    });
                } else {
                    showMessage("Please Wait VPN is Initializing");
//                    showMessage("Login please");
                }
            }

            @Override
            public void failure(@androidx.annotation.NonNull VpnException e) {

            }
        });
    }

    @Override
    protected void disconnectFromVnp() {
        UnifiedSDK.getInstance().getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
                hideConnectProgress();
                stopUIUpdateTask(true);
            }

            @Override
            public void error(VpnException e) {

                hideConnectProgress();
                updateUI();

                handleError(e);
            }
        });
    }

    @Override
    protected void chooseServer() {
        MainApplication.unifiedSDK.getBackend().isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@androidx.annotation.NonNull Boolean aBoolean) {
                if (aBoolean) {
                   // RegiochooserDialog.newInstance().show(getSupportFragmentManager(), RegiochooserDialog.TAG);
                startActivity(new Intent(VPNActivity.this,RegionsScreen.class));
                } else {
//                    showMessage("Login please");
                    showMessage("Please Wait VPN is Initializing");
                }
            }
            @Override
            public void failure(@androidx.annotation.NonNull VpnException e) {

            }
        });
    }

    @Override
    protected void getCurrentServer(final Callback<String> callback) {
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@androidx.annotation.NonNull VPNState state) {
                if (state == VPNState.CONNECTED) {
                    UnifiedSDK.getStatus(new Callback<SessionInfo>() {
                        @Override
                        public void success(@androidx.annotation.NonNull SessionInfo sessionInfo) {
                            callback.success(CredentialsCompat.getServerCountry(sessionInfo.getCredentials()));
                        }
                        @Override
                        public void failure(@androidx.annotation.NonNull VpnException e) {
                            callback.success(selectedCountry);
                        }
                    });
                } else {
                    callback.success(selectedCountry);
                }
            }
            @Override
            public void failure(@androidx.annotation.NonNull VpnException e) {
                callback.failure(e);
            }
        });
    }
    @Override
    protected void checkRemainingTraffic() {
        UnifiedSDK.getInstance().getBackend().remainingTraffic(new Callback<RemainingTraffic>() {
            @Override
            public void success(RemainingTraffic remainingTraffic) {
                updateRemainingTraffic(remainingTraffic);
            }

            @Override
            public void failure(VpnException e) {
                updateUI();

                handleError(e);
            }
        });
    }

    public void setLoginParams(String hostUrl, String carrierId) {
        ((MainApplication) getApplication()).setNewHostAndCarrier(hostUrl, carrierId);
    }

    @Override
    public void onRegionSelected(Country item) {

        selectedCountry = item.getCountry();
        updateUI();

        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@androidx.annotation.NonNull VPNState state) {
                if (state == VPNState.CONNECTED) {
                    showMessage("Reconnecting to VPN with " + selectedCountry);
                    UnifiedSDK.getInstance().getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                        @Override
                        public void complete() {
                            connectToVpn();
                        }

                        @Override
                        public void error(VpnException e) {
                            // In this case we try to reconnect
                            selectedCountry = "";
                            connectToVpn();
                        }
                    });
                }
            }

            @Override
            public void failure(@androidx.annotation.NonNull VpnException e) {

            }
        });
    }

    public void loginUser() {
        loginToVpn();
    }

    public void handleError(Throwable e) {
        Log.w(TAG, e);
        if (e instanceof NetworkRelatedException) {
            showMessage("Check internet connection fun");
        } else if (e instanceof VpnException) {
            if (e instanceof VpnPermissionRevokedException) {
                showMessage("User revoked vpn permissions");
            } else if (e instanceof VpnPermissionDeniedException) {
                showMessage("User canceled to grant vpn permissions");
            } else if (e instanceof HydraVpnTransportException) {
                HydraVpnTransportException hydraVpnTransportException = (HydraVpnTransportException) e;
                if (hydraVpnTransportException.getCode() == HydraVpnTransportException.HYDRA_ERROR_BROKEN) {
                    showMessage("Connection with vpn server was lost");
                } else if (hydraVpnTransportException.getCode() == HydraVpnTransportException.HYDRA_DCN_BLOCKED_BW) {
//                    showMessage("Client traffic exceeded");
                    showMessage("Please buy our Premium plan");
                    Log.d("limitexced", "handleError: ");
                   /* AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.customdialog, viewGroup, false);
                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();*/

                } else {
                    showMessage("Error in VPN transport");
                }
            } else if (e instanceof PartnerApiException) {
                switch (((PartnerApiException) e).getContent()) {
                    case PartnerApiException.CODE_NOT_AUTHORIZED:
//                        showMessage("User unauthorized");
                        showMessage("VPN is Initializing");
                        break;
                    case PartnerApiException.CODE_TRAFFIC_EXCEED:
                        //showMessage("Server unavailable");

//                        Intent serviceIntent = new Intent(MainActivity.this, ExampleService.class);
//                        serviceIntent.putExtra("inputExtra", "this is vpn warning message");
//                        startService(serviceIntent);

                     /*   AlertDialog.Builder builder = new AlertDialog.Builder(VPNActivity.this);
                        ViewGroup viewGroup = findViewById(android.R.id.content);
                        View dialogView = LayoutInflater.from(VPNActivity.this).inflate(R.layout.customdialog, viewGroup, false);
                        builder.setView(dialogView);
                        AlertDialog alertDialog = builder.create();*/
                        // alertDialog.show();
                        showMessage("Your limit will be exceed");
                        if(dialogShown)
                        {
                            return;
                        }
                        else
                        {
                            dialogShown = true;
                            // dialog =  new AlertDialog.Builder(context);
                           // alertDialog.show();
                        }
                        break;
                    default:
//                        showMessage("Other error. Check PartnerApiException constants");
                        showMessage("Something went wrong");
                        break;
                }
            }
        } else {
            showMessage("Error in VPN Service");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_rate) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(VPNActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.rateus_dialog,null);
            Button dialogButtonExit =  mView.findViewById(R.id.laterbtn);
            Button dialogButtonBack =  mView.findViewById(R.id.rateusbtn);
            alert.setView(mView);
            final AlertDialog alertDialog = alert.create();
            alertDialog.setCanceledOnTouchOutside(false);
            dialogButtonExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            dialogButtonBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                }
            });
            alertDialog.show();
        }
        if (id==R.id.id_btnlogout){
            FirebaseAuth.getInstance().signOut();
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()){
                                startActivity(new Intent(VPNActivity.this,LoginScreen.class));
                                Toast.makeText(getApplicationContext(),"Logout",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getApplicationContext(),"Session not close",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        if (id==R.id.nav_profile){
            startActivity(new Intent(VPNActivity.this,ViewUserProfile.class));
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(VPNActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.exit_dialog,null);
        Button dialogButtonExit =  mView.findViewById(R.id.extbtn);
        Button dialogButtonBack =  mView.findViewById(R.id.extback);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        dialogButtonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
        dialogButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        alertDialog.show();
    }

    @Override
    public void onConnectionFailed(@NonNull @NotNull ConnectionResult connectionResult) {

    }
}
