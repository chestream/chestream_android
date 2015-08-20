package kuchbhilabs.chestream.fragments.channels;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.activities.ChannelsActivity;
import kuchbhilabs.chestream.helpers.CircularImageView;

/**
 * Created by naman on 20/08/15.
 */
public class AllChannelAdapter extends RecyclerView.Adapter<AllChannelAdapter.AllChannelsRowHolder> {

    private List<ChannelModel> arrayList;
    private Context mContext;

    private static final String TAG = "AllChannelAdapter";

    public AllChannelAdapter(Context context, List<ChannelModel> arrayList) {
        this.arrayList = arrayList;
        this.mContext = context;
    }

    public void updateDataSet(List<ChannelModel> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public AllChannelsRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.channel_item_grid, null);
        AllChannelsRowHolder ml = new AllChannelsRowHolder(v);
        return ml;
    }

    @Override
    public void onBindViewHolder(final AllChannelsRowHolder itemHolder, int i) {

        ChannelModel channel = arrayList.get(i);
        itemHolder.name.setText(channel.name);
        itemHolder.details.setText(channel.activeUsers+" Active Users");

        ImageLoader.getInstance().displayImage(channel.picture, itemHolder.avatar,
                new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisk(true)
                        .resetViewBeforeLoading(true)
                        .displayer(new FadeInBitmapDisplayer(400))
                        .build(),new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    }
                });

    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    public class AllChannelsRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected CircularImageView avatar;
        protected TextView name, details;

        public AllChannelsRowHolder(View view) {
            super(view);
            this.avatar = (CircularImageView) view.findViewById(R.id.channel_picture);
            this.name = (TextView) view.findViewById(R.id.name);
            this.details = (TextView) view.findViewById(R.id.details);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent=new Intent(mContext,ChannelsActivity.class);
            mContext.startActivity(intent);
        }
    }
}

