package com.example.travelplanner.model;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.travelplanner.controller.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    public String fullname;
    public String email;
    public String link_ava_user;
    public Boolean active;

    //static DatabaseReference modelUser = FirebaseDatabase.getInstance().getReference();
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public User(){};
    public User (String email, String name){
        this.email = email;
        this.fullname = name;
        this.link_ava_user = "Avatar/avatar.png";
        this.active = true;
    }
    public User (String fullname, String email, String link_ava_user, boolean active){
        this.fullname = fullname;
        this.email = email;
        this.link_ava_user = link_ava_user;
        this.active = active;
    }
    public String getFullname() { return fullname; }
    public String getEmail() { return email; }
    public String getLink_ava_user() { return link_ava_user; }
    public Boolean getActive() { return active; }

    public void setLink_ava_user(String link_ava_user) {
        this.link_ava_user = link_ava_user;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

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
    public static void editInfo(String userID, String newname){
        db.collection("User").document(userID).update("fullname", newname);
        db.collection("User").document(userID).update("link_ava_user", "Avatar/" + userID);
    }
}