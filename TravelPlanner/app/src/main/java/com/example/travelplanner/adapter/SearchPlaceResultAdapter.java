package com.example.travelplanner.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.R;
import com.example.travelplanner.controller.PlaceDetailActivity;
import com.example.travelplanner.fragment.HomeFragment;
import com.example.travelplanner.model.MyPlace;
import com.example.travelplanner.model.URLRequest;
import com.example.travelplanner.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.travelplanner.fragment.SearchPlaceResultFragment.EXTRA_TEXT_ADDRESS;
import static com.example.travelplanner.fragment.SearchPlaceResultFragment.EXTRA_TEXT_IMG;
import static com.example.travelplanner.fragment.SearchPlaceResultFragment.EXTRA_TEXT_NAME;
import static com.example.travelplanner.fragment.SearchPlaceResultFragment.EXTRA_TEXT_PLACEID_DETAIL;

public class SearchPlaceResultAdapter extends
        RecyclerView.Adapter<SearchPlaceResultAdapter.MyViewHolder> {
    private final String TAG= "Thu SearchPlaceResultAdapter";
    private ArrayList<MyPlace> places;
    private Context context;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();


    public SearchPlaceResultAdapter(Context context, ArrayList<MyPlace> places) {
        Log.i(TAG, places.toString());
        this.places = places;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder "+ position);
        MyPlace place = places.get(position);
        String urlPhoto = URLRequest.getPhotoRequest(place.getImage());
        if(place.getImage()!= null)
            Picasso.with(context).load(urlPhoto).into(holder.placeCover);
        else
            holder.placeCover.setImageResource(R.drawable.discover);
        holder.txtName.setText(place.getName());
        holder.txtAddress.setText(place.getAddress());

        if (HomeFragment.mAuth.getCurrentUser() != null) {
            db.collection("User").document(HomeFragment.mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Log.i(TAG, "onSuccess " + position);
                    if (documentSnapshot.exists()) {
                        User b = documentSnapshot.toObject(User.class);
                        ArrayList<String> saved_places = b.getSaved_places();
                        if (saved_places.contains(place.getPlace_id())) {
                            holder.unSave.setVisibility(View.VISIBLE);
                            holder.save.setVisibility(View.GONE);
                            Log.i(TAG, "contain " + place.getPlace_id());

                        } else {
                            holder.unSave.setVisibility(View.GONE);
                            holder.save.setVisibility(View.VISIBLE);
                            Log.i(TAG, "not contain " + place.getPlace_id());

                        }
                    }
                }
            });
            holder.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (HomeFragment.mAuth.getCurrentUser() == null) {
                        Toast.makeText(context, "Bạn vui lòng đăng nhập để lưu địa điểm", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    User.savePlace(HomeFragment.mAuth.getUid(), place.getPlace_id());
                    Toast.makeText(context, "Đã lưu địa điểm", Toast.LENGTH_SHORT).show();
                    holder.unSave.setVisibility(View.VISIBLE);
                    holder.save.setVisibility(View.GONE);
                    place.addPlace();
                    //notifyDataSetChanged();
                }
            });
            holder.unSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User.unsavePlace(HomeFragment.mAuth.getUid(), place.getPlace_id());
                    Toast.makeText(context, "Đã bỏ lưu địa điểm", Toast.LENGTH_SHORT).show();
                    holder.unSave.setVisibility(View.GONE);
                    holder.save.setVisibility(View.VISIBLE);
                    //notifyDataSetChanged();
                }
            });
        }
        if(place.getRating()!= null)
            holder.ratingBar.setRating(Float.parseFloat(place.getRating()));
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout item_row;

        public ImageView placeCover;
        public TextView txtName;
        public TextView txtAddress;
        public ImageView save;
        public ImageView unSave;
        public RatingBar ratingBar;
        public MyViewHolder(View view) {
            super(view);
            item_row = view.findViewById(R.id.item_row);

            placeCover = view.findViewById(R.id.placeCover);
            txtName = view.findViewById(R.id.placeHeader);
            txtAddress = view.findViewById(R.id.address);
            save = view.findViewById(R.id.save);
            unSave = view.findViewById(R.id.unSave);
            ratingBar = view.findViewById(R.id.rating_bar);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.result_place_item,parent, false);

        final MyViewHolder mviewholder = new MyViewHolder(v);

        mviewholder.item_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPlace selected_p = places.get(mviewholder.getAdapterPosition());

                Intent intent = new Intent(context, PlaceDetailActivity.class);
                intent.putExtra(EXTRA_TEXT_PLACEID_DETAIL, selected_p.getPlace_id());
                intent.putExtra(EXTRA_TEXT_NAME, selected_p.getName());
                intent.putExtra(EXTRA_TEXT_IMG, selected_p.getImage());
                intent.putExtra(EXTRA_TEXT_ADDRESS, selected_p.getAddress());

                context.startActivity(intent);

                notifyDataSetChanged();
            }
        });

        return mviewholder;
    }
}