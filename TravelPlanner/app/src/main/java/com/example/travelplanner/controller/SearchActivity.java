package com.example.travelplanner.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.travelplanner.R;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "Thu SearchActivity";

    private TextView mNameView;
 
    public static final String EXTRA_TEXT_KEYWORD = "anhthubui.app.search_place.EXTRA_TEXT_KEYWORD";

    private Button search_button;
    private TextView keyword_warning;
    private TextView otherLoc_warning;
    private RadioGroup radio_group;
    private AutoCompleteTextView otherLoc_input;
    private SearchView keyword_input;
    private Spinner category_spinner;
    private EditText distance_input;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreateView");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_fragment);

        keyword_input = findViewById(R.id.keyword_input);

        radio_group =  findViewById(R.id.radio_group);
        keyword_warning =  findViewById(R.id.keyword_warning);

        keyword_input.setSubmitButtonEnabled(true);

        keyword_input.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchView keyword_input = findViewById(R.id.keyword_input);
                String keyword = keyword_input.getQuery().toString();
                if(keyword.trim().length() == 0){
                    keyword_warning.setVisibility(View.VISIBLE);
                    Toast.makeText(SearchActivity.this,"Vui lòng nhập từ khóa tìm kiếm",Toast.LENGTH_SHORT).show();
                }else {
                    keyword_warning.setVisibility(View.GONE);

                    Intent intent = new Intent(SearchActivity.this, SearchPlaceTable.class);

                    intent.putExtra(EXTRA_TEXT_KEYWORD, keyword);
                    startActivity(intent);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }
}
