package com.example.travelplanner.model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class URLRequest {
    static final String TAG = "Thu URLRequest";
    static final String API_key = "AIzaSyBkOL6u8dicZ2nZHOmkr1faQU9KbkzDhR4";
    static final String PlaceDetail = "https://maps.googleapis.com/maps/api/place/details/json?";

    static final String TextSearch = "https://maps.googleapis.com/maps/api/place/textsearch/json?";

    static final String NearbySearch = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    ;
    static final String Direction = "https://maps.googleapis.com/maps/api/directions/json?";
    static final String Photo = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1200&photoreference=";

    static public String getPhotoRequest(String photo_reference) {
        return Photo + photo_reference + "&key=" + API_key;
    }

    static public String getPlaceDetailRequest(String place_id) {
        return PlaceDetail + "place_id=" + place_id + "&language=vi" + "&key=" + API_key;
    }

    static public String getTextSearchRequest(String keyword) {
        return TextSearch + "query=" + keyword + "&language=vi" + "&key=" + API_key;
    }
    static public String getTextSearchRequest(String keyword, String category) {
        category = category.toLowerCase();
        category.replace(' ','_');
        return TextSearch + "query=" + keyword + "&type=" + category+ "&language=vi" + "&key=" + API_key;
    }

    static public String getTextSearchRequestPageToken(String next_p) {
        return TextSearch + "pagetoken=" + next_p + "&key=" + API_key;
    }
    static public String getNearbySearchRequest(String latitude, String longitude, int radius) {
        return NearbySearch + "location=" + latitude + "," + longitude + "&radius=" + radius +"&language=vi&key=" + API_key;
    }

    static public String getDirectionRequest(String originLat, String originLng, String desLat, String desLng) {
        return Direction+ "origin=" + originLat + "," + originLng + "&destination="
                + desLat + "," + desLng + "&key=" + API_key;
//        urlString.append("&waypoints=");// via
//        for(LatLng waypoint: waypoints)
//        {
//            if (hasWaypoints)
//                urlString.append("%7C");
//            urlString.append(waypoint.latitude);
//            urlString.append("%2C");
//            urlString.append(waypoint.longitude);
//            hasWaypoints = true;
 //       }
    }
    static public String getDirectionRequest(ArrayList<MyPlace> waypoints) {
        StringBuilder url = new StringBuilder(Direction + "origin=place_id:" + waypoints.get(0).getPlace_id()
                + "&destination=place_id:" + waypoints.get(waypoints.size() - 1).getPlace_id());

        if(waypoints.size() > 2) {
            url.append("&waypoints=");
            for (int i = 1; i< waypoints.size()-1;i++)
            {
                MyPlace place = waypoints.get(i);
                if(i>1) url.append("|");
                url.append("place_id:").append(place.getPlace_id());
            }
        }
        url.append("&key=" + API_key);
        Log.i(TAG, url.toString());

        return url.toString();
    }
}
