package com.example.travelplanner.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.travelplanner.R;
import com.example.travelplanner.controller.LoginActivity;
import com.example.travelplanner.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserInfoFragment extends Fragment {

    private static final String TAG = "UserInfoFragment";
    private LinearLayout userInfoGotoLogin;
    private LinearLayout userinformation_progress;
    private Button userInfoBtnGotoLogin;
    private CardView changeAvatar;
    private LinearLayout userInfoNormal;
    private TextView edtEmail;
    private EditText edtName;
    private ImageView imgvAvatar;
    private Button btnSaveInfo;
    private TextView edtEditInfo;
    private Button btnSignout;

    private FirebaseAuth mAuth;

    private FirebaseStorage storage;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    int REQUEST_CODE_IMAGE = 1;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserInfoFragment newInstance(String param1, String param2) {
        UserInfoFragment fragment = new UserInfoFragment();
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
        FrameLayout userInfo = (FrameLayout) inflater.inflate(R.layout.fragment_user_info, container, false);


        mAuth = FirebaseAuth.getInstance();

        userinformation_progress = (LinearLayout) userInfo.findViewById(R.id.userinformation_progress);

        //check user đăng nhập hay chưa
        userInfoGotoLogin = (LinearLayout) userInfo.findViewById(R.id.userInfoGotoLogin);
        userInfoNormal = (LinearLayout) userInfo.findViewById(R.id.userInfoNormal);
        userInfoBtnGotoLogin = (Button) userInfo.findViewById(R.id.userInfoBtnGotoLogin);
        if (mAuth.getCurrentUser() == null){
            userInfoGotoLogin.setVisibility(View.VISIBLE);
            userInfoNormal.setVisibility(View.GONE);
            userInfoBtnGotoLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            });
            return  userInfo;
        }

        //user đã đăng nhập thì thực hiện
        changeAvatar = (CardView) userInfo.findViewById(R.id.userinformation_chooseType);
        changeAvatar.setVisibility(View.GONE);
        edtEmail = (TextView) userInfo.findViewById(R.id.userinformation_edtEmail);
        edtName = (EditText) userInfo.findViewById(R.id.userinformation_edtName);
        imgvAvatar = (ImageView) userInfo.findViewById(R.id.userinformation_imgvAvatar);
        btnSaveInfo = (Button) userInfo.findViewById(R.id.userinformation_btnSaveInfo);
        edtEditInfo = (TextView) userInfo.findViewById(R.id.userinformation_edtEditInfo);
        btnSignout = (Button) userInfo.findViewById(R.id.userinformation_btnSignout);


        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        //Get User Information
        getUserInformation();


        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
            }
        });

        edtEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtEditInfo.setVisibility(View.GONE);
                btnSaveInfo.setVisibility(View.VISIBLE);
                changeAvatar.setVisibility(View.VISIBLE);
                edtName.setEnabled(true);
                edtName.requestFocus();
                changeAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BottomChooseCamera ChooseCamera = new BottomChooseCamera(mAuth.getUid(), imgvAvatar);
                        ChooseCamera.show(getActivity().getSupportFragmentManager(),TAG);
                    }
                });
            }
        });

        btnSaveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtEditInfo.setVisibility(View.VISIBLE);
                btnSaveInfo.setVisibility(View.GONE);
                changeAvatar.setVisibility(View.GONE);
                edtName.setEnabled(false);

                //upload Aavatar moi len storage/
                imgvAvatar.setDrawingCacheEnabled(true);
                imgvAvatar.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) imgvAvatar.getDrawable()).getBitmap();
                //hàm này up avatar mới lên storage, xong lấy link URL của avatar mới bỏ vô database user luôn
                User.uploadAvatar(mAuth.getCurrentUser().getUid(), edtName.getText().toString(),bitmap);
            }
        });
        return userInfo;
    }

    private void getUserInformation(){
        db.collection("User").document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.exists()){
                    User user = documentSnapshot.toObject(User.class);
                    //get name + email
                    edtName.setText(user.getFullname());
                    edtEmail.setText(user.getEmail());

                    //get avatar
                    Picasso.with(getContext()).load(user.getLink_ava_user()).into(imgvAvatar);
                    imgvAvatar.setVisibility(View.VISIBLE);
                    userinformation_progress.setVisibility(View.GONE);
                }
            }
        });
    }
}