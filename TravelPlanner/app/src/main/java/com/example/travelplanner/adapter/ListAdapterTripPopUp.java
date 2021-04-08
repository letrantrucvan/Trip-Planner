package com.example.travelplanner.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.R;
import com.example.travelplanner.controller.PlaceDetailActivity;
import com.example.travelplanner.fragment.PlaceDetailFragment;
import com.example.travelplanner.model.MyPlace;
import com.example.travelplanner.model.Tour;
import com.example.travelplanner.model.URLRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.travelplanner.controller.SearchPlaceTable.EXTRA_TEXT_ADDRESS;
import static com.example.travelplanner.controller.SearchPlaceTable.EXTRA_TEXT_IMG;
import static com.example.travelplanner.controller.SearchPlaceTable.EXTRA_TEXT_NAME;
import static com.example.travelplanner.controller.SearchPlaceTable.EXTRA_TEXT_PLACEID_DETAIL;

public class ListAdapterTripPopUp extends RecyclerView.Adapter<ListAdapterTripPopUp.ViewHolder>{

    private static final String TAG = "Thu ListAdapterTripPopUp";

    ArrayList<Tour> tours;
    Context context;

    public ListAdapterTripPopUp(@NonNull Context context, @NonNull ArrayList<Tour> tours) {
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
//        //String urlPhoto = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1200&photoreference=" + place.getImage() + "&key=" + context.getResources().getString(R.string.google_maps_key);
//        String urlPhoto = URLRequest.getPhotoRequest(tour.getCover());
//
//        if(tour.getCover() != null){
//            Picasso.with(context).load(urlPhoto).into(holder.headerImage);
//        }
//        else
//            holder.headerImage.setImageResource(R.drawable.discover);
//        Log.i(TAG,"urlPhoto: "+urlPhoto);
//
        StorageReference imgRef = FirebaseStorage.getInstance().getReference().child(tour.getCover());
        final long ONE_MEGABYTE = 1024 * 1024;
        imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Log.i("Thu Photo",  tour.getName());
                Log.i("Thu Photo",  tour.getCover());
                holder.headerImage.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

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

    class ViewHolder extends RecyclerView.ViewHolder {

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
