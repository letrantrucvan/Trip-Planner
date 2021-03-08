package com.example.tripplanner.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User {
    public String fullname;
    public String email;
    public String password;
    public String link_ava_user;
    public Boolean active;

    User (String fullname, String email, String password){
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.link_ava_user = "";
        this.active = true;
    }
    static DatabaseReference modelUser = FirebaseDatabase.getInstance().getReference();

    public static void addUser(User user){
        modelUser.child("User").push().setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null){
                    //luu thànhh công
                } else {
                    //Lưu thất bại
                }
            }
        });
    }
    public static void editInfo(User user){
        modelUser.child("User").push().setValue(user);
    }
}
