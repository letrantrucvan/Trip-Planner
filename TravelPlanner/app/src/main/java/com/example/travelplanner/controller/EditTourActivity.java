package com.example.travelplanner.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.travelplanner.R;

public class EditTourActivity extends AppCompatActivity {
    private Intent intent;
    private String tourID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tour);

        intent = getIntent();
        tourID = intent.getStringExtra("Key");
    }
}