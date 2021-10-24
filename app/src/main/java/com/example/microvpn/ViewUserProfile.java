package com.example.microvpn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class ViewUserProfile extends AppCompatActivity {
TextView tv_username,tv_email,tv_password;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);

        tv_username =  findViewById(R.id.idviewname);
        tv_email =  findViewById(R.id.idviewemail);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        /*firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                model_useInfo userProfile = snapshot.getValue(model_useInfo.class);
                tv_username.setText(userProfile.getUsername());
                tv_email.setText(userProfile.getUseremail());
                tv_password.setText(userProfile.getUsepasswor());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewUserProfile.this, error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });*/

/*uid = firebaseAuth.getUid();
        databaseReference = firebaseDatabase.getReference("users");
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                model_useInfo userProfile = snapshot.getValue(model_useInfo.class);
                tv_username.setText(userProfile.getUsername());
                tv_email.setText(userProfile.getUseremail());
                tv_password.setText(userProfile.getUsepasswor());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });*/
    }

    public void viewprofile(View view) {

        SharedPreferences app_preferences = getSharedPreferences("vpnlimitvalue", Context.MODE_PRIVATE);
        String updatevale = app_preferences.getString("limitvalue","john");

        tv_username.setText(updatevale);

        SharedPreferences app_preferencesemial = getSharedPreferences("useremail", Context.MODE_PRIVATE);
        String updatevaleemail = app_preferencesemial.getString("useremailid","john");

        tv_email.setText(updatevaleemail);
    }
}