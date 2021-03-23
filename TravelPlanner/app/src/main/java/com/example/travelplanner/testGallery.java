package com.example.travelplanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class testGallery extends AppCompatActivity {

    private static int PICK_IMAGE_REQUEST = 1;
    private Button mBtnChooseImage;
    private Button mBtnUpload;
    private TextView mTvShowUpload;
    private EditText mEdtFileName;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_gallery);
        mBtnChooseImage = (Button) findViewById(R.id.button_choose_image);
        mBtnUpload = (Button) findViewById(R.id.button_upload);
        mEdtFileName = (EditText) findViewById(R.id.edit_text_file_name);
        mImageView = (ImageView) findViewById(R.id.image_view_gallery);

        mBtnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void openFileChooser(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.getData() != null){
            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).into(mImageView);

        }
    }
}