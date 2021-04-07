package com.example.travelplanner.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelplanner.controller.PlaceDetailActivity;
import com.example.travelplanner.model.MyPlace;
import com.example.travelplanner.model.URLRequest;
import com.squareup.picasso.Picasso;
import com.example.travelplanner.R;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

import static com.example.travelplanner.controller.SearchPlaceTable.EXTRA_TEXT_ADDRESS;
import static com.example.travelplanner.controller.SearchPlaceTable.EXTRA_TEXT_IMG;
import static com.example.travelplanner.controller.SearchPlaceTable.EXTRA_TEXT_NAME;
import static com.example.travelplanner.controller.SearchPlaceTable.EXTRA_TEXT_PLACEID_DETAIL;

public class PlaceOverViewAdapter extends
        RecyclerView.Adapter<PlaceOverViewAdapter.MyViewHolder> {
    private final String TAG= "Thu PlaceOverViewAdapter";
    private ArrayList<MyPlace> places;
    private Context context;


    public PlaceOverViewAdapter(Context context, ArrayList<MyPlace> places) {
        Log.i(TAG, places.toString());
        this.places = places;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        View decorView = ((Activity) context).getWindow().getDecorView();

        Drawable windowBackground = decorView.getBackground();
        holder.blur.setupWith(holder.cardView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(context))
                .setBlurRadius(10f)
                .setBlurAutoUpdate(true)
                .setHasFixedTransformationMatrix(false);
        MyPlace p = places.get(position);
        String urlPhoto = URLRequest.getPhotoRequest(p.getImage());

        if(p.getImage()!= null)
            Picasso.with(context).load(urlPhoto).into(holder.placeCover);
        else
            holder.placeCover.setImageResource(R.drawable.discover);
        holder.txtName.setText(p.getName());
        holder.txtAddress.setText(p.getAddress());

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout item_row;
        public BlurView blur;
        public ImageView placeCover;
        public TextView txtName;
        public TextView txtAddress;
        CardView cardView;


        public MyViewHolder(View view) {
            super(view);
            cardView = itemView.findViewById(R.id.parentCard);
            item_row = view.findViewById(R.id.holderCard);
            blur = view.findViewById(R.id.blur);
            placeCover = view.findViewById(R.id.cover);
            txtName = view.findViewById(R.id.header);
            txtAddress = view.findViewById(R.id.content);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.fragment_place_overview,parent, false);

        final MyViewHolder mviewholder = new MyViewHolder(v);

        mviewholder.item_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPlace selected_p = places.get(mviewholder.getAdapterPosition());

                Intent intent = new Intent(context, PlaceDetailActivity.class);
                intent.putExtra(EXTRA_TEXT_PLACEID_DETAIL, selected_p.getPlace_id());
                intent.putExtra(EXTRA_TEXT_NAME, selected_p.getName());
                intent.putExtra(EXTRA_TEXT_IMG, selected_p.getImage());
                intent.putExtra(EXTRA_TEXT_ADDRESS, selected_p.getAddress());

                context.startActivity(intent);

                notifyDataSetChanged();
            }
        });

        return mviewholder;
    }
}