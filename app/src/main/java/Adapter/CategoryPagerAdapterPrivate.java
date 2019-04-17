package Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import Fragment.ClassNewsFragment;
import Fragment.GroupNewsFragment;
import Fragment.SubjectNewsFragment;
import xyz.santeri.wvp.WrappingFragmentStatePagerAdapter;


public class CategoryPagerAdapterPrivate extends WrappingFragmentStatePagerAdapter {

    int mNoOfTabs;

    public CategoryPagerAdapterPrivate(FragmentManager fm, int NumberOfTabs)

    {
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                GroupNewsFragment tab1 = new GroupNewsFragment();
                return tab1;
            case 1:
                ClassNewsFragment tab2 = new ClassNewsFragment();
                return tab2;
            case 2:
                SubjectNewsFragment tab3 = new SubjectNewsFragment();
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

