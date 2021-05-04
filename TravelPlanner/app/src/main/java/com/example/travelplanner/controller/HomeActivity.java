package com.example.travelplanner.controller;

import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.travelplanner.R;
import com.example.travelplanner.fragment.BookmarkFragment;
import com.example.travelplanner.fragment.DiscoverFragment;
import com.example.travelplanner.fragment.HomeFragment;
import com.example.travelplanner.fragment.NotiFragment;
import com.example.travelplanner.fragment.UserInfoFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class HomeActivity extends AppCompatActivity {

    static final String TAG ="Thu HomeActivity";
    private ViewGroup progressView;
    public boolean LOADING;

    static public String cur_location;
    static public Double cur_lat = 0.0;
    static public Double cur_lng = 0.0;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private static final int DEFAULT_ZOOM = 15;
    private Location lastKnownLocation;

    private ActionBar toolbar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private BottomNavigationView bt_nav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LOADING = false;
        showProgressingView();

        bt_nav = findViewById(R.id.bottomNavigationView);
        bt_nav.setOnNavigationItemSelectedListener(navListener);
        if(savedInstanceState != null) {
            Log.i(TAG,savedInstanceState.toString());
            bt_nav.setSelectedItemId(savedInstanceState.getInt("Nav", R.id.discoverFragment));
        }else
            bt_nav.setSelectedItemId( R.id.discoverFragment);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            db.collection("Notification").whereEqualTo("userID",mAuth.getUid())
                    .whereEqualTo("seen", false).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    BadgeDrawable badge = bt_nav.getOrCreateBadge(R.id.notiFragment);
                    badge.setVisible(true);
                    badge.setNumber(value.size());
                    if(value.size() == 0) badge.setVisible(false);
                }
            });
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // To get current location
        getDeviceLocation();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i(TAG,"onSaveInstanceState");
        Log.i(TAG, bt_nav.getSelectedItemId()+"");
        outState.putInt("Nav", bt_nav.getSelectedItemId());
        super.onSaveInstanceState(outState);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.discoverFragment:
                            showProgressingView();
                            selectedFragment = new DiscoverFragment();
                            break;
                        case R.id.userInfoFragment:
                            selectedFragment = new UserInfoFragment();
                            break;
                        case R.id.homeFragment:
                            showProgressingView();
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.bookmarkFragment:
                            selectedFragment = new BookmarkFragment();
                            break;
                        case R.id.notiFragment:
                            selectedFragment = new NotiFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, selectedFragment,selectedFragment.getClass().getSimpleName()).commit();
                    return true;
                }


            };

    private void getDeviceLocation() {
        Log.i(TAG, "getDeviceLocation");

        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Log.i(TAG, "onComplete");

                        if (task.isSuccessful()) {
                            Log.i(TAG, "isSuccessful");

                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                cur_lat = lastKnownLocation.getLatitude();
                                cur_lng = lastKnownLocation.getLongitude();
                                cur_location = cur_lat.toString() +","+ cur_lng.toString();
                                Log.i(TAG, cur_location);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Unable to get current location:", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
            else getLocationPermission();
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }


    private void getLocationPermission() {
        Log.i(TAG, "getLocationPermission");

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void refresh(){
        Fragment frg = getSupportFragmentManager().findFragmentByTag("NotiFragment");
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }
    public void showProgressingView() {

        if (!LOADING) {
            LOADING = true;
            AnimationDrawable animationDrawable;
            progressView = (ViewGroup) getLayoutInflater().inflate(R.layout.loading_spinner, null);

            View v = findViewById(android.R.id.content).getRootView();
            ImageView loading = progressView.findViewById(R.id.loading);
            animationDrawable = (AnimationDrawable) loading.getDrawable();
            animationDrawable.start();
            ViewGroup viewGroup = (ViewGroup) v;
            viewGroup.addView(progressView);
        }
    }

    public void hideProgressingView() {
        View v = findViewById(android.R.id.content).getRootView();
        ViewGroup viewGroup = (ViewGroup) v;
        viewGroup.removeView(progressView);
        LOADING = false;
    }
}