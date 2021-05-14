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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.R;
import com.example.travelplanner.activity.TourDetailsActivity;
import com.example.travelplanner.model.Tour;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ToursOfUserAdapter extends
        FirestoreRecyclerAdapter<Tour, ToursOfUserAdapter.MyViewHolder> {
    private final String TAG= "Thu ToursOfUserAdapter";
    private ArrayList<Tour> tours;
    private final Context context;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();


    public ToursOfUserAdapter(Context context, @NonNull FirestoreRecyclerOptions<Tour> options) {
        super(options);
        this.context = context;
        Log.i(TAG, "ToursOfUserAdapter");
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Tour tour) {
        Log.i(TAG, "onBindViewHolder "+ position);
        //Tour p = tours.get(position);
        Picasso.with(context).load(tour.getCover()).into(holder.tourCover);
        holder.txtName.setText(tour.getName());
        holder.txtAddress.setText(tour.getWaypoints().size()+ " địa điểm");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, TourDetailsActivity.class);
                intent.putExtra("Key", tour.getTour_id());

                context.startActivity(intent);
                notifyDataSetChanged();
            }
        });

        if(tour.getRating_avg() > 0)
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.ratingBar.setRating(tour.getRating_avg());
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout item_row;

        public ImageView tourCover;
        public TextView txtName;
        public TextView txtAddress;
        public ImageView save;
        public ImageView unSave;
        public RatingBar ratingBar;
        public MyViewHolder(View view) {
            super(view);

            tourCover = view.findViewById(R.id.bookmarkAvatar);
            txtName = view.findViewById(R.id.bookmarkTourName);
            txtAddress = view.findViewById(R.id.bookmarkWayPoint);
//            save = view.findViewById(R.id.save);
//            unSave = view.findViewById(R.id.unSave);
            ratingBar = view.findViewById(R.id.rating_bar);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycle_view_tour_bookmark,parent, false);
        return new MyViewHolder(v);
    }
}