package com.example.travelplanner.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.travelplanner.R;
import com.example.travelplanner.fragment.FragmentMaps;
import com.example.travelplanner.fragment.FragmentTwo;
import com.example.travelplanner.fragment.TabFragmentAdapter;
import com.example.travelplanner.model.Comment;
import com.example.travelplanner.model.Tour;
import com.example.travelplanner.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    private TextView tourPublishDay;
    private TextView tourRating;

    private RecyclerView mResultCommentTour;
    private FirestoreRecyclerAdapter adapterTourComment;

    private Button btnGoToLogin;
    private ImageView current_user_avatar;
    private EditText current_user_comment;
    private RadioButton current_user_rate_1;
    private RadioButton current_user_rate_2;
    private RadioButton current_user_rate_3;
    private RadioButton current_user_rate_4;
    private RadioButton current_user_rate_5;
    private Button btnUploadComment;

    private LinearLayout CommentBox;

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
        tourPublishDay = (TextView) findViewById(R.id.tourPublishDay);
        tourRating = (TextView) findViewById(R.id.tourRating);
        tourAuthorName = (TextView) findViewById(R.id.tour_author_txt);
        tourAuthorImg = (ImageView) findViewById(R.id.tour_author_img);

        mResultCommentTour = (RecyclerView) findViewById(R.id.recycleviewCommentDetail);
        mResultCommentTour.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();

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
        //Kết thúc set up Tab layout



        //Start UI
        loadUI();
        loadUIUser(); //check user đăng nhập hay chưa
        loadRatingComment();

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

                    //get thông tin tour
                    tourName.setText(formatTourName(tour.getName()));
                    tourDescription.setText(tour.getDes());
                    tourDescriptionFull.setText(tour.getDes());
                    tourPublishDay.setText("Đăng ngày " + tour.getPublish_day());
                    tourRating.setText(formatTourRating(tour.getRating_avg().toString()));

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

    private void loadRatingComment(){
        Query searchQuery  = db.collection("Rating").whereEqualTo("tour_id", tourID);
        //Bind data
        FirestoreRecyclerOptions<Comment> response = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(searchQuery, Comment.class)
                .build();

        adapterTourComment = new FirestoreRecyclerAdapter<Comment, CommentViewHolder>(response) {
            @Override
            public void onBindViewHolder(CommentViewHolder holder, int position, Comment model) {
                db.collection("User").document(model.getUser_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    //chỗ code ngu nè
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            User b = documentSnapshot.toObject(User.class);
                            holder.setDetail(model, b);
                        }
                    }
                });
                //tourRating.setText(Tour.);

            }

            @Override
            public CommentViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View mView = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.recycle_view_comment_detail, group, false);
                return new CommentViewHolder(mView);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };
        adapterTourComment.notifyDataSetChanged();
        adapterTourComment.startListening();
        mResultCommentTour.setAdapter(adapterTourComment);
    }

    private void loadUIUser(){
        //check user đăng nhập hay chưa
        btnUploadComment = (Button) findViewById(R.id.detail_comment_current_user_btn_upload);
        btnGoToLogin = (Button) findViewById(R.id.detail_btn_GotoLogin);
        CommentBox = (LinearLayout) findViewById(R.id.detail_comment_current_user_box);
        current_user_avatar = (ImageView) findViewById(R.id.detail_comment_current_user_avatar);
        current_user_comment = (EditText) findViewById(R.id.detail_comment_current_user_comment);
        current_user_rate_1 = (RadioButton) findViewById(R.id.detail_comment_current_user_rate_1);
        current_user_rate_1 = (RadioButton) findViewById(R.id.detail_comment_current_user_rate_2);
        current_user_rate_1 = (RadioButton) findViewById(R.id.detail_comment_current_user_rate_3);
        current_user_rate_1 = (RadioButton) findViewById(R.id.detail_comment_current_user_rate_4);
        current_user_rate_1 = (RadioButton) findViewById(R.id.detail_comment_current_user_rate_5);
        current_user_comment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    current_user_comment.setBackground(getResources().getDrawable(R.drawable.border_white));
                    current_user_comment.setTextColor(getResources().getColor(R.color.white));
                }
                else {
                    current_user_comment.setBackground(getResources().getDrawable(R.drawable.border_lightgrey));
                    current_user_comment.setTextColor(getResources().getColor(R.color.light_grey));
                }
            }
        });

        if (mAuth.getCurrentUser() == null){
            CommentBox.setVisibility(View.GONE);
            btnGoToLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(DetailsActivity.this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });
        }
        else {
            btnGoToLogin.setVisibility(View.GONE);

            db.collection("User").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        User user = documentSnapshot.toObject(User.class);
                        //get avatar
                        StorageReference islandRef = storage.getReference().child(user.getLink_ava_user());
                        final long ONE_MEGABYTE = 1024 * 1024;
                        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                // Data for "images/island.jpg" is returns, use this as needed
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                current_user_avatar.setImageBitmap(bitmap);
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

    String formatTourName(String name){
        if (name.length() > 30){
            name = name.substring(0, 27);
            name += "...";
        }
        return name;
    }
    String formatTourRating(String rating){
        if (rating.charAt(rating.length()-1) == '0'){
            return rating.substring(0, 1);
        }
        if (rating.length() > 3){
            return rating.substring(0, 3);
        }
        return rating;
    }
}
