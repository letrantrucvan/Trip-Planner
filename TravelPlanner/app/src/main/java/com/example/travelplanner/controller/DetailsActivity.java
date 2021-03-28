package com.example.travelplanner.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.TextView;

import com.example.travelplanner.R;
import com.example.travelplanner.fragment.FragmentMaps;
import com.example.travelplanner.fragment.FragmentTwo;
import com.example.travelplanner.fragment.TabFragmentAdapter;
import com.example.travelplanner.model.Tour;
import com.example.travelplanner.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
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
    private TextView tourDescriptionFull;
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

    //tablayout
    private  TabLayout mTabs;
    private View mIndicator;
    private ViewPager mViewPager;

    private int indicatorWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        isOpenDescription = false;
        readMore = (Button) findViewById(R.id.detail_btnReadMore);
        tourDescription = (TextView) findViewById(R.id.tour_description_txt);
        tourDescriptionFull = (TextView) findViewById(R.id.tour_description_full_txt);

        intent = getIntent();
        tourID = intent.getStringExtra("Key");
        System.out.println(tourID);

        tourName = (TextView) findViewById(R.id.tour_name_txt);
        tourCover = (ImageView) findViewById(R.id.cover_img);
        tourAuthorName = (TextView) findViewById(R.id.tour_author_txt);
        tourAuthorImg = (ImageView) findViewById(R.id.tour_author_img);
        TourDescription =  (TextView ) findViewById(R.id.tour_description_txt);

        //Tab layout
        //Assign view reference
        mTabs = findViewById(R.id.tab);
        mIndicator = findViewById(R.id.indicator);
        mViewPager = findViewById(R.id.viewPager);

        //Set up the view pager and fragments
        TabFragmentAdapter adapter = new TabFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(FragmentMaps.newInstance(), "Lộ trình");
        adapter.addFragment(FragmentTwo.newInstance(), "Địa điểm");
        mViewPager.setAdapter(adapter);
        mTabs.setupWithViewPager(mViewPager);

        //Determine indicator width at runtime
        mTabs.post(new Runnable() {
            @Override
            public void run() {
                indicatorWidth = mTabs.getWidth() / mTabs.getTabCount();

                //Assign new width
                FrameLayout.LayoutParams indicatorParams = (FrameLayout.LayoutParams) mIndicator.getLayoutParams();
                indicatorParams.width = indicatorWidth;
                mIndicator.setLayoutParams(indicatorParams);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float positionOffset, int positionOffsetPx) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)mIndicator.getLayoutParams();

                //Multiply positionOffset with indicatorWidth to get translation
                float translationOffset =  (positionOffset+i) * indicatorWidth ;
                params.leftMargin = (int) translationOffset;
                mIndicator.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });




        //Start UI
        loadUI();


        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpenDescription){
                    tourDescription.setVisibility(View.VISIBLE);
                    tourDescriptionFull.setVisibility(View.GONE);
                    readMore.setText("Xem thêm");
                    isOpenDescription = false;
                }
                else {
                    tourDescription.setVisibility(View.GONE);
                    tourDescriptionFull.setVisibility(View.VISIBLE);
                    readMore.setText("Thu gọn");
                    isOpenDescription = true;
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
                    tourName.setText(formatTourName(tour.getName()));
                    tourDescription.setText(tour.getDes());
                    tourDescriptionFull.setText(tour.getDes());
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

    String formatTourName(String name){
        if (name.length() > 30){
            name = name.substring(0, 27);
            name += "...";
        }
        return name;
    }
}
