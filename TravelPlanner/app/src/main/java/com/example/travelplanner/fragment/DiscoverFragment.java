package com.example.travelplanner.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.travelplanner.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment {
    private RecyclerView test;
    private View discover;
    private ArrayList<horizontoModel> testz;
    horizontoAdapter for_test;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiscoverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoverFragment newInstance(String param1, String param2) {
        DiscoverFragment fragment = new DiscoverFragment();
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
        discover = inflater.inflate(R.layout.fragment_discover, container, false);

        Integer[] logo = {R.drawable.tokyo, R.drawable.okyto, R.drawable.seoul, R.drawable.rome, R.drawable.paris, R.drawable.newyork};
        String[] name = {"Tokyo", "Okyto", "Seoul", "Rome", "Paris", "New York"};



        test = (RecyclerView) discover.findViewById(R.id.spring);
        test.setLayoutManager(new LinearLayoutManager(getContext()));

        testz = new ArrayList<>();
        for (int i = 0; i < logo.length; i++){
            horizontoModel temp = new horizontoModel(name[i],logo[i]);
            testz.add(temp);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(
            getActivity(),LinearLayoutManager.HORIZONTAL,false
        );

        test.setLayoutManager((layoutManager));
        test.setItemAnimator(new DefaultItemAnimator());


        for_test = new horizontoAdapter(getContext(),testz);
        test.setAdapter(for_test);

        return  discover;
    }

    private class horizontoModel {
        String name;
        Integer logo;

        horizontoModel(String a, Integer b){
            name = a;
            logo = b;
        }

        public Integer getLogo() {
            return logo;
        }

        public String getName() {
            return name;
        }
    }

    private class horizontoAdapter extends RecyclerView.Adapter<horizontoAdapter.ViewHolder> {
        ArrayList<horizontoModel> models;
        Context context;

        public horizontoAdapter(Context a, ArrayList<horizontoModel> b){
            models = b;
            context = a;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.small_trip,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.image.setImageResource(models.get(position).getLogo());

            holder.text.setText(models.get(position).getName());
        }

        @Override
        public int getItemCount() {

            return models.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView text;
            public ViewHolder(@NonNull View itemView) {

                super(itemView);
                image = itemView.findViewById(R.id.logo);
                text = itemView.findViewById(R.id.trip_name);
            }
        }
    }
}