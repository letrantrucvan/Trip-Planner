package com.example.travelplanner.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.travelplanner.R;
import com.example.travelplanner.adapter.DestinationAdapter;
import com.example.travelplanner.adapter.PhotosPlaceAdapter;
import com.example.travelplanner.adapter.RelativeTourAdapter;
import com.example.travelplanner.controller.HomeActivity;
import com.example.travelplanner.model.MyPlace;
import com.example.travelplanner.model.Reviews;
import com.example.travelplanner.model.Tour;
import com.example.travelplanner.model.URLRequest;
import com.example.travelplanner.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class PlaceDetailFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "Thu PlaceDetailFragment";
    double width, height;
    GoogleMap map;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RequestQueue requestQueue;


    private TextView name;
    private ImageView save;
    private ImageView unSave;
    private ImageView place_photo;
    private TextView val_address;
    private TextView val_phone;
    private TextView val_price;
    private RatingBar val_rating;
    private TextView total_rating;
    private TextView val_ggPage;
    private TextView val_web;
    private TextView name_review;
    private RatingBar rating_review;
    private TextView time_review;
    private TextView text_review;
    private ImageView img_review;
    private TextView show_all_reviews;
    private LinearLayout review_zone;
    private ViewGroup progressView;
    private BlurView blurName;
    private BlurView blurDetail;
    private BlurView blurPhoto;
    private BlurView blurNearby;
    private BlurView blurMap;
    private BlurView blurReview;
    private BlurView blurTrip;
    private FloatingActionButton addButton;

    private LinearLayout row_address;
    private LinearLayout row_phone;
    private LinearLayout row_price;
    private LinearLayout row_rating;
    private LinearLayout row_ggPage;
    private LinearLayout row_web;

    private boolean LOADING;
    static public String cur_placeID;
    static public String cur_name;
    static public String cur_latitude;
    static public String cur_longitude;
    static public String cur_web;
    static public String cur_vicinity;
    static public String image_reference;
    static public ArrayList<Reviews> reviewsList;
    private ArrayList<String> photos_reference;
    private ArrayList<MyPlace> nearByPlaces;
    RelativeTourAdapter relativeTourAdapter;

    RecyclerView recyclerDestination, recyclerTour, recyclerPhoto, relativeTour;
    JsonObjectRequest jsonObjectNearByRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreateView");
        final View view = inflater.inflate(R.layout.place_detail_fragment, container, false);


        LOADING = false;
        showProgressingView();

        Bundle bundle = requireArguments();
        width = bundle.getDouble("width");
        height = bundle.getDouble("height");
        cur_placeID = bundle.getString("ID");
        cur_name = bundle.getString("Name");
        image_reference = bundle.getString("Img");
        cur_vicinity = bundle.getString("Address");

        requestQueue = Volley.newRequestQueue(getActivity());
        name = view.findViewById(R.id.name);
        place_photo = view.findViewById(R.id.photo);
        val_address = view.findViewById(R.id.val_address);
        val_phone = view.findViewById(R.id.val_phone);
        val_price = view.findViewById(R.id.val_price);
        val_rating = (RatingBar) view.findViewById(R.id.val_rating);
        total_rating = view.findViewById(R.id.total_rating);
        val_ggPage = view.findViewById(R.id.val_ggPage);
        val_web = view.findViewById(R.id.val_webpage);
        row_address = view.findViewById(R.id.row_address);
        row_phone = view.findViewById(R.id.row_phone);
        row_price = view.findViewById(R.id.row_price);
        row_rating = view.findViewById(R.id.row_rating);
        row_ggPage = view.findViewById(R.id.row_ggPage);
        row_web = view.findViewById(R.id.row_webpage);
        name_review = view.findViewById(R.id.name_review);
        rating_review = view.findViewById(R.id.rating_review);
        time_review = view.findViewById(R.id.time_review);
        text_review = view.findViewById(R.id.text_review);
        img_review = view.findViewById(R.id.img_review);
        show_all_reviews = view.findViewById(R.id.show_all_reviews);
        review_zone = view.findViewById(R.id.review_zone);
        blurName = view.findViewById(R.id.blurName);
        blurDetail = view.findViewById(R.id.blurDetail);
        blurPhoto = view.findViewById(R.id.blurPhoto);
        blurNearby = view.findViewById(R.id.blurNearby);
        blurMap = view.findViewById(R.id.blurMap);
        blurReview = view.findViewById(R.id.blurReview);
        blurTrip = view.findViewById(R.id.blurTrip);
        addButton = view.findViewById(R.id.addButton);
        save = view.findViewById(R.id.save);
        unSave = view.findViewById(R.id.unSave);
        recyclerDestination = view.findViewById(R.id.nearby_destinations);
        recyclerDestination.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        recyclerTour = view.findViewById(R.id.relative_tour);
        recyclerTour.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        recyclerPhoto = view.findViewById(R.id.place_photos);
        recyclerPhoto.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        reviewsList = new ArrayList<Reviews>();
        photos_reference = new ArrayList<>();


        float radius = 10f;

        ViewGroup rootView = (ViewGroup) view.findViewById(R.id.root);

        BlurryView(blurName,rootView, radius);
        BlurryView(blurDetail,rootView, radius);
        BlurryView(blurPhoto,rootView, radius);
        BlurryView(blurNearby,rootView, radius);
        BlurryView(blurMap,rootView, radius);
        BlurryView(blurReview,rootView, radius);
        BlurryView(blurTrip,rootView, radius);


        ScrollView mScrollView = (ScrollView) view.findViewById(R.id.scrollView);


        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.mapPlace);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);

            mapFragment.setListener(new MapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    mScrollView.requestDisallowInterceptTouchEvent(true);
                }
            });
        }
        db.collection("User").document(HomeFragment.mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User b = documentSnapshot.toObject(User.class);
                    ArrayList<String> saved_places = b.getSaved_places();
                    if (saved_places.contains(cur_placeID))
                    {
                        unSave.setVisibility(View.VISIBLE);
                        save.setVisibility(View.GONE);
                        Log.i(TAG, "contain "+ cur_placeID);

                    }
                    else
                    {
                        unSave.setVisibility(View.GONE);
                        save.setVisibility(View.VISIBLE);
                        Log.i(TAG, "not contain "+ cur_placeID);

                    }
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HomeFragment.mAuth.getCurrentUser() == null){
                    Toast.makeText(getContext(), "Bạn vui lòng đăng nhập để lưu địa điểm", Toast.LENGTH_SHORT).show();
                    return;
                }
                User.savePlace(HomeFragment.mAuth.getUid(), cur_placeID);
                Toast.makeText(getContext(), "Đã lưu địa điểm", Toast.LENGTH_SHORT).show();
                unSave.setVisibility(View.VISIBLE);
                save.setVisibility(View.GONE);
                //notifyDataSetChanged();
            }
        });
        unSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.unsavePlace(HomeFragment.mAuth.getUid(), cur_placeID);
                Toast.makeText(getContext(), "Đã bỏ lưu địa điểm", Toast.LENGTH_SHORT).show();
                unSave.setVisibility(View.GONE);
                save.setVisibility(View.VISIBLE);
                //notifyDataSetChanged();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_y,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.slide_out_y
                        )
                        .setReorderingAllowed(true)
                        .add(R.id.fragment_container_view, TripPopUpFragment.class, null)
                        .addToBackStack("TripPopUp")
                        .commit();
            }
        });


        //String urlPhoto = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1200&photoreference=" + image_reference + "&key=" + getResources().getString(R.string.google_maps_key);
        String urlPhoto = URLRequest.getPhotoRequest(image_reference);
        Picasso.with(getActivity()).load(urlPhoto).into(place_photo);
        name.setText(cur_name);

       // String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + cur_placeID + "&language=vi" + "&key=" + getResources().getString(R.string.google_maps_key);
        String url = URLRequest.getPlaceDetailRequest(cur_placeID);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Thu Map Debug ", "1");

                        Log.i("Before Nearby: ", response.toString());
                        // For table contents
                        try {
                            Log.d(TAG, response.toString());
                            JSONObject jsonObj = new JSONObject(response.toString());
                            JSONObject jsonDetail = jsonObj.getJSONObject("result");
                            Log.i(TAG, jsonDetail.toString());
                            try {
                                JSONArray photos = jsonDetail.getJSONArray("photos");
                                String photo_ref = photos.getJSONObject(0).getString(("photo_reference"));

                                String urlPhoto = URLRequest.getPhotoRequest(photo_ref);

                                Picasso.with(getActivity()).load(urlPhoto).into(place_photo);
                                for (int i = 0; i < photos.length(); i++) {
                                    JSONObject photo_reference = photos.getJSONObject(i);
                                    photos_reference.add(photo_reference.getString(("photo_reference")));
                                    PhotosPlaceAdapter destinationAdapter = new PhotosPlaceAdapter(getActivity(), width, height, photos_reference);
                                    recyclerPhoto.setAdapter(destinationAdapter);
                                }

                            } catch (JSONException e) {
                                place_photo.setImageResource(R.drawable.discover);
                                e.printStackTrace();
                            }

                            // 1. address
                            try {
                                Log.i("Thu Map Debug ", "1");

                                //formatted_address?
                                String address = jsonDetail.getString("vicinity");
                                val_address.setText(address);

                            } catch (JSONException e) {
                                row_address.setVisibility(View.GONE);
                                e.printStackTrace();
                            }
                            // 2. phone number
                            try {
                                final String phone = jsonDetail.getString("formatted_phone_number");
                                val_phone.setText(phone);
                                val_phone.setMovementMethod(LinkMovementMethod.getInstance());
                                String text = "<a href=''>" + phone + "</a>";
                                val_phone.setText(Html.fromHtml(text));
                                val_phone.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        String tel = "tel:" + phone;
                                        intent.setData(Uri.parse(tel));
                                        startActivity(intent);
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                                row_phone.setVisibility(View.GONE);
                            }
                            // 3. price level
                            try {
                                String price = jsonDetail.getString("price_level");
                                String dollar = "";
                                if (price.equals("0")) dollar = "Miễn phí";
                                else if (price.equals("1")) dollar = "Khá rẻ";
                                else if (price.equals("2")) dollar = "Vừa phải";
                                else if (price.equals("3")) dollar = "Đắt đỏ";
                                else if (price.equals("4")) dollar = "Rất đắt đỏ";
                                else dollar = "$$$$$";
                                val_price.setText(dollar);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                row_price.setVisibility(View.GONE);
                            }
                            // 4. rating
                            try {
                                String rating = jsonDetail.getString("rating").toString();
//                                val_rating.setText(rating);
                                val_rating.setRating(Float.parseFloat(rating));

                            } catch (JSONException e) {
                                e.printStackTrace();
                                row_rating.setVisibility(View.GONE);
                            }
                            // total ratings
                            try {
                                String rating = jsonDetail.getString("user_ratings_total");
//                                val_rating.setText(rating);
                                total_rating.setText(rating + " đánh giá");

                            } catch (JSONException e) {
                                e.printStackTrace();
                                row_rating.setVisibility(View.GONE);
                            }
                            // 5. google url
                            try {
                                String ggPage = jsonDetail.getString("url").toString();
                                val_ggPage.setMovementMethod(LinkMovementMethod.getInstance());
                                String text = "<a href='" + ggPage + "'>" + ggPage + "</a>";
                                val_ggPage.setText(Html.fromHtml(text));

                            } catch (JSONException e) {
                                e.printStackTrace();
                                row_ggPage.setVisibility(View.GONE);
                            }
                            // 6. website
                            try {
                                String web = jsonDetail.getString("website").toString();
                                cur_web = web;
                                val_web.setMovementMethod(LinkMovementMethod.getInstance());
                                String text = "<a href='" + web + "'>" + web + "</a>";
                                val_web.setText(Html.fromHtml(text));

                            } catch (JSONException e) {
                                e.printStackTrace();
                                row_web.setVisibility(View.GONE);
                            }
                            // 8. reviews
                            try {
                                JSONArray review_arr = jsonDetail.getJSONArray("reviews");
                                JSONObject reviewSample = review_arr.getJSONObject(0);

                                name_review.setText(reviewSample.getString("author_name"));

                                Picasso.with(getActivity()).load(reviewSample.getString("profile_photo_url")).into(img_review);

                                int rate = Integer.parseInt(reviewSample.getString("rating"));
                                rating_review.setRating(rate);
                                time_review.setText(Reviews.converTime(reviewSample.getString("time")));
                                text_review.setText(reviewSample.getString("text"));

                                for (int i = 0; i < review_arr.length(); i++) {
                                    review_zone.setVisibility(View.VISIBLE);
                                    JSONObject reviewDetail = review_arr.getJSONObject(i);
                                    reviewsList.add(new Reviews(
                                            reviewDetail.getString("author_url"),
                                            reviewDetail.getString("profile_photo_url"),
                                            reviewDetail.getString("author_name"),
                                            reviewDetail.getString("rating"),
                                            reviewDetail.getString("time"),
                                            "",
                                            reviewDetail.getString("text")
                                    ));
                                }
                                Log.i(TAG, ""+reviewsList.size());
                                show_all_reviews.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getParentFragmentManager().beginTransaction()
                                                .setReorderingAllowed(true)
                                                .replace(R.id.fragment_container_view, ReviewsFragment.class, null)
                                                .addToBackStack("review")
                                                .commit();
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                                // EMPTY REVIEW SESSION! ERROR HANDLE HERE!!!
                            }

                            // For yelp
                            String latitude = "";
                            String longitude = "";
                            String city = "";
                            String state = "";
                            String country = "";
                            String address1 = "";
                            String phone_num = "";


                            // (2,3) latitude, longitude
                            try {
                                JSONObject geoJson = jsonDetail.getJSONObject("geometry");
                                JSONObject locJson = geoJson.getJSONObject("location");
                                latitude = locJson.getString("lat").toString();
                                longitude = locJson.getString("lng").toString();

                                cur_latitude = latitude;
                                cur_longitude = longitude;
                                LatLng des = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                                map.addMarker(new MarkerOptions().position(des).title(PlaceDetailFragment.cur_name)).showInfoWindow();

                                CameraPosition mCameraPosition = new CameraPosition.Builder().target(des).zoom(17).build();
                                map.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
                                showDirection();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // (4,5,6) city, state, country, postal_code
                            try {
                                JSONArray addCompJson = jsonDetail.getJSONArray("address_components");
                                for (int i = 0; i < addCompJson.length(); i++) {
                                    JSONObject addCom = addCompJson.getJSONObject(i);
                                    JSONArray typesJson = addCom.getJSONArray("types");
                                    String type = typesJson.get(0).toString();
                                    if (type.equals("country")) {
                                        country = addCom.getString("short_name");
                                    } else if (type.equals("administrative_area_level_1")) {
                                        state = addCom.getString("short_name");
                                    } else if (type.equals("locality")) {
                                        city = addCom.getString("short_name");
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            //String nearByUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=5000&language=vi&key=" + getResources().getString(R.string.google_maps_key);
                            String nearByUrl = URLRequest.getNearbySearchRequest(latitude, longitude,5000);

                            jsonObjectNearByRequest = new JsonObjectRequest(Request.Method.GET, nearByUrl, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        Log.i("Nearby: ", response.toString());
                                        Log.i("Nearby: ", nearByUrl);
                                        JSONArray nearByPlacesArray = response.getJSONArray("results");
                                        nearByPlaces = new ArrayList<>();

                                        for (int i = 0; i < 10; i++) { //nearByPlacesArray.length()
                                            JSONObject result = nearByPlacesArray.getJSONObject(i);
                                            try {
                                                String photo_ref = result.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                                                nearByPlaces.add(new MyPlace(
                                                        result.getString("place_id"),
                                                        result.getString("name"),
                                                        result.getString("vicinity"),
                                                        photo_ref
                                                        ));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                nearByPlaces.add(new MyPlace(
                                                        result.getString("place_id"),
                                                        result.getString("name"),
                                                        result.getString("vicinity")
                                                ));
                                            }
                                        }
                                        DestinationAdapter destinationAdapter = new DestinationAdapter(getActivity(), width, height, nearByPlaces);
                                        recyclerDestination.setAdapter(destinationAdapter);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("Nearby: ", error.toString());
                                    error.printStackTrace();
                                }
                            });
                            RequestQueue queue = Volley.newRequestQueue(getActivity());
                            queue.add(jsonObjectNearByRequest);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Nothing found!", Toast.LENGTH_SHORT).show();
                        }

                        hideProgressingView();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideProgressingView();
                Toast.makeText(getActivity(), "Nothing found!", Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonObjectRequest);
        ArrayList<Tour> tours = new ArrayList<>();
        new Thread(new Runnable() {
            public void run() {
//                 relativeTourAdapter = new RelativeTourAdapter(getActivity(), width, height,
//                        Tour.getRelativeTour(PlaceDetailFragment.cur_placeID));
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Query query = db.collection("Tour").whereArrayContains("waypoints", PlaceDetailFragment.cur_placeID);
                FirestoreRecyclerOptions<Tour> options= new FirestoreRecyclerOptions.Builder<Tour>()
                        .setQuery(query,Tour.class)
                        .build();
                relativeTourAdapter = new RelativeTourAdapter(getActivity(), width, height,options);
                relativeTourAdapter.startListening();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerTour.setAdapter(relativeTourAdapter);

                    }
                });
            }
        }).start();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (relativeTourAdapter != null) relativeTourAdapter.startListening();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");

        relativeTourAdapter.stopListening();
        super.onStop();
    }

    private void showProgressingView() {

        if (!LOADING) {
            LOADING = true;
            progressView = (ViewGroup) getLayoutInflater().inflate(R.layout.loading_spinner, null);
            View v = getActivity().findViewById(android.R.id.content).getRootView();
            ViewGroup viewGroup = (ViewGroup) v;
            viewGroup.addView(progressView);
        }
    }

    private void hideProgressingView() {
        View v = getActivity().findViewById(android.R.id.content).getRootView();
        ViewGroup viewGroup = (ViewGroup) v;
        viewGroup.removeView(progressView);
        LOADING = false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
    }

    private void showDirection(){
        List<Polyline> path_list = new ArrayList<>();
        //String origin_fromAuto = "611 điện biên phủ";
        RequestQueue mrequestQueue = Volley.newRequestQueue(getActivity());
       // origin_fromAuto.replace(" ","+");
        //String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+origin_fromAuto+"&destination=" + PlaceDetailFragment.cur_latitude +","+ PlaceDetailFragment.cur_longitude
          //      +"&key="+getResources().getString(R.string.google_maps_key);
        try {
            String url = URLRequest.getDirectionRequest(HomeActivity.cur_lat.toString(), HomeActivity.cur_lng.toString(), PlaceDetailFragment.cur_latitude, PlaceDetailFragment.cur_longitude);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                Log.d(TAG, response.toString());

                                JSONArray jsonArray = response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
                                LatLng ori = new LatLng(jsonArray.getJSONObject(0).getJSONObject("start_location").getDouble("lat"),
                                        jsonArray.getJSONObject(0).getJSONObject("start_location").getDouble("lng"));
                                map.addMarker(new MarkerOptions().position(ori).title("Vị trí của bạn")).showInfoWindow();

                                CameraPosition mCameraPosition = new CameraPosition.Builder().target(ori).zoom(13).build();
                                map.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));

                                // display direction!
                                String[] poly_path = getFullPath(jsonArray.getJSONObject(0).getJSONArray("steps"));
                                int path_len = poly_path.length;
                                for (int i = 0; i < path_len; i++) {
                                    PolylineOptions mPolylineOptions = new PolylineOptions();
                                    mPolylineOptions.color(Color.BLUE);
                                    mPolylineOptions.width(10);
                                    mPolylineOptions.addAll(PolyUtil.decode(poly_path[i]));

                                    path_list.add(map.addPolyline(mPolylineOptions));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "Không thể chỉ đường từ vị trí của bạn", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, error.toString());

                    Toast.makeText(getActivity(), "Không thể hiện thị chỉ đường từ vị trí của bạn", Toast.LENGTH_SHORT).show();
                }
            });

            mrequestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Không thể chỉ đường từ vị trí của bạn", Toast.LENGTH_SHORT).show();
        }
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private void BlurryView (BlurView blurView, ViewGroup rootView, float radius){
        View decorView = ((Activity) getContext()).getWindow().getDecorView();

        Drawable windowBackground = decorView.getBackground();
        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(getActivity()))
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true)
                .setHasFixedTransformationMatrix(false);
    }
}
