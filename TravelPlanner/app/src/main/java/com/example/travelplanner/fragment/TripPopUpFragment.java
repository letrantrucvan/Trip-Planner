package com.example.travelplanner.fragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.R;
import com.example.travelplanner.adapter.ListTripPopUpAdapter;
import com.example.travelplanner.model.Tour;
import com.example.travelplanner.model.URLRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class TripPopUpFragment extends Fragment {
    private static final String TAG = "Thu TripPopUpFragment";
    private RecyclerView list_tour_popup;
    private ArrayList<Tour> tours;
    private BlurView blurView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG,"onCreateView");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final View view = inflater.inflate(R.layout.list_tour_popup, container, false);
        list_tour_popup = view.findViewById(R.id.list_tour_popup);
        tours = new ArrayList<>();
        blurView = view.findViewById(R.id.container_popup);
        ImageView place_photo = view.findViewById(R.id.photo);

        String urlPhoto = URLRequest.getPhotoRequest(PlaceDetailFragment.image_reference);
        Picasso.with(getActivity()).load(urlPhoto).into(place_photo);

        View decorView = ((Activity) getContext()).getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) view.findViewById(R.id.root);
        Drawable windowBackground = decorView.getBackground();
        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(getActivity()))
                .setBlurRadius(10f)
                .setBlurAutoUpdate(true)
                .setHasFixedTransformationMatrix(false);

        ImageView close_ic = view.findViewById(R.id.close);
        close_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        db.collection("Tour")
                .whereEqualTo("author_id", mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Tour tour = document.toObject(Tour.class);
//                                Log.d(TAG, "tour: "+ tour.toString());
                                tour.setId(document.getId());
                                tours.add(tour);
                            }

                            ListTripPopUpAdapter adapter = new ListTripPopUpAdapter(
                                    getActivity().getApplicationContext(), tours
                            );
                            list_tour_popup.setAdapter(adapter);
                            list_tour_popup.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return view;
    }
}
