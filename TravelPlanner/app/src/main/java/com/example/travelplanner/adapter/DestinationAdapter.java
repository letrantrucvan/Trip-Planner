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
import com.example.travelplanner.controller.PlaceDetailActivity;
import com.example.travelplanner.model.MyPlace;
import com.example.travelplanner.model.URLRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.travelplanner.fragment.SearchPlaceResultFragment.EXTRA_TEXT_ADDRESS;
import static com.example.travelplanner.fragment.SearchPlaceResultFragment.EXTRA_TEXT_IMG;
import static com.example.travelplanner.fragment.SearchPlaceResultFragment.EXTRA_TEXT_NAME;
import static com.example.travelplanner.fragment.SearchPlaceResultFragment.EXTRA_TEXT_PLACEID_DETAIL;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder>{

    private static final String TAG = "Thu DestinationAdapter";

    ArrayList<MyPlace> myPlaces;
    Context context;
    double height,width;

    public DestinationAdapter(Context context, double width, double height, ArrayList<MyPlace> myPlaces){
        Log.i(TAG,"DestinationAdapter");
        Log.i(TAG, myPlaces.toString());
        this.context = context;
        this.width = width;
        this.height = height;
        this.myPlaces = myPlaces;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG,"onCreateViewHolder");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.destinations_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.i(TAG,"onBindViewHolder");

        RecyclerView.LayoutParams layoutParams2 = (RecyclerView.LayoutParams) holder.cardView.getLayoutParams();
        double wi = height/4;
        wi = wi*4/5;
        layoutParams2.height = (int)height/4;
        layoutParams2.width = (int)wi;
        holder.cardView.setLayoutParams(layoutParams2);


        Log.i("Name",myPlaces.get(position).getName());
        MyPlace place = myPlaces.get(position);
        holder.headerText.setText(place.getName());
        //String urlPhoto = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1200&photoreference=" + place.getImage() + "&key=" + context.getResources().getString(R.string.google_maps_key);
        String urlPhoto = URLRequest.getPhotoRequest(place.getImage());

        if(place.getImage() != null){
            Picasso.with(context).load(urlPhoto).into(holder.headerImage);
        }
        else
            holder.headerImage.setImageResource(R.drawable.discover);
        Log.i(TAG,"urlPhoto: "+urlPhoto);

        holder.headerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, PlaceDetailActivity.class);
                intent.putExtra(EXTRA_TEXT_PLACEID_DETAIL, place.getPlace_id());
                intent.putExtra(EXTRA_TEXT_NAME, place.getName());
                intent.putExtra(EXTRA_TEXT_IMG, place.getImage());
                intent.putExtra(EXTRA_TEXT_ADDRESS, place.getAddress());
                context.startActivity(intent);
                notifyDataSetChanged();
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
        RelativeLayout relativeLayout;
        CardView cardView;
        ImageView headerImage ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.parentCard);
            headerImage = itemView.findViewById(R.id.destinationCover);
            headerText = itemView.findViewById(R.id.destinationHeader);
            relativeLayout = itemView.findViewById(R.id.holderCard);
        }
    }

}
