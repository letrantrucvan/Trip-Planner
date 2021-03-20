package com.example.travelplanner.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.travelplanner.R;
import com.example.travelplanner.controller.BookmarksViewHolder;
import com.example.travelplanner.controller.ToursViewHolder;
import com.example.travelplanner.model.Tour;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookmarkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookmarkFragment extends Fragment {
    private RecyclerView mResultList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;
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

        mResultList = (RecyclerView) BookmarkFragmentView.findViewById(R.id.recycleviewBookmark);
        mResultList.setLayoutManager(new LinearLayoutManager(getContext()));


        firestoreUserSearch();
        return  BookmarkFragmentView;
    }

    private void firestoreUserSearch() {
        //Query query =  db.collection("Tour");
        Query searchQuery  = db.collection("Tour").orderBy("name");

        //Bind data
        FirestoreRecyclerOptions<Tour> response = new FirestoreRecyclerOptions.Builder<Tour>()
                .setQuery(searchQuery, Tour.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Tour, BookmarksViewHolder>(response) {
            @Override
            public void onBindViewHolder(BookmarksViewHolder holder, int position, Tour model) {
                holder.setDetail(model);
            }

            @Override
            public BookmarksViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View mView = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.recycle_view_bookmarks, group, false);
                return new BookmarksViewHolder(mView);
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

