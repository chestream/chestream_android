package kuchbhilabs.chestream.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kuchbhilabs.chestream.ApplicationBase;
import kuchbhilabs.chestream.CompressionUploadService;
import kuchbhilabs.chestream.QueueVideos;
import kuchbhilabs.chestream.QueueVideosAdapter;
import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.VolleySingleton;
import kuchbhilabs.chestream.helpers.CircularRevealView;
import kuchbhilabs.chestream.helpers.Helper;

/**
 * Created by naman on 20/06/15.
 */
public class QueueFragment extends Fragment {

    public static SimpleDraweeView gifView;

    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private ArrayList<QueueVideos> queueVideos;
    private FloatingActionButton upload;
    ArrayList<QueueVideos> entries = new ArrayList<QueueVideos>();
    private CircularRevealView revealView;
    private View selectedView;
    android.os.Handler handler;
    private String TAG = "QueueFragment";

    Toolbar toolbar;

    public QueueFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_queue, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        revealView=(CircularRevealView) rootView.findViewById(R.id.reveal);


        gifView = (SimpleDraweeView) rootView.findViewById(R.id.preview_gif);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse("http://i.giphy.com/3o85xru50pUfrbXo9W.gif"))
                .setAutoPlayAnimations(true)
                .build();
        gifView.setController(controller);

        toolbar=(Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Stream");


        upload = (FloatingActionButton) rootView.findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Image Source");
                builder.setItems(new CharSequence[] {"Gallery", "Camera"},
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:

                                        // GET Video FROM THE GALLERY

                                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

                                        Intent chooser = Intent.createChooser(intent, "Choose a Video");
                                        startActivityForResult(chooser, 2);

                                        break;

                                    case 1:

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

                                        break;

                                    default:
                                        break;
                                }
                            }
                        });

                builder.show();


            }
        });
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        loadFromParse();
//        QueueVideosAdapter adapter = new QueueVideosAdapter(queueVideos);
//        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 1)
            {
                Uri videoUri = data.getData();
                Intent serviceIntent = new Intent(getActivity(), CompressionUploadService.class);
                serviceIntent.putExtra("path", getRealPathFromUri(getActivity(), videoUri));
                getActivity().startService(serviceIntent);
            }

            if (requestCode == 2)
            {
                Uri videoUri = data.getData();
                Intent serviceIntent = new Intent(getActivity(), CompressionUploadService.class);
                serviceIntent.putExtra("path", getRealPathFromUri(getActivity(), videoUri));
                getActivity().startService(serviceIntent);
            }

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


    public void loadDatafromAssets()
    {

        JSONObject obj=null;
        try {
             obj = new JSONObject(loadJSONFromAsset());
        } catch (JSONException e) {
            e.printStackTrace();
        }



        Log.d("data", obj.toString());

        JSONArray arrayOutlets = null;

        try {
            arrayOutlets = obj.getJSONArray("documents");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < arrayOutlets.length(); i++) {
            try {
                String title = arrayOutlets.getJSONObject(i).getString("video_title");
                String avatar_url = arrayOutlets.getJSONObject(i).getString("user_avatar");
                String username = arrayOutlets.getJSONObject(i).getString("user_name");
                String gif_url = arrayOutlets.getJSONObject(i).getString("video_gif");
                String location = arrayOutlets.getJSONObject(i).getString("user_location");
                String numberOfVotes = arrayOutlets.getJSONObject(i).getString("video_upvotes");

                entries.add(
                        new QueueVideos(title, username, avatar_url, gif_url, numberOfVotes, location)
                );

                Log.d("data", title + username + avatar_url + gif_url + numberOfVotes + location);

            } catch (JSONException e) {
                e.printStackTrace();
            }
//                                Log.d("data", title + username + avatar_url + gif_url + numberOfVotes + location);
        }

        QueueVideosAdapter queueVideosAdapter = new QueueVideosAdapter(getActivity(),entries);
        queueVideosAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(queueVideosAdapter);

    }

    public void loadData()
    {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                "https://api-eu.clusterpoint.com/1104/chestream/_search.json", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                JSONObject jsonObject = response;
                Log.d("data", jsonObject.toString());

                JSONArray arrayOutlets = null;

                try {
                    arrayOutlets = jsonObject.getJSONArray("documents");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < arrayOutlets.length(); i++) {
                    try {
                        String title = arrayOutlets.getJSONObject(i).getString("video_title");
                        String avatar_url = arrayOutlets.getJSONObject(i).getString("user_avatar");
                        String username = arrayOutlets.getJSONObject(i).getString("user_name");
                        String gif_url = arrayOutlets.getJSONObject(i).getString("video_gif");
                        String location = arrayOutlets.getJSONObject(i).getString("user_location");
                        String numberOfVotes = arrayOutlets.getJSONObject(i).getString("video_upvotes");

                        entries.add(
                                new QueueVideos(title, username, avatar_url, gif_url, numberOfVotes, location)
                        );

                        Log.d("data", title + username + avatar_url + gif_url + numberOfVotes + location);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                                Log.d("data", title + username + avatar_url + gif_url + numberOfVotes + location);
                }

                QueueVideosAdapter queueVideosAdapter = new QueueVideosAdapter(getActivity(),entries);
                queueVideosAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(queueVideosAdapter);
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


    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getActivity().getAssets().open("sample.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    public  void loadFromParse()
    {

        clear_lists();
        ParseQuery<ParseObject> query = new ParseQuery<>(
                "Videos");

        query.orderByDescending("video_upvotes");
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                for (ParseObject videos : parseObjects) {

                    entries.add(
                            new QueueVideos(  videos.get("title").toString(),
                                    videos.get("user_avatar").toString(),
                                    videos.get("username").toString(),
                                    videos.get("video_gif").toString(),
                                    videos.get("upvotes").toString(),
                                    videos.get("user_location").toString()
                                    ));
                }
                QueueVideosAdapter queueVideosAdapter = new QueueVideosAdapter(getActivity(),entries);
                queueVideosAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(queueVideosAdapter);
            }

        });
    }

    private void clear_lists() {
        entries.clear();
    }

}
