package com.example.travelplanner.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.travelplanner.R;
import com.example.travelplanner.activity.SearchActivity;
import com.example.travelplanner.model.Tour;

import java.util.ArrayList;

import eightbitlab.com.blurview.BlurView;

public class CategoryFragment extends Fragment {
    private static final String TAG = "Thu CategoryFragment";
    private ListView list_category;
    private ArrayList<Tour> tours;
    private BlurView blurView;
    private SearchActivity searchActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG,"onCreateView");

        View view = inflater.inflate(R.layout.list_category, container, false);
        list_category = view.findViewById(R.id.list_category);

        ImageView close_ic = view.findViewById(R.id.close);
        close_ic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
        searchActivity = (SearchActivity) getActivity();
        String[] category = getResources().getStringArray(R.array.spinnerCategory);

        list_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchActivity.filter(category[position]) ;
            }
        });
        return view;
    }
}
