package com.example.travelplanner.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.travelplanner.R;
import com.example.travelplanner.controller.BookmarksPlaceViewHolder;
import com.example.travelplanner.controller.BookmarksTourViewHolder;
import com.example.travelplanner.model.Tour;
import com.example.travelplanner.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookmarkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookmarkFragment extends Fragment {
    private RecyclerView mResultTourList;
    private RecyclerView mResultPlaceList;
    private Button btnShowTour;
    private Button btnShowPlace;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapterTour;
    private FirestoreRecyclerAdapter adapterPlace;
    private View BookmarkFragmentView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BookmarkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookmarkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookmarkFragment newInstance(String param1, String param2) {
        BookmarkFragment fragment = new BookmarkFragment();
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
        BookmarkFragmentView = inflater.inflate(R.layout.fragment_bookmark, container, false);


        btnShowTour = (Button) BookmarkFragmentView.findViewById(R.id.btn_showTourBookmark);
        btnShowPlace = (Button) BookmarkFragmentView.findViewById(R.id.btn_showPlaceBookmark);

        mResultTourList = (RecyclerView) BookmarkFragmentView.findViewById(R.id.recycleviewTourBookmark);
        mResultTourList.setLayoutManager(new LinearLayoutManager(getContext()));

        mResultPlaceList = (RecyclerView) BookmarkFragmentView.findViewById(R.id.recycleviewPlaceBookmark);
        mResultPlaceList.setLayoutManager(new GridLayoutManager(getContext(), 2));

        firestoreTourSearch();
        firestorePlaceSearch();

        btnShowTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShowTour.setBackground(getResources().getDrawable(R.drawable.bg_button_switch_bookmark_active));
                btnShowTour.setTextColor(getResources().getColor(R.color.black));
                btnShowPlace.setBackground(getResources().getDrawable(R.drawable.bg_button_switch_bookmark));
                btnShowPlace.setTextColor(getResources().getColor(R.color.orange));
                mResultTourList.setVisibility(View.VISIBLE);
                mResultPlaceList.setVisibility(View.GONE);
            }
        });

        btnShowPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShowPlace.setBackground(getResources().getDrawable(R.drawable.bg_button_switch_bookmark_active));
                btnShowPlace.setTextColor(getResources().getColor(R.color.black));
                btnShowTour.setBackground(getResources().getDrawable(R.drawable.bg_button_switch_bookmark));
                btnShowTour.setTextColor(getResources().getColor(R.color.orange));
                mResultTourList.setVisibility(View.GONE);
                mResultPlaceList.setVisibility(View.VISIBLE);
            }
        });



        return  BookmarkFragmentView;
    }

    private void firestoreTourSearch() {
        db.collection("User").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User a = documentSnapshot.toObject(User.class);
                    //Query searchQuery  = db.collection("Tour").whereIn("tour_id", a.getSaved_tour()).orderBy("des");
                    Query searchQuery  = db.collection("Tour").whereIn("tour_id", a.getSaved_tour());
                    //Bind data
                    FirestoreRecyclerOptions<Tour> response = new FirestoreRecyclerOptions.Builder<Tour>()
                            .setQuery(searchQuery, Tour.class)
                            .build();

                    adapterTour = new FirestoreRecyclerAdapter<Tour, BookmarksTourViewHolder>(response) {
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
                    adapterTour.notifyDataSetChanged();
                    adapterTour.startListening();
                    mResultTourList.setAdapter(adapterTour);
                }
            }
        });

    }

    private void firestorePlaceSearch() {
        db.collection("User").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User a = documentSnapshot.toObject(User.class);
                    Query searchQuery  = db.collection("Tour").whereIn("tour_id", a.getSaved_tour());
                    //Bind data
                    FirestoreRecyclerOptions<Tour> response = new FirestoreRecyclerOptions.Builder<Tour>()
                            .setQuery(searchQuery, Tour.class)
                            .build();

                    adapterPlace = new FirestoreRecyclerAdapter<Tour, BookmarksPlaceViewHolder>(response) {
                        @Override
                        public void onBindViewHolder(BookmarksPlaceViewHolder holder, int position, Tour model) {
                            holder.setDetail(model);
                        }

                        @Override
                        public BookmarksPlaceViewHolder onCreateViewHolder(ViewGroup group, int i) {
                            View mView = LayoutInflater.from(group.getContext())
                                    .inflate(R.layout.recycle_view_place_bookmark, group, false);
                            return new BookmarksPlaceViewHolder(mView);
                        }

                        @Override
                        public void onError(FirebaseFirestoreException e) {
                            Log.e("error", e.getMessage());
                        }
                    };
                    adapterPlace.notifyDataSetChanged();
                    adapterPlace.startListening();
                    mResultPlaceList.setAdapter(adapterPlace);
                }
            }
        });

    }
}

