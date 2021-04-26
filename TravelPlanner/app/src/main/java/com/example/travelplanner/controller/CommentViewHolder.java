package com.example.travelplanner.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.R;
import com.example.travelplanner.model.Rating;
import com.example.travelplanner.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    View mView;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setDetail(Rating modelRating, User modelUser){
        ImageView user_avatar = (ImageView) mView.findViewById(R.id.detail_comment_avatar);
        TextView name  = (TextView) mView.findViewById(R.id.detail_comment_name);
        TextView rating = (TextView) mView.findViewById(R.id.detail_comment_rating);
        TextView comment = (TextView) mView.findViewById(R.id.detail_comment_comment);

        name.setText(modelUser.getFullname());
        rating.setText(modelRating.getRate().toString());
        comment.setText(modelRating.getComment());

        //get avatar
        Picasso.with(mView.getContext()).load(modelUser.getLink_ava_user()).into(user_avatar);


    }
}
