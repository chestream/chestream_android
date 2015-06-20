package kuchbhilabs.chestream.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by naman on 20/06/15.
 */
public class VideoFragment extends Fragment {

    Activity activity;
    SlidingUpPanelLayout slidingUpPanelLayout;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_video, null);

        activity = getActivity();

        slidingUpPanelLayout=(SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setOverlayed(true);

        CommentsFragment commentsFragment = new CommentsFragment();

        getChildFragmentManager().beginTransaction().add(R.id.comments, commentsFragment).commit();

        return rootView;
    }
}