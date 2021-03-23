package com.example.travelplanner.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ToursViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public ToursViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDetail(String cover_link, String name_tour,String des_tour){
            ImageView cover = (ImageView) mView.findViewById(R.id.cover);
            TextView name  = (TextView) mView.findViewById(R.id.name);
            TextView des = (TextView) mView.findViewById(R.id.des);

            name.setText(formatTourName(name_tour));
            //des.setText(des_tour);

            StorageReference imgRef = FirebaseStorage.getInstance().getReference().child(cover_link);
            final long ONE_MEGABYTE = 1024 * 1024;
            imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    cover.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    System.out.println(exception.getMessage());
                }
            });

        }
        String formatTourName(String name){
            if (name.length() >= 19){
                name = name.substring(0, 17);
                name += "...";
            }
            return name;
        }
    }


