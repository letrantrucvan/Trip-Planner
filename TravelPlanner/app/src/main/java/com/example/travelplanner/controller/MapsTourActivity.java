package com.example.travelplanner.controller;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.travelplanner.adapter.PlaceOverViewAdapter;
import com.example.travelplanner.fragment.PlaceDetailFragment;
import com.example.travelplanner.model.MyPlace;
import com.example.travelplanner.model.URLRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

import com.example.travelplanner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MapsTourActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnInfoWindowCloseListener,
        GoogleMap.OnMarkerClickListener
{

    private final static String TAG ="Thu MapsTourActivity";
    private final String serverKey = "AIzaSyBkOL6u8dicZ2nZHOmkr1faQU9KbkzDhR4";
    private LatLng origin = new LatLng(37.7849569, -122.4068855);
    private LatLng destination = new LatLng(37.7814432, -122.4460177);
    private ViewPager2 viewPager;
    private GoogleMap mMap;
    private ArrayList<Marker> markers;
    private RequestQueue requestQueue;
    private ArrayList<Polyline> path_list = new ArrayList<>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment1);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
 //       getSupportActionBar().hide();
        viewPager = findViewById(R.id.viewpager);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowCloseListener(this);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.8696, 151.2094), 15));
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        markers = new ArrayList<>();
        // ArrayList<MyPlace> myPlaces = new ArrayList<>();
        for(int i = 0; i< DetailsActivity.waypoints.size(); i++)
        {
            MyPlace place = DetailsActivity.waypoints.get(i);
            addIcon(i, new LatLng(place.getLatitude(), place.getlongtitude()), place.getName());
        }

        viewPager.setAdapter(new PlaceOverViewAdapter(this,DetailsActivity.waypoints));
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(markers.size()>0) {
                    Marker marker = markers.get(position);
                    IconGenerator iconFactory = new IconGenerator(MapsTourActivity.this);

                    iconFactory.setStyle(IconGenerator.STYLE_GREEN);
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(marker.getTag().toString())));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                    marker.showInfoWindow();
                }
            }
        });
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
//        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//        Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
//        Canvas canvas1 = new Canvas(bmp);
//        Paint color = new Paint();
//        color.setTextSize(20);
//
//        color.setColor(Color.BLACK);
//        canvas1.drawCircle(0,0,10,color);
//        canvas1.drawText("User Name!",10,10, color);
//
//        mMap.addMarker(new MarkerOptions()
//                .position(new LatLng( latLng.latitude,latLng.longitude))
//                .snippet("OK")
//                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
//                .title("Thu's home"));

        addIcon(markers.size(), latLng, "Tên địa điểm");
        showDirection();

    }

    private void addIcon(int number, LatLng position, String PlaceName) {
        IconGenerator iconFactory = new IconGenerator(this);

        //iconFactory.setStyle(IconGenerator.STYLE_GREEN);
        MarkerOptions markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory
                        .fromBitmap(iconFactory.makeIcon(Integer.toString(number))))
                .position(position)
                .title(PlaceName)
                .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());


        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(number);
        markers.add(marker);
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setStyle(IconGenerator.STYLE_DEFAULT);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(marker.getTag().toString())));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        IconGenerator iconFactory = new IconGenerator(this);

        iconFactory.setStyle(IconGenerator.STYLE_GREEN);
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(marker.getTag().toString())));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        marker.showInfoWindow();
        viewPager.setCurrentItem(Integer.parseInt(marker.getTag().toString()), true);
        return true;
    }
    private void showDirection(){

        if (path_list !=null)
        {
            Log.i(TAG, "NOT NULL");
            for(Polyline p:path_list){
                p.remove(); // reset the previous path
            }
            path_list.clear();
        }
        else
            Log.i(TAG, "NULL");



        requestQueue = Volley.newRequestQueue(this);
        //String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+origin_fromAuto+"&destination=" + PlaceDetailFragment.cur_latitude +","+ PlaceDetailFragment.cur_longitude+ "&mode=" + mode_fromSpinner.toLowerCase()
        //+"&key="+getResources().getString(R.string.google_maps_key);
        //String url = URLRequest.getDirectionRequest("10.769267", "106.676273", PlaceDetailFragment.cur_latitude ,PlaceDetailFragment.cur_longitude);
        String url = URLRequest.getDirectionRequest(DetailsActivity.waypoints);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.d(TAG, response.toString());

                            JSONArray jsonArray = response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
                            //ori = new LatLng(jsonArray.getJSONObject(0).getJSONObject("start_location").getDouble("lat"),
                                //    jsonArray.getJSONObject(0).getJSONObject("start_location").getDouble("lng"));
                           // mMap.addMarker(new MarkerOptions().position(ori).title(origin_fromAuto)).showInfoWindow();

                            //CameraPosition mCameraPosition = new CameraPosition.Builder().target(ori).zoom(13).build();
                           // mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));

                            // display direction!
                            ArrayList<String> poly_path = new ArrayList<>();

                           // ArrayList<String> poly_path = getFullPath(jsonArray.getJSONObject(0).getJSONArray("steps"));
                            for(int i = 0; i< jsonArray.length();i++)
                            {
                                poly_path.addAll(getFullPath(jsonArray.getJSONObject(i).getJSONArray("steps")));
                            }
                            int path_len = poly_path.size();
                            for(int i=0;i<path_len;i++){
                                PolylineOptions mPolylineOptions = new PolylineOptions();
                                mPolylineOptions.color(Color.BLUE);
                                mPolylineOptions.width(9);
                                mPolylineOptions.addAll(PolyUtil.decode(poly_path.get(i)));

                                path_list.add(mMap.addPolyline(mPolylineOptions));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MapsTourActivity.this, "Không thể chỉ đường từ các địa điểm trong hành trình của bạn", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());

                Toast.makeText(MapsTourActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private ArrayList<String> getFullPath(JSONArray jsonArr){
        int len = jsonArr.length();
        ArrayList<String> lines = new ArrayList<>();

        for(int i=0;i<len;i++){
            try{
                lines.add(getSinglePath(jsonArr.getJSONObject(i)));
            }catch (JSONException e){
                // error
            }
        }
        return lines;
    }
    private String getSinglePath(JSONObject jsonObj){
        String line = "";
        try{
            line = jsonObj.getJSONObject("polyline").getString("points");
        }catch (JSONException e){
            //
        }
        return line;
    }
}


