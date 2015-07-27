package kuchbhilabs.chestream;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import kuchbhilabs.chestream.fragments.profile.ProfileFragment;
import kuchbhilabs.chestream.fragments.queue.QueueFragment;
import kuchbhilabs.chestream.fragments.stream.VideoFragment;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    static ViewPager mViewPager;
    PagerAdapter mPagerAdapter;

    VideoFragment videoFragment;
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ApplicationBase application = (ApplicationBase) getApplication();
        mTracker = application.getDefaultTracker();


        mViewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(1);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(this);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (videoFragment == null) {
            videoFragment = (VideoFragment) mPagerAdapter.getRegisteredFragment(1);
        }
        videoFragment.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {



        if (position==0){
            Log.i("MainActivity", "Setting screen name: " + "ProfileFragment");
            mTracker.setScreenName("Image~" + "ProfileFragment");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());        }

        if (position==1){
            Log.i("MainActivity", "Setting screen name: " + "VideoFragment");
            mTracker.setScreenName("Image~" + "VideoFragment");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());        }


        if (position==2){
            QueueFragment.bounceUploadButton();
            Log.i("MainActivity", "Setting screen name: " + "QueueFragment");
            mTracker.setScreenName("Image~" + "QueueFragment");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class PagerAdapter extends FragmentPagerAdapter {

        //Note: Change Fragment to WeakReference<Fragment> in case of more than 3 fragments
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        private final String[] TITLES = { "Profile","Video","Feed" };

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
                    fragment = new ProfileFragment();
                    break;
                case 1:
                    fragment = new VideoFragment();
                    break;
                case 2:
                    fragment = new QueueFragment();
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
    public void onBackPressed() {

        if(mViewPager.getCurrentItem()!=1) {
            mViewPager.setCurrentItem(1, true);

        } else if (VideoFragment.slidingUpPanelLayout.isPanelExpanded()){
              VideoFragment.slidingUpPanelLayout.collapsePanel();

    } else {
            super.onBackPressed();
        }
    }

    public static void movetoQueueAndUploadVideo(){
        mViewPager.setCurrentItem(2);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                QueueFragment.upload.performClick();
            }
        },500);

    }
}
