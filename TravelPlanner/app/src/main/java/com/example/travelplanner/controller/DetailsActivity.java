package com.example.travelplanner.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.travelplanner.R;

public class DetailsActivity extends AppCompatActivity {

    private Button readMore;
    private TextView tourDescription;
    private boolean isOpenDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        isOpenDescription = false;
        readMore = (Button) findViewById(R.id.detail_btnReadMore);
        tourDescription = (TextView) findViewById(R.id.detail_TourDescription);

        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpenDescription){
                    tourDescription.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    tourDescription.requestLayout();
                    readMore.setText("- Thu gọn");
                    isOpenDescription = true;
                }
                else {
                    tourDescription.getLayoutParams().height = 80;
                    tourDescription.requestLayout();
                    readMore.setText("+ Xem thêm");
                    isOpenDescription = false;
                }
            }
        });
    }
}