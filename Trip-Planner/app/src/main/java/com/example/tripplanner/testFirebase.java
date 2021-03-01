package com.example.tripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class testFirebase extends AppCompatActivity implements ValueEventListener {

    private Button btnsubmit;
    private EditText edtext;
    private TextView label;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference ref = firebaseDatabase.getReference();
    private DatabaseReference Text = ref.child("Text");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_firebase);

        btnsubmit = (Button) findViewById(R.id.btnsubmit);
        edtext = (EditText) findViewById(R.id.tvtext);
        label = (TextView) findViewById(R.id.label);
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = edtext.getText().toString();
                Text.setValue(data);
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