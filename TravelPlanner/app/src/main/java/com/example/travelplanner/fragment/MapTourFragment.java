package com.example.travelplanner.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.travelplanner.controller.DetailsActivity;
import com.example.travelplanner.controller.MapsTourActivity;
import com.example.travelplanner.model.CustomMapView;
import com.example.travelplanner.model.MyPlace;
import com.example.travelplanner.model.URLRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.example.travelplanner.R;

import static android.content.ContentValues.TAG;

public class MapTourFragment extends Fragment {
    private static final String TAG = "Thu MapTourFragment";

    CustomMapView mapView;
    private GoogleMap mMap;

    public static MapTourFragment newInstance() {
        Log.i(TAG, "MapTourFragment");

        return new MapTourFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        View rootView = inflater.inflate(R.layout.map_fragment, container, false);

        mapView = rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                mMap = map;
                Log.i(TAG, "onMapReady");
                mMap = map;
                for (int i = 0; i < DetailsActivity.waypoints.size(); i++) {
                    MyPlace place = DetailsActivity.waypoints.get(i);
                    addIcon(i, new LatLng(place.getLatitude(), place.getlongtitude()), place.getName());
                }
                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        startActivity(new Intent(getActivity(), MapsTourActivity.class));
                        Toast.makeText(getContext(), "longClick", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "OK");
                    }
                });
            }
        });
        mapView.setOnLongClickListener(new View.OnLongClickListener() {
               @Override
               public boolean onLongClick(View v) {

               return true;
               }
           }
        );
        return rootView;
    }


    private void addIcon(int number, LatLng position, String PlaceName) {
        IconGenerator iconFactory = new IconGenerator(getActivity());

        //iconFactory.setStyle(IconGenerator.STYLE_GREEN);
        MarkerOptions markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory
                        .fromBitmap(iconFactory.makeIcon(Integer.toString(number))))
                .position(position)
                .title(PlaceName)
                .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(number);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(12).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}