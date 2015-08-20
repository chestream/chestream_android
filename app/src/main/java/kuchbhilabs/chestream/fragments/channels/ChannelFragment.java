package kuchbhilabs.chestream.fragments.channels;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.comments.CommentsFragment;
import kuchbhilabs.chestream.fragments.queue.QueueFragment;

/**
 * Created by naman on 20/08/15.
 */
public class ChannelFragment extends Fragment {

    Activity activity;
    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;

    public static ChannelFragment newInstance(long id) {
        ChannelFragment fragment = new ChannelFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_channels, null);
        activity=getActivity();

        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new PagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(1);
        mViewPager.setOffscreenPageLimit(2);


        return rootView;

    }

    public class PagerAdapter extends FragmentPagerAdapter {

        //Note: Change Fragment to WeakReference<Fragment> in case of more than 3 fragments
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        private final String[] TITLES = { "About","Videos","Chat" };

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment=null;
            switch (position) {

                case 0:
                    fragment = new QueueFragment();
                    break;
                case 1:
                    fragment = new QueueFragment();
                    break;
                case 2:
                    fragment = new CommentsFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

}
