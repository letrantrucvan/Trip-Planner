package com.example.travelplanner.adapter;

import android.content.Context;
import android.content.Intent;
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

import com.example.travelplanner.R;
import com.example.travelplanner.controller.DetailsActivity;
import com.example.travelplanner.controller.PlaceDetailActivity;
import com.example.travelplanner.model.MyPlace;
import com.example.travelplanner.model.URLRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.travelplanner.controller.SearchPlaceTable.EXTRA_TEXT_ADDRESS;
import static com.example.travelplanner.controller.SearchPlaceTable.EXTRA_TEXT_IMG;
import static com.example.travelplanner.controller.SearchPlaceTable.EXTRA_TEXT_NAME;
import static com.example.travelplanner.controller.SearchPlaceTable.EXTRA_TEXT_PLACEID_DETAIL;

public class WaypointAdapter extends RecyclerView.Adapter<WaypointAdapter.ViewHolder>{

    private static final String TAG = "Thu WaypointAdapter";

    ArrayList<MyPlace> myPlaces;
    Context context;

    public WaypointAdapter(Context context, ArrayList<MyPlace> myPlaces){
        Log.i(TAG,"WaypointAdapter");
        Log.i(TAG, myPlaces.toString());
        this.context = context;
        this.myPlaces = myPlaces;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG,"onCreateViewHolder");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waypoint_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.i(TAG,"onBindViewHolder");

//        RecyclerView.LayoutParams layoutParams2 = (RecyclerView.LayoutParams) holder.cardView.getLayoutParams();
//        double wi = height/4;
//        wi = wi*4/5;
//        layoutParams2.height = (int)height/4;
//        layoutParams2.width = (int)wi;
//        holder.cardView.setLayoutParams(layoutParams2);


        Log.i("Name",myPlaces.get(position).getName());
        MyPlace place = myPlaces.get(position);
        holder.headerText.setText(place.getName());
        holder.headerAddress.setText(place.getAddress());
        String urlPhoto = URLRequest.getPhotoRequest(place.getImage());

        if(place.getImage() != null){
            Picasso.with(context).load(urlPhoto).into(holder.headerImage);
        }
        else
            holder.headerImage.setImageResource(R.drawable.discover);
        Log.i(TAG,"urlPhoto: "+urlPhoto);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"onClick");

                Intent intent = new Intent(context, PlaceDetailActivity.class);
                intent.putExtra(EXTRA_TEXT_PLACEID_DETAIL, place.getPlace_id());
                intent.putExtra(EXTRA_TEXT_NAME, place.getName());
                intent.putExtra(EXTRA_TEXT_IMG, place.getImage());
                intent.putExtra(EXTRA_TEXT_ADDRESS, place.getAddress());
                context.startActivity(intent);
                notifyDataSetChanged();
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i(TAG,"onLongClick");

                holder.delete.setVisibility(View.VISIBLE);
                return true;
            }
        });
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i(TAG,"onFocusChange");

                if(!hasFocus) holder.delete.setVisibility(View.GONE);

            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"delete");
                DetailsActivity.cur_Tour.deleteWaypoint(place.getPlace_id());
                holder.itemView.setVisibility(View.GONE);
                //delete Waypoint
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i("Size", String.valueOf(myPlaces.size()));
        return myPlaces.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView headerText;
        TextView headerAddress;
        RelativeLayout relativeLayout;
        CardView cardView;
        ImageView headerImage ;
        ImageView delete ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.parentCard);
            headerImage = itemView.findViewById(R.id.destinationCover);
            headerText = itemView.findViewById(R.id.destinationHeader);
            headerAddress = itemView.findViewById(R.id.destinationAddress);
            relativeLayout = itemView.findViewById(R.id.holderCard);
            delete = itemView.findViewById(R.id.delete);
        }
    }

}
