package com.example.travelplanner.controller;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    static final String TAG ="Thu HomeActivity";
    static public String cur_location;
    static public Double cur_lat;
    static public Double cur_lng;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private static final int DEFAULT_ZOOM = 15;
    private Location lastKnownLocation;

    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bt_nav = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bt_nav.setOnNavigationItemSelectedListener(navListener);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // To get current location
        getDeviceLocation();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.discoverFragment:
                            selectedFragment = new DiscoverFragment();
                            break;
                        case R.id.userInfoFragment:
                            selectedFragment = new UserInfoFragment();
                            break;
                        case R.id.homeFragment:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.bookmarkFragment:
                            selectedFragment = new BookmarkFragment();
                            break;
                        case R.id.notiFragment:
                            selectedFragment = new NotiFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, selectedFragment).commit();
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

}