package com.example.tripplanner.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tripplanner.R;
import com.example.tripplanner.model.User;
import com.google.firebase.auth.FirebaseAuth;

public class UserInfomationActivity extends AppCompatActivity {
    private Button btnLogin;
    private EditText edtEmail;
    private EditText edtPassword;
    private TextView edtName;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_infomation);

        edtEmail = (EditText) findViewById(R.id.userinfomation_edtEmail);
        edtPassword = (EditText) findViewById(R.id.userinfomation_edtPassword);
        edtName = (TextView) findViewById(R.id.userinfomation_edtName);

        User currentUser = (User)getIntent().getSerializableExtra("currentUser");
        edtName.setText(currentUser.fullname);
        edtEmail.setText(currentUser.email);
    }
}