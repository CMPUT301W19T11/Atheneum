package com.example.atheneum.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;

public class AddBookFragment extends Fragment {
    private View view;
    private MainActivity mainActivity;
    private Context context;


    public AddBookFragment() {
        // required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_add_edit_book, container, false);

        this.context = getContext();
        mainActivity = (MainActivity) getActivity();
        // set action bar title
        mainActivity.setActionBarTitle(context.getResources().getString(R.string.add_book_prompt));

        return this.view;
    }

}
