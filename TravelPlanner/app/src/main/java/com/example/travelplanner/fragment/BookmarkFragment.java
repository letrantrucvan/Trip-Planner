package com.example.travelplanner.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.travelplanner.R;
import com.example.travelplanner.adapter.SavedPlacesAdapter;
import com.example.travelplanner.controller.BookmarksPlaceViewHolder;
import com.example.travelplanner.controller.BookmarksTourViewHolder;
import com.example.travelplanner.controller.DetailsActivity;
import com.example.travelplanner.controller.LoginActivity;
import com.example.travelplanner.controller.SearchPlaceTable;
import com.example.travelplanner.model.MyPlace;
import com.example.travelplanner.model.Tour;
import com.example.travelplanner.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookmarkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookmarkFragment extends Fragment {

    private LinearLayout bookmarkGotoLogin;
    private Button bookmarkBtnGotoLogin;

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

        mResultTourList = (RecyclerView) BookmarkFragmentView.findViewById(R.id.recycleviewTourBookmark);
        mResultTourList.setLayoutManager(new LinearLayoutManager(getContext()));

        mResultPlaceList = (RecyclerView) BookmarkFragmentView.findViewById(R.id.recycleviewPlaceBookmark);
       // mResultPlaceList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mResultPlaceList.setLayoutManager(new LinearLayoutManager(getContext()));

        //check user đăng nhập hay chưa
        bookmarkGotoLogin = (LinearLayout) BookmarkFragmentView.findViewById(R.id.bookmarkGotoLogin);
        bookmarkBtnGotoLogin = (Button) BookmarkFragmentView.findViewById(R.id.bookmarkBtnGotoLogin);
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            bookmarkGotoLogin.setVisibility(View.VISIBLE);
            mResultTourList.setVisibility(View.GONE);
            mResultPlaceList.setVisibility(View.GONE);
            bookmarkBtnGotoLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });
            return  BookmarkFragmentView;
        }
        else {
            bookmarkGotoLogin.setVisibility(View.GONE);
        }


        //user đã đăng nhập thì thực hiện
        btnShowTour = (Button) BookmarkFragmentView.findViewById(R.id.btn_showTourBookmark);
        btnShowPlace = (Button) BookmarkFragmentView.findViewById(R.id.btn_showPlaceBookmark);



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
        db.collection("User").document(FirebaseAuth.getInstance().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.exists()) {
                    User a = documentSnapshot.toObject(User.class);

                    if (a.getSaved_tour() == null) return;

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

                    if (a.getSaved_places().size() == 0) return;


                        Query searchQuery = db.collection("Place").whereIn("place_id", a.getSaved_places());
                        //Bind data
                        FirestoreRecyclerOptions<MyPlace> response = new FirestoreRecyclerOptions.Builder<MyPlace>()
                                .setQuery(searchQuery, MyPlace.class)
                                .build();

                        SavedPlacesAdapter savedPlacesAdapter = new SavedPlacesAdapter(getActivity(), response);
                        savedPlacesAdapter.notifyDataSetChanged();
                        savedPlacesAdapter.startListening();
                        mResultPlaceList.setAdapter(savedPlacesAdapter);

                }
            }
        });

    }
}

