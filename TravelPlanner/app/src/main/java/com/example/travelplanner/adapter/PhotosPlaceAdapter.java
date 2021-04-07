package com.example.travelplanner.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.R;
import com.example.travelplanner.model.URLRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PhotosPlaceAdapter extends RecyclerView.Adapter<PhotosPlaceAdapter.ViewHolder>{

    ArrayList<String> photos_reference;
    Context context;
    double height,width;

    public PhotosPlaceAdapter(Context context, double width, double height, ArrayList<String>  photos_reference ){
        this.context = context;
        this.width = width;
        this.height = height;
        this.photos_reference = photos_reference;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        String urlPhoto = URLRequest.getPhotoRequest(photos_reference.get(position));

        Picasso.with(context).load(urlPhoto).into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i("Size", String.valueOf(photos_reference.size()));
        return photos_reference.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        CardView cardView;
        ImageView image ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.parentCard);
            image = itemView.findViewById(R.id.destinationCover);
            relativeLayout = itemView.findViewById(R.id.holderCard);
        }
    }

}
