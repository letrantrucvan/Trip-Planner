package com.example.travelplanner.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.R;
import com.example.travelplanner.fragment.PlaceDetailFragment;
import com.example.travelplanner.model.MyPlace;
import com.example.travelplanner.model.Tour;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListTripPopUpAdapter extends RecyclerView.Adapter<ListTripPopUpAdapter.ViewHolder>{

    private static final String TAG = "Thu ListAdapterTripPopUp";

    ArrayList<Tour> tours;
    Context context;

    public ListTripPopUpAdapter(@NonNull Context context, @NonNull ArrayList<Tour> tours) {
        Log.i(TAG,"ListAdapterTripPopUp" + tours.size());

        this.tours = tours;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG,"onCreateViewHolder");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_tour_popup_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.i(TAG,"onBindViewHolder");

        Log.i("Name",tours.get(position).getName());
        Tour tour = tours.get(position);
        holder.headerText.setText(tour.getName());

        Picasso.with(context).load(tour.getCover()).into(holder.headerImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tour.addWaypoint(PlaceDetailFragment.cur_placeID);
                new MyPlace(PlaceDetailFragment.cur_placeID,
                        PlaceDetailFragment.cur_name,
                        PlaceDetailFragment.cur_vicinity,
                        PlaceDetailFragment.image_reference,
                        Double.parseDouble(PlaceDetailFragment.cur_latitude),
                        Double.parseDouble(PlaceDetailFragment.cur_longitude)).addPlace();
                Log.i(TAG, tours.get(position).getTour_id() +"   "+PlaceDetailFragment.cur_placeID);
                Toast.makeText(context, "Thêm điểm đến vào chuyến đi thành công <3", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i("Size", String.valueOf(tours.size()));
        return tours.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView headerText;
        TextView content;
        ImageView headerImage ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            headerImage = itemView.findViewById(R.id.img);
            headerText = itemView.findViewById(R.id.name);
            content = itemView.findViewById(R.id.content);
        }
    }

}
