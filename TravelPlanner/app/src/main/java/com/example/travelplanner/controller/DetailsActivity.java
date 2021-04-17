package com.example.travelplanner.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import com.example.travelplanner.R;
import com.example.travelplanner.adapter.WaypointAdapter;
import com.example.travelplanner.fragment.BottomSheetFragment;
import com.example.travelplanner.fragment.FragmentTwo;
import com.example.travelplanner.fragment.MapTourFragment;
import com.example.travelplanner.fragment.TabFragmentAdapter;
import com.example.travelplanner.model.MyPlace;
import com.example.travelplanner.model.Rating;
import com.example.travelplanner.model.Tour;
import com.example.travelplanner.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.TreeMap;

public class DetailsActivity extends AppCompatActivity implements Runnable{


    private static final String TAG = "DetailsActivity";
    private LinearLayout layoutDetails;
    private LinearLayout progressDetails;
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
    private int count;
    private RecyclerView mResultCommentTour, waypointRecyclerView;
    private FirestoreRecyclerAdapter adapterTourComment;
    private TextView tourViews;
    private Button btnGoToLogin;
    private ImageView current_user_avatar;
    private EditText current_user_comment;
    private RadioButton current_user_rate_1;
    private RadioButton current_user_rate_2;
    private RadioButton current_user_rate_3;
    private RadioButton current_user_rate_4;
    private RadioButton current_user_rate_5;
    private Button btnUploadComment;
    private ImageView iconSaved;
    private ImageView iconUnSaved;
    private LinearLayout CommentBox;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String tourID;
    private ImageView btnMore;
    private Fragment moreFragment;

    //tablayout
    private  TabLayout mTabs;
    private View mIndicator;
    private ViewPager mViewPager;

    private int indicatorWidth;
    //Waypoints
    public static ArrayList<MyPlace> waypoints = new ArrayList<>();
    public static Tour cur_Tour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        layoutDetails = (LinearLayout) findViewById(R.id.layoutDetails);
        progressDetails = (LinearLayout) findViewById(R.id.progressDetails);
        layoutDetails.setVisibility(View.GONE);
        progressDetails.setVisibility(View.VISIBLE);

        isOpenDescription = false;
        readMore = (Button) findViewById(R.id.detail_btnReadMore);

        tourDescription = (TextView) findViewById(R.id.tour_description_txt);
        tourDescriptionFull = (TextView) findViewById(R.id.tour_description_full_txt);

        intent = getIntent();
        tourID = intent.getStringExtra("Key");
        System.out.println(tourID);

        tourName = (TextView) findViewById(R.id.tour_name_txt);
        tourViews = (TextView) findViewById(R.id.tour_views);
        tourCover = (ImageView) findViewById(R.id.cover_img);
        tourPublishDay = (TextView) findViewById(R.id.tourPublishDay);
        tourRating = (TextView) findViewById(R.id.tourRating);
        tourAuthorName = (TextView) findViewById(R.id.tour_author_txt);
        tourAuthorImg = (ImageView) findViewById(R.id.tour_author_img);

        mResultCommentTour = (RecyclerView) findViewById(R.id.recycleviewCommentDetail);
        mResultCommentTour.setLayoutManager(new LinearLayoutManager(this));

        iconSaved = (ImageView) findViewById(R.id.detail_icon_not_saved_tour);
        iconUnSaved = (ImageView) findViewById(R.id.detail_icon_saved_tour);
        btnMore = (ImageView) findViewById(R.id.detail_more);

        waypointRecyclerView = findViewById(R.id.waypoints);
        waypointRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        CommentBox = (LinearLayout) findViewById(R.id.detail_comment_current_user_box);

        mAuth = FirebaseAuth.getInstance();

        //Tab layout
        //Assign view reference
        mTabs = findViewById(R.id.tab);
        mIndicator = findViewById(R.id.indicator);
        mViewPager = findViewById(R.id.viewPager);

        //Set up the view pager and fragments
        TabFragmentAdapter adapter = new TabFragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(MapTourFragment.newInstance(), "Lộ trình");
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

        iconSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null){
                    Toast.makeText(DetailsActivity.this, "Bạn vui lòng đăng nhập để lưu Tour", Toast.LENGTH_SHORT).show();
                    return;
                }
                User.saveTour(mAuth.getUid(), tourID);
                iconSaved.setVisibility(View.GONE);
                iconUnSaved.setVisibility(View.VISIBLE);
                Toast.makeText(DetailsActivity.this, "Đã lưu Tour", Toast.LENGTH_SHORT).show();
            }
        });

        iconUnSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.unsaveTour(mAuth.getUid(), tourID);
                iconSaved.setVisibility(View.VISIBLE);
                iconUnSaved.setVisibility(View.GONE);
            }
        });

    }

    private void loadUI() {
        db.collection("Tour").document(tourID).update("views", FieldValue.increment(1.0));
        db.collection("Tour").document(tourID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.exists()) {

                    Tour tour = documentSnapshot.toObject(Tour.class);
                    cur_Tour = tour;
                    //nếu chủ tour là user thì không cho đánh giá
                    if (tour.getAuthor_id().equals(mAuth.getUid())) {
                        CommentBox.setVisibility(View.GONE);
                        iconSaved.setVisibility(View.GONE);
                        iconUnSaved.setVisibility(View.GONE);
                    }

                    //get thông tin tour
                    waypoints.clear();
                    tourName.setText(tour.getName());
                    tourDescription.setText(tour.getDes());
                    tourDescriptionFull.setText(tour.getDes());
                    tourPublishDay.setText("Đăng ngày " + tour.getPublish_day());
                    tourViews.setText("Số lượt xem "+ tour.getViews());

                    tourRating.setText(formatTourRating(tour.getRating_avg()));

                    ArrayList<String> waypointIDs = tour.getWaypoints();

                    TreeMap<Integer, MyPlace> map = new TreeMap<Integer, MyPlace>();
                    for(int i = 0; i< waypointIDs.size(); i++)
                    {
                        int finalI = i;
                        db.collection("Place").document(waypointIDs.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        map.put(finalI, document.toObject(MyPlace.class));

                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                    if ( map.size() == waypointIDs.size())
                                    {
                                        Log.i(TAG, map.toString());
                                        for (Integer key : map.keySet()) {
                                            waypoints.add( map.get(key));
                                            System.out.println(key + " - " + map.get(key));
                                        }
                                        WaypointAdapter destinationAdapter = new WaypointAdapter(DetailsActivity.this, waypoints);
                                        waypointRecyclerView.setAdapter(destinationAdapter);
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                    }


                    //get avatar
                    StorageReference islandRef = storage.getReference().child(tour.getCover());
                    final long ONE_MEGABYTE = 1024 * 1024;

                    islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Data for "images/island.jpg" is returns, use this as needed
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            tourCover.setImageBitmap(bitmap);
                            layoutDetails.setVisibility(View.VISIBLE);
                            progressDetails.setVisibility(View.GONE);
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
        FirestoreRecyclerOptions<Rating> response = new FirestoreRecyclerOptions.Builder<Rating>()
                .setQuery(searchQuery, Rating.class)
                .build();

        adapterTourComment = new FirestoreRecyclerAdapter<Rating, CommentViewHolder>(response) {
            @Override
            public void onBindViewHolder(CommentViewHolder holder, int position, Rating model) {
                db.collection("User").document(model.getUser_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    //chỗ code ngu nè
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            User b = documentSnapshot.toObject(User.class);
                            holder.setDetail(model, b);

                            //Nếu user đánh giá rồi thì không cho đánh giá nữa
                            if (b.getId().equals(mAuth.getUid())) CommentBox.setVisibility(View.GONE);

                        }
                    }
                });

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

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(tourID);
               // BottomSheetFragment.newInstance(tourID);
                bottomSheetFragment.show(getSupportFragmentManager(),TAG);
            }
        });
    }

    private void loadUIUser(){
        //check user đăng nhập hay chưa
        btnUploadComment = (Button) findViewById(R.id.detail_comment_current_user_btn_upload);
        btnGoToLogin = (Button) findViewById(R.id.detail_btn_GotoLogin);
        current_user_avatar = (ImageView) findViewById(R.id.detail_comment_current_user_avatar);
        current_user_comment = (EditText) findViewById(R.id.detail_comment_current_user_comment);
        current_user_rate_1 = (RadioButton) findViewById(R.id.detail_comment_current_user_rate_1);
        current_user_rate_2 = (RadioButton) findViewById(R.id.detail_comment_current_user_rate_2);
        current_user_rate_3 = (RadioButton) findViewById(R.id.detail_comment_current_user_rate_3);
        current_user_rate_4 = (RadioButton) findViewById(R.id.detail_comment_current_user_rate_4);
        current_user_rate_5 = (RadioButton) findViewById(R.id.detail_comment_current_user_rate_5);
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


        //chưa đăng nhập thì ẩn nút đánh giá và không cho lưu
        if (mAuth.getCurrentUser() == null){
            CommentBox.setVisibility(View.GONE);
            iconUnSaved.setVisibility(View.GONE);
            btnGoToLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(DetailsActivity.this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });
        }

        //đăng nhập rồi
        else {
            btnGoToLogin.setVisibility(View.GONE);

            db.collection("User").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()){
                        User user = documentSnapshot.toObject(User.class);

                        //set icon save or unsave tour
                        if (user.getSaved_tour() == null || !user.getSaved_tour().contains(tourID)){
                            iconUnSaved.setVisibility(View.GONE);
                        }
                        else {
                            iconSaved.setVisibility(View.GONE);
                        }

                        //get avatar
                        StorageReference islandRef = storage.getReference().child(user.getLink_ava_user());
                        final long ONE_MEGABYTE = 1024 * 1024;
                        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                //set avatar
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                current_user_avatar.setImageBitmap(bitmap);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                System.out.println("Fail to load avatar user");
                            }
                        });

                    }
                }
            });

            btnUploadComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkValidInput() == true){
                        int rate;
                        if (current_user_rate_1.isChecked()) rate = 1;
                        else if (current_user_rate_2.isChecked()) rate = 2;
                        else if (current_user_rate_3.isChecked()) rate = 3;
                        else if (current_user_rate_4.isChecked()) rate = 4;
                        else rate = 5;
                        Rating userRating = new Rating(mAuth.getUid(), tourID, current_user_comment.getText().toString(), rate);
                        Rating.addRating(userRating);
                        Tour.alterRating(tourID, rate);
                    }
                }
            });

        }
    }

    boolean checkValidInput(){
        if (current_user_comment.getText().toString().equals("")){
            Toast.makeText(DetailsActivity.this, "Vui lòng nhập ý kiến đánh giá của bạn", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!current_user_rate_1.isChecked() && !current_user_rate_2.isChecked() && !current_user_rate_3.isChecked() && !current_user_rate_4.isChecked() && !current_user_rate_5.isChecked()){
            Toast.makeText(DetailsActivity.this, "Vui lòng chọn đánh giá của bạn", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    String formatTourRating(Double rate){
        if (rate == null){
            return  "Chưa có";
        }
        String rating = rate.toString();
        if (rating.charAt(rating.length()-1) == '0'){
            return rating.substring(0, 1);
        }
        if (rating.length() > 3){
            return rating.substring(0, 3);
        }
        return rating;
    }

    @Override
    public void run() {

    }
}
