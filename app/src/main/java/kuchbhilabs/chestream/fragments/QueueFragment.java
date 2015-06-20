package kuchbhilabs.chestream.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import kuchbhilabs.chestream.CompressionUploadService;
import kuchbhilabs.chestream.QueueVideos;
import kuchbhilabs.chestream.QueueVideosAdapter;
import kuchbhilabs.chestream.R;

/**
 * Created by naman on 20/06/15.
 */
public class QueueFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private ArrayList<QueueVideos> queueVideos;
    private FloatingActionButton upload;


    private void initializeData() {
        queueVideos = new ArrayList<>();
//        queueVideos.add(new QueueVideos("YO YO", "", 1000, "Delhi"));
//        queueVideos.add(new QueueVideos("Game Of Thrones", "", 500, "Mumbai"));
//        queueVideos.add(new QueueVideos("Silicon Valley", "", 200, "New York"));
    }

    public QueueFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_queue, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        upload = (FloatingActionButton) rootView.findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, 1);
                }
            }
        });
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        initializeData();
        QueueVideosAdapter adapter = new QueueVideosAdapter(queueVideos);
        recyclerView.setAdapter(adapter);

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
}
