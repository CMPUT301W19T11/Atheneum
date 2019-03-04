package com.example.atheneum.fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewProfileFragment extends Fragment {
    private View view;

    public ViewProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_profile, container, false);

        FloatingActionButton triggerEditUserProfile = view.findViewById(R.id.trigger_edit_user_profile);
        triggerEditUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    EditProfileFragment editProfileFragment = new EditProfileFragment();
                    editProfileFragment.setEditProfileCompleteListener(new EditProfileFragment.OnEditProfileCompleteListener() {
                        private void startViewProfileFragment(EditProfileFragment fragment) {
                            fragment.getActivity()
                                    .getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame, new ViewProfileFragment())
                                    .commit();
                        }

                        @Override
                        public void onSuccess(EditProfileFragment fragment) {
                            startViewProfileFragment(fragment);
                        }

                        @Override
                        public void onFailure(EditProfileFragment fragment) {
                            startViewProfileFragment(fragment);
                        }
                    });
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, editProfileFragment).commit();
                }
            }
        });

        return view;
    }

}
