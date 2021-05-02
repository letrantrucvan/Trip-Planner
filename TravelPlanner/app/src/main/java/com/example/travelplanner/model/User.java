package com.example.travelplanner.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.travelplanner.controller.LoginActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {
    private String id;
    private String fullname;
    private String email;
    private String link_ava_user;
    private Boolean active;
    private List<String> saved_tour;
    private ArrayList<String> saved_places = new ArrayList<>();
    private ArrayList<String> following = new ArrayList<>();
    private ArrayList<String> follower = new ArrayList<>();

    //static DatabaseReference modelUser = FirebaseDatabase.getInstance().getReference();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public User(){};
    public User (String id, String email, String name){
        this.id = id;
        this.email = email;
        this.fullname = name;
        this.link_ava_user = "https://firebasestorage.googleapis.com/v0/b/travel-planner-ed66f.appspot.com/o/Avatar%2Favatar.png?alt=media&token=d598b64a-67cb-4c5a-9af1-c974a708dd61";
        this.active = true;
    }
    public User (String id, String fullname, String email, String link_ava_user, boolean active){
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.link_ava_user = link_ava_user;
        this.active = active;
    }

    //set get
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getLink_ava_user() {
        return link_ava_user;
    }
    public void setLink_ava_user(String link_ava_user) {
        this.link_ava_user = link_ava_user;
    }
    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }
    public void setSaved_tour(List<String> saved_tour) {
        this.saved_tour = saved_tour;
    }
    public List<String> getSaved_tour() {
        return saved_tour;
    }

    public ArrayList<String> getSaved_places() {
        return saved_places;
    }
    public void setSaved_places(ArrayList<String> saved_places) {
        this.saved_places = saved_places;
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }

    public ArrayList<String> getFollower() {
        return follower;
    }

    public void setFollower(ArrayList<String> follower) {
        this.follower = follower;
    }

    //end of set get

    public static void addUser(String userID, User user){
        db.collection("User").document(userID).set(user);
    }
    public static void changePassword(String newPassword){ //password phai co it nhat 6 ky tu
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //hien thong bao doi pass thanh cong
                        }
                    }
                });
    }
    public static void editInfo(String userID, String newname, String newlinkava){
        db.collection("User").document(userID).update("fullname", newname);
        db.collection("User").document(userID).update("link_ava_user", newlinkava);
    }

    public static void uploadAvatar(String userID, String newname, Bitmap avatar){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Avatar/" + userID);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        avatar.compress(Bitmap.CompressFormat.JPEG, 60, baos);
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
                            System.out.println("newLink: " + avalink);
                            User.editInfo(userID, newname, avalink);
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

    public static void saveTour(String userID, String tourID){
        db.collection("User").document(userID).update("saved_tour", FieldValue.arrayUnion(tourID));
    }

    public static void unsaveTour(String userID, String tourID){
        db.collection("User").document(userID).update("saved_tour", FieldValue.arrayRemove(tourID));
    }

    public static void savePlace(String userID, String placeID){
        db.collection("User").document(userID).update("saved_places", FieldValue.arrayUnion(placeID));
    }
    public static void unsavePlace(String userID, String placeID){
        //db.collection("User").document(userID).update("saved_tour", FieldValue.arrayRemove(tourID));
        db.collection("User").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User b = documentSnapshot.toObject(User.class);
                    ArrayList<String> save_places = b.getSaved_places();
                    db.collection("User").document(userID).update("saved_places", FieldValue.arrayRemove(placeID));

//                    if (save_places.size() == 1){
//                        db.collection("User").document(userID).update("saved_places", null);
//                    }
//                    else {
//                        db.collection("User").document(userID).update("saved_places", FieldValue.arrayRemove(placeID));
//                    }

                }
            }
        });
    }
}
