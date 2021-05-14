package com.example.travelplanner.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.PlacesListActivity;
import com.example.travelplanner.R;

import com.example.travelplanner.activity.DetailsActivity;
import com.example.travelplanner.model.MyPlace;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;


import java.util.ArrayList;

public class FragmentTwo extends Fragment {
    private RecyclerView myplaces;
    private FirestoreRecyclerAdapter adapter;
    private ArrayList<MyPlace> places = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static FragmentTwo newInstance()
    {
        return new FragmentTwo();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);

        for (int i = 0; i < DetailsActivity.waypoints.size(); i++) {
            MyPlace place = DetailsActivity.waypoints.get(i);
            places.add(place);
        }
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        myplaces = (RecyclerView) view.findViewById(R.id.myplaces);
        myplaces.setHasFixedSize(true);
        myplaces.setLayoutManager(layoutManager);

        Query searchQuery  = db.collection("Place");
        //Bind data
        FirestoreRecyclerOptions<MyPlace> response = new FirestoreRecyclerOptions.Builder<MyPlace>()
                .setQuery(searchQuery, MyPlace.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<MyPlace,PlacesViewHolder>(response) {
            @Override
            public void onBindViewHolder(PlacesViewHolder holder, int position, MyPlace model) {
                if (getItemCount()==0){
                    System.out.println("No matching found");
                }
                else {
                    holder.setDetail(model);
                    System.out.println("Fragment Two");
                    System.out.println(model.getName());
//                    holder.itemView.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View view) {
//                            Intent i = new Intent(getActivity(), PlacesListActitvity.class);
////                            String documentId = getSnapshots().getSnapshot(position).getId();
////                            i.putExtra("Key", documentId);
//                            startActivity(i);
//                        }
//                    });
                }
            }

            @NonNull
            @Override
            public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View mView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_my_place, parent, false);
                return new PlacesViewHolder(mView);
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }


            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };
        adapter.notifyDataSetChanged();
        adapter.startListening();
        myplaces.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        myplaces.addItemDecoration(dividerItemDecoration);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),PlacesListActivity.class);
                intent.putExtra("Key",places);
                startActivity(intent);
            }
        });
        return view;

    }
    public static class PlacesViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public PlacesViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDetail(MyPlace model){
            TextView name  = (TextView) mView.findViewById(R.id.name);
            name.setText(String.valueOf(model.getName()));
        }
    }
}