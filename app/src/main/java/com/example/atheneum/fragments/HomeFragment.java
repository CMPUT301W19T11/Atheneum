package com.example.atheneum.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;

/**
 * A simple {@link Fragment} subclass that displays the home page.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // set title
        ((MainActivity) getActivity()).setActionBarTitle(getContext().getResources().getString(R.string.app_name));

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

}
