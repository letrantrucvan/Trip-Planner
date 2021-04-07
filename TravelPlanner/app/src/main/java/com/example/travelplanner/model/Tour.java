package com.example.travelplanner.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.travelplanner.R;
import com.example.travelplanner.adapter.ListAdapterTripPopUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tour {
    static final String TAG = "Thu Tour";
    private String tour_id;
    private String name;
    private String author_id;
    private String author_name;
    private String cover;
    private String des;
    private String publish_day;
    private Integer rating_number;
    private Double rating_avg;
    private boolean archived_mode;
    private boolean isActive;

    public ArrayList<String> waypoints = new ArrayList<>();

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Tour(){}


    //get
    public String getTour_id() {
        return tour_id;
    }
    public String getName() {
        return name;
    }
    public String getAuthor_id() {
        return author_id;
    }
    public String getAuthor_name() {
        return author_name;
    }
    public String getCover() {
        return cover;
    }
    public String getDes() {
        return des;
    }
    public String getPublish_day() {
        return publish_day;
    }
    public Integer getRating_number() {
        return rating_number;
    }
    public Double getRating_avg() {
        return rating_avg;
    }
    public boolean isArchived_mode() {
        return archived_mode;
    }
    public boolean isActive() {
        return isActive;
    }
    public ArrayList<String> getWaypoints() {return waypoints;}

    //set
    public void setId(String id) {
        this.tour_id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }
    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }
    public void setCover(String cover) {
        this.cover = cover;
    }
    public void setDes(String des) {
        this.des = des;
    }
    public void setPublish_day(String publish_day) {
        this.publish_day = publish_day;
    }
    public void setRating_number(Integer rating_number) {
        this.rating_number = rating_number;
    }
    public void setRating_avg(Double rating_avg) {
        this.rating_avg = rating_avg;
    }
    public void setTour_id(String tour_id) {
        this.tour_id = tour_id;
    }

    public void setArchived_mode(boolean archived_mode) {
        this.archived_mode = archived_mode;
    }
    public void setActive(boolean active) {
        isActive = active;
    }

    static public void getHighlightedTour(){}
    static public void getNearByTour(){}

    public static void alterRating(String tourID, Integer rate) {
        db.collection("Tour").document(tourID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Tour tour = documentSnapshot.toObject(Tour.class);
                    if (tour.getRating_number() == 0){
                        tour.setRating_number(1);
                        tour.setRating_avg(rate*1.0);
                    }
                    else {
                        Integer newRatingNumber = tour.getRating_number() + 1;
                        Double newRatingAvg = ((tour.getRating_avg()*tour.getRating_number()) + (float) rate) / newRatingNumber;
                        tour.setRating_number(newRatingNumber);
                        tour.setRating_avg(newRatingAvg);
                    }
                    Tour.editRating(tour);
                }
            }
        });
    }
    public static void editRating(Tour tour){
        db.collection("Tour").document(tour.getTour_id()).update("rating_number", tour.getRating_number());
        db.collection("Tour").document(tour.getTour_id()).update("rating_avg", tour.getRating_avg());
    }

    public void addWaypoint(String PlaceID){
        db.collection("Tour").document(getTour_id())
                .update("waypoints", FieldValue.arrayUnion(PlaceID));
    }
    public void deleteWaypoint(String PlaceID){

        db.collection("Tour").document(getTour_id())
                .update("waypoints", FieldValue.arrayRemove(PlaceID));
    }

    public static ArrayList<Tour> getRelativeTour(String placeID)
    {
        ArrayList<Tour> tours = new ArrayList<>();
        final Boolean[] done = {false};
        Log.i(TAG, "getRelativeTour");
        db.collection("Tour").whereArrayContains("waypoints", placeID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.i(TAG, "onComplete");

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Tour tour = document.toObject(Tour.class);
//                                Log.d(TAG, "tour: "+ tour.toString());
                                tour.setId(document.getId());
                                tours.add(tour);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        done[0] = true;
                    }
                });
        while(!done[0]){}
        return tours;
    }
    @Override
    public String toString() {
        return "Tour{" +
                "tour_id='" + tour_id + '\'' +
                ", name='" + name + '\'' +
                ", author_id='" + author_id + '\'' +
                ", author_name='" + author_name + '\'' +
                ", cover='" + cover + '\'' +
                ", des='" + des + '\'' +
                ", publish_day='" + publish_day + '\'' +
                ", rating_number=" + rating_number.toString() +
                ", rating_avg=" + rating_avg.toString() +
                ", archived_mode=" + archived_mode +
                ", isActive=" + isActive +
                '}';
    }
}
