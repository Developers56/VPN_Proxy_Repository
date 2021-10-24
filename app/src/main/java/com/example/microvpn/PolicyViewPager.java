package com.example.microvpn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PolicyViewPager extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_view_pager);
        ViewPager viewPager = (ViewPager) findViewById(R.id.idviewpager);
        viewPager.setAdapter(new CustomPageAdapter(this));
    }

    public void goestosignup(View view) {
        startActivity(new Intent(getApplicationContext(),SignupScreen.class));
    }
}