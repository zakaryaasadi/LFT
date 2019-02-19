package Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import Fragment.MessageInboxFragment;
import Fragment.MessageSentFragment;

import xyz.santeri.wvp.WrappingFragmentStatePagerAdapter;


public class MessagePagerAdapter extends WrappingFragmentStatePagerAdapter {

        int mNoOfTabs;

        public MessagePagerAdapter(FragmentManager fm, int NumberOfTabs)

        {
                super(fm);
                this.mNoOfTabs = NumberOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
                switch (position) {

                        case 0:
                                MessageInboxFragment tab1 = new MessageInboxFragment();
                                return tab1;
                        case 1:
                                MessageSentFragment tab2 = new MessageSentFragment();
                                return tab2;

                        default:
                                return null;

                }
        }

        @Override
        public int getCount() {
                return mNoOfTabs;

        }
}

