package com.example.travelplanner.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.R;
import com.example.travelplanner.activity.DetailsActivity;
import com.example.travelplanner.activity.UserPageActivity;
import com.example.travelplanner.model.Notification;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends
        FirestoreRecyclerAdapter<Notification, NotificationAdapter.MyViewHolder> {
    private static final String TAG= "Thu NotificationAdapter";
    private ArrayList<Notification> places;
    private Context context;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public NotificationAdapter(Context context, @NonNull FirestoreRecyclerOptions<Notification> options) {
        super(options);
        this.context = context;
        Log.i(TAG, "NotificationAdapter");
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Notification notification) {
        Log.i(TAG, "onBindViewHolder "+ position);
        //Notification p = places.get(position);
        holder.id = notification.getId();
        if(notification.getImg()!= null)
            Picasso.with(context).load(notification.getImg()).into(holder.cover);
        else
            holder.cover.setImageResource(R.drawable.discover);
        holder.name.setText(notification.getContent());
        long duration = (System.currentTimeMillis() - notification.getTime())/1000;
        if (duration < 60)
            holder.time.setText(duration + " giây trước");
        else if(duration < 3600)
            holder.time.setText(duration/60 + " phút trước");
        else if(duration < 86400)
            holder.time.setText(duration/3600 + " giờ trước");
        else
            holder.time.setText(duration/86400 + " ngày trước");

        if(!notification.isSeen())
            holder.itemView.setBackgroundColor(Color.parseColor("#454545"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Notification").document(notification.getId()).update("seen",true);
                holder.itemView.setBackgroundColor(Color.parseColor("#000000"));
                Intent intent;
                switch (notification.getType()) {
                    case 0:
                        intent = new Intent(context, UserPageActivity.class);
                        intent.putExtra("id", notification.getLink());
                        break;
                    case 1:
                        intent = new Intent(context, DetailsActivity.class);
                        intent.putExtra("Key", notification.getLink());
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + notification.getType());
                }
                context.startActivity(intent);
                notifyDataSetChanged();
            }
        });

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView cover;
        public TextView name;
        public TextView time;
        public String id;
        public MyViewHolder(View view) {
            super(view);
            cover = view.findViewById(R.id.cover);
            name = view.findViewById(R.id.name);
            time = view.findViewById(R.id.numberPlace);
            name.setTextSize(14);
        }
    }
    public void deleteItem(int position)
    {
        getSnapshots().getSnapshot(position).getReference().delete();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_view,parent, false);

        return new MyViewHolder(v);
    }

}