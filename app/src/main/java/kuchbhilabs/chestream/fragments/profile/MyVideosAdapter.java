package kuchbhilabs.chestream.fragments.profile;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.ParseObject;

import java.util.List;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.helpers.CircularRevealView;
import kuchbhilabs.chestream.parse.ParseVideo;

/**
 * Created by naman on 17/07/15.
 */
public class MyVideosAdapter extends RecyclerView.Adapter<MyVideosAdapter.MVHolder> {

    private List<ParseVideo> videos;
    private Context context;

    public static class MVHolder extends RecyclerView.ViewHolder {
        TextView videoTitle;
        TextView location;
        TextView username;
        ImageButton upVote;
        ImageButton downVote;
        TextView totalVotes;
        CardView rootLayout;
        CircularRevealView revealView;
        SimpleDraweeView draweeView, thumbnail;

        MVHolder(final View itemView) {
            super(itemView);


            videoTitle = (TextView) itemView.findViewById(R.id.video_title);
            location = (TextView) itemView.findViewById(R.id.video_location);
            username = (TextView) itemView.findViewById(R.id.username);
            totalVotes = (TextView) itemView.findViewById(R.id.video_score);
            upVote = (ImageButton) itemView.findViewById(R.id.up_vote);
            downVote = (ImageButton) itemView.findViewById(R.id.down_vote);
            rootLayout = (CardView) itemView.findViewById(R.id.root_layout);
            revealView = (CircularRevealView) itemView.findViewById(R.id.reveal);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.profile_picture);
            thumbnail = (SimpleDraweeView) itemView.findViewById(R.id.thumbnail);
        }
    }

    public MyVideosAdapter(Context context, final List<ParseVideo> videos) {
        this.videos = videos;
        this.context = context;
    }

    @Override
    public MVHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_myvideos, null);
        MVHolder ml = new MVHolder(v);
        return ml;
    }

    @Override

    public void onBindViewHolder(final MVHolder videosRowHolder, int i) {
        ParseObject video = videos.get(i);

    }

    @Override
    public int getItemCount() {
        return (null != videos ? videos.size() : 0);
    }



}
