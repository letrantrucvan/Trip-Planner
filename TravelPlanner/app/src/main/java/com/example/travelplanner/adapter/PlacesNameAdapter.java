package com.example.travelplanner.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.travelplanner.R;
import com.example.travelplanner.fragment.FragmentTwo;
import com.example.travelplanner.model.MyPlace;

import java.util.ArrayList;

public class PlacesNameAdapter extends RecyclerView.Adapter<FragmentTwo.PlacesViewHolder> {

    private static final String TAG = "Van PlaceNameAdapter";
    public ArrayList<MyPlace> mData;
    public LayoutInflater mInflater;
    public String author_id;

    // data is passed into the constructor
    public PlacesNameAdapter(Context context, ArrayList<MyPlace> data,String author_id) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.author_id = author_id;
    }

    @NonNull
    @Override
    public FragmentTwo.PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder");
        View mView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_my_place, parent, false);
        return new FragmentTwo.PlacesViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentTwo.PlacesViewHolder holder, int position) {
        Log.i(TAG,"onBindViewHolder");
        MyPlace place = mData.get(position);
        holder.setDetail(place,author_id);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
