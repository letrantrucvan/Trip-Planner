package com.example.travelplanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.travelplanner.controller.HomeActivity;
import com.example.travelplanner.fragment.BottomChooseCamera;
import com.example.travelplanner.model.Tour;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Create_new_tour extends AppCompatActivity {
    private static final String TAG = "Create Tour";
    private TextView trip_name;
    private TextView des;
    private ImageView cover_pic;
    private Button finish;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    int REQUEST_CODE_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_tour);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        ImageView close = (ImageView) findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Create_new_tour.this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });



        //Get info to create
        trip_name = (TextView) findViewById(R.id.tourName);
        des = (TextView) findViewById(R.id.description);
        cover_pic = (ImageView) findViewById(R.id.cover_pic);
        finish = (Button) findViewById(R.id.finish_button);


        //gan hinh cover vo
        cover_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomChooseCamera ChooseCamera = new BottomChooseCamera(mAuth.getUid(), cover_pic);
                ChooseCamera.show(getSupportFragmentManager(),TAG);
            }
        });

        finish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String pattern = "dd/MM/yyyy";
                String dateInString = new SimpleDateFormat(pattern).format(new Date());
                String tourName = trip_name.getText().toString();
                String tourDescription = des.getText().toString();
                Tour newTour = new Tour(tourName, mAuth.getUid(), tourDescription, dateInString);
                System.out.println("00000000000000000000000000000000"+newTour.getSearch_keywords());
                String tourID = Tour.addTour(newTour);

                //up avatar vô Storage
                cover_pic.setDrawingCacheEnabled(true);
                cover_pic.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) cover_pic.getDrawable()).getBitmap();
                Tour.uploadCover(tourID, bitmap);

                //chuyển về fragment home
                Intent i = new Intent(Create_new_tour.this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data!=null){
            Picasso.with(Create_new_tour.this).load(data.getData()).into(cover_pic);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}