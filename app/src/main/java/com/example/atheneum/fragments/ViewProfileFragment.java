package com.example.atheneum.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.atheneum.R;
import com.example.atheneum.activities.MainActivity;
import com.example.atheneum.controllers.PictureController;
import com.example.atheneum.models.User;

import org.w3c.dom.Text;

/**
 * The fragment for users to view their own profiles
 * See: https://stackoverflow.com/questions/39959747/how-to-convert-string-into-image-android-studio
 * See: https://stackoverflow.com/questions/9931993/passing-an-object-from-an-activity-to-a-fragment
 */
public class ViewProfileFragment extends Fragment {
    private View view;
    private MainActivity mainActivity = null;
    private Context context;
    private ImageView profilePicture;
    private PictureController pictureController;
    private Bitmap bitmapPhoto;

    private User user;

    public ViewProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_profile, container, false);
        this.context = getContext();

        Intent i = getActivity().getIntent();

        user = (User) i.getSerializableExtra("user");

        profilePicture = view.findViewById(R.id.user_profile_pic);
        try {
            String userPic = user.getPhotos().get(0);
            bitmapPhoto = StringToBitMap(userPic);
            profilePicture.setImageBitmap(bitmapPhoto);
        } catch (Exception ignore) {

        }

        TextView username = view.findViewById(R.id.username);
        TextView phone = view.findViewById(R.id.phone);
        TextView borrower_rating = view.findViewById(R.id.borrower);
        TextView owner_rating = view.findViewById(R.id.owner);

        username.setText("User Name: " + user.getUserName());
        phone.setText("Phone Number: " + user.getPhoneNumber());

        borrower_rating.setText(Double.toString(user.getBorrowerRate()));
        owner_rating.setText(Double.toString(user.getOwnerRate()));

        if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();
            // set action bar title
            mainActivity.setActionBarTitle(context.getResources().getString(R.string.profile_title));
        }

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

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}
