package com.example.travelplanner.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.R;
import com.example.travelplanner.model.Tour;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class BookmarksPlaceViewHolder extends RecyclerView.ViewHolder{
    View mView;

    public BookmarksPlaceViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }


    public void setDetail(Tour model){
        ImageView cover = (ImageView) mView.findViewById(R.id.bookmarkPlaceAvatar);
        TextView name  = (TextView) mView.findViewById(R.id.bookmarkPlaceName);

        name.setText(model.getName());

        StorageReference imgRef = FirebaseStorage.getInstance().getReference().child(model.getCover());
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
                System.out.println("Fail");
            }
        });
    }

}


