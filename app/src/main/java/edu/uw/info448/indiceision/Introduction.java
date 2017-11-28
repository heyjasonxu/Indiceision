package edu.uw.info448.indiceision;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Introduction extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private static final String TAG = "Introduction";
    private CallbackManager mCallbackManager;
    private LoginButton facebookLoginButton;
    private FirebaseAuth auth;
    private static final int SIGN_IN_RESPONSE_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        //get hash key programatically
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "edu.uw.info448.indiceision",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.v(TAG, "KeyHash:" +  Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }


        mDatabase = FirebaseDatabase.getInstance().getReference();
        final Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Button clicked");
                FirebaseUser user = auth.getCurrentUser();
                if(user != null){
                    Log.v(TAG, "This is the current user: " + user.getEmail());
                    AuthUI.getInstance().signOut(Introduction.this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.v(TAG, "Logged out");
                                    login.setText("Login With Email");
                                }
                            });
                }else{
                    startActivityForResult(
                            AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_RESPONSE_CODE);
                    login.setText("Log out");
                }


            }
        });

        auth = FirebaseAuth.getInstance();


        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        facebookLoginButton.setReadPermissions("email");

        mCallbackManager = CallbackManager.Factory.create();
        facebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v(TAG, "Facebook login successful: " + loginResult.toString());
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.v(TAG, "Facebook login cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.v(TAG, "Facebook login error: " + error);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public void handleFacebookAccessToken(AccessToken token){
        Log.v(TAG, "handleFacebookAccessToken " + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser currentUser = auth.getCurrentUser();
                            Log.v(TAG, "signInWithCreditential success: " + currentUser.getDisplayName());
                        }else{
                            Log.v(TAG, "signInWithCreditential failed: " + task.getException());
                        }
                    }
                });

    }
}
