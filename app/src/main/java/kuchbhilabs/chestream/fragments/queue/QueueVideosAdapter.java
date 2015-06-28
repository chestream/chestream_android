package kuchbhilabs.chestream.fragments.queue;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;

import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.List;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.helpers.CircularRevealView;
import kuchbhilabs.chestream.helpers.Helper;


public class QueueVideosAdapter extends RecyclerView.Adapter<QueueVideosAdapter.QVHolder> {


    private static boolean isSpeakButtonLongPressed = false;
    private List<ParseObject> videos;

    AlertDialog.Builder builder;
    AlertDialog dialog;
    Context context;

    public static class QVHolder extends RecyclerView.ViewHolder {
        ImageView uploaderImage;
        TextView videoTitle;
        TextView location;
        TextView username;
        TextView upVote;
        TextView downVote;
        TextView totalVotes;
        CardView rootLayout;
        CircularRevealView revealView;
        SimpleDraweeView draweeView;


        QVHolder(final View itemView) {
            super(itemView);


            videoTitle = (TextView)itemView.findViewById(R.id.video_title);
            location = (TextView)itemView.findViewById(R.id.video_location);
            username = (TextView)itemView.findViewById(R.id.username);
            totalVotes = (TextView)itemView.findViewById(R.id.video_score);
            upVote = (TextView)itemView.findViewById(R.id.up_vote);
            downVote = (TextView)itemView.findViewById(R.id.down_vote);
            rootLayout = (CardView)itemView.findViewById(R.id.root_layout);
            revealView=(CircularRevealView) itemView.findViewById(R.id.reveal);
            draweeView=(SimpleDraweeView) itemView.findViewById(R.id.profile_picture);
        }
    }

    public QueueVideosAdapter(Context context, List<ParseObject> videos){
        this.videos = videos;
        this.context = context;
    }

    @Override
    public QVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_item, parent, false);
        QVHolder qvh = new QVHolder(v);

         builder = new AlertDialog.Builder(context);
         builder.setMessage("Test for preventing dialog close");
         dialog = builder.create();

        return qvh;

    }



    @Override
    public void onBindViewHolder(final QVHolder holder, final int position) {
        final ParseObject video = videos.get(position);
        holder.videoTitle.setText(video.getString(ParseTables.Videos.TITLE));
        try {
            holder.username.setText(video.getParseUser(ParseTables.Videos.USER).fetchIfNeeded()
                    .getString(ParseTables.Users.USERNAME));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.location.setText(video.getString(ParseTables.Videos.LOCATION));
        Uri uri = Uri.parse(video.getString(ParseTables.Videos.AVATAR));
        holder.draweeView.setImageURI(uri);

        holder.totalVotes.setText(String.valueOf(video.getInt(ParseTables.Videos.UPVOTE)));
//        holder.uploaderImage.
        final int[] total_votes = {Integer.parseInt(holder.totalVotes.getText().toString())};

        holder.upVote.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int votes = total_votes[0] + 1;
                if (Math.abs(total_votes[0] - votes) == 1) {
                    holder.totalVotes.setText(votes + "");
                  //  holder.upVote.getBackground().setAlpha(165);
                  //  holder.downVote.getBackground().setAlpha(65);
                    final int color = Color.parseColor("#00bcd4");
                    final Point p = Helper.getLocationInView(holder.revealView, v);

                    holder.revealView.reveal(p.x, p.y, color, v.getHeight() / 2, 440, null);


                } else {
                }
            }
        });
        holder.downVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int votes = total_votes[0] - 1;
                if (Math.abs(total_votes[0] - votes) == 1) {
                    holder.totalVotes.setText(votes + "");
                  //  holder.downVote.getBackground().setAlpha(165);
                  //  holder.upVote.getBackground().setAlpha(65);

                    final int color = Color.TRANSPARENT;
                    final Point p = Helper.getLocationInView(holder.revealView, v);

                    holder.revealView.hide(p.x, p.y, color, v.getHeight() / 5, 440, null);
                } else {
                }
            }
        });

        holder.rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setSelected(true);
                    Log.d("press", "pressed");


//                    dialog.show();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setUri(Uri.parse(video.getString(ParseTables.Videos.GIF)))
                            .setAutoPlayAnimations(true)
                            .build();
                    QueueFragment.gifView.setController(controller);
                    QueueFragment.gifView.setVisibility(View.VISIBLE);

//Overriding the handler immediately after show is probably a better approach than OnShowListener as described below


                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setSelected(false);
                    Log.d("press", "release");
//                    dialog.dismiss();
                    QueueFragment.gifView.setVisibility(View.INVISIBLE);
                    return true;

                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setSelected(false);
                    Log.d("press", "release");
//                    dialog.dismiss();
                    QueueFragment.gifView.setVisibility(View.INVISIBLE);
                    return true;

                } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    v.setSelected(false);
                    Log.d("press", "release");
//                    dialog.dismiss();
                    QueueFragment.gifView.setVisibility(View.INVISIBLE);
                    return true;
                } else
                    return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

}

