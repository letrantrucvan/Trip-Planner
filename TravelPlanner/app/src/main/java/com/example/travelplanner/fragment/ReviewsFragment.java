package com.example.travelplanner.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.example.travelplanner.adapter.ListAdapterReview;
import com.example.travelplanner.model.Reviews;
import com.example.travelplanner.model.URLRequest;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import com.example.travelplanner.R;

public class ReviewsFragment extends Fragment {
    private static final String TAG = "Thu ReviewsFragment";

    private RequestQueue requestQueue;

    static public String cur_placeID;
    private boolean LOADING;

    private Spinner spinner_order;
    private TextView no_review_label;
    private ListView ggReview_listView;
    private ViewGroup progressView;

    private ArrayList<Reviews> ggReview_list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG,"onCreateView");

        final View view = inflater.inflate(R.layout.reviews_fragment, container, false);
        ViewGroup rootView = view.findViewById(R.id.root);
        BlurView blurView = view.findViewById(R.id.blurReviews);
        ImageView place_photo = view.findViewById(R.id.photo);
        //String urlPhoto = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1200&photoreference=" + PlaceDetailFragment.image_reference + "&key=" + getResources().getString(R.string.google_maps_key);
        String urlPhoto = URLRequest.getPhotoRequest(PlaceDetailFragment.image_reference);

        Picasso.with(getActivity()).load(urlPhoto).into(place_photo);
        View decorView = ((Activity) getContext()).getWindow().getDecorView();

        Drawable windowBackground = decorView.getBackground();
        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(getActivity()))
                .setBlurRadius(10f)
                .setBlurAutoUpdate(true)
                .setHasFixedTransformationMatrix(false);

        LOADING = false;
        showProgressingView();

        ggReview_list = PlaceDetailFragment.reviewsList;

        ggReview_listView = (ListView) view.findViewById(R.id.ggReview_list);
        no_review_label = (TextView) view.findViewById(R.id.no_review_label);


        // Spinner2 for orders
        spinner_order = (Spinner) view.findViewById(R.id.review_order);
        ArrayAdapter<CharSequence> spinner_adapter2 = ArrayAdapter.createFromResource(getActivity(), R.array.spinnerOrder, android.R.layout.simple_dropdown_item_1line);
        spinner_adapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner_order.setAdapter(spinner_adapter2);
        spinner_order.setSelection(0);



        spinner_order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                displayReview(view, ggReview_list, ggReview_listView, spinner_order.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });



        clickableReviewCell(ggReview_listView);
        hideProgressingView();
        return view;
    }

    private void clickableReviewCell(ListView lv) {
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Reviews entry= (Reviews) parent.getAdapter().getItem(position);

//                        Toast.makeText(getActivity(), "Cilcked " + entry.getAuthor_name() + "'s review", Toast.LENGTH_SHORT).show();
                        String url = entry.getAuthor_url();
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse(url.toString()));
                        startActivity(intent);
                    }
                }
        );
    }

    private void displayReview(View view, ArrayList<Reviews> list, ListView review_list, String order) {

        ArrayList<Reviews> reOrder_list = new ArrayList<>();
        for(Reviews r:list){
            reOrder_list.add(r);
        }

        if (order.equals("Cao nhất")){
            Collections.sort(reOrder_list, Reviews.HiRatingComparator);
//            Toast.makeText(getActivity().getApplicationContext(), "Click Highest Rating", Toast.LENGTH_SHORT).show();
        }else if (order.equals("Thấp nhất")){
            Collections.sort(reOrder_list, Reviews.LowRatingComparator);
//            Toast.makeText(getActivity().getApplicationContext(), "Click Lowest Rating", Toast.LENGTH_SHORT).show();
        }else if (order.equals("Mới nhất")){
            Collections.sort(reOrder_list, Reviews.RecentComparator);
//            Toast.makeText(getActivity().getApplicationContext(), "Click Most Recent", Toast.LENGTH_SHORT).show();
        }else if (order.equals("Cũ nhất")){
            Collections.sort(reOrder_list, Reviews.OldComparator);
//            Toast.makeText(getActivity().getApplicationContext(), "Click Least Recent", Toast.LENGTH_SHORT).show();
        }else{
            // Default Order :: do nothing
//            Toast.makeText(getActivity().getApplicationContext(), "WHAT!!!", Toast.LENGTH_SHORT).show();
        }

        // Fill up the list
        ListAdapterReview adapter = new ListAdapterReview(
                getActivity().getApplicationContext(), R.layout.reviews_fragment, reOrder_list
        );
        review_list.setAdapter(adapter);
//        Toast.makeText(getActivity().getApplicationContext(), "REORDERED!", Toast.LENGTH_SHORT).show();

    }

    private void showProgressingView() {

        if (!LOADING) {
            LOADING = true;
            progressView = (ViewGroup) getLayoutInflater().inflate(R.layout.loading_spinner, null);
            View v = getActivity().findViewById(android.R.id.content).getRootView();
            ViewGroup viewGroup = (ViewGroup) v;
            viewGroup.addView(progressView);
        }
    }

    private void hideProgressingView() {
        View v = getActivity().findViewById(android.R.id.content).getRootView();
        ViewGroup viewGroup = (ViewGroup) v;
        viewGroup.removeView(progressView);
        LOADING = false;
    }

}
