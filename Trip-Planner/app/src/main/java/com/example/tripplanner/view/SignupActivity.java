package com.example.tripplanner.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tripplanner.R;
import com.example.tripplanner.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnSignup;
    private EditText edtName;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtRetype;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btnLogin = (Button) findViewById(R.id.signup_btnLogin);
        btnSignup = (Button) findViewById(R.id.signup_btnSignup);
        edtName = (EditText) findViewById(R.id.signup_edtName);
        edtEmail = (EditText) findViewById(R.id.signup_edtEmail);
        edtPassword = (EditText) findViewById(R.id.signup_edtPassword);
        edtRetype = (EditText) findViewById(R.id.signup_edtRetype);

        btnLogin.setText(Html.fromHtml("<i>Already have an account?</i> <font size=\"18sp\"><b>LOG IN</b></font>"));

        mAuth = FirebaseAuth.getInstance();
        //user = mAuth.getCurrentUser();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                String retype = edtRetype.getText().toString();
                if (checkInput(name, email, password, retype)){
                    signUp(name, email, password);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
    }

    private void signUp(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = mAuth.getCurrentUser();
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(SignupActivity.this, "Sign up successfully, please verify your email.", Toast.LENGTH_SHORT).show();
                            User a = new User(user.getEmail(), name);
                            User.addUser(user.getUid(), a);
                        }
                    });
                } else {
                    // If sign in fails, display a message to the user.
                    //Toast.makeText(SignupActivity.this, "This email has been used for another account.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkInput(String name, String email, String password, String retype){
        if (name.equals("")){
            Toast.makeText(SignupActivity.this, "Please input your full name.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (email.equals("")){
            Toast.makeText(SignupActivity.this, "Please input your email.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (password.equals("")){
            Toast.makeText(SignupActivity.this, "Please input your password.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (retype.equals("")){
            Toast.makeText(SignupActivity.this, "Please confirm your password.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (password.length()<6){
            Toast.makeText(SignupActivity.this, "Password contains at least 6 characters.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!password.equals(retype)){
            Toast.makeText(SignupActivity.this, "Confirm password does not match.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
