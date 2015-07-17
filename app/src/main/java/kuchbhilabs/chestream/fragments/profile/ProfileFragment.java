package kuchbhilabs.chestream.fragments.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import kuchbhilabs.chestream.R;

/**
 * Created by naman on 12/07/15.
 */
public class ProfileFragment extends Fragment {

    Toolbar toolbar;
    SimpleDraweeView profile;
    FrameLayout header;
    RecyclerView recyclerView;
    MyVideosAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_profile, null);

        toolbar=(Toolbar) rootView.findViewById(R.id.toolbar);
        profile=(SimpleDraweeView) rootView.findViewById(R.id.profile_picture);
        header=(FrameLayout) rootView.findViewById(R.id.header);
        recyclerView=(RecyclerView) rootView.findViewById(R.id.recycler_view);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profile");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

//        recyclerView.setAdapter(adapter);

        return rootView;


    }
}
