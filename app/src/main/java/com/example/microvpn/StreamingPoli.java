package com.example.microvpn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StreamingPoli extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming_poli);

    }

    public void NextStreaming(View view) {
        startActivity(new Intent(StreamingPoli.this,MalwarePoli.class));
    }
}