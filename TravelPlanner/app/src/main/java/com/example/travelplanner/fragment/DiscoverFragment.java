package com.example.travelplanner.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.travelplanner.R;
import com.example.travelplanner.controller.BookmarksTourViewHolder;
import com.example.travelplanner.controller.DetailsActivity;
import com.example.travelplanner.controller.ToursViewHolder;
import com.example.travelplanner.model.Tour;
import com.example.travelplanner.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView mytour;
    private FirestoreRecyclerAdapter adapter;
    private FirestoreRecyclerAdapter adapter_vertical;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView islandTour;
    private RecyclerView fypTour;
    private RecyclerView budgetTour;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_discover, container, false);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mytour = (RecyclerView) v.findViewById(R.id.spring);
        mytour.setHasFixedSize(true);
        mytour.setLayoutManager(layoutManager);

        islandTour = (RecyclerView) v.findViewById(R.id.island);
        islandTour.setLayoutManager(new LinearLayoutManager(getContext()));

        fypTour = (RecyclerView) v.findViewById(R.id.fyp);
        fypTour.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        budgetTour = (RecyclerView) v.findViewById(R.id.budget);
        budgetTour.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        Query searchQuery  = db.collection("Tour");

        //Bind data
        FirestoreRecyclerOptions<Tour> response = new FirestoreRecyclerOptions.Builder<Tour>()
                .setQuery(searchQuery, Tour.class)
                .build();

        // tạo addapter ngang
//        adapter = new FirestoreRecyclerAdapter<Tour, ViewHolder>(response) {
//            @Override
//            public void onBindViewHolder(ViewHolder holder, int position, Tour model) {
//                holder.setDetail(model.getCover(), model.getName());
//            }
//
//            @Override
//            public ViewHolder onCreateViewHolder(ViewGroup group, int i) {
//                View mView = LayoutInflater.from(group.getContext())
//                        .inflate(R.layout.small_trip, group, false);
//                return new ViewHolder(mView);
//            }
//
//            @Override
//            public void onError(FirebaseFirestoreException e) {
//                Log.e("error", e.getMessage());
//            }
//        };
        //--

        adapter = horizonto(searchQuery);

        adapter.notifyDataSetChanged();
        adapter.startListening();


        mytour.setAdapter(adapter);
        fypTour.setAdapter(adapter);
        budgetTour.setAdapter(adapter);


        //adapter dọc
        adapter_vertical= new FirestoreRecyclerAdapter<Tour, BookmarksTourViewHolder>(response) {
            @Override
            public void onBindViewHolder(BookmarksTourViewHolder holder, int position, Tour model) {
                db.collection("User").document(model.getAuthor_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    //chỗ code ngu nè
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            User b = documentSnapshot.toObject(User.class);
                            model.setAuthor_name(b.getFullname());
                            holder.setDetail(model);
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View view) {
                                    Intent i = new Intent(getActivity(), DetailsActivity.class);
                                    String documentId = getSnapshots().getSnapshot(position).getId();
                                    i.putExtra("Key", documentId);
                                    startActivity(i);
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public BookmarksTourViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View mView = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.recycle_view_tour_bookmark, group, false);
                return new BookmarksTourViewHolder(mView);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };
        //--

        adapter_vertical.notifyDataSetChanged();
        adapter_vertical.startListening();
        islandTour.setAdapter(adapter_vertical);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        //adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        //adapter.stopListening();
    }

    public FirestoreRecyclerAdapter horizonto(Query search){
        FirestoreRecyclerOptions<Tour> response = new FirestoreRecyclerOptions.Builder<Tour>()
                .setQuery(search, Tour.class)
                .build();
        return new FirestoreRecyclerAdapter<Tour, ViewHolder>(response) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position, Tour model) {
                holder.setDetail(model);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent i = new Intent(getActivity(), DetailsActivity.class);
                        String documentId = getSnapshots().getSnapshot(position).getId();
                        i.putExtra("Key", documentId);
                        startActivity(i);
                    }
                });
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View mView = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.small_trip, group, false);
                return new ViewHolder(mView);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDetail(Tour model){
            ImageView cover = (ImageView) mView.findViewById(R.id.logo);
            TextView name  = (TextView) mView.findViewById(R.id.trip_name);
            TextView waypoints  = (TextView) mView.findViewById(R.id.trip_waypoints);

            name.setText(model.getName());
            if (model.getWaypoints() == null){
                waypoints.setText("0 địa điểm");
            }
            else waypoints.setText(model.getWaypoints().size() + " địa điểm");

            StorageReference imgRef = FirebaseStorage.getInstance().getReference().child(model.getCover());
            final long ONE_MEGABYTE = 1024 * 1024;
            imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    cover.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    System.out.println(exception.getMessage());
                }
            });
        }
    }

}