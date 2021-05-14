package com.example.travelplanner.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.R;
import com.example.travelplanner.activity.*;
import com.example.travelplanner.activity.HomeActivity;
import com.example.travelplanner.activity.SearchActivity;
import com.example.travelplanner.adapter.BookmarksTourViewHolder;
import com.example.travelplanner.adapter.ToursViewHolder;
import com.example.travelplanner.model.Tour;
import com.example.travelplanner.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment {

    private static String TAG ="Thu DiscoverFragment";
    private HomeActivity homeActivity;

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
    private TextView TNWT_author;
    private TextView TNWT_placeNum;
    private CardView cardview1, cardview2, cardview3, cardview4;
    private String TOTW_id;
    private String TNWT_id;

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
        try {
            homeActivity = (HomeActivity) getActivity();
            // Inflate the layout for this fragment
            View v = inflater.inflate(R.layout.fragment_discover, container, false);

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
                    startActivity(new Intent(getActivity(), SearchActivity.class));
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

            Query weekTour = db.collection("Tour").whereEqualTo("is_public", true).whereEqualTo("is_delete", false).orderBy("views", Query.Direction.DESCENDING).limit(1); //Lượt tải nhiều nhất tuần
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

                        TOTW_placeNum.setText(String.valueOf(tour.waypoints.size()) + " địa điểm");
                        TOTW_id = tour.getTour_id();

                        //get avatar
                        Picasso.with(getContext()).load(tour.getCover()).into(TOTW_img);
                        tourofweek.setVisibility(View.VISIBLE);
                        if(homeActivity.LOADING) homeActivity.hideProgressingView();
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

            Query theNewestTour = db.collection("Tour").whereEqualTo("is_public", true).whereEqualTo("is_delete", false).orderBy("rating_avg", Query.Direction.DESCENDING).limit(1); // Tour mới nhất
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
                                    TNWT_author.setText("Đăng bởi " + String.valueOf(b.getFullname()));
                                }
                            }
                        });
                        TNWT_placeNum.setText(String.valueOf(tour.waypoints.size()) + " địa điểm");
                        TNWT_id = tour.getTour_id();

                        //get avatar
                        Picasso.with(getContext()).load(tour.getCover()).into(TNWT_img);
                        latesttour.setVisibility(View.VISIBLE);
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

            Query PopularTour = db.collection("Tour").whereEqualTo("is_public", true).whereEqualTo("is_delete", false).orderBy("views", Query.Direction.DESCENDING); //Rating avg cao nhất + lượt views nhiều nhất
            Query budget = db.collection("Tour").whereEqualTo("is_public", true).whereEqualTo("is_delete", false).orderBy("rating_avg", Query.Direction.DESCENDING);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user!= null) {
                String curUid = user.getUid();
                db.collection("User").document(curUid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                        if (documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            ArrayList<String> fl = user.getFollowing();
                            if(fl.size()>0) {
                                Query fy = db.collection("Tour").whereIn("author_id",fl).orderBy("views", Query.Direction.ASCENDING).limit(10);
                                adapter_fyp = horizonto(fy);
                            }
                            else {
                                Query fyp = db.collection("Tour").orderBy("views", Query.Direction.ASCENDING);
                                adapter_fyp = horizonto(fyp);
                            }
                            adapter_fyp.notifyDataSetChanged();
                            adapter_fyp.startListening();
                            fypTour.setAdapter(adapter_fyp);
                        }
                    }
                });
            }
            else{
                Query fyp = db.collection("Tour").orderBy("views", Query.Direction.ASCENDING);
                adapter_fyp = horizonto(fyp);
                adapter_fyp.notifyDataSetChanged();
                adapter_fyp.startListening();
                fypTour.setAdapter(adapter_fyp);
            }

            adapter = horizonto(PopularTour);
            adapter.notifyDataSetChanged();
            adapter.startListening();

            adapter_budget = horizonto(budget);
            adapter_budget.notifyDataSetChanged();
            adapter_budget.startListening();

            popularTour.setAdapter(adapter_budget);
            budgetTour.setAdapter(adapter);

            Query RecommendedTour = db.collection("Tour"); // Số địa điểm trùng nhau nhiều nhất
            Query searchQuery = db.collection("Tour").whereEqualTo("is_public", true).whereEqualTo("is_delete", false).orderBy("publish_day", Query.Direction.DESCENDING).limit(5);
            FirestoreRecyclerOptions<Tour> response = new FirestoreRecyclerOptions.Builder<Tour>()
                    .setQuery(searchQuery, Tour.class)
                    .build();

            //adapter dọc
            adapter_vertical = new FirestoreRecyclerAdapter<Tour, BookmarksTourViewHolder>(response) {
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
        } catch (Exception e) {
            Log.e(TAG, "onCreateView", e);
            throw e;
        }
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
