package com.example.travelplanner.controller;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.travelplanner.R;
import com.example.travelplanner.fragment.BookmarkFragment;
import com.example.travelplanner.fragment.DiscoverFragment;
import com.example.travelplanner.fragment.HomeFragment;
import com.example.travelplanner.fragment.NotiFragment;
import com.example.travelplanner.fragment.UserInfoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bt_nav = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bt_nav.setOnNavigationItemSelectedListener(navListener);

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
}