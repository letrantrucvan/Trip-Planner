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

import com.example.travelplanner.model.Tour;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class BookmarksTourViewHolder extends RecyclerView.ViewHolder{
    View mView;

    public BookmarksTourViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }


    public void setDetail(Tour model){
        ImageView cover = (ImageView) mView.findViewById(R.id.bookmarkAvatar);
        TextView name  = (TextView) mView.findViewById(R.id.bookmarkTourName);
        TextView author = (TextView) mView.findViewById(R.id.bookmarkAuthor);
        //TextView publish_day = (TextView) mView.findViewById(R.id.bookmarkPublishDay);
        //TextView upvote = (TextView) mView.findViewById(R.id.bookmarkLikeNumber);

        name.setText(formatTourName(model.getName()));
        author.setText("Đăng bởi " + model.getAuthor_name());
        //publish_day.setText(model.getPublish_day());
        //upvote.setText(model.getUpvote_number().toString());

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

    String formatTourName(String name){
        if (name.length() > 40){
            name = name.substring(0, 37);
            name += "...";
        }
        return name;
    }

}


