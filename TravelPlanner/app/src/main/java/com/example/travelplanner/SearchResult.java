package com.example.travelplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.travelplanner.controller.DetailsActivity;
import com.example.travelplanner.controller.SearchActivity;
import com.example.travelplanner.controller.SearchPlaceTable;
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

    public static final String EXTRA_TEXT_KEYWORD = "anhthubui.app.search_place.EXTRA_TEXT_KEYWORD";

    private EditText mSearchField;
    private ImageButton mSearchBtn;
    private RecyclerView mResultList;


    private TextView keyword_warning;
    private TextView otherLoc_warning;
    private RadioGroup radio_group;
    private RadioButton btn_search_place;
    private RadioButton btn_search_tour;

    private CardView openQRCam;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mTour;
    private FirestoreRecyclerAdapter adapter;
    Intent intent = getIntent();
   // private DatabaseReference mTour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        radio_group =  findViewById(R.id.radio_group);
        btn_search_place = findViewById(R.id.radio0);
        btn_search_tour = findViewById(R.id.radio1);
        keyword_warning =  findViewById(R.id.keyword_warning);

        mSearchField = (EditText) findViewById(R.id.search_input);
        mSearchBtn = (ImageButton) findViewById(R.id.search_icon);
        mResultList = (RecyclerView) findViewById(R.id.list_item);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        mResultList.addItemDecoration(itemDecoration);

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        firestoreUserSearch("");
        mResultList.setVisibility(View.GONE);

        openQRCam = (CardView) findViewById(R.id.btnStartQRCam);
        openQRCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (SearchResult.this, ScanActivity.class);
                startActivity(i);
            }
        });

        btn_search_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultList.setVisibility(View.GONE);
            }
        });
        btn_search_tour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultList.setVisibility(View.VISIBLE);
                keyword_warning.setVisibility(View.GONE);
            }
        });

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

        mSearchField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    mSearchBtn.callOnClick();
                    return true;
                }
                return false;
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = mSearchField.getText().toString();
                firestoreUserSearch(searchText);
                if (btn_search_place.isChecked()){
                    mResultList.setVisibility(View.GONE);
                    if(searchText.trim().length() == 0){
                        keyword_warning.setVisibility(View.VISIBLE);
                        Toast.makeText(SearchResult.this,"Vui lòng nhập từ khóa tìm kiếm",Toast.LENGTH_SHORT).show();
                    }else {
                        keyword_warning.setVisibility(View.GONE);
                        Intent intent = new Intent(SearchResult.this, SearchPlaceTable.class);
                        intent.putExtra(EXTRA_TEXT_KEYWORD, searchText);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private void firestoreUserSearch(String text) {

        Query searchQuery  = db.collection("Tour").orderBy("name");
        //Bind data
        FirestoreRecyclerOptions<Tour> response = new FirestoreRecyclerOptions.Builder<Tour>()
                .setQuery(searchQuery, Tour.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Tour, ToursViewHolder>(response) {
            @Override
            public void onBindViewHolder(ToursViewHolder holder, int position, Tour model) {
                if (model.getName().toLowerCase().indexOf(text.toLowerCase()) == -1){
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    return;
                }
                else {
                    holder.setDetail(model);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            Intent i = new Intent(SearchResult.this, DetailsActivity.class);
                            String documentId = getSnapshots().getSnapshot(position).getId();
                            i.putExtra("Key", documentId);
                            startActivity(i);
                        }
                    });
                }
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
    }
