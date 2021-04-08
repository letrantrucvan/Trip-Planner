package com.example.travelplanner.model;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;

public class CustomMapView extends MapView {

    public CustomMapView(Context context) {
        super(context);
    }

    public CustomMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomMapView(Context context, GoogleMapOptions options) {
        super(context, options);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        /**
         * Request all parents to relinquish the touch events
         */
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        Log.i("Thu Motion: ", String.valueOf(action));
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Disallow ScrollView to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_UP:
                // Allow ScrollView to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(false);
                break;

        }

        // Handle MapView's touch events.
        super.onTouchEvent(ev);
        return true;
    }
}