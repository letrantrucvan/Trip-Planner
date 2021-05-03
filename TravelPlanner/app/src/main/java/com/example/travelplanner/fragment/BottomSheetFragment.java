package com.example.travelplanner.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.travelplanner.QrCodeActivity;
import com.example.travelplanner.R;
import com.example.travelplanner.controller.TourDetailsActivity;
import com.example.travelplanner.controller.EditTourActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BottomSheetFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "key";
    private static final String TAG = "Van BottomSheetDialog";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private TextView txtShareClick;
    private TextView editTour;
    private String id;

    public BottomSheetFragment(String tourid) {
        this.id =  tourid;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment BottomSheetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BottomSheetFragment newInstance(String param1) {
        BottomSheetFragment fragment = new BottomSheetFragment(param1);
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
        View v = inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
        txtShareClick = (TextView) v.findViewById(R.id.txtShare);
        editTour = (TextView) v.findViewById(R.id.editTour);
        if(TourDetailsActivity.cur_Tour.getAuthor_id().equals(FirebaseAuth.getInstance().getUid())) editTour.setVisibility(View.VISIBLE);

        txtShareClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("KEY" + id);
                Intent i = new Intent (getActivity(),QrCodeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("key", id);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        //hiệu ứng click
        txtShareClick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        txtShareClick.setBackgroundColor(getResources().getColor(R.color.grey));
                        break;
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        txtShareClick.setBackgroundColor(getResources().getColor(R.color.notSoDark));
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        // RELEASED
                        txtShareClick.setBackgroundColor(getResources().getColor(R.color.notSoDark));
                        break;
                }
                return false;
            }
        });

        editTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), EditTourActivity.class);
                i.putExtra("Key", TourDetailsActivity.cur_Tour.getTour_id());
                startActivity(i);
            }
        });
        return v;
    }
}