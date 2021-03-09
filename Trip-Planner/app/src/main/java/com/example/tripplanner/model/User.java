package com.example.tripplanner.model;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tripplanner.view.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class User implements Serializable {
    public String fullname;
    public String email;
    public String link_ava_user;
    public Boolean active;

    static DatabaseReference modelUser = FirebaseDatabase.getInstance().getReference();
    public User(){};
    public User (String email, String name){
        this.email = email;
        this.fullname = name;
        this.link_ava_user = "";
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
    public Boolean getActive() { return active; }

    public static void addUser(String userID, User user){
        modelUser.child("User").child(userID).setValue(user);
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
    public static void editInfo(String userID, User user){
        modelUser.child("User").child(userID).setValue(user);
    }
}
