package com.example.travelplanner.controller;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import androidx.appcompat.app.AppCompatActivity;
import com.example.travelplanner.R;
import com.example.travelplanner.fragment.PlaceDetailFragment;
import com.example.travelplanner.fragment.SearchPlaceResultFragment;


public class PlaceDetailActivity extends AppCompatActivity {

    private static final String TAG = "Thu PlaceDetailActivity";


    public static String selected_placeId;
    public static String selected_name;
    public static String selected_img;
    public static String selected_address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);


        Intent intent = getIntent();
        selected_placeId = intent.getStringExtra(SearchPlaceResultFragment.EXTRA_TEXT_PLACEID_DETAIL);
        selected_name = intent.getStringExtra(SearchPlaceResultFragment.EXTRA_TEXT_NAME);
        selected_img = intent.getStringExtra(SearchPlaceResultFragment.EXTRA_TEXT_IMG);
        selected_address = intent.getStringExtra(SearchPlaceResultFragment.EXTRA_TEXT_ADDRESS);
        Log.i("Thu id: ", selected_placeId);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        double width = size.x;
        double height = size.y;

        Bundle bundle = new Bundle();
        bundle.putDouble("width",width);
        bundle.putDouble("height",height);
        bundle.putCharSequence("ID", selected_placeId);
        bundle.putCharSequence("Name", selected_name);
        bundle.putCharSequence("Img", selected_img);
        bundle.putCharSequence("Address", selected_address);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, PlaceDetailFragment.class, bundle)
                    .commit();
        }
    }
}
