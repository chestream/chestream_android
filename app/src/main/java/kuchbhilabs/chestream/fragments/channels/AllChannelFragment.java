package kuchbhilabs.chestream.fragments.channels;

import android.app.Activity;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.RequestPasswordResetCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.widgets.SectionedGridRecyclerViewAdapter;

/**
 * Created by naman on 20/08/15.
 */
public class AllChannelFragment extends Fragment {

    private static final String CHANNELS_URL = "http://104.215.136.204:8000/channels";
    private static final String TAG = "AllChannelsFragment";

    Toolbar toolbar;
    RecyclerView recyclerView;
    Activity activity;
    AllChannelAdapter adapter;
    SmoothProgressBar progressBar;

    long startTime = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_all_channels,container, false);
        activity=getActivity();

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        progressBar=(SmoothProgressBar) rootView.findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView=(RecyclerView) rootView.findViewById(R.id.recycler_view_channels);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(activity,3));

        getChannels2();

        return rootView;

    }

    private void getChannels2(){
        ParseQuery<ParseObject> query =  ParseQuery.getQuery("Channels");

//        query.orderByDescending("active_users");
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.include("video_ids");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (parseObjects!=null && !parseObjects.isEmpty()) {

                    final ArrayList<ChannelModel> channelList =new ArrayList<>();
                    for (int i=0;i<parseObjects.size();i++){
                        String name = parseObjects.get(i).getString("name");
                        String info = parseObjects.get(i).getString("info");
                        String category = parseObjects.get(i).getString("Category");
                        String picture = parseObjects.get(i).getString("picture");
                        String id = parseObjects.get(i).getString("channel_id");
                        boolean nonSynchronous = parseObjects.get(i).getBoolean("nonSynchronous");
                        int activeUsers =parseObjects.get(i).getInt("active_users");
                        JSONArray videoIds = parseObjects.get(i).getJSONArray("video_ids");
                        List<String> arr=new ArrayList<>();
                        for(int j=0;j<videoIds.length();j++)
                            try {
                                arr.add(videoIds.getString(j));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        if(!name.equals("global")) {
                            channelList.add(new ChannelModel(id, picture, name, info,category, activeUsers, arr, nonSynchronous));
                        }
                    }

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ArrayList<ChannelModel> sortredChannels = new ArrayList<ChannelModel>();

                                    List<SectionedGridRecyclerViewAdapter.Section> sections =
                                            new ArrayList<SectionedGridRecyclerViewAdapter.Section>();

                                    ArrayList<String> categories = new ArrayList<String>();

                                    for (int i = 0; i < channelList.size(); i++) {
                                        ChannelModel channel = channelList.get(i);
                                        if (!categories.contains(channel.category))
                                            categories.add(channel.category);
                                    }

                                    int positionOfHeader = 0;
                                    for (int i = 0; i < categories.size(); i++) {

                                        ArrayList<ChannelModel> categoryItems = new ArrayList<ChannelModel>();
                                        for (int j = 0; j < channelList.size(); j++) {
                                            if (channelList.get(j).category.equals(categories.get(i))) {
                                                categoryItems.add(channelList.get(j));
                                            }
                                        }

                                        sections.add(new SectionedGridRecyclerViewAdapter.Section(positionOfHeader, categories.get(i)));
                                        sortredChannels.addAll(categoryItems);
                                        positionOfHeader += categoryItems.size();
                                    }

                                    adapter = new AllChannelAdapter(activity, sortredChannels);

                                    SectionedGridRecyclerViewAdapter.Section[] dummy = new SectionedGridRecyclerViewAdapter.Section[sections.size()];
                                    SectionedGridRecyclerViewAdapter mSectionedAdapter = new
                                            SectionedGridRecyclerViewAdapter(getActivity(), R.layout.channel_category_header, R.id.section_text, recyclerView, adapter);
                                    mSectionedAdapter.setSections(sections.toArray(dummy));

                                    recyclerView.setAdapter(mSectionedAdapter);
                                    int spacingInPixels = getActivity().getResources().getDimensionPixelSize(R.dimen.spacing_channel_grid);
                                    recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "There is something wrong",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getChannels() {

        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, CHANNELS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response = " + response);
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            JSONArray responseArray;
                            responseArray = responseObject.getJSONArray("data");

                            final ArrayList<ChannelModel> channelList =new ArrayList<>();
                            for (int i=0;i<responseArray.length();i++){
                                String name = responseArray.getJSONObject(i).getString("name");
                                String info = responseArray.getJSONObject(i).getString("info");
                                String category = responseArray.getJSONObject(i).getString("category");
                                String picture = responseArray.getJSONObject(i).getString("picture");
                                String id = responseArray.getJSONObject(i).getString("channel_id");
                                boolean nonSynchronous = responseArray.getJSONObject(i).getBoolean("nonSynchronous");
                                int activeUsers =responseArray.getJSONObject(i).getInt("active_users");
                                JSONArray videoIds = responseArray.getJSONObject(i).getJSONArray("video_ids");
                                List<String> arr=new ArrayList<>();
                                for(int j=0;j<videoIds.length();j++)
                                    arr.add(videoIds.getString(j));

                                if(!name.equals("global")) {
                                    channelList.add(new ChannelModel(id, picture, name, info,category, activeUsers, arr, nonSynchronous));
                                    Log.d("hihi",id+ picture+ name+ info+category+ activeUsers+ arr+ nonSynchronous);
                                }
                            }

                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ArrayList<ChannelModel> sortredChannels = new ArrayList<ChannelModel>();

                                            List<SectionedGridRecyclerViewAdapter.Section> sections =
                                                    new ArrayList<SectionedGridRecyclerViewAdapter.Section>();

                                            ArrayList<String> categories =new ArrayList<String>();

                                            for (int i=0;i<channelList.size();i++){
                                               ChannelModel channel=channelList.get(i);
                                                if (!categories.contains(channel.category))
                                                categories.add(channel.category);
                                            }

                                            int positionOfHeader =0;
                                            for (int i=0;i<categories.size();i++) {

                                                ArrayList<ChannelModel> categoryItems=new ArrayList<ChannelModel>();
                                                for (int j=0;j<channelList.size();j++){
                                                    if (channelList.get(j).category.equals(categories.get(i))){
                                                        categoryItems.add(channelList.get(j));
                                                    }
                                                }

                                                sections.add(new SectionedGridRecyclerViewAdapter.Section(positionOfHeader, categories.get(i)));
                                                sortredChannels.addAll(categoryItems);
                                                positionOfHeader+=categoryItems.size();
                                            }

                                            adapter=new AllChannelAdapter(activity,sortredChannels);

                                            SectionedGridRecyclerViewAdapter.Section[] dummy = new SectionedGridRecyclerViewAdapter.Section[sections.size()];
                                            SectionedGridRecyclerViewAdapter mSectionedAdapter = new
                                                    SectionedGridRecyclerViewAdapter(getActivity(),R.layout.channel_category_header,R.id.section_text,recyclerView,adapter);
                                            mSectionedAdapter.setSections(sections.toArray(dummy));

                                            recyclerView.setAdapter(mSectionedAdapter);
                                            int spacingInPixels = getActivity().getResources().getDimensionPixelSize(R.dimen.spacing_channel_grid);
                                            recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            });


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);

    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {

            outRect.left = space;
            outRect.top=space;
            outRect.right=space;
            outRect.bottom=space;

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
        ParseAnalytics.trackEventInBackground(TAG, dimensions);
    }

    public long getElapsedTimeSecs() {
        long elapsed = 0;
        elapsed = ((System.currentTimeMillis() - startTime) / 1000) % 60;
        return elapsed;
    }
}
