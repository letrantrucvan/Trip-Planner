package com.example.travelplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Map;


public class SearchResult extends AppCompatActivity {
    private EditText mSearchField;
    private ImageButton mSearchBtn;

    private RecyclerView mResultList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mTour;
    private FirestoreRecyclerAdapter adapter;
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
                addToFireStore("This book is fantasty");
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

    // FULL TEXT SEARCH : Split string => Find keywords
    public void addToFireStore(String text){
        ArrayList<String> key_words = generateKeyWords(text);
        System.out.println(key_words);
        Map<String, ArrayList<String>> tourlist = new HashMap<String, ArrayList<String>>();

        tourlist.put("search_keyword",key_words);

        System.out.println(tourlist);
    }

    private ArrayList<String> generateKeyWords(String text) {
        ArrayList<String> res = new ArrayList<String>();
        text = text.toLowerCase();

        String []words = text.split(" ");

        for (String word : words) {
            String appendStr = "";

            //Printing the characters
            for (char output : text.toCharArray()) {
                appendStr += String.valueOf(output);
                res.add(appendStr);
            }
            text = text.replace(word,"");
        }
        return res;
    }
    ////////////////////////////////////////////////////////////

    private void firestoreUserSearch(String text) {
        //Query query =  db.collection("Tour");
//        Query searchQuery  = db.collection("Tour").whereArrayContains("search_keywords",text);
        Query searchQuery  = db.collection("Tour").orderBy("name").startAt(text).endAt(text+ '\uf8ff');
        //Bind data
        FirestoreRecyclerOptions<Tour> response = new FirestoreRecyclerOptions.Builder<Tour>()
                .setQuery(searchQuery, Tour.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Tour, ToursViewHolder>(response) {
                @Override
                public void onBindViewHolder(ToursViewHolder holder, int position, Tour model) {
                    holder.setDetail(model.getCover(), model.getName(), model.getDes());
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
