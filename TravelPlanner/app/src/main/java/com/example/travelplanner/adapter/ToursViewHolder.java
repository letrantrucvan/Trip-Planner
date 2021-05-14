package com.example.travelplanner.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.R;

import com.example.travelplanner.model.Tour;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class ToursViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public ToursViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }
        public void setDetail(Tour model){
            ImageView cover = (ImageView) mView.findViewById(R.id.cover);
            TextView name  = (TextView) mView.findViewById(R.id.name);
            TextView numberPlace = (TextView) mView.findViewById(R.id.numberPlace);
            LinearLayout progress = (LinearLayout) mView.findViewById(R.id.progress);
            name.setText(model.getName());

            cover.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);

            if (model.getWaypoints() == null){
                numberPlace.setText("0 địa điểm");
            }
            else numberPlace.setText(model.getWaypoints().size() + " địa điểm");

            //get avatar
            Picasso.with(mView.getContext()).load(model.getCover()).into(cover);
            cover.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);

        }
    }


