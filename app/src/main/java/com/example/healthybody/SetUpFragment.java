package com.example.healthybody;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetUpFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private NumberPicker weight, height;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SetUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetUpFragment newInstance(String param1, String param2) {
        SetUpFragment fragment = new SetUpFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_up, container, false);
        weight = view.findViewById(R.id.weight_picker);
        height = view.findViewById(R.id.height_picker);

        Context thisContext = container.getContext();

        weight.setMinValue(30);
        weight.setMaxValue(200);
        height.setMinValue(140);
        height.setMaxValue(200);

        height.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                SharedPreferences sharedPref = thisContext.getSharedPreferences("setUp", 0);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.putString("height", String.valueOf(newVal));
                editor.apply();
            }
        });

        weight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                SharedPreferences sharedPref = thisContext.getSharedPreferences("weight", 0);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.putString("weight", String.valueOf(newVal));
                editor.apply();
            }
        });
        return view;
    }
}