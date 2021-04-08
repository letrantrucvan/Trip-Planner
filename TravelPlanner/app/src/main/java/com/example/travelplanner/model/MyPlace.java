package com.example.travelplanner.model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.Serializable;

public class MyPlace implements Serializable{

    private String place_id;
    private String name;
    private String image;
    private String address;
    private String rating;
    private Double latitude;
    private Double longtitude;

    public MyPlace(){}
    public MyPlace(String place_id, String name, String address, String img, String rating){
        this.place_id = place_id;
        this.name = name;
        this.image = img;
        this.address = address;
        this.rating = rating;
        Log.i("Thu place5 img", img);
        Log.i("Thu place5 add", address);

    }
    public MyPlace(String place_id, String name, String address, String img){
        this.place_id = place_id;
        this.name = name;
        this.image = img;
        this.address = address;
        Log.i("Thu place4 img", img);
        Log.i("Thu place4 add", address);

    }
    public MyPlace(String place_id, String name, String address, String img, Double lat, Double lng){
        this.place_id = place_id;
        this.name = name;
        this.image = img;
        this.address = address;
        this.latitude = lat;
        this.longtitude = lng;
        Log.i("Thu place4 img", img);
        Log.i("Thu place4 add", address);

    }
    public MyPlace(String place_id, String name, String address){
        this.place_id = place_id;
        this.name = name;
        this.address = address;
        Log.i("Thu place3 add", address);
    }

    public MyPlace(String place_id, String name){
        this.place_id = place_id;
        this.name = name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getlongtitude() {
        return longtitude;
    }

    public void setlongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }

    public String getRating() {
        if(rating == "0") return null;
        return rating;
    }

    public void addPlace()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.i("Thu MyPlace ", ""+latitude +" " +longtitude);
        db.collection("Place").document(place_id).set(this, SetOptions.merge());
    }
}
