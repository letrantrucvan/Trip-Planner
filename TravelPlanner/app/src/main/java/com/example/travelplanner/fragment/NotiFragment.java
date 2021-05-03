package com.example.travelplanner.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.travelplanner.R;
import com.example.travelplanner.adapter.NotificationAdapter;
import com.example.travelplanner.controller.HomeActivity;
import com.example.travelplanner.model.Notification;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Query.*;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Comparator;

public class NotiFragment extends Fragment {

    private static final String TAG = " Thu NotiFragment";
    private HomeActivity homeActivity;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    NotificationAdapter notificationAdapter;
    public NotiFragment() {
        Log.i(TAG, "NotiFragment");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        if(notificationAdapter != null)
            notificationAdapter.startListening();
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        if(notificationAdapter != null)
            notificationAdapter.stopListening();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        homeActivity = (HomeActivity) getActivity();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        try {
            // Inflate the layout for this fragment
            FrameLayout v = (FrameLayout) inflater.inflate(R.layout.fragment_noti, container, false);

            RecyclerView newNoti = v.findViewById(R.id.newNoti);
            ImageButton seenAll = v.findViewById(R.id.seenAll);

            newNoti.setLayoutManager(new LinearLayoutManager(getContext()));
            seenAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.collection("Notification")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            document.getReference().update("seen", true);
                                            //notificationAdapter.notifyDataSetChanged();
                                        }
                                        homeActivity.refresh();
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
            });
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                Query query = db.collection("Notification")
                        .whereEqualTo("userID", mAuth.getUid())
                        .orderBy("time", Direction.DESCENDING);

                FirestoreRecyclerOptions<Notification> response = new FirestoreRecyclerOptions.Builder<Notification>()
                        .setQuery(query, Notification.class)
                        .build();

                notificationAdapter = new NotificationAdapter(getActivity(), response);
                notificationAdapter.notifyDataSetChanged();
                newNoti.setAdapter(notificationAdapter);

                new ItemTouchHelper( new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        ViewGroup.LayoutParams params = viewHolder.itemView.getLayoutParams();
                        params.height = 0;
                        viewHolder.itemView.setLayoutParams(params);
                        notificationAdapter.deleteItem(viewHolder.getAdapterPosition());
                    }
                }).attachToRecyclerView(newNoti);
            }
            return v;
        } catch (Exception e) {
            Log.e(TAG, "onCreateView", e);
            throw e;
        }
    }
    public static Comparator<Notification> comparator = new Comparator<Notification>() {

        public int compare(Notification s1, Notification s2) {
            Long time1 = s1.getTime();
            Long time2 = s2.getTime();

            //ascending order
//            return review1.compareTo(review2);

            //descending order
            return time2.compareTo(time1);
        }
    };
}