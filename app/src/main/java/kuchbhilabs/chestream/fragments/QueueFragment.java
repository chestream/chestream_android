package kuchbhilabs.chestream.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kuchbhilabs.chestream.ApplicationBase;
import kuchbhilabs.chestream.CompressionUploadService;
import kuchbhilabs.chestream.QueueVideos;
import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.VolleySingleton;
import kuchbhilabs.chestream.helpers.CircularRevealView;
import kuchbhilabs.chestream.helpers.Helper;

/**
 * Created by naman on 20/06/15.
 */
public class QueueFragment extends Fragment {

    private ArrayList<String> listTitle, listAvatarUrl, listUsername, listGifUrl, listVotes, listLocation;

    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private ArrayList<QueueVideos> queueVideos;
    private FloatingActionButton upload;
    ArrayList<QueueVideos> entries = new ArrayList<QueueVideos>();
    private CircularRevealView revealView;
    private View selectedView;
    android.os.Handler handler;
    private String TAG = "QueueFragment";

    public QueueFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_queue, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        revealView=(CircularRevealView) rootView.findViewById(R.id.reveal);

        upload = (FloatingActionButton) rootView.findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    final int color = Color.parseColor("#00bcd4");
                    final Point p = Helper.getLocationInView(revealView, view);

                    revealView.reveal(p.x, p.y, color, view.getHeight() / 2, 440, null);
                    selectedView = view;

                    handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            startActivityForResult(takeVideoIntent, 1);

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    revealView.hide(p.x, p.y, android.R.color.transparent, 0, 330, null);
                                }
                            }, 300);

                        }
                    }, 500);

                }
            }
        });
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        loadData();
//        QueueVideosAdapter adapter = new QueueVideosAdapter(queueVideos);
//        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri videoUri = data.getData();
            Intent serviceIntent = new Intent(getActivity(), CompressionUploadService.class);
            serviceIntent.putExtra("path", getRealPathFromUri(getActivity(), videoUri));
            getActivity().startService(serviceIntent);
        }
    }



    private String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public void loadData()
    {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                "https://api-eu.clusterpoint.com/1104/chestream/_search.json", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("Authorization", ApplicationBase.basicAuth);
                return headers;
            }

            @Override
            public byte[] getBody() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("query", "*");
                    jsonObject.put("docs", "50");
                    jsonObject.put("offset","0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, jsonObject.toString());
                return jsonObject.toString().getBytes();
            }
        };
        request.setShouldCache(false);
        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(request);
    }
}
