package com.example.fbgoogle_login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    LinearLayout fb,google;
    CallbackManager callbackManager;
    GoogleSignInOptions options;
    GoogleSignInClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fb=findViewById(R.id.facebook_btn);
        google=findViewById(R.id.google_btn);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    " com.example.fbgoogle_login",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {
        }
        catch (NoSuchAlgorithmException e) {
        }



        AccessToken accessToken = AccessToken.getCurrentAccessToken();
         if (accessToken != null && !accessToken.isExpired()){
             startActivity(new Intent(MainActivity.this,MainActivity2.class));
             finish();
         }

        options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        client = GoogleSignIn.getClient(this, options);

        callbackManager = CallbackManager.Factory.create();

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sinin();
            }


        });

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        startActivity(new Intent(MainActivity.this,MainActivity2.class));
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        exception.printStackTrace();
                    }
                });

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile"));

            }
        });
    }



    private void sinin() {
        Intent signinIntent = client.getSignInIntent();
        startActivityForResult(signinIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
    void navigateToSecondActivity(){
        finish();
        Intent intent = new Intent(MainActivity.this,MainActivity2.class);
        startActivity(intent);
    }
}
