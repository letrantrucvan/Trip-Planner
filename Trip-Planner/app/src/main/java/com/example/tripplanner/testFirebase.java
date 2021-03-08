package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class testFirebase extends AppCompatActivity implements ValueEventListener {

    private Button btnsubmit;
    private EditText edtext;
    private TextView label;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference ref = firebaseDatabase.getReference();
    private DatabaseReference Text = ref.child("Text");
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_firebase);

        mAuth = FirebaseAuth.getInstance();

        btnsubmit = (Button) findViewById(R.id.btnsubmit);
        edtext = (EditText) findViewById(R.id.tvtext);
        label = (TextView) findViewById(R.id.label);
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String data = edtext.getText().toString();
//                Text.setValue(data);
                logIn();
            }
        });
//        User a = new User("Trúc Vân", "letrantrucvan@gamil.com","vanmapdjt");
//        User.addUser(a);
//        User b = new User("Hoàng Minh", "minh@gmail.com","vanmapdjt");
//        User.addUser(a);
        signUp();
    }
    private void authentication(){
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://www.example.com/finishSignUp?cartId=1234")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setIOSBundleId("com.example.ios")
                        .setAndroidPackageName(
                                "com.example.android",
                                true, /* installIfNotAvailable */
                                "12"    /* minimumVersion */)
                        .build();
    }

    private void signUp(){
        String email = "minh.nguyen28122000@gmail.com";
        String password= "hellococo";
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(testFirebase.this, "Please check your email verification",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(testFirebase.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
    });
    }

    private void logIn(){
        mAuth.signInWithEmailAndPassword("minh.nguyen28122000@gmail.com", "hellococo").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    if (mAuth.getCurrentUser().isEmailVerified()){
                        Toast.makeText(testFirebase.this, mAuth.getCurrentUser().getUid(),
                                Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(testFirebase.this, "Please verify your email.",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(testFirebase.this, task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Text.addValueEventListener(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.getValue(String.class)!=null){
            String key = snapshot.getKey();
            if (key.equals("Text")){
                String a = snapshot.getValue(String.class);
                label.setText(a);
            }
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}