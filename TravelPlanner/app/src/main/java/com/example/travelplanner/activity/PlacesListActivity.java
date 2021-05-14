package com.example.travelplanner.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.travelplanner.adapter.PlacesNameAdapter;
import com.example.travelplanner.fragment.FragmentTwo;
import com.example.travelplanner.model.MyPlace;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import com.example.travelplanner.R;

public class PlacesListActivity extends AppCompatActivity {
    private RecyclerView myplaces;
    private PlacesNameAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<MyPlace> places = new ArrayList<>();
    private static final String TAG = "Van PlacesListActitvity";
    private FloatingActionButton editBtn;
    private ImageView backbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_list);
        Log.i(TAG, "onCreate");
        places.clear();
        places = (ArrayList<MyPlace>) getIntent().getSerializableExtra("Key");
        String author_id = getIntent().getStringExtra("Author");

        Log.i(TAG, "size of places" + String.valueOf(places.size()));
        backbtn = (ImageView) findViewById(R.id.backbtn);
        editBtn = (FloatingActionButton) findViewById(R.id.editbtn);
       // doneBtn = (TextView) findViewById(R.id.donebtn);
        //reorderBtn = (ImageView) findViewById(R.id.reorderIcon);

        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        myplaces = (RecyclerView) findViewById(R.id.myplace);
        //myplaces.setHasFixedSize(true);
        myplaces.setLayoutManager(layoutManager);
        adapter = new PlacesNameAdapter(PlacesListActivity.this,places, author_id);
        myplaces.setAdapter(adapter);
        Log.i(TAG,"Adapter mDATA"+ adapter.mData.toString());
        adapter.notifyDataSetChanged();

//        Query searchQuery = db.collection("Place");
//        //Bind data
//        FirestoreRecyclerOptions<MyPlace> response = new FirestoreRecyclerOptions.Builder<MyPlace>()
//                .setQuery(searchQuery, MyPlace.class)
//                .build();
//
//        adapter = new FirestoreRecyclerAdapter<MyPlace, FragmentTwo.PlacesViewHolder>(response) {
//            @Override
//            public void onBindViewHolder(FragmentTwo.PlacesViewHolder holder, int position, MyPlace model) {
//                if (getItemCount() == 0) {
//                    System.out.println("No matching found");
//                } else {
//                    if (isModified)
//                        reorderBtn.setVisibility(View.VISIBLE);
//                    else
//                        reorderBtn.setVisibility(View.GONE);
//                    holder.setDetail(model);
//                    //places.add(model);
//                    System.out.println("Places List ");
//                    System.out.println(model.getName());
//                    notifyDataSetChanged();
////                    holder.itemView.setOnClickListener(new View.OnClickListener() {
////                        public void onClick(View view) {
////                            Intent i = new Intent(getActivity(), PlacesListActitvity.class);
//////                            String documentId = getSnapshots().getSnapshot(position).getId();
//////                            i.putExtra("Key", documentId);
////                            startActivity(i);
////                        }
////                    });
//                }
//            }
//
//
//            @NonNull
//            @Override
//            public FragmentTwo.PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View mView = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.list_my_place, parent, false);
//                return new FragmentTwo.PlacesViewHolder(mView);
//            }
//
//            @Override
//            public int getItemCount() {
//                return super.getItemCount();
//            }
//
//
//            @Override
//            public void onError(FirebaseFirestoreException e) {
//                Log.e("error", e.getMessage());
//            }
//
//        };

        myplaces.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && editBtn.getVisibility() == View.VISIBLE) {
                    editBtn.hide();
                } else if (dy < 0 && editBtn.getVisibility() != View.VISIBLE) {
                    editBtn.show();
                }
            }
        });


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        myplaces.addItemDecoration(dividerItemDecoration);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(myplaces);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Back Button On Click");
                finish();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Edit On Click");

               // findViewById(R.id.donebtn).setVisibility(View.VISIBLE);
            }
        });

//        doneBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "Done On Click");
//                isModified = false;
//                findViewById(R.id.donebtn).setVisibility(View.GONE);
//            }
//        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.START | ItemTouchHelper.END | ItemTouchHelper.UP | ItemTouchHelper.DOWN,
             ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            Log.i(TAG, "OnMove");
            int fromPosition = viewHolder.getAdapterPosition();
            int ToPosition = target.getAdapterPosition();
            Collections.swap(places, fromPosition, ToPosition);
            myplaces.getAdapter().notifyItemMoved(fromPosition, ToPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int fromPosition = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.LEFT) {
                MyPlace curr = places.get(fromPosition);
                places.remove(curr);
                myplaces.getAdapter().notifyItemRemoved(fromPosition);
                Log.i(TAG,"deleted at "+ fromPosition);
                Snackbar.make(myplaces, "Địa điểm sẽ bị xóa khỏi hành trình", Snackbar.LENGTH_LONG).setAction("Hoàn tác", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        places.add(fromPosition,curr);
                        Snackbar snackbar1 = Snackbar.make(myplaces, "Khôi phục thành công", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                        Log.i(TAG,"inserted at "+ fromPosition);
                        myplaces.getAdapter().notifyItemInserted(fromPosition);
                    }
                }).show();
            }
        }


        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                viewHolder.itemView.setAlpha(0.75f);
            }
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setAlpha(1.0f);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myplaces.setAdapter(adapter);
    }
}