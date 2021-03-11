package com.example.travelplanner.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelplanner.R;
import com.example.travelplanner.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class UserInformationActivity extends AppCompatActivity {

    private TextView edtEmail;
    private EditText edtName;
    private ImageView imgvAvatar;
    private Button btnSaveInfo;
    private TextView edtEditInfo;
    private Button btnSignout;

    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private FirebaseStorage storage;


    int REQUEST_CODE_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        edtEmail = (TextView) findViewById(R.id.userinformation_edtEmail);
        edtName = (EditText) findViewById(R.id.userinformation_edtName);
        imgvAvatar = (ImageView) findViewById(R.id.userinformation_imgvAvatar);
        btnSaveInfo = (Button) findViewById(R.id.userinformation_btnSaveInfo);
        edtEditInfo = (TextView) findViewById(R.id.userinformation_edtEditInfo);
        btnSignout = (Button) findViewById(R.id.userinformation_btnSignout);


        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("User");
        storage = FirebaseStorage.getInstance();

        if(mAuth.getCurrentUser() == null){
            Intent i = new Intent(UserInformationActivity.this, LoginActivity.class);
            startActivity(i);
        }
        getUserInformation();

        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent i = new Intent(UserInformationActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        edtEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtEditInfo.setVisibility(View.GONE);
                btnSaveInfo.setVisibility(View.VISIBLE);
                edtName.setEnabled(true);
                edtName.requestFocus();
                imgvAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CODE_IMAGE);
                    }
                });
            }
        });

        btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserInformationActivity.this, "Đã lưu thay đổi.", Toast.LENGTH_SHORT).show();
                edtEditInfo.setVisibility(View.VISIBLE);
                btnSaveInfo.setVisibility(View.GONE);
                edtName.setEnabled(false);
                imgvAvatar.setOnClickListener(null);

                //upload Aavatar moi len storage
                // Get the data from an ImageView as bytes
                StorageReference storageRef = storage.getReference().child("Avatar/" + mAuth.getCurrentUser().getUid());
                imgvAvatar.setDrawingCacheEnabled(true);
                imgvAvatar.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imgvAvatar.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = storageRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    }
                });

                //saveInfo vo database user
                User userUpdate = new User(edtName.getText().toString(), edtEmail.getText().toString(), "Avatar/" + mAuth.getUid(), true);
                User.editInfo(mAuth.getUid(), userUpdate);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data!=null){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgvAvatar.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getUserInformation(){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.child(mAuth.getUid()).getValue(User.class);
                //get name + email
                edtName.setText(user.getFullname());
                edtEmail.setText(user.getEmail());

                //get avatar
                StorageReference islandRef = storage.getReference().child(user.link_ava_user);

                final long ONE_MEGABYTE = 1024 * 1024;
                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Data for "images/island.jpg" is returns, use this as needed
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imgvAvatar.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        System.out.println("Fail");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}