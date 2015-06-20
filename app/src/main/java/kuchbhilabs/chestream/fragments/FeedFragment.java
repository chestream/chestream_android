package kuchbhilabs.chestream.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kuchbhilabs.chestream.QueueVideos;
import kuchbhilabs.chestream.QueueVideosAdapter;
import kuchbhilabs.chestream.R;

/**
 * Created by naman on 20/06/15.
 */
public class FeedFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private ArrayList<QueueVideos> queueVideos;


    private void initializeData(){
        queueVideos = new ArrayList<>();
        queueVideos.add(new QueueVideos("YO YO", "", 1000, "Delhi"));
        queueVideos.add(new QueueVideos("Game Of Thrones", "", 500, "Mumbai"));
        queueVideos.add(new QueueVideos("Silicon Valley", "", 200, "New York"));
    }

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_queue, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        initializeData();
        QueueVideosAdapter adapter = new QueueVideosAdapter(queueVideos);
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}
