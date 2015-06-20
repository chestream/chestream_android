package kuchbhilabs.chestream.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import kuchbhilabs.chestream.CompressionUploadService;
import kuchbhilabs.chestream.R;

/**
 * Created by naman on 20/06/15.
 */
public class VideoFragment extends Fragment {

    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_video,null);

        activity = getActivity();

        Button uploadButton = (Button) rootView.findViewById(R.id.button_upload);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadServiceInten = new Intent(activity, CompressionUploadService.class);
                activity.startService(uploadServiceInten);
            }
        });

        return rootView;
    }
}