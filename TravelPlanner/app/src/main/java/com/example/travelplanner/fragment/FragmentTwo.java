package com.example.travelplanner.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.R;
import com.example.travelplanner.activity.MapsTourActivity;
import com.example.travelplanner.activity.DetailsActivity;
import com.example.travelplanner.activity.PlacesListActivity;
import com.example.travelplanner.activity.UserPageActivity;
import com.example.travelplanner.adapter.WaypointAdapter;
import com.example.travelplanner.model.MyPlace;
import com.example.travelplanner.model.Tour;
import com.example.travelplanner.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.TreeMap;

public class FragmentTwo extends Fragment {
    private RecyclerView myplaces;
    private FirestoreRecyclerAdapter adapter;
    private ArrayList<MyPlace> places = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Van FragmentTwo";
    public static ArrayList<MyPlace> waypoints = new ArrayList<>();
    ArrayList<String> waypointIDs;
    public static FragmentTwo newInstance()
    {
        return new FragmentTwo();
    }

        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_two, container, false);
            Log.i(TAG, "onCreate");
            Log.i(TAG, "waypoints"+ DetailsActivity.cur_Tour.getWaypoints().toString());
            waypoints.clear();
            waypointIDs = DetailsActivity.cur_Tour.getWaypoints();
            TreeMap<Integer, MyPlace> map = new TreeMap<Integer, MyPlace>();
            for(int i = 0; i< waypointIDs.size(); i++)
            {
                int finalI = i;
                Log.i(TAG,"Loop "+ i+ " in "+waypointIDs.size());
                db.collection("Place").document(waypointIDs.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.i(TAG, "waypoints size: "+ waypoints.size());
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                map.put(finalI, document.toObject(MyPlace.class));

                            } else {
                                Log.d(TAG, "No such document");
                            }
                            if ( map.size() == waypointIDs.size())
                            {
                                for (Integer key : map.keySet()) {
                                    Log.i(TAG,key + map.get(key).getName());
                                    waypoints.add( map.get(key));
                                    System.out.println(key + " - " + map.get(key));
                                }
                                Log.i(TAG, map.toString());
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
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
                        holder.setDetail(model,DetailsActivity.cur_Tour.getAuthor_id());
                        System.out.println("Fragment Two");
                        System.out.println(model.getName());
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
                    Log.i(TAG,"on viewClick");
                    Intent intent = new Intent(getContext(), PlacesListActivity.class);
                    Log.i(TAG,waypoints.toString());
                    intent.putExtra("Key",waypoints);
                    intent.putExtra("Author",DetailsActivity.cur_Tour.getAuthor_id());
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
            public void setDetail(MyPlace model,String id){
                ImageView icon = (ImageView) mView.findViewById(R.id.reorderIcon);
                TextView name  = (TextView) mView.findViewById(R.id.name);
                name.setText(String.valueOf(model.getName()));
                if (id == FirebaseAuth.getInstance().getUid()){
                    icon.setVisibility(View.GONE);
                }
                Log.i(TAG,FirebaseAuth.getInstance().getUid() + " " + id);
            }
        }
    }