package com.example.microvpn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.anchorfree.partner.api.data.Country;
import com.anchorfree.partner.api.response.AvailableCountries;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

public class RegionsScreen extends AppCompatActivity implements RegionListAdapter.RegionListAdapterInterface,
 NavigationView.OnNavigationItemSelectedListener{
    RecyclerView regionsRecyclerView;
    private RegionListAdapter regionAdapter;
    private RegionChooserInterface regionChooserInterface;
    ProgressBar pgregions;
    private DrawerLayout drawer;
    ImageView navmenu;
    View view;
   // RegionListAdapter.onClickInterface onclickInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regions_screen);

        pgregions = findViewById(R.id.regions_progress);
        navmenu = findViewById(R.id.idmenupremium);

        regionChooserInterface = new RegionChooserInterface() {
            @Override
            public void onRegionSelected(Country item) {

            }
        };
    /*    onclickInterface = new RegionListAdapter.onClickInterface() {
            @Override
            public void setClick(int abc) {
                Toast.makeText(RegionsScreen.this,"Position is"+abc,Toast.LENGTH_LONG).show();
                regionAdapter.notifyDataSetChanged();
                SharedPreferences preferencesvar =getSharedPreferences("checkvalue", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorval = preferencesvar.edit();
                editorval.putInt("valueMB", abc);
                editorval.commit();
                editorval.apply();
            }
        };*/
        regionsRecyclerView = findViewById(R.id.countries_recycler_view);
        regionsRecyclerView.setHasFixedSize(true);
        regionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        regionAdapter = new RegionListAdapter(this);
        regionsRecyclerView.setAdapter(regionAdapter);
        regionAdapter.notifyDataSetChanged();
        regionAdapter.setOnItemClickedListener(new RegionListAdapter.OnitemClickListener() {
            @Override
            public void onItemClick(int position) {
                  Toast.makeText(RegionsScreen.this, "pos" + position, Toast.LENGTH_SHORT).show();
//                  regionAdapter.notifyItemChanged(position);
            }
        });
        navmenu.setOnClickListener(view -> drawer.openDrawer(GravityCompat.START));
        setupDrawer();
        loadServers();
    }

    private void setupDrawer() {
        drawer = findViewById(R.id.drawer_layout_premium);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                RegionsScreen.this, drawer, null, 0, 0);//R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(RegionsScreen.this);
    }

    private void loadServers() {
        UnifiedSDK.getInstance().getBackend().countries(new Callback<AvailableCountries>() {
            @Override
            public void success(@NonNull final AvailableCountries countries) {
                regionAdapter.setRegions(countries.getCountries());
                pgregions.setVisibility(View.GONE);
            }
            @Override
            public void failure(VpnException e) {

            }
        });
    }
    @Override
    public void onCountrySelected(Country item) {
        regionChooserInterface.onRegionSelected(item);
    }

    public void gotopreiumActivity3(View view) {
        startActivity(new Intent(RegionsScreen.this,PremiumScreen.class));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        return false;
    }


    public interface RegionChooserInterface {
        void onRegionSelected(Country item);
    }

}