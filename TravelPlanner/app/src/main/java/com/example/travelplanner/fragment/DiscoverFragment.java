package com.example.travelplanner.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.travelplanner.Configure;
import com.example.travelplanner.R;
import com.example.travelplanner.SearchResult;
import com.example.travelplanner.controller.BookmarksTourViewHolder;
import com.example.travelplanner.controller.SearchActivity;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

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

    private FirestoreRecyclerAdapter adapter;
    private FirestoreRecyclerAdapter adapter_fyp;
    private FirestoreRecyclerAdapter adapter_budget;


    private FirestoreRecyclerAdapter adapter_vertical;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private RecyclerView popularTour;
    private RecyclerView islandTour;
    private RecyclerView fypTour;
    private RecyclerView budgetTour;

    private ImageView search_ic;

    private ImageView TOTW_img; //TOUR OF THE WEEK
    private TextView TOTW_name;
    private TextView TOTW_author;
    private TextView TOTW_placeNum;

    private ImageView TNWT_img; //THE NEWEST TOUR
    private TextView TNWT_name;
    private  TextView TNWT_author;
    private TextView TNWT_placeNum;
    private CardView cardview1, cardview2, cardview3, cardview4;
    private String TOTW_id;
    private String TNWT_id;

    private LinearLayout progress1;
    private LinearLayout progress2;
    private TextView tourofweek;
    private TextView latesttour;
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
        progress1 = (LinearLayout) v.findViewById(R.id.discover_progress1);
        progress2 = (LinearLayout) v.findViewById(R.id.discover_progress2);
        progress1.setVisibility(View.VISIBLE);
        progress2.setVisibility(View.VISIBLE);
        tourofweek = (TextView) v.findViewById(R.id.textView4);
        latesttour = (TextView) v.findViewById(R.id.textView7);
        tourofweek.setVisibility(View.GONE);
        latesttour.setVisibility(View.GONE);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        popularTour = (RecyclerView) v.findViewById(R.id.popularTour);
        popularTour.setHasFixedSize(true);
        popularTour.setLayoutManager(layoutManager);

        islandTour = (RecyclerView) v.findViewById(R.id.island);
        islandTour.setLayoutManager(new LinearLayoutManager(getContext()));

        fypTour = (RecyclerView) v.findViewById(R.id.fyp);
        fypTour.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        budgetTour = (RecyclerView) v.findViewById(R.id.budget);
        budgetTour.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        search_ic = (ImageView) v.findViewById(R.id.search_ic);
        search_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchResult.class));
                //startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });

        TOTW_img = (ImageView) v.findViewById(R.id.tourOfTheWeek_img);
        TOTW_name = (TextView) v.findViewById(R.id.tourOfTheWeek_name);
        TOTW_author = (TextView) v.findViewById(R.id.tourOfTheWeek_author);
        TOTW_placeNum = (TextView) v.findViewById(R.id.tourOfTheWeek_placeNum);

        TNWT_img = (ImageView) v.findViewById(R.id.theNewestTour_img);
        TNWT_name = (TextView) v.findViewById(R.id.theNewestTour_name);
        TNWT_author = (TextView) v.findViewById(R.id.theNewestTour_author);
        TNWT_placeNum = (TextView) v.findViewById(R.id.theNewestTour_placeNum);

        cardview1 = (CardView) v.findViewById(R.id.cardView1);
        cardview3 = (CardView) v.findViewById(R.id.cardView3);

        Query weekTour  = db.collection("Tour").orderBy("views", Query.Direction.DESCENDING).limit(1); //Lượt tải nhiều nhất tuần
        weekTour.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                queryDocumentSnapshots.forEach(doc -> {
                    Tour tour = doc.toObject(Tour.class);
                  //  System.out.println(tour.waypoints.size());
                    //TOTW_name.setText(String.valueOf(Configure.formatTourName(tour.getName())));
                    TOTW_name.setText(String.valueOf(tour.getName()));
                    db.collection("User").document(tour.getAuthor_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        //chỗ code ngu nè
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                User b = documentSnapshot.toObject(User.class);
                                TOTW_author.setText("Đăng bởi " + String.valueOf(b.getFullname()));
                            }
                        }
                    });

                    TOTW_placeNum.setText(String.valueOf(tour.waypoints.size())+ " địa điểm");
                    TOTW_id = tour.getTour_id();
                    StorageReference islandRef = storage.getReference().child(tour.getCover());
                    final long ONE_MEGABYTE = 1024 * 1024;

                    islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Data for "images/island.jpg" is returns, use this as needed
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            TOTW_img.setImageBitmap(bitmap);
                            progress1.setVisibility(View.GONE);
                            tourofweek.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            System.out.println("Fail");
                        }
                    });
                });
            }
        });

        cardview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DetailsActivity.class);
                i.putExtra("Key", TOTW_id);
                startActivity(i);
            }
        });

        Query theNewestTour = db.collection("Tour").orderBy("rating_avg", Query.Direction.DESCENDING).limit(1); // Tour mới nhất
        theNewestTour.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                queryDocumentSnapshots.forEach(doc -> {
                    Tour tour = doc.toObject(Tour.class);
                    //  System.out.println(tour.waypoints.size());
                    //TNWT_name.setText(String.valueOf(Configure.formatTourName(tour.getName())));
                    TNWT_name.setText(String.valueOf(tour.getName()));
                    db.collection("User").document(tour.getAuthor_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        //chỗ code ngu nè
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                User b = documentSnapshot.toObject(User.class);
                                TNWT_author.setText("Đăng bởi " +String.valueOf(b.getFullname()));
                            }
                        }
                    });
                    TNWT_placeNum.setText(String.valueOf(tour.waypoints.size())+ " địa điểm");
                    TNWT_id = tour.getTour_id();
                    StorageReference islandRef = storage.getReference().child(tour.getCover());
                    final long ONE_MEGABYTE = 1024 * 1024;

                    islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Data for "images/island.jpg" is returns, use this as needed
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            TNWT_img.setImageBitmap(bitmap);
                            progress2.setVisibility(View.GONE);
                            latesttour.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            System.out.println("Fail");
                        }
                    });
                });
            }
        });
        cardview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DetailsActivity.class);
                i.putExtra("Key", TNWT_id);
                startActivity(i);
            }
        });

        Query PopularTour  = db.collection("Tour").orderBy("views", Query.Direction.DESCENDING).orderBy("rating_avg", Query.Direction.DESCENDING); //Rating avg cao nhất + lượt views nhiều nhất
        Query fyp = db.collection("Tour").orderBy("views", Query.Direction.DESCENDING);
        Query budget = db.collection("Tour").orderBy("rating_avg", Query.Direction.DESCENDING);

        //Bind data

        // tạo adapter ngang
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

        adapter = horizonto(PopularTour);
        adapter.notifyDataSetChanged();
        adapter.startListening();

        adapter_fyp = horizonto(fyp);
        adapter_fyp.notifyDataSetChanged();
        adapter_fyp.startListening();

        adapter_budget = horizonto(budget);
        adapter_budget.notifyDataSetChanged();
        adapter_budget.startListening();

        popularTour.setAdapter(adapter);
        fypTour.setAdapter(adapter_fyp);
        budgetTour.setAdapter(adapter_budget);

        Query RecommendedTour  = db.collection("Tour"); // Số địa điểm trùng nhau nhiều nhất
        Query searchQuery  = db.collection("Tour").limit(5);
        FirestoreRecyclerOptions<Tour> response = new FirestoreRecyclerOptions.Builder<Tour>()
                .setQuery(searchQuery, Tour.class)
                .build();
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
        return new FirestoreRecyclerAdapter<Tour, ToursViewHolder>(response) {
            @Override
            public void onBindViewHolder(ToursViewHolder holder, int position, Tour model) {
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
            public ToursViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View mView = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.small_trip, group, false);
                return new ToursViewHolder(mView);
            }

            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };
    }


}