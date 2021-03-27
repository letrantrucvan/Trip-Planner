package com.example.travelplanner.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.travelplanner.R;
import com.example.travelplanner.model.Tour;
import com.example.travelplanner.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailsActivity extends AppCompatActivity {

    private Button readMore;
    private TextView tourDescription;
    private boolean isOpenDescription;
    private Intent intent;
    private TextView tourName;
    private ImageView tourCover;
    private TextView tourAuthorName;
    private ImageView tourAuthorImg;
    private TextView TourDescription;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String tourID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        isOpenDescription = false;
        readMore = (Button) findViewById(R.id.detail_btnReadMore);
        tourDescription = (TextView) findViewById(R.id.tour_description_txt);

        intent = getIntent();
        tourID = intent.getStringExtra("Key");
        System.out.println(tourID);

        tourName = (TextView) findViewById(R.id.tour_name_txt);
        tourCover = (ImageView) findViewById(R.id.cover_img);
        tourAuthorName = (TextView) findViewById(R.id.tour_author_txt);
        tourAuthorImg = (ImageView) findViewById(R.id.tour_author_img);
        TourDescription =  (TextView ) findViewById(R.id.tour_description_txt);

        //Start UI
        loadUI();

        readMore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isOpenDescription){
                    tourDescription.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    tourDescription.requestLayout();
                    readMore.setText("- Thu gọn");
                    isOpenDescription = true;
                }
                else {
                    tourDescription.getLayoutParams().height = 80;
                    tourDescription.requestLayout();
                    readMore.setText("+ Xem thêm");
                    isOpenDescription = false;
                }
            }
        });
    }

    private void loadUI() {

        db.collection("Tour").document(tourID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Tour tour = documentSnapshot.toObject(Tour.class);

                    //get name + email
                    tourName.setText(tour.getName());
                    tourDescription.setText(tour.getDes());
                    //get avatar
                    StorageReference islandRef = storage.getReference().child(tour.getCover());
                    final long ONE_MEGABYTE = 1024 * 1024;

                    islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Data for "images/island.jpg" is returns, use this as needed
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            tourCover.setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            System.out.println("Fail");
                        }
                    });

                    db.collection("User").document(tour.getAuthor_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override

                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);
                                System.out.println(user.toString());
                                tourAuthorName.setText(user.getFullname());

                                //get avatar
                                StorageReference islandRef = storage.getReference().child(user.getLink_ava_user());

                                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        // Data for "images/island.jpg" is returns, use this as needed
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        tourAuthorImg.setImageBitmap(bitmap);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                        System.out.println("Fail");
                                    }
                                });

                            }
                        }
                        });
                }
            }
        });
    }
}
