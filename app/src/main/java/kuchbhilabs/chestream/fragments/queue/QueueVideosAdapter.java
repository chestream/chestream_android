package kuchbhilabs.chestream.fragments.queue;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.helpers.CircularRevealView;
import kuchbhilabs.chestream.helpers.Helper;
import kuchbhilabs.chestream.parse.ParseVideo;


public class QueueVideosAdapter extends RecyclerView.Adapter<QueueVideosAdapter.QVHolder> {

    private static final String TAG = "QueueVideosAdapter";

    private static boolean isSpeakButtonLongPressed = false;
    private List<ParseVideo> videos;

    private int[] isVoted;

    AlertDialog.Builder builder;
    AlertDialog dialog;
    Context context;

    private static final String BLANK_AVATAR = "http://www.loanstreet.in/loanstreet-b2c-theme/img/avatar-blank.jpg";

    public static class QVHolder extends RecyclerView.ViewHolder {
        TextView videoTitle;
        TextView location;
        TextView username;
        ImageButton upVote;
        ImageButton downVote;
        TextView totalVotes;
        CardView rootLayout;
        CircularRevealView revealView;
        SimpleDraweeView draweeView;

        QVHolder(final View itemView) {
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
        }
    }

    public QueueVideosAdapter(Context context, final List<ParseVideo> videos) {
        this.videos = videos;
        this.context = context;
    }

    public void updateDataSet(List<ParseVideo> list) {
        this.videos = list;
    }

    public void updateItem (final int location) {
        ParseObject video = this.videos.get(location);
        video.fetchInBackground(new GetCallback<ParseVideo>() {
            @Override
            public void done(ParseVideo parseVideo, ParseException e) {
                Log.d(TAG, "notifying about the change");
                notifyItemChanged(location);
            }
        });
    }

    public List<ParseVideo> getDataSet() {
        return this.videos;
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

        ParseUser user = video.getParseUser(ParseTables.Videos.USER);
        holder.username.setText(video.getString(ParseTables.Videos.USER_USERNAME));
        holder.draweeView.setImageURI(Uri.parse(video.getString(ParseTables.Videos.USER_AVATAR)));

        holder.location.setText(video.getString(ParseTables.Videos.LOCATION));
        holder.totalVotes.setText(String.valueOf(video.getInt(ParseTables.Videos.UPVOTE)));
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

                    holder.revealView.reveal(p.x, p.y, color, v.getHeight() / 10, 440, null);
                    upvote(position);
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
                    final int color = Color.TRANSPARENT;
                    final Point p = Helper.getLocationInView(holder.revealView, v);
                    holder.revealView.hide(p.x, p.y, color, v.getHeight() / 20, 440, null);
                    downvote(position);
                } else {
                }
            }
        });

        final android.os.Handler handler = new android.os.Handler();
        final Runnable mLongPressed = new Runnable() {
            public void run() {
                Log.i("", "Long press!");
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(Uri.parse(video.getString(ParseTables.Videos.GIF)))
                        .setAutoPlayAnimations(true)
                        .build();
                QueueFragment.gifView.setController(controller);
                QueueFragment.gifView.setVisibility(View.VISIBLE);
            }
        };

        holder.rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setSelected(true);
                    handler.postDelayed(mLongPressed, 500);
                    Log.d("press", "pressed");

                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setSelected(false);
                    Log.d("press", "release");
//                    dialog.dismiss();
                    handler.removeCallbacks(mLongPressed);
                    QueueFragment.gifView.setVisibility(View.INVISIBLE);
                    return true;

                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setSelected(false);
                    Log.d("press", "release");
//                    dialog.dismiss();
                    handler.removeCallbacks(mLongPressed);
                    QueueFragment.gifView.setVisibility(View.INVISIBLE);
                    return true;

                } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    v.setSelected(false);
                    Log.d("press", "release");
//                    dialog.dismiss();
                    handler.removeCallbacks(mLongPressed);
                    QueueFragment.gifView.setVisibility(View.INVISIBLE);
                    return true;
                } else
                    return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (videos == null) {
            Log.e(TAG, "video list is null. setting the size to 0");
            return 0;
        }

        return videos.size();
    }

    private void upvote(int position) {
        ParseObject video = videos.get(position);
        int currentVotes = video.getInt(ParseTables.Videos.UPVOTE);
        String objectId = video.getObjectId();
        video.put(ParseTables.Videos.UPVOTE, currentVotes + 1);
        video.saveInBackground();
        notifyVotes(objectId);
    }

    private void downvote(int position) {
        ParseObject video = videos.get(position);
        int currentVotes = video.getInt(ParseTables.Videos.UPVOTE);
        if (currentVotes > 0) {
            video.put(ParseTables.Videos.UPVOTE, currentVotes - 1);
            video.saveInBackground();
        }
    }

    private void notifyVotes(String videoId) {
        ParsePush push = new ParsePush();
        JSONObject message = new JSONObject();
        try {
            message.put("messageType", "votes");
            message.put("videoId", videoId);
            Log.d(TAG, "sending message = " + message.toString() + " videoId = " + videoId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        push.setMessage(message.toString());
        push.sendInBackground();
        Log.d(TAG, "Sending GCM request to notify others");
    }
}

