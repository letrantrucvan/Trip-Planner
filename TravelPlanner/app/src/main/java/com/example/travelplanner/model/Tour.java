package com.example.travelplanner.model;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import android.graphics.Bitmap;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

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
    private Float rating_avg;
    private boolean is_public; // cong khai chuyen di
    private boolean is_delete; // delete chuyen di
    private ArrayList<String> search_keywords;
    private int views = 0;
    public static String currentTour;

    public ArrayList<String> waypoints = new ArrayList<>();

    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Tour(){}

    public Tour(String name, String author_id, String des, String publish_day) {
        this.name = name;
        this.author_id = author_id;
        this.des = des;
        this.publish_day = publish_day;
        this.search_keywords = generateKeyWords(name);
        this.is_delete = false;
        this.is_public = false;
    }

    public boolean isIs_delete() {
        return is_delete;
    }

    public void setIs_public(boolean is_public) {
        this.is_public = is_public;
    }

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
    @Exclude
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

    public boolean isIs_public() {
        return is_public;
    }

    public Integer getRating_number() {
        return rating_number;
    }
    public float getRating_avg() {
        return rating_avg;
    }
    public boolean isArchived_mode() {
        return is_public;
    }
    public boolean isActive() {
        return is_delete;
    }
    public ArrayList<String> getWaypoints() {return waypoints;}
    public int getViews() { return views; }


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
    public void setRating_avg(Float rating_avg) {
        this.rating_avg = rating_avg;
    }
    public void setTour_id(String tour_id) {
        this.tour_id = tour_id;
    }
    public void setArchived_mode(boolean archived_mode) {
        this.is_public = archived_mode;
    }
    public void setActive(boolean active) {
        is_delete = active;
    }
    public ArrayList<String> getSearch_keywords() {
        return search_keywords;
    }
    public void setSearch_keywords(ArrayList<String> search_keywords) { this.search_keywords = search_keywords; }
    public void setViews(int views) { this.views = views; }

    public void setWaypoints(ArrayList<String> waypoints) {
        this.waypoints = waypoints;
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
                        tour.setRating_avg((float) (rate*1.0));
                    }
                    else {
                        Integer newRatingNumber = tour.getRating_number() + 1;
                        Float newRatingAvg = ((tour.getRating_avg()*tour.getRating_number()) + (float) rate) / newRatingNumber;
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

    public static void delete(Tour tour){
        db.collection("Tour").document(tour.getTour_id()).update("is_delete", tour.isActive());
    }

    public static void editTour(Tour tour){
        db.collection("Tour").document(tour.getTour_id()).update("name", tour.getName());
        db.collection("Tour").document(tour.getTour_id()).update("des", tour.getDes());
        db.collection("Tour").document(tour.getTour_id()).update("is_public", tour.isIs_public());


    }

    public static void editImage(String tourID, String newavalink){
        db.collection("Tour").document(tourID).update("cover", newavalink);
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
    public static String addTour(Tour tour){
        DocumentReference newTourReference = db.collection("Tour").document();
        String tourID = newTourReference.getId();
        tour.setId(tourID);
        tour.setCover("Tour/" + tourID);
        tour.setRating_number(0);
        tour.setViews(0);
        tour.setPublish_day(java.time.LocalDate.now().toString());
        newTourReference.set(tour);
        Tour.currentTour = tourID;
        return tourID;
    }


    public static void uploadCover(String tourID, Bitmap cover){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Tour/" + tourID);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cover.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] data = baos.toByteArray();

        final UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return storageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String avalink = task.getResult().toString();
                            System.out.println("newLinkTour: " + avalink);
                            Tour.editImage(tourID, avalink);
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
    // FULL TEXT SEARCH : Split string => Find keywords

    private ArrayList<String> generateKeyWords(String text) {
        ArrayList<String> res = new ArrayList<String>();
        text = text.toLowerCase();

        String []words = text.split(" ");

        for (String word : words) {
            String appendStr = "";
            //Printing the characters
            for (char output : text.toCharArray()) {
                appendStr += String.valueOf(output);
                res.add(appendStr);
            }
            word = word + " ";
            text = text.replace(word,"");
        }
        System.out.println(res);
        return res;
    }
    ////////////////////////////////////////////////////////////


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
                ", rating_number=" + rating_number +
                ", rating_avg=" + rating_avg +
                ", is_public=" + is_public +
                ", is_delete=" + is_delete +
                ", search_keywords=" + search_keywords +
                ", views=" + views +
                ", waypoints=" + waypoints +
                '}';
    }
}
