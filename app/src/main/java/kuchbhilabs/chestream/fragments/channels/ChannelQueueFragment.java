package kuchbhilabs.chestream.fragments.channels;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.fragments.queue.QueueVideosAdapter;
import kuchbhilabs.chestream.parse.ParseVideo;

/**
 * Created by naman on 21/08/15.
 */
public class ChannelQueueFragment extends Fragment {

    RecyclerView recyclerView;
    List<String> videoIDS;
    int videoPosition =0;

    ParseVideo video;

    public static ChannelQueueFragment newInstance(List<String> videoIds) {
        ChannelQueueFragment fragment = new ChannelQueueFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("ids",new ArrayList<String>(videoIds));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_channel_queue, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        videoIDS=getArguments().getStringArrayList("ids");

        final List<ParseVideo> videoList =new ArrayList<>();

        for (int i=0;i<videoIDS.size();i++) {
            ParseQuery<ParseVideo> query = ParseQuery.getQuery(ParseTables.Videos._NAME);
            query.whereEqualTo("objectId", videoIDS.get(i));
            query.findInBackground(new FindCallback<ParseVideo>() {
                @Override
                public void done(List<ParseVideo> list, ParseException e) {
                    if (list != null) {
                        video = list.get(0);
                        videoList.add(video);
                    }
                }
            });

        }

        QueueVideosAdapter adapter=new QueueVideosAdapter(getActivity(),videoList);
        recyclerView.setAdapter(adapter);

        return rootView;

    }
}
