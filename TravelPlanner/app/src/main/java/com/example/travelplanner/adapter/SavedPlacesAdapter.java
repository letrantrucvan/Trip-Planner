package com.example.travelplanner.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.R;
import com.example.travelplanner.activity.PlaceDetailActivity;
import com.example.travelplanner.fragment.HomeFragment;
import com.example.travelplanner.model.MyPlace;
import com.example.travelplanner.model.URLRequest;
import com.example.travelplanner.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.travelplanner.fragment.SearchPlaceResultFragment.EXTRA_TEXT_ADDRESS;
import static com.example.travelplanner.fragment.SearchPlaceResultFragment.EXTRA_TEXT_IMG;
import static com.example.travelplanner.fragment.SearchPlaceResultFragment.EXTRA_TEXT_NAME;
import static com.example.travelplanner.fragment.SearchPlaceResultFragment.EXTRA_TEXT_PLACEID_DETAIL;

public class SavedPlacesAdapter extends
        FirestoreRecyclerAdapter<MyPlace, SavedPlacesAdapter.MyViewHolder> {
    private static final String TAG= "Thu SavedPlacesAdapter";
    private ArrayList<MyPlace> places;
    private Context context;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();


    public SavedPlacesAdapter(Context context, @NonNull FirestoreRecyclerOptions<MyPlace> options) {
        super(options);
        this.context = context;
        Log.i(TAG, "SavedPlacesAdapter");
    }


    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull MyPlace place) {
        Log.i(TAG, "onBindViewHolder "+ position);
        //MyPlace p = places.get(position);
        String urlPhoto = URLRequest.getPhotoRequest(place.getImage());
        if(place.getImage()!= null)
            Picasso.with(context).load(urlPhoto).into(holder.placeCover);
        else
            holder.placeCover.setImageResource(R.drawable.discover);
        holder.txtName.setText(place.getName());
        holder.txtAddress.setText(place.getAddress());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, PlaceDetailActivity.class);
                intent.putExtra(EXTRA_TEXT_PLACEID_DETAIL, place.getPlace_id());
                intent.putExtra(EXTRA_TEXT_NAME, place.getName());
                intent.putExtra(EXTRA_TEXT_IMG, place.getImage());
                intent.putExtra(EXTRA_TEXT_ADDRESS, place.getAddress());

                context.startActivity(intent);
                notifyDataSetChanged();
            }
        });

        holder.unSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.unsavePlace(HomeFragment.mAuth.getUid(), place.getPlace_id());
                Toast.makeText(context, "Đã bỏ lưu địa điểm", Toast.LENGTH_SHORT).show();
                holder.itemView.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
                params.height = 0;
                p.setMargins(0,0,0,0);
                p.height = 0;
                holder.itemView.setLayoutParams(p);
            }
        });
        if(place.getRating()!= null)
            holder.ratingBar.setRating(Float.parseFloat(place.getRating()));
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView item_row;

        public ImageView placeCover;
        public TextView txtName;
        public TextView txtAddress;
        public ImageView save;
        public ImageView unSave;
        public RatingBar ratingBar;
        public MyViewHolder(View view) {
            super(view);
            item_row = view.findViewById(R.id.containerActivities);

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

        return new MyViewHolder(v);
    }
}