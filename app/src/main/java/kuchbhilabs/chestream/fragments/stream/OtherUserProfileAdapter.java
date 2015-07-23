package kuchbhilabs.chestream.fragments.stream;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
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
import kuchbhilabs.chestream.parse.ParseVideo;

public class OtherUserProfileAdapter extends RecyclerView.Adapter<OtherUserProfileAdapter.QVHolder> {

    private static final String TAG = "OtherUserProfileAdapter";

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
        TextView totalVotes;
        CardView rootLayout;
        CircularRevealView revealView;
        SimpleDraweeView thumbnail;

        QVHolder(final View itemView) {
            super(itemView);
            videoTitle = (TextView) itemView.findViewById(R.id.video_title);
            location = (TextView) itemView.findViewById(R.id.video_location);
            totalVotes = (TextView) itemView.findViewById(R.id.video_score);
            rootLayout = (CardView) itemView.findViewById(R.id.root_layout);
            revealView = (CircularRevealView) itemView.findViewById(R.id.reveal);
            thumbnail=(SimpleDraweeView) itemView.findViewById(R.id.thumbnail);
        }
    }


    public OtherUserProfileAdapter(Context context, final List<ParseVideo> videos) {
        this.videos = videos;
        this.context = context;
    }

    public void updateDataSet(List<ParseVideo> list) {
        this.videos = list;
    }

    public void updateItem(final int location) {
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

        Log.d(TAG, "Hi");
            View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myvideos, parent, false);
            QVHolder qvh = new QVHolder(v2);
            return qvh;
    }

    @Override
    public void onBindViewHolder(final QVHolder holder, final int position) {


        final ParseObject video = videos.get(position);
        holder.videoTitle.setText(video.getString(ParseTables.Videos.TITLE));

        ParseUser user = video.getParseUser(ParseTables.Videos.USER);
        try {
            holder.thumbnail.setImageURI(Uri.parse(video.getString(ParseTables.Videos.VIDEO_THUMBNAIL)));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        holder.location.setText(video.getString(ParseTables.Videos.LOCATION));
        holder.totalVotes.setText(String.valueOf(video.getInt(ParseTables.Videos.UPVOTE)));
        final int[] total_votes = {Integer.parseInt(holder.totalVotes.getText().toString())};

        final android.os.Handler handler = new android.os.Handler();
        final Runnable mLongPressed = new Runnable() {
            public void run() {
                Log.i("", "Long press!");
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(Uri.parse(video.getString(ParseTables.Videos.GIF)))
                        .setAutoPlayAnimations(true)
                        .build();
                VideoFragment.gifView.setController(controller);
                VideoFragment.gifView.setVisibility(View.VISIBLE);
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
                    VideoFragment.gifView.setVisibility(View.INVISIBLE);
                    return true;

                } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setSelected(false);
                    Log.d("press", "release");
//                    dialog.dismiss();
                    handler.removeCallbacks(mLongPressed);
                    VideoFragment.gifView.setVisibility(View.INVISIBLE);
                    return true;

                } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    v.setSelected(false);
                    Log.d("press", "release");
//                    dialog.dismiss();
                    handler.removeCallbacks(mLongPressed);
                    VideoFragment.gifView.setVisibility(View.INVISIBLE);
                    return true;
                } else
                    return false;
            }
        });

        if (getItemViewType(position) == 0) {
            //do something different for current video layout
        } else {

        }
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

    public void removeItem(int i) {
        videos.remove(i);
    }


    @Override
    public int getItemViewType(int position) {
        int viewType;
        if (position == 0) {
            viewType = 0;
        } else {
            viewType = 1;
        }
        return viewType;
    }

}