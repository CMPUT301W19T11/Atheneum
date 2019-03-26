/*
 * Copyright <YEAR> <COPYRIGHT HOLDER>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.atheneum.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.atheneum.R;

import java.util.ArrayList;
import java.util.List;

public class BorrowerFragment extends Fragment {
    private View view;
    private Context context;
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;

    private  static  final int NUM_PAGES = 2;

//    private OnFragmentInteractionListener mListener;

    public BorrowerFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_borrower, container, false);

        this.context = getContext();

        mPager = (ViewPager) this.view.findViewById(R.id.borrowerViewPager);
        setUpViewPager(mPager);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.borrowerTabLayout);
        tabs.setupWithViewPager(mPager);
//        pagerAdapter  = new TabsAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        return this.view;
    }

    private void setUpViewPager(ViewPager viewPager){
        TabsAdapter adapter = new TabsAdapter(getChildFragmentManager());
        adapter.addFragment(new BorrowedBooksFragment(), "Borrowed Books");
        adapter.addFragment(new BorrowerRequestsFragment(), "Borrower Requests");
        mPager.setAdapter(adapter);

    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

    private class TabsAdapter extends FragmentStatePagerAdapter{
        private final List<Fragment>  mFragmentList = new ArrayList<>();
        private final List<String> mFragmnetTitleList = new ArrayList<>();

        public TabsAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position){
            return mFragmentList.get(position);
        }

        @Override
        public int getCount(){
            return mFragmentList.size();
        }
        public void addFragment(Fragment fragment, String  title){
            mFragmentList.add(fragment);
            mFragmnetTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return mFragmnetTitleList.get(position);
        }
//        int mNumOfTabs;
//        public TabsAdapter(FragmentManager fm, int NoofTabs){
//            super(fm);
//            this.mNumOfTabs = NoofTabs;
//        }
//        @Override
//        public int getCount() {
//            return mNumOfTabs;
//        }
//        @Override
//        public Fragment getItem(int position){
//            switch (position){
//                case 0:
//                    BorrowedBooksFragment borrowedBooksFragment = new BorrowedBooksFragment();
//                    return borrowedBooksFragment;
//                case 1:
//                    BorrowerRequestsFragment borrowerRequestsFragment = new BorrowerRequestsFragment();
//                    return borrowerRequestsFragment;
//
//                default:
//                    return null;
//            }
//        }
    }
}
