package com.example.atheneum.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;

public class newRequest extends Fragment {
    private View view;
    private MainActivity mainActivity = null;
    private Context context;

    private Button savebtn;

    public newRequest() {
        // required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.new_request, container, false);

        this.context = getContext();

        if (getActivity() instanceof  MainActivity) {
            mainActivity = (MainActivity) getActivity();
            // set action bar title
            mainActivity.setActionBarTitle(context.getResources().getString(R.string.borrower_page_title));

        }

        final FragmentManager fragmentManager = getFragmentManager();
        savebtn = this.view.findViewById(R.id.save);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentManager.beginTransaction().replace(R.id.content_frame, new BorrowerPageFragment()).commit();
            }
        });

        return this.view;
    }

}
