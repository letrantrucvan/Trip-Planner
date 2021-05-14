package com.example.travelplanner.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.example.travelplanner.R;
import com.example.travelplanner.fragment.BottomChooseCamera;
import com.example.travelplanner.model.Tour;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

public class EditTourActivity extends AppCompatActivity {
    private Intent intent;
    private String tourID;
    private EditText name;
    private EditText description;
    private ImageView cover_pic;
    private RadioButton is_publicc;
    private RadioButton is_private;
    private AppCompatButton deleteTour;
    private AppCompatButton save_Tour;
    private Tour current;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    int REQUEST_CODE_IMAGE = 1;

    private static final String TAG = "Edit Tour";

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tour);

        intent = getIntent();
        tourID = intent.getStringExtra("Key");

        mAuth = FirebaseAuth.getInstance();

        name = (EditText) findViewById(R.id.tourName);
        description = (EditText) findViewById(R.id.description);
        cover_pic = (ImageView) findViewById(R.id.cover_pic);
        is_publicc = (RadioButton) findViewById(R.id.public_mode);
        is_private = (RadioButton) findViewById(R.id.private_mode);
        deleteTour = (AppCompatButton) findViewById(R.id.deleteTour);
        save_Tour = (AppCompatButton) findViewById(R.id.finish_button);

        loadUI();

        description.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (description.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });

        deleteTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tour tour;
                tour = new Tour();
                tour.setId(tourID);
                tour.setActive(false);

                Tour.delete(tour);

                Intent i = new Intent(EditTourActivity.this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });


        save_Tour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tour tour;
                tour = new Tour();
                tour.setId(tourID);
                tour.setArchived_mode(is_publicc.isChecked());
                tour.setName(name.getText().toString());
                tour.setDes(description.getText().toString());

                Tour.editTour(tour);


                //up avatar vô Storage
                cover_pic.setDrawingCacheEnabled(true);
                cover_pic.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) cover_pic.getDrawable()).getBitmap();
                Tour.uploadCover(tourID, bitmap);

                //chuyển về fragment home
                Intent i = new Intent(EditTourActivity.this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        cover_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomChooseCamera ChooseCamera = new BottomChooseCamera(mAuth.getUid(), cover_pic);
                ChooseCamera.show(getSupportFragmentManager(),TAG);
            }
        });



    }

    private void loadUI() {
        db.collection("Tour").document(tourID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.exists()){
                    Tour current = documentSnapshot.toObject(Tour.class);
                    //get name + email
                    name.setText(current.getName());
                    description.setText(current.getDes());

                    //get avatar
                    Picasso.with(EditTourActivity.this).load(current.getCover()).into(cover_pic);
                    System.out.println("TRang thai tour: "+ current.isIs_public());

                    is_publicc.setChecked(current.isIs_public());
                    is_private.setChecked(!current.isIs_public());
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data!=null){
            Picasso.with(EditTourActivity.this).load(data.getData()).into(cover_pic);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}