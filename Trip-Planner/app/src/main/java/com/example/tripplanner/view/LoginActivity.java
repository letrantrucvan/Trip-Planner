package com.example.tripplanner.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tripplanner.R;
import com.example.tripplanner.model.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnSignup;
    private EditText edtEmail;
    private EditText edtPassword;
    private TextView edtForgot;

    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private User user;
    private DataSnapshot dataSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        btnLogin = (Button) findViewById(R.id.login_btnLogin);
        btnSignup = (Button) findViewById(R.id.login_btnSignup);
        edtEmail = (EditText) findViewById(R.id.login_edtEmail);
        edtPassword = (EditText) findViewById(R.id.login_edtPassword);
        edtForgot = (TextView) findViewById(R.id.login_edtForgot);

        btnSignup.setText(Html.fromHtml("<i>Don't have an account?</i> <font size=\"18sp\"><b>SIGN UP</b></font>"));
        edtForgot.setText(Html.fromHtml("<i><u>Forgot your password?</u></i>"));

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();

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

    private void logIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    if (mAuth.getCurrentUser().isEmailVerified()){
                        //LOGIN SUCCESSFULLY
//                        getUserInformation();
//                        Intent i = new Intent(LoginActivity.this, UserInfomationActivity.class);
//                        i.putExtra("currentUser", user);
//                        startActivity(i);
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

    private void getUserInformation(){

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("hoho");
                user = dataSnapshot.child("User").child(mAuth.getUid()).getValue(User.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref.addValueEventListener(postListener);
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


}