package com.example.travelplanner.fragment;

import android.content.Context;
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

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.travelplanner.model.URLRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends SupportMapFragment {
    private static final String TAG = "Thu MapFragment";
    private OnTouchListener mListener;

    MapView map_mapView;
    private GoogleMap gg_map;
    private LatLng ori;
    private LatLng des;

    //private PlacesAutoAdapter mPlacesAutoAdapter;
    private RequestQueue mrequestQueue;

    private Spinner map_spinner;
    private AutoCompleteTextView map_fromAuto;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    private List<Polyline> path_list;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.map_fragment1, container, false);
        Log.d(TAG, "onCreateView");
        path_list = new ArrayList<>();
        View layout = super.onCreateView(inflater, container, savedInstanceState);

        TouchableWrapper frameLayout = new TouchableWrapper(getActivity());
        frameLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        ((ViewGroup) layout).addView(frameLayout,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        return layout;
    }

    private void showDirection(){
        final String origin_fromAuto = map_fromAuto.getText().toString();
        String mode_fromSpinner = map_spinner.getSelectedItem().toString();

        for(Polyline p:path_list){
            p.remove(); // reset the previous path
        }
        path_list.clear();

        mrequestQueue = Volley.newRequestQueue(getActivity());
        origin_fromAuto.replace(" ","+");
        //String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+origin_fromAuto+"&destination=" + PlaceDetailFragment.cur_latitude +","+ PlaceDetailFragment.cur_longitude+ "&mode=" + mode_fromSpinner.toLowerCase()
        //+"&key="+getResources().getString(R.string.google_maps_key);
        String url = URLRequest.getDirectionRequest("10.769267", "106.676273", PlaceDetailFragment.cur_latitude ,PlaceDetailFragment.cur_longitude);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.d(TAG, response.toString());

                            JSONArray jsonArray = response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
                            ori = new LatLng(jsonArray.getJSONObject(0).getJSONObject("start_location").getDouble("lat"),
                                            jsonArray.getJSONObject(0).getJSONObject("start_location").getDouble("lng"));
                            gg_map.addMarker(new MarkerOptions().position(ori).title(origin_fromAuto)).showInfoWindow();

                            CameraPosition mCameraPosition = new CameraPosition.Builder().target(ori).zoom(13).build();
                            gg_map.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));

                            // display direction!
                            String[] poly_path = getFullPath(jsonArray.getJSONObject(0).getJSONArray("steps"));
                            int path_len = poly_path.length;
                            for(int i=0;i<path_len;i++){
                                PolylineOptions mPolylineOptions = new PolylineOptions();
                                mPolylineOptions.color(Color.BLUE);
                                mPolylineOptions.width(10);
                                mPolylineOptions.addAll(PolyUtil.decode(poly_path[i]));

                                path_list.add(gg_map.addPolyline(mPolylineOptions));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity().getApplicationContext(), "noting from json", Toast.LENGTH_SHORT).show();
                        }

                    }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, error.toString());

                    Toast.makeText(getActivity().getApplicationContext(), "Nothing found!", Toast.LENGTH_SHORT).show();
                }
            });

        mrequestQueue.add(jsonObjectRequest);
    }

    private String[] getFullPath(JSONArray jsonArr){
        int len = jsonArr.length();
        String[] lines = new String[len];

        for(int i=0;i<len;i++){
            try{
                lines[i] = getSinglePath(jsonArr.getJSONObject(i));
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

    public void setListener(OnTouchListener listener) {
        mListener = listener;
    }

    public interface OnTouchListener {
        public abstract void onTouch();
    }

    public class TouchableWrapper extends FrameLayout {

        public TouchableWrapper(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mListener.onTouch();
                    Log.i("Thu touch", "ok");
                    break;
                case MotionEvent.ACTION_UP:
                    mListener.onTouch();
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }
}
