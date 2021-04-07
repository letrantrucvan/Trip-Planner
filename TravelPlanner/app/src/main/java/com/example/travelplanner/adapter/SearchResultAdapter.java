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
import com.example.travelplanner.controller.PlaceDetailActivity;
import com.example.travelplanner.model.MyPlace;
import com.example.travelplanner.model.URLRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.travelplanner.controller.SearchPlaceTable.EXTRA_TEXT_ADDRESS;
import static com.example.travelplanner.controller.SearchPlaceTable.EXTRA_TEXT_IMG;
import static com.example.travelplanner.controller.SearchPlaceTable.EXTRA_TEXT_NAME;
import static com.example.travelplanner.controller.SearchPlaceTable.EXTRA_TEXT_PLACEID_DETAIL;

public class SearchResultAdapter extends
        RecyclerView.Adapter<SearchResultAdapter.MyViewHolder> {
    private final String TAG= "CustomRecyclerAdapter";
    private ArrayList<MyPlace> places;
    private Context context;


    public SearchResultAdapter(Context context, ArrayList<MyPlace> places) {
        Log.i(TAG, places.toString());
        this.places = places;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyPlace p = places.get(position);
        String urlPhoto = URLRequest.getPhotoRequest(p.getImage());
        if(p.getImage()!= null)
            Picasso.with(context).load(urlPhoto).into(holder.placeCover);
        else
            holder.placeCover.setImageResource(R.drawable.discover);
        holder.txtName.setText(p.getName());
        holder.txtAddress.setText(p.getAddress());
        holder.favIcon.setImageResource(R.drawable.ic_baseline_favorite_24);
        if(p.getRating()!= null)
            holder.ratingBar.setRating(Float.parseFloat(p.getRating()));
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
        public ImageView favIcon;
        public RatingBar ratingBar;
        public MyViewHolder(View view) {
            super(view);
            item_row = view.findViewById(R.id.item_row);

            placeCover = view.findViewById(R.id.placeCover);
            txtName = view.findViewById(R.id.placeHeader);
            txtAddress = view.findViewById(R.id.address);
            favIcon = view.findViewById(R.id.favIcon);
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

        //
        final ImageView favIcon = v.findViewById(R.id.favIcon);
        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // selected MyPlace object info :: selected_p
                MyPlace selected_p = places.get(mviewholder.getAdapterPosition());

            }
        });

        return mviewholder;
    }
}