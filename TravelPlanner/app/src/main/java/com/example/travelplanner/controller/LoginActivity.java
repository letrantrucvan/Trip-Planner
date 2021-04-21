package com.example.travelplanner.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelplanner.R;
import com.example.travelplanner.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.WebDialog;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    public static final int GOOGLE_SIGN_IN_CODE = 1005;
    private static final String TAG = "FacebookAuthentication";
    private Button btnLogin;
    private Button btnLoginGoogle;
    private Button btnSignup;
    private EditText edtEmail;
    private EditText edtPassword;
    private TextView edtForgot;

    private LoginButton facbook_login;
    private CallbackManager mCbm;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.login_btnLogin);
        btnLoginGoogle = (Button) findViewById(R.id.login_btnGoogle);
        btnSignup = (Button) findViewById(R.id.login_btnSignup);
        edtEmail = (EditText) findViewById(R.id.login_edtEmail);
        edtPassword = (EditText) findViewById(R.id.login_edtPassword);
        edtForgot = (TextView) findViewById(R.id.login_edtForgot);

        btnSignup.setText(Html.fromHtml("<i>Don't have an account?</i> <font size=\"18sp\"><b>SIGN UP</b></font>"));
        edtForgot.setText(Html.fromHtml("<i><u>Forgot your password?</u></i>"));

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("User");
        FacebookSdk.sdkInitialize(getApplicationContext());
        facbook_login = (LoginButton) findViewById(R.id.login_button);
        mCbm = CallbackManager.Factory.create();
        
        facbook_login.setReadPermissions("email", "public_profile");

        facbook_login.registerCallback(mCbm, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess" + loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError" + error);
            }
        });


        if(mAuth.getCurrentUser() != null){
            Intent i = new Intent(this, HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }


        // Configure Google Sign In
         gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                 .requestIdToken("783867193943-4k5st8evpm88ck81l3d2o4jup25o2192.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnLoginGoogle.setOnClickListener(v -> SignInGoogle());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password= edtPassword.getText().toString();
                if (checkInput(email, password)){
                    logIn(email, password);
                }
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });

        edtForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i);
            }
        });
    }

    private void SignInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(LoginActivity.this, "Google Account is connect.", Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(LoginActivity.this, "Google Sign In failed.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else {
            mCbm.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            createIfNotExist(user);
                            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(i);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Sign in fail.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void logIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    if (mAuth.getCurrentUser().isEmailVerified()){
                        //LOGIN SUCCESSFULLY
                        //chuyen qua man hinh khac
                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(i);
                    } else{
                        Toast.makeText(LoginActivity.this, "Please verify your email.",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkInput(String email, String password){
        if (email.equals("")){
            Toast.makeText(LoginActivity.this, "Please input your email.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (password.equals("")){
            Toast.makeText(LoginActivity.this, "Please input your password.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (password.length()<6){
            Toast.makeText(LoginActivity.this, "Password contains at least 6 characters.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void createIfNotExist(FirebaseUser user){
        FirebaseFirestore.getInstance().collection("User").document(user.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()){
                    User a = new User(user.getUid(), user.getEmail(), user.getDisplayName());
                    User.addUser(user.getUid(), a);
                }
            }
        });
    }

    public static class OnMapAndViewReadyListener implements ViewTreeObserver.OnGlobalLayoutListener, OnMapReadyCallback {

        /** A listener that needs to wait for both the GoogleMap and the View to be initialized. */
        public interface OnGlobalLayoutAndMapReadyListener {
            void onMapReady(GoogleMap googleMap);
        }

        private final SupportMapFragment mapFragment;
        private final View mapView;
        private final OnGlobalLayoutAndMapReadyListener devCallback;

        private boolean isViewReady;
        private boolean isMapReady;
        private GoogleMap googleMap;

        public OnMapAndViewReadyListener(
                SupportMapFragment mapFragment, OnGlobalLayoutAndMapReadyListener devCallback) {
            this.mapFragment = mapFragment;
            mapView = mapFragment.getView();
            this.devCallback = devCallback;
            isViewReady = false;
            isMapReady = false;
            googleMap = null;

            registerListeners();
        }

        private void registerListeners() {
            // View layout.
            if ((mapView.getWidth() != 0) && (mapView.getHeight() != 0)) {
                // View has already completed layout.
                isViewReady = true;
            } else {
                // Map has not undergone layout, register a View observer.
                mapView.getViewTreeObserver().addOnGlobalLayoutListener(this);
            }

            // GoogleMap. Note if the GoogleMap is already ready it will still fire the callback later.
            mapFragment.getMapAsync(this);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            // NOTE: The GoogleMap API specifies the listener is removed just prior to invocation.
            this.googleMap = googleMap;
            isMapReady = true;
            fireCallbackIfReady();
        }

        @SuppressWarnings("deprecation")  // We use the new method when supported
        @SuppressLint("NewApi")  // We check which build version we are using.
        @Override
        public void onGlobalLayout() {
            // Remove our listener.
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
                mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
            isViewReady = true;
            fireCallbackIfReady();
        }

        private void fireCallbackIfReady() {
            if (isViewReady && isMapReady) {
                devCallback.onMapReady(googleMap);
            }
        }
    }

    private void handleFacebookToken(AccessToken token){
        Log.d(TAG, "handleFacebookToken" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "sign with credential: successful");
                    FirebaseUser new_user = mAuth.getCurrentUser();
                    createIfNotExist(new_user);
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);
                }else {
                    Toast.makeText(LoginActivity.this, "Sign in fail.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}