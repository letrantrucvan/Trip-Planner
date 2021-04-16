package com.example.travelplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;


import com.example.travelplanner.controller.ToursViewHolder;
import com.example.travelplanner.model.Tour;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.protobuf.Any;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class SearchResult extends AppCompatActivity {
    private EditText mSearchField;
    private ImageButton mSearchBtn;

    private RecyclerView mResultList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mTour;
    private FirestoreRecyclerAdapter adapter;
    Intent intent = getIntent();
   // private DatabaseReference mTour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mSearchField = (EditText) findViewById(R.id.search_input);
        mSearchBtn = (ImageButton) findViewById(R.id.search_icon);
        mResultList = (RecyclerView) findViewById(R.id.list_item);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        mResultList.addItemDecoration(itemDecoration);

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        firestoreUserSearch("");
//        firebaseUserSearch();
        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = mSearchField.getText().toString();
                firestoreUserSearch(searchText);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = mSearchField.getText().toString();
                firestoreUserSearch(searchText);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }

    private void firestoreUserSearch(String text) {
        Query searchQuery  = db.collection("Tour").whereArrayContains("search_keywords",text);
        //Query searchQuery  = db.collection("Tour").orderBy("name").startAt(text).endAt(text+ '\uf8ff');
        //Bind data
        FirestoreRecyclerOptions<Tour> response = new FirestoreRecyclerOptions.Builder<Tour>()
                .setQuery(searchQuery, Tour.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Tour, ToursViewHolder>(response) {
                @Override
                public void onBindViewHolder(ToursViewHolder holder, int position, Tour model) {
                    holder.setDetail(model);
                }

                @Override
                public ToursViewHolder onCreateViewHolder(ViewGroup group, int i) {
                    View mView = LayoutInflater.from(group.getContext())
                            .inflate(R.layout.list_view, group, false);
                    return new ToursViewHolder(mView);
                }

                @Override
                public void onError(FirebaseFirestoreException e) {
                    Log.e("error", e.getMessage());
                }
            };
        adapter.notifyDataSetChanged();
        adapter.startListening();
        mResultList.setAdapter(adapter);
        }

//        private void updateTourName(){
//            db.collection("Tour").document("7fcmQHk9DmOTEgzD4WL1").update("rating_number", );
//        }
    }
