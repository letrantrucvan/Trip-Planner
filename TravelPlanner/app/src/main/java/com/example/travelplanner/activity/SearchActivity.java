package com.example.travelplanner.activity;

import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.R;
import com.example.travelplanner.adapter.ToursViewHolder;
import com.example.travelplanner.fragment.CategoryFragment;
import com.example.travelplanner.fragment.SearchPlaceResultFragment;
import com.example.travelplanner.model.Tour;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "Thu SearchActivity";

    private CardView openQRCam;
    private EditText mSearchField;
    private ImageButton mSearchBtn;
    private RecyclerView mResultList;

    private RadioGroup radio_group;
    private RadioButton btn_search_place;
    private RadioButton btn_search_tour;

    private ImageButton filter;
    private String category ="Default";

    private SearchPlaceResultFragment searchPlaceResultFragment;
    private FragmentTransaction ft;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mTour;
    private FirestoreRecyclerAdapter adapter;
    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreateView");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_fragment);
        searchPlaceResultFragment = new SearchPlaceResultFragment();
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.search_result, searchPlaceResultFragment);
        ft.commit();

        mSearchField = findViewById(R.id.search_input);
        mSearchBtn = findViewById(R.id.search_icon);
        mResultList = findViewById(R.id.list_item);

        radio_group =  findViewById(R.id.radio_group);
        btn_search_place = findViewById(R.id.radio0);
        btn_search_tour = findViewById(R.id.radio1);
        filter = findViewById(R.id.filter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mResultList.addItemDecoration(itemDecoration);

        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(this));

        firestoreUserSearch("");
        mResultList.setVisibility(View.GONE);

        openQRCam = (CardView) findViewById(R.id.btnStartQRCam);
        openQRCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchActivity.this, ScanActivity.class);
                startActivity(i);
            }
        });

        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (btn_search_tour.isChecked()) {
                    String searchText = mSearchField.getText().toString();
                    firestoreUserSearch(searchText);
                }
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

        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radio0) {
                    filter.setVisibility(View.VISIBLE);
                    mResultList.setVisibility(View.GONE);
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.search_result, searchPlaceResultFragment);
                    ft.commit();
                    Toast.makeText(SearchActivity.this, "0", Toast.LENGTH_SHORT).show();
                }
                else {
                    filter.setVisibility(View.GONE);
                    mResultList.setVisibility(View.VISIBLE);
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.remove(searchPlaceResultFragment);
                    ft.commit();
                }
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_y,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.slide_out_y
                        )
                        .setReorderingAllowed(true)
                        .add(R.id.fragment_container_search_view, CategoryFragment.class, null)
                        .addToBackStack("Category")
                        .commit();
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = mSearchField.getText().toString();
                if (btn_search_place.isChecked()) {
                    if(searchText.trim().length() == 0){
                        Toast.makeText(SearchActivity.this,"Vui lòng nhập từ khóa tìm kiếm",Toast.LENGTH_SHORT).show();
                    }else {

                        searchPlaceResultFragment.search(searchText,category);
                    }
                }
                else
                {
                    firestoreUserSearch(searchText);
                }
            }
        });
    }

    public void filter(String _category)
    {
        getSupportFragmentManager().popBackStack();
        category = _category;
        Toast.makeText(SearchActivity.this, category, Toast.LENGTH_SHORT).show();
        String keyword = mSearchField.getText().toString();
        if(keyword.trim().length() == 0){
            Toast.makeText(SearchActivity.this,"Vui lòng nhập từ khóa tìm kiếm",Toast.LENGTH_SHORT).show();
        }
        else
        {
            searchPlaceResultFragment.search(keyword,category);
        }
    }
    private void firestoreUserSearch(String text) {

        Query searchQuery  = db.collection("Tour").whereEqualTo("is_public", true).whereEqualTo("is_delete", false).orderBy("name");
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
                            Intent i = new Intent(SearchActivity.this, DetailsActivity.class);
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
