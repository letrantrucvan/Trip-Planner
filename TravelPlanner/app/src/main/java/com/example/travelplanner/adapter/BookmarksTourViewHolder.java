package com.example.travelplanner.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telephony.CellSignalStrength;
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


public class BookmarksTourViewHolder extends RecyclerView.ViewHolder{
    View mView;

    public BookmarksTourViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }


    public void setDetail(Tour model){
        LinearLayout progress = (LinearLayout) mView.findViewById(R.id.bookmarkProgress);
        ImageView cover = (ImageView) mView.findViewById(R.id.bookmarkAvatar);
        TextView name  = (TextView) mView.findViewById(R.id.bookmarkTourName);
        TextView author = (TextView) mView.findViewById(R.id.bookmarkAuthor);
        TextView waypoint = (TextView) mView.findViewById(R.id.bookmarkWayPoint);
        name.setText(model.getName());
        author.setText("Đăng bởi " + model.getAuthor_name());

        cover.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        if (model.getWaypoints() == null){
            waypoint.setText("0 địa điểm");
        }
        else waypoint.setText(model.getWaypoints().size() + " địa điểm");

        //get avatar
        Picasso.with(mView.getContext()).load(model.getCover()).into(cover);
        cover.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);

    }
}


