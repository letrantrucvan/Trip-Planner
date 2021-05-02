package com.example.travelplanner.controller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.R;
import com.example.travelplanner.adapter.ToursOfUserAdapter;
import com.example.travelplanner.model.Tour;
import com.example.travelplanner.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class UserPageActivity extends AppCompatActivity {

    ImageView avatar;
    TextView name;
    TextView num_followers;
    String uID;
    private static String TAG = "Thu UserPageActivity";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    ToursOfUserAdapter toursOfUserAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.user_page);
        RecyclerView recyclerView = findViewById(R.id.tours);
        avatar = findViewById(R.id.avatar);
        name = findViewById(R.id.name);
        num_followers = findViewById(R.id.follower);
        Button follow = findViewById(R.id.follow);
        Button unfollow = findViewById(R.id.unfollow);

        uID = intent.getStringExtra("id");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user!= null) {
            String curUid = user.getUid();
            db.collection("User").document(curUid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User curUser = documentSnapshot.toObject(User.class);
                            if (curUser.getFollowing().contains(uID)) {
                                follow.setVisibility(View.GONE);
                                unfollow.setVisibility(View.VISIBLE);
                            }
                            else {
                                if (!curUid.equals(uID))
                                    follow.setVisibility(View.VISIBLE);
                            }
                        }
                    });

            follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    follow.setVisibility(View.GONE);
                    unfollow.setVisibility(View.VISIBLE);
                    db.collection("User").document(uID).update("follower", FieldValue.arrayUnion(curUid));
                    db.collection("User").document(curUid).update("following", FieldValue.arrayUnion(uID));
                }
            });

            unfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    follow.setVisibility(View.VISIBLE);
                    unfollow.setVisibility(View.GONE);
                    db.collection("User").document(uID).update("follower", FieldValue.arrayRemove(curUid));
                    db.collection("User").document(curUid).update("following", FieldValue.arrayRemove(uID));
                }
            });
        }
        getUserInformation();
        Query searchQuery  = db.collection("Tour").whereEqualTo("author_id", uID);
        //Bind data
        FirestoreRecyclerOptions<Tour> response = new FirestoreRecyclerOptions.Builder<Tour>()
                .setQuery(searchQuery, Tour.class)
                .build();

        Log.i(TAG, uID);
        toursOfUserAdapter = new ToursOfUserAdapter(this,response);
        toursOfUserAdapter.notifyDataSetChanged();
        toursOfUserAdapter.startListening();
        recyclerView.setAdapter(toursOfUserAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void getUserInformation(){
        db.collection("User").document(uID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    //get name + email
                    name.setText(user.getFullname());
                    //get avatar
                    Picasso.with(UserPageActivity.this).load(user.getLink_ava_user()).into(avatar);
                    num_followers.setText(user.getFollower().size() +" Người theo dõi");
                }
            }
        });
    }
}