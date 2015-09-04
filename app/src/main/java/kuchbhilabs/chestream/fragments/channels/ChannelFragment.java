package kuchbhilabs.chestream.fragments.channels;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.ParseAnalytics;

import java.util.HashMap;
import java.util.Map;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.comments.CommentsFragment;

/**
 * Created by naman on 20/08/15.
 */
public class ChannelFragment extends Fragment {

    Activity activity;
    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;
    Toolbar toolbar;

    long startTime = 0;
    public  static String TAG = "ChannelFragment";
    ChannelModel channel;

    public static ChannelFragment newInstance(ChannelModel channelModel) {
        ChannelFragment fragment = new ChannelFragment();
        Bundle args = new Bundle();
        args.putSerializable("channel",channelModel);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_channels, null);
        activity=getActivity();


        channel= (ChannelModel)getArguments().getSerializable("channel");

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(channel.name);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPagerAdapter = new PagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(1);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



        CommentsFragment.setUpComments(channel.id);

        getChildFragmentManager().beginTransaction().replace(R.id.video_container,new ChannelVideoFragment().newInstance(channel.videoIds)).commit();
        return rootView;

    }

    public class PagerAdapter extends FragmentPagerAdapter {

        //Note: Change Fragment to WeakReference<Fragment> in case of more than 3 fragments
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        private final String[] TITLES = { "About","Discuss","Videos" };

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
                    fragment = new AboutChannelFragment().newInstance(channel);
                    break;
                case 1:
                    fragment = new CommentsFragment().newInstance(channel);
                    break;
                case 2:
                    fragment = new ChannelQueueFragment().newInstance(channel.videoIds);
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

    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        Map<String, String> dimensions = new HashMap<String, String>();
        int elapsedTime = (int) getElapsedTimeSecs();
        String time = " - ";
        if(elapsedTime<15){
            time = "0-15";
        }
        else if(elapsedTime>=15 && elapsedTime<30){
            time = "15-30";
        }
        else if(elapsedTime>=30 && elapsedTime<60){
            time = "30-60";
        }
        else if(elapsedTime>=60 && elapsedTime<90){
            time = "60-90";
        }
        else if(elapsedTime>=90 && elapsedTime<120){
            time = "90-120";
        }
        else if(elapsedTime>=120 && elapsedTime<150){
            time = "120-150";
        }
        else if(elapsedTime>=150 && elapsedTime<180){
            time = "150-180";
        }
        else if(elapsedTime>=180 && elapsedTime<210){
            time = "180-210";
        }
        else if(elapsedTime>=210 && elapsedTime<240){
            time = "210-240";
        }
        else {
            time = ">240";
        }
        dimensions.put("time", time);
        dimensions.put("channelName", channel.name);
        ParseAnalytics.trackEventInBackground(TAG, dimensions);
    }

    public long getElapsedTimeSecs() {
        long elapsed = 0;
        elapsed = ((System.currentTimeMillis() - startTime) / 1000) % 60;
        return elapsed;
    }


}
