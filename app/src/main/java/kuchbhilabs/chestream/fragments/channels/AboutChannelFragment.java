package kuchbhilabs.chestream.fragments.channels;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.helpers.CircularImageView;
import kuchbhilabs.chestream.helpers.Helper;

/**
 * Created by naman on 21/08/15.
 */
public class AboutChannelFragment extends Fragment {

    CircularImageView channelPicture;
    TextView name,details,active_users;
    View header;

    public static AboutChannelFragment newInstance(ChannelModel channel) {
        AboutChannelFragment fragment = new AboutChannelFragment();
        Bundle args = new Bundle();
        args.putSerializable("channel",channel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_about_channel, container, false);
        channelPicture=(CircularImageView) rootView.findViewById(R.id.channel_picture);
        name=(TextView) rootView.findViewById(R.id.name);
        details=(TextView) rootView.findViewById(R.id.details);
        active_users=(TextView) rootView.findViewById(R.id.active_users);
        header=rootView.findViewById(R.id.header);

        ChannelModel channel=(ChannelModel) getArguments().getSerializable("channel");

        name.setText(channel.name);
        details.setText(channel.info);
        active_users.setText(channel.activeUsers+" Active Users");

        ImageLoader.getInstance().displayImage(channel.picture, channelPicture,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisk(true)
                        .resetViewBeforeLoading(true)
                        .displayer(new FadeInBitmapDisplayer(400))
                        .build(),new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        header.setBackground(Helper.createBlurredImageFromBitmap(loadedImage, getActivity(), 8));
                    }
                });

        return rootView;
    }
}
