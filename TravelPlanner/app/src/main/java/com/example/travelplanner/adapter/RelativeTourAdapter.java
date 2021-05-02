package com.example.travelplanner.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.travelplanner.R;
import com.example.travelplanner.controller.DetailsActivity;
import com.example.travelplanner.controller.PlaceDetailActivity;
import com.example.travelplanner.model.MyPlace;
import com.example.travelplanner.model.Tour;
import com.example.travelplanner.model.URLRequest;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class RelativeTourAdapter extends FirestoreRecyclerAdapter<Tour, RelativeTourAdapter.ViewHolder> {

    private static final String TAG = "Thu RelativeTourAdapter";

    ArrayList<Tour> tours;
    Context context;
    double height,width;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public RelativeTourAdapter(Context context, double width, double height, @NonNull FirestoreRecyclerOptions<Tour> options){
        super(options);
        Log.i(TAG,"RelativeTourAdapter");
        this.context = context;
        this.width = width;
        this.height = height;
        //this.tours = tours;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG,"onCreateViewHolder");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_place_overview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Tour tour) {
        Log.i(TAG,"onBindViewHolder");

        RecyclerView.LayoutParams layoutParams2 = (RecyclerView.LayoutParams) holder.cardView.getLayoutParams();
        double wi = height/4;
        wi = wi*4/5;
        layoutParams2.height = (int)height/3;
        layoutParams2.width = (int)width/7*6;
        holder.cardView.setLayoutParams(layoutParams2);

        holder.headerText.setText(tour.getName());

        //get avatar
        Picasso.with(context).load(tour.getCover()).into(holder.headerImage);

        holder.headerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, DetailsActivity.class);
                i.putExtra("Key", tour.getTour_id());
                context.startActivity(i);
            }
        });
        View decorView = ((Activity) context).getWindow().getDecorView();

        Drawable windowBackground = decorView.getBackground();
        holder.blur.setupWith(holder.cardView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(context))
                .setBlurRadius(10f)
                .setBlurAutoUpdate(true)
                .setHasFixedTransformationMatrix(false);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView headerText;
        TextView content;
        RelativeLayout relativeLayout;
        CardView cardView;
        ImageView headerImage;
        BlurView blur;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.parentCard);
            headerImage = itemView.findViewById(R.id.cover);
            headerText = itemView.findViewById(R.id.header);
            content = itemView.findViewById(R.id.content);
            relativeLayout = itemView.findViewById(R.id.holderCard);
            blur = itemView.findViewById(R.id.blur);
        }
    }

}
