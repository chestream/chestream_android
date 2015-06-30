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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;

import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.helpers.CircularRevealView;
import kuchbhilabs.chestream.helpers.Helper;


public class QueueVideosAdapter extends RecyclerView.Adapter<QueueVideosAdapter.QVHolder> {

    private static final String TAG = "QueueVideosAdapter";

    private static boolean isSpeakButtonLongPressed = false;
    private List<ParseObject> videos;

    private int[] isVoted;

    AlertDialog.Builder builder;
    AlertDialog dialog;
    Context context;

    private static final String BLANK_AVATAR = "http://www.loanstreet.in/loanstreet-b2c-theme/img/avatar-blank.jpg";

    public static class QVHolder extends RecyclerView.ViewHolder {
        ImageView uploaderImage;
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

    public QueueVideosAdapter(Context context, List<ParseObject> videos) {
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

        video.getParseUser(ParseTables.Videos.USER).fetchIfNeededInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    holder.username.setText(user.getString(ParseTables.Users.USERNAME));
                    String url = user.getString(ParseTables.Users.AVATAR);
                    if (url == null) {
                        url = BLANK_AVATAR;
                    }
                    holder.draweeView.setImageURI(Uri.parse(url));
                } else {
                    holder.username.setText("ERROR USER IS NULL");
                }
            }
        });

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

        holder.rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setSelected(true);
                    Log.d("press", "pressed");
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setUri(Uri.parse(video.getString(ParseTables.Videos.GIF)))
                            .setAutoPlayAnimations(true)
                            .build();
                    QueueFragment.gifView.setController(controller);
                    QueueFragment.gifView.setVisibility(View.VISIBLE);
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
        if (videos == null) {
            Log.e(TAG, "video list is null. setting the size to 0");
            return 0;
        }

        return videos.size();
    }

    private void upvote(int position) {
        ParseObject video = videos.get(position);
        int currentVotes = video.getInt(ParseTables.Videos.UPVOTE);
        video.put(ParseTables.Videos.UPVOTE, currentVotes + 1);
        video.saveInBackground();
/*
        ParseCloud.callFunctionInBackground("votes", new HashMap<String, Object>(), new FunctionCallback<String>() {
            public void done(String result, ParseException e) {
                if (e == null) {
                    // result is "Hello world!"
                    Log.d(TAG, "success");
                } else {
                    e.printStackTrace();
                }
            }
        }); */

        notifyVotes();
    }

    private void downvote(int position) {
        ParseObject video = videos.get(position);
        int currentVotes = video.getInt(ParseTables.Videos.UPVOTE);
        if (currentVotes > 0) {
            video.put(ParseTables.Videos.UPVOTE, currentVotes - 1);
            video.saveInBackground();
        }
    }

    private void notifyVotes() {
        ParsePush push = new ParsePush();
        push.setMessage("The Giants just scored! It's now 2-2 against the Mets.");
        push.sendInBackground();
        Log.d(TAG, "Sending GCM request to notify others");
    }
}

