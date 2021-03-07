package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
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

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnSignup;
    private EditText edtEmail;
    private EditText edtPassword;
    private TextView edtForgot;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtForgot = (TextView) findViewById(R.id.edtForgot);

        btnSignup.setText(Html.fromHtml("<i>Don't have an account?</i> <font size=\"18sp\"><b>SIGN UP</b></font>"));
        edtForgot.setText(Html.fromHtml("<i><u>Forgot your password?</u></i>"));

        mAuth = FirebaseAuth.getInstance();

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
                edtForgot.setText("buon ngu qua di ngu");
            }
        });
    }

    private void logIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    if (mAuth.getCurrentUser().isEmailVerified()){
                        Toast.makeText(LoginActivity.this, "hello " + mAuth.getCurrentUser().getEmail(),
                                Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(LoginActivity.this, "Please verify your email.",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Invalid email or password.",
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
            Toast.makeText(LoginActivity.this, "Password contains at least 6 character.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}