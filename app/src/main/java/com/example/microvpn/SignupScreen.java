package com.example.microvpn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class SignupScreen extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private EditText inputEmail, inputPassword,inputusername;
    private FirebaseAuth auth;
    CheckBox checkBoxsignup;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;
    String name, email,emailuser,passworduser,username;
    String idToken;

    //database
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    model_useInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_signup_screen);

        auth = FirebaseAuth.getInstance();
        inputEmail = (EditText) findViewById(R.id.edtusernamesignup);
        inputPassword = (EditText) findViewById(R.id.edtpasswordsignup);
        checkBoxsignup = findViewById(R.id.checkBoxsig);
        inputusername = findViewById(R.id.username);

        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))//you can also use R.string.default_web_client_id
                .requestEmail()
                .build();
        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");



    }

    public void Signup(View view) {

        emailuser = inputEmail.getText().toString().trim();
        passworduser = inputPassword.getText().toString().trim();
        username = inputusername.getText().toString().trim();

        SharedPreferences preferenceslim = getSharedPreferences("vpnlimitvalue", Context.MODE_PRIVATE);
        SharedPreferences.Editor editorlim = preferenceslim.edit();
        editorlim.putString("limitvalue", username);
        editorlim.apply();

        SharedPreferences preferencesemail = getSharedPreferences("useremail", Context.MODE_PRIVATE);
        SharedPreferences.Editor editoremail = preferencesemail.edit();
        editoremail.putString("useremailid", emailuser);
        editoremail.apply();

        if (TextUtils.isEmpty(emailuser)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(passworduser)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passworduser.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (checkBoxsignup.isChecked()) {
            auth.createUserWithEmailAndPassword(emailuser, passworduser)
                    .addOnCompleteListener(SignupScreen.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {

                                Toast.makeText(SignupScreen.this, "Authentication failed." + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(new Intent(SignupScreen.this, LoginScreen.class));
                                finish();
                            }
                        }
                    });
        }
        /*firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(auth.getInstance().getCurrentUser());*/

        /*model_useInfo userProfile = new model_useInfo(username, emailuser, passworduser);
        databaseReference.child(auth.getUid()).setValue(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                Toast.makeText(SignupScreen.this, "uploaded", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    public void addDatatoFirebase() {
       /* // below 3 lines of code is used to set
        // data in our object class.
        userInfo.setUsername(username);
        userInfo.setUseremail(email);
        userInfo.setUsepasswor(password);

        // we are use add value event listener method
        // which is called with database reference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // inside the method of on Data change we are setting
                // our object class to our database reference.
                // data base reference will sends data to firebase.
                databaseReference.setValue(userInfo);

                // after adding this data we are showing toast message.
                Toast.makeText(SignupScreen.this, "data added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                Toast.makeText(SignupScreen.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });*/

       /* model_useInfo userProfile = new model_useInfo(username, emailuser, passworduser);
        databaseReference.child(auth.getUid()).setValue(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                Toast.makeText(SignupScreen.this, "uploaded", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }



    public void gotologin(View view) {
        startActivity(new Intent(SignupScreen.this,LoginScreen.class));
    }

    public void signupviagoogle(View view) {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull @NotNull ConnectionResult connectionResult) {

    }

    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            idToken = account.getIdToken();
            name = account.getDisplayName();
            email = account.getEmail();
            // you can store user data to SharedPreference
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuthWithGoogle(credential);
        }else{
            // Google Sign In failed, update UI appropriately
//            Log.e(TAG, "Login Unsuccessful. "+result);
            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(AuthCredential credential){
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if(task.isSuccessful()){
                            Toast.makeText(SignupScreen.this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignupScreen.this,VPNActivity.class));
                           /* Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", false);
                            if (isFirstRun) {
                                startActivity(new Intent(LoginScreen.this, VPNActivity.class));
                                finish();
                            }
                            else{
                                startActivity(new Intent(LoginScreen.this, PrivacyPolicy.class));
                            }*/

                        }else{
//                            Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                            Toast.makeText(SignupScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}