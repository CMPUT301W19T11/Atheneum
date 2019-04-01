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
import com.example.atheneum.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * The Fragment for showing sliding windows for BorrowerRequestFragment and BorrowerBookFragment
 *
 *  See: https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
 *  See: https://medium.com/android-grid/how-to-use-firebaserecycleradpater-with-latest-firebase-dependencies-in-android-aff7a33adb8b
 *  See: https://github.com/firebase/FirebaseUI-Android/blob/master/database/README.md#using-the-firebaserecycleradapter
 *  See: https://stackoverflow.com/questions/41413150/fragment-tabs-inside-fragment/41656303#41656303
 */
public class BorrowerFragment extends Fragment {
    private View view;
    private Context context;
    private ViewPager mPager;
    private MainActivity mainActivity = null;


    /**
     * Instantiates a new Borrower fragment.
     */
    public BorrowerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_borrower, container, false);

        this.context = getContext();

        if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();
            // set action bar title
            mainActivity.setActionBarTitle(context.getResources().getString(R.string.borrower_page_title));
        }

        mPager = (ViewPager) this.view.findViewById(R.id.borrowerViewPager);
        setUpViewPager(mPager);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.borrowerTabLayout);
        tabs.setupWithViewPager(mPager);

        return this.view;
    }

    private void setUpViewPager(ViewPager viewPager){
        TabsAdapter adapter = new TabsAdapter(getChildFragmentManager());
        adapter.addFragment(new BorrowerRequestsFragment(), "Borrower Requests");
        adapter.addFragment(new BorrowedBooksFragment(), "Borrowed Books");
        mPager.setAdapter(adapter);

    }


    private class TabsAdapter extends FragmentStatePagerAdapter{
        private final List<Fragment>  mFragmentList = new ArrayList<>();
        private final List<String> mFragmnetTitleList = new ArrayList<>();

        /**
         * Instantiates a new Tabs adapter.
         *
         * @param manager the manager
         */
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

        /**
         * Add fragment.
         *
         * @param fragment the fragment
         * @param title    the title
         */
        public void addFragment(Fragment fragment, String  title){
            mFragmentList.add(fragment);
            mFragmnetTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return mFragmnetTitleList.get(position);
        }
    }
}
