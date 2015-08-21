package kuchbhilabs.chestream.fragments.channels;

import android.app.Activity;
import android.graphics.Bitmap;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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
    Toolbar toolbar;
    ImageView preview;


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

        ChannelModel channel= (ChannelModel)getArguments().getSerializable("channel");

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        preview=(ImageView) rootView.findViewById(R.id.preview);
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


        ImageLoader.getInstance().displayImage("http://104.131.207.33/chestream_raw/11785415_805808706184034_1008859156_n/thumbnail.png", preview,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisk(true)
                        .resetViewBeforeLoading(true)
                        .displayer(new FadeInBitmapDisplayer(400))
                        .build(),new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    }
                });

        CommentsFragment.setUpComments();

        getChildFragmentManager().beginTransaction().replace(R.id.video_container,new ChannelVideoFragment().newInstance(channel.videoIds)).commit();
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
