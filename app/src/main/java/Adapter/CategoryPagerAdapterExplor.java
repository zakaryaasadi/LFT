package Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import Fragment.EventsFragment;
import Fragment.VotingFragment;
import Fragment.WhtsNewFragment;
import xyz.santeri.wvp.WrappingFragmentStatePagerAdapter;


public class CategoryPagerAdapterExplor extends WrappingFragmentStatePagerAdapter {

        int mNoOfTabs;

        public CategoryPagerAdapterExplor(FragmentManager fm, int NumberOfTabs)

        {
                super(fm);
                this.mNoOfTabs = NumberOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
                switch (position) {

                        case 0:
                                WhtsNewFragment tab1 = new WhtsNewFragment();
                                return tab1;
                        case 1:
                                VotingFragment tab2 = new VotingFragment();
                                return tab2;
                        case 2:
                                EventsFragment tab3 = new EventsFragment();
                                return tab3;


                        default:
                                return null;

                }
        }

        @Override
        public int getCount() {
                return mNoOfTabs;

        }
}

