package com.example.travelplanner.fragment;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.travelplanner.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;


public class BottomChooseCamera  extends BottomSheetDialogFragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "key";
    private static final String TAG = "Van BottomSheetDialog";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String userID;
    private ImageView imageHolder;
    private LinearLayout chooseCamera;
    private LinearLayout chooseLibrary;
    private LinearLayout chooseDrive;
    int REQUEST_CODE_IMAGE = 1;

    public BottomChooseCamera(String userid) {
        this.userID =  userid;
    }

    public BottomChooseCamera(String userid, ImageView image){
        this.userID =  userid;
        this.imageHolder = image;
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment BottomSheetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomChooseCamera newInstance(String param1) {
        BottomChooseCamera fragment = new BottomChooseCamera(param1);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bottom_choosecamera, container, false);

        chooseCamera = (LinearLayout) v.findViewById(R.id.chooseCamera);
        chooseLibrary = (LinearLayout) v.findViewById(R.id.chooseLibrary);
        chooseDrive = (LinearLayout) v.findViewById(R.id.chooseDrive);

        chooseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Mở máy chụp hình
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });

        chooseLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mở album
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });

        chooseDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mở Drive
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i, REQUEST_CODE_IMAGE);
            }
        });

        //Hiệu ứng chọn
        chooseCamera.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        chooseCamera.setBackgroundColor(getResources().getColor(R.color.grey));
                        break;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        chooseCamera.setBackgroundColor(getResources().getColor(R.color.notSoDark));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        // RELEASED
                        chooseCamera.setBackgroundColor(getResources().getColor(R.color.notSoDark));
                        break;
                }
                return false;
            }
        });

        chooseLibrary.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        chooseLibrary.setBackgroundColor(getResources().getColor(R.color.grey));
                        break;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        chooseLibrary.setBackgroundColor(getResources().getColor(R.color.notSoDark));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        // RELEASED
                        chooseLibrary.setBackgroundColor(getResources().getColor(R.color.notSoDark));
                        break;
                }
                return false;
            }
        });

        chooseDrive.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        chooseDrive.setBackgroundColor(getResources().getColor(R.color.grey));
                        break;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        chooseDrive.setBackgroundColor(getResources().getColor(R.color.notSoDark));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        // RELEASED
                        chooseDrive.setBackgroundColor(getResources().getColor(R.color.notSoDark));
                        break;
                }
                return false;
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data!=null){
            Picasso.with(getContext()).load(data.getData()).into(imageHolder);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
