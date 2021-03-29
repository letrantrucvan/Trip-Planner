package com.example.travelplanner.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.R;
import com.example.travelplanner.model.Comment;
import com.example.travelplanner.model.Tour;
import com.example.travelplanner.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    View mView;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setDetail(Comment modelComment, User modelUser){
        ImageView user_avatar = (ImageView) mView.findViewById(R.id.detail_comment_avatar);
        TextView name  = (TextView) mView.findViewById(R.id.detail_comment_name);
        TextView rating = (TextView) mView.findViewById(R.id.detail_comment_rating);
        TextView comment = (TextView) mView.findViewById(R.id.detail_comment_comment);

        name.setText(modelUser.getFullname());
        rating.setText(modelComment.getRate().toString());
        comment.setText(modelComment.getComment());

        StorageReference imgRef = FirebaseStorage.getInstance().getReference().child(modelUser.getLink_ava_user());
        final long ONE_MEGABYTE = 1024 * 1024;
        imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                user_avatar.setImageBitmap(bitmap);
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
