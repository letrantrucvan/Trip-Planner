package com.example.travelplanner.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.travelplanner.adapter.SearchPlaceResultAdapter;
import com.example.travelplanner.model.MyPlace;
import com.example.travelplanner.model.URLRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import com.example.travelplanner.R;


public class SearchPlaceTable extends AppCompatActivity {
    private static final String TAG = "Thu SearchPlaceTable";


    public static final String EXTRA_TEXT_PLACEID_DETAIL = "EXTRA_TEXT_PLACEID_DETAIL";
    public static final String EXTRA_TEXT_NAME = "EXTRA_TEXT_NAME";
    public static final String EXTRA_TEXT_IMG = "EXTRA_TEXT_IMG";
    public static final String EXTRA_TEXT_ADDRESS = "EXTRA_TEXT_ADDRESS";
    public static final String EXTRA_TEXT_RATING = "EXTRA_TEXT_RATING";

    private RequestQueue requestQueue;

    private String keyword;
    private String category;
    private String distance;
    private String from;
    private String otherLoc;
    private String location;
    private boolean LOADING;

    private TextView urlView;
    private TextView responseName;
    private ViewGroup progressView;

    private RecyclerView recyclerView;
    private SearchPlaceResultAdapter re_adapter;

    private ImageButton next_btn;
    private ImageButton prev_btn;
    private TextView no_place_lable;

    private ArrayList<ArrayList<MyPlace>> myPlaceBook;
    private ArrayList<MyPlace> arrayList;
    private int curPage;
    private String next_page_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate");

        LOADING = false;
        showProgressingView();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_table);
        setTitle("Search results");

        Intent intent = getIntent();
        keyword = intent.getStringExtra(SearchActivity.EXTRA_TEXT_KEYWORD);

        requestQueue = Volley.newRequestQueue(this);

        String url = URLRequest.getTextSearchRequest(keyword);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
//                        Toast.makeText(getApplicationContext(), "Get Json!", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getApplicationContext(), "response:!"+response.toString(), Toast.LENGTH_SHORT).show();
                            Log.i("Thu res", response.toString());
                            Boolean noData_flag = true;
                            // For table contents
                            try {
                                JSONArray jsonArray = response.getJSONArray("results");

                               // Log.d("Thu debug", jsonArray.toString());

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject result = jsonArray.getJSONObject(i);
                                    Log.i("Thu debug", "i: "+ i);

                                    //Debug no photo
                                    String rating = "0";
                                    if (result.has("rating"))
                                        rating = result.getString("rating");
                                    try {
                                        String photo_ref = result.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                                        Log.i(TAG, "photo_ref !=null" + photo_ref);


                                        arrayList.add(new MyPlace(
                                                result.getString("place_id"),
                                                result.getString("name"),
                                                result.getString("formatted_address"),
                                                photo_ref,
                                                rating
                                        ));
                                    }

                                    catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                        arrayList.add(new MyPlace(
                                                result.getString("place_id"),
                                                result.getString("name"),
                                                result.getString("formatted_address")
                                        ));
                                    }
                                }

                                if (arrayList.size() == 0) {
                                    noData_flag = true;
                                } else {
                                    noData_flag = false;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                //error
                            }

                            if (noData_flag) {
                                no_place_lable.setVisibility(View.VISIBLE);
                            } else {
                                no_place_lable.setVisibility(View.GONE);
                            }

                            // recycler
                            Log.i(TAG, arrayList.toString());
             //               Log.i(TAG, arrayList.get(0).getPlace_id());
                            re_adapter = new SearchPlaceResultAdapter(SearchPlaceTable.this, arrayList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(SearchPlaceTable.this));
                            recyclerView.setAdapter(re_adapter);


                            //copy it to book
                            myPlaceBook.add(arrayList);
//                        Toast.makeText(getApplicationContext(), "myPlaceBook size:" + myPlaceBook.size(), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getApplicationContext(), "myPlaceBook's 1st item in 1st page:" + myPlaceBook.get(0).get(0).getName(), Toast.LENGTH_SHORT).show();

                            // For next page
                            try {
                                String next_p = response.getString("next_page_token").toString();
//                            Toast.makeText(getApplicationContext(), next_p, Toast.LENGTH_SHORT).show();
//                            Toast.makeText(getApplicationContext(), next_p.toString(), Toast.LENGTH_SHORT).show();
                                next_page_token = next_p;
                                next_btn.setEnabled(true);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                next_btn.setEnabled(false);
                            }

                            hideProgressingView();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SearchPlaceTable.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
                    Log.e("Thu error", error.getMessage());
                    Log.e("Thu: ", error.toString());
                    hideProgressingView();
                    no_place_lable.setVisibility(View.VISIBLE);
                }

                //Fav Handle

            });

            requestQueue.add(jsonObjectRequest);

            recyclerView = findViewById(R.id.recyclerView);

            next_btn = findViewById(R.id.next_button);
            prev_btn = findViewById(R.id.prev_button);
            no_place_lable = (TextView) findViewById(R.id.no_place_label);

            myPlaceBook = new ArrayList<>();
            arrayList = new ArrayList<>();
            curPage = 0;
            next_page_token = "";


            LOADING = true;

            // Button Handler
            prev_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    curPage--;
                    buttonHandler();

                    // recycler
                    re_adapter = new SearchPlaceResultAdapter(SearchPlaceTable.this, myPlaceBook.get(curPage));
                    recyclerView.setLayoutManager(new LinearLayoutManager(SearchPlaceTable.this));
                    recyclerView.setAdapter(re_adapter);

                }
            });
            next_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    curPage++;
                    buttonHandler();

                    if (curPage <= myPlaceBook.size() - 1) {
                        re_adapter = new SearchPlaceResultAdapter(SearchPlaceTable.this, myPlaceBook.get(curPage));
                        recyclerView.setLayoutManager(new LinearLayoutManager(SearchPlaceTable.this));
                        recyclerView.setAdapter(re_adapter);

                    } else {
                        saveNextPageToMySerchBook(next_page_token);
                    }
                }
            });
        }
    private void saveNextPageToMySerchBook(String next_p) {
        LOADING = false;
        showProgressingView();
        String url = URLRequest.getTextSearchRequestPageToken(next_p);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<MyPlace> tempList = new ArrayList<>();
                        Log.d("Thu res", response.toString());

                        // For table contents
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");

                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject result = jsonArray.getJSONObject(i);
                                try {
                                    String photo_ref = result.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                                    tempList.add(new MyPlace(
                                            result.getString("place_id"),
                                            result.getString("name"),
                                            result.getString("formatted_address"),
                                            photo_ref,
                                            result.getString("rating")
                                    ));
                                }

                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                    tempList.add(new MyPlace(
                                            result.getString("place_id"),
                                            result.getString("name"),
                                            result.getString("formatted_address")
                                    ));
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        myPlaceBook.add(tempList);
                        re_adapter = new SearchPlaceResultAdapter(SearchPlaceTable.this, myPlaceBook.get(curPage));
                        recyclerView.setLayoutManager(new LinearLayoutManager(SearchPlaceTable.this));
                        recyclerView.setAdapter(re_adapter);

                        // For next page
                        try {
                            String next_p = response.getString("next_page_token").toString();
                            next_page_token = next_p;
                            next_btn.setEnabled(true);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            next_btn.setEnabled(false);
                        }

                        hideProgressingView();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Thu error", error.getMessage());

                hideProgressingView();
                Toast.makeText(getApplicationContext(), "Nothing found!", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }

    private void buttonHandler(){
        if(curPage == 0){
            prev_btn.setEnabled(false);
        }else{
            prev_btn.setEnabled(true);
        }

        if(curPage >= myPlaceBook.size()-1){
            next_btn.setEnabled(false);
        }else{
            next_btn.setEnabled(true);
        }
    }

    private void showProgressingView() {

        if (!LOADING) {
            LOADING = true;
            progressView = (ViewGroup) getLayoutInflater().inflate(R.layout.loading_spinner, null);
            View v = this.findViewById(android.R.id.content).getRootView();
            ViewGroup viewGroup = (ViewGroup) v;
            viewGroup.addView(progressView);
        }
    }

    private void hideProgressingView() {
        View v = this.findViewById(android.R.id.content).getRootView();
        ViewGroup viewGroup = (ViewGroup) v;
        viewGroup.removeView(progressView);
        LOADING = false;
    }


    /** Called when the activity is about to become visible. */
    @Override
    protected void onStart() {
        super.onStart();
//        Toast.makeText(getApplicationContext(),"The onStart() event", Toast.LENGTH_SHORT).show();
    }

    /** Called when the activity has become visible. */
    @Override
    protected void onResume() {
        super.onResume();

        if(myPlaceBook.size() != 0){
            re_adapter.notifyDataSetChanged();
        }

//        Toast.makeText(getApplicationContext(), "The onResume() event", Toast.LENGTH_SHORT).show();

    }

    /** Called when another activity is taking focus. */
    @Override
    protected void onPause() {
        super.onPause();
//        Toast.makeText(getApplicationContext(), "The onPause() event", Toast.LENGTH_SHORT).show();
    }

    /** Called when the activity is no longer visible. */
    @Override
    protected void onStop() {
        super.onStop();
//        Toast.makeText(getApplicationContext(), "The onStop() event", Toast.LENGTH_SHORT).show();
    }


}