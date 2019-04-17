package Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import Controller.Common;
import Fragment.WhtsNewFragment;
import Models.SubcategoryClass;
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
                SubcategoryClass sub = Common.categoryClass.getSubcategories().get(position);
                WhtsNewFragment tab1 = new WhtsNewFragment();
                tab1.subcategoryId = sub.getId();
                return tab1;
        }

        @Override
        public int getCount() {
                return mNoOfTabs;

        }
}

