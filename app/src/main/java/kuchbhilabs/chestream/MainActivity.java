package kuchbhilabs.chestream;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import kuchbhilabs.chestream.fragments.profile.ProfileFragment;
import kuchbhilabs.chestream.fragments.VideoFragment;
import kuchbhilabs.chestream.fragments.queue.QueueFragment;

public class MainActivity extends AppCompatActivity {

    ViewPager mViewPager;
    PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(1);
        mViewPager.setOffscreenPageLimit(2);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class PagerAdapter extends FragmentPagerAdapter {

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
    }

    @Override
    public void onBackPressed() {

        if(mViewPager.getCurrentItem()!=1) {
            mViewPager.setCurrentItem(1, true);

        } else if (VideoFragment.slidingUpPanelLayout.isPanelExpanded()){
              VideoFragment.slidingUpPanelLayout.collapsePanel();

//        } else if (VideoFragment.slidingUpPanelLayout2.isPanelExpanded()) {
//              VideoFragment.slidingUpPanelLayout2.collapsePanel();
    } else {
            super.onBackPressed();
        }
    }



}
