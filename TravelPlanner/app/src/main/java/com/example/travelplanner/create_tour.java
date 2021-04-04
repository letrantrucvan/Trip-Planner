package com.example.travelplanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.travelplanner.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link create_tour#newInstance} factory method to
 * create an instance of this fragment.
 */
public class create_tour extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView trip_name;
    private TextView des;
    private ImageView cover_pic;
    private Button finish;
    private String user_id;
    private String Tour_id;

    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    int REQUEST_CODE_IMAGE = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public create_tour() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment create_tour.
     */
    // TODO: Rename and change types and number of parameters
    public static create_tour newInstance(String param1, String param2) {
        create_tour fragment = new create_tour();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout create_tour = (FrameLayout) inflater.inflate(R.layout.fragment_create_tour, container, false);

        mAuth = FirebaseAuth.getInstance();

        getUserID();
        storage = FirebaseStorage.getInstance();

        ImageView close = (ImageView) create_tour.findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(create_tour).navigate(R.id.action_create_tour_to_homeFragment);
            }
        });



        //Get info to create
        trip_name = (TextView) create_tour.findViewById(R.id.tourName);
        des = (TextView) create_tour.findViewById(R.id.description);
        cover_pic = (ImageView) create_tour.findViewById(R.id.cover_pic);
        finish = (Button) create_tour.findViewById(R.id.finish_button);


        //gan hinh cover vo
        cover_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mở máy chụp hình
                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(intent, REQUEST_CODE_IMAGE);
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, REQUEST_CODE_IMAGE);
            }
        });

        finish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Tour_id = "";
                String pattern = "dd-MM-yyyy";
                String dateInString =new SimpleDateFormat(pattern).format(new Date());

                Map<String, Object> data = new HashMap<>();
                data.put("author_id", user_id);
                data.put("name", trip_name.getText().toString());
                data.put("des", des.getText().toString());
                data.put("publish_day", dateInString);
                data.put("upvote_number", 0);

                db.collection("Tour")
                        .add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Tour_id = documentReference.getId();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("failed");
                            }
                        });

                data.put("cover", "Tour/"+Tour_id);
                data.put("Tour_id", Tour_id);
                db.collection("Tour").document(Tour_id)
                        .set(data, SetOptions.merge());



                StorageReference storageRef = storage.getReference().child("Tour/" + Tour_id);
                cover_pic.setDrawingCacheEnabled(true);
                cover_pic.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) cover_pic.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] dataz = baos.toByteArray();

                UploadTask uploadTask = storageRef.putBytes(dataz);
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
            }
        });



        return create_tour;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data!=null){
            //Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            //imgvAvatar.setImageBitmap(bitmap);
            Picasso.with(getContext()).load(data.getData()).into(cover_pic);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getUserID(){
        db.collection("User").document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    user_id = user.getId();
                }
            }
        });
    }
}