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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import kuchbhilabs.chestream.R;

/**
 * Created by naman on 20/08/15.
 */
public class AllChannelFragment extends Fragment {

    private static final String CHANNELS_URL = "http://104.131.207.33:8800/channels";
    private static final String TAG = "AllChannelsFragment";

    Toolbar toolbar;
    RecyclerView recyclerView;
    Activity activity;
    AllChannelAdapter adapter;
    SmoothProgressBar progressBar;

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

        recyclerView.setLayoutManager(new GridLayoutManager(activity,2));

        getChannels();

        return rootView;

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

                            final ArrayList channelList =new ArrayList<>();
                            for (int i=0;i<responseArray.length();i++){
                                String name = responseArray.getJSONObject(i).getString("name");
                                String info = responseArray.getJSONObject(i).getString("info");
                                String picture = responseArray.getJSONObject(i).getString("picture");
                                String id = responseArray.getJSONObject(i).getString("channel_id");
                                int activeUsers =responseArray.getJSONObject(i).getInt("active_users");
                                JSONArray videoIds = responseArray.getJSONObject(i).getJSONArray("video_ids");
                                List<String> arr=new ArrayList<>();
                                for(int j=0;j<videoIds.length();j++)
                                    arr.add(videoIds.getString(j));

                                if(!name.equals("global")) {
                                    channelList.add(new ChannelModel(id, picture, name, info, activeUsers, arr));
                                }
                            }

                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter=new AllChannelAdapter(activity,channelList);
                                            recyclerView.setAdapter(adapter);
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


}
