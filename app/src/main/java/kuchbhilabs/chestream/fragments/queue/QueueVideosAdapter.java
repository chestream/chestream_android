package kuchbhilabs.chestream.fragments.queue;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import kuchbhilabs.chestream.ApplicationBase;
import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.activities.LoginActivity;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.fragments.channels.ChannelFragment;
import kuchbhilabs.chestream.fragments.channels.ChannelVideoFragment;
import kuchbhilabs.chestream.fragments.channels.NonSynchronous.ChannelVideoFragmentNonSynchronous;
import kuchbhilabs.chestream.fragments.channels.NonSynchronous.ChannelVideoFragmentNonSynchronousWithoutExo;
import kuchbhilabs.chestream.helpers.Utilities;
import kuchbhilabs.chestream.parse.ParseVideo;


public class QueueVideosAdapter extends RecyclerView.Adapter<QueueVideosAdapter.QVHolder> {

    private static final String TAG = "QueueVideosAdapter";

    private static boolean isSpeakButtonLongPressed = false;
    private List<ParseVideo> videos;

    private int[] isVoted;
    boolean nonSynchronous=false;

    AlertDialog.Builder builder;
    AlertDialog dialog;
    Activity activity;

    private static final String BLANK_AVATAR = "http://www.loanstreet.in/loanstreet-b2c-theme/img/avatar-blank.jpg";

    private List<ParseVideo> upVotedVideos;
    private List<ParseVideo> downVotedVideos;

    ParseUser currentUser;

    Handler handler;

    public static class QVHolder extends RecyclerView.ViewHolder {
        TextView videoTitle;
        TextView location;
        TextView username;
        ImageButton upVote;
        ImageButton downVote;
        TextView totalVotes;
        FrameLayout rootLayout;
        SimpleDraweeView draweeView,thumbnail;
        View palette;

        QVHolder(final View itemView) {
            super(itemView);

            videoTitle = (TextView) itemView.findViewById(R.id.video_title);
            location = (TextView) itemView.findViewById(R.id.video_location);
            username = (TextView) itemView.findViewById(R.id.username);
            totalVotes = (TextView) itemView.findViewById(R.id.video_score);
            upVote = (ImageButton) itemView.findViewById(R.id.up_vote);
            downVote = (ImageButton) itemView.findViewById(R.id.down_vote);
            rootLayout = (FrameLayout) itemView.findViewById(R.id.root_layout);
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.profile_picture);
            thumbnail=(SimpleDraweeView) itemView.findViewById(R.id.thumbnail);
            palette=(View) itemView.findViewById(R.id.pallete);
        }
    }


    public QueueVideosAdapter(Activity activity, final List<ParseVideo> videos, boolean nonSynchronous) {
        this.videos = videos;
        this.activity = activity;
        this.nonSynchronous=nonSynchronous;
        currentUser = ParseUser.getCurrentUser();
        if(currentUser!=null) {
            ParseRelation<ParseVideo> relation = currentUser.getRelation(ParseTables.Users.UPVOTED);
            try {
                upVotedVideos = relation.getQuery().find();
                relation = currentUser.getRelation(ParseTables.Users.DOWNVOTED);
                downVotedVideos = relation.getQuery().find();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handler = new Handler();
            }
        });
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

        if(videos.get(0)!=null){
            ChannelVideoFragmentNonSynchronousWithoutExo.playVideo(videos.get(0).getString(ParseTables.Videos.URL));
        }
        
        if (viewType == 0) {
            View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_current_item, parent, false);
            QVHolder cvh = new QVHolder(v1);
            return cvh;
        } else {
            View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_item, parent, false);
            QVHolder qvh = new QVHolder(v2);
            return qvh;
        }
    }

    @Override
    public void onBindViewHolder(final QVHolder holder, final int position) {



        final ParseVideo video = videos.get(position);
        Log.d(TAG, "Video voting = " + video.isVoted);
        holder.videoTitle.setText(video.getString(ParseTables.Videos.TITLE));

        holder.username.setText(video.getString(ParseTables.Videos.USER_USERNAME));
        try {
            ImageLoader.getInstance().displayImage(video.getString(ParseTables.Videos.VIDEO_THUMBNAIL), holder.thumbnail,
                    new DisplayImageOptions.Builder().cacheInMemory(true)
                            .cacheOnDisk(true)

                            .resetViewBeforeLoading(true)
                            .displayer(new FadeInBitmapDisplayer(400))
                            .build(),new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            Palette palette=Palette.generate(loadedImage);
                            int color=palette.getVibrantColor(Color.parseColor("#33ffffff"));
                            holder.palette.setBackgroundColor(ColorUtils.setAlphaComponent(color, 20));
                        }
                    });
            holder.draweeView.setImageURI(Uri.parse(video.getString(ParseTables.Videos.USER_AVATAR)));
//            holder.thumbnail.setImageURI(Uri.parse(video.getString(ParseTables.Videos.VIDEO_THUMBNAIL)));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        holder.location.setText(video.getString(ParseTables.Videos.LOCATION));
        holder.totalVotes.setText(String.valueOf(video.getInt(ParseTables.Videos.UPVOTE)));

        if (video.isVoted == -2) {
            Log.d(TAG, "searching for the first time");
            if (upVotedVideos != null && upVotedVideos.contains(video)) {
                video.isVoted = 1;
            } else if (downVotedVideos != null && downVotedVideos.contains(video)) {
                video.isVoted = -1;
            } else {
                video.isVoted = 0;
            }
        }
        setVoteButtons(video.isVoted, holder.upVote, holder.downVote);

        holder.upVote.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Tracker mTracker;
                ApplicationBase application = (ApplicationBase) activity.getApplication();
                mTracker = application.getDefaultTracker();
                mTracker.setScreenName("QueueFragment");
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Upvote")
                        .setAction("click")
                        .setLabel(Utilities.getUserEmail(activity))
                        .build());

                if(currentUser!=null) {

                    int currentVotes = Integer.parseInt(holder.totalVotes.getText().toString());
                v.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.pop_out));
                holder.totalVotes.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.pop_out));
                if (video.isVoted == 1) {
                    holder.totalVotes.setText(String.valueOf(currentVotes - 1));
                    video.isVoted = 0;
                    downvote(position, 1);
                } else if (video.isVoted == -1) {
                    holder.totalVotes.setText(String.valueOf(currentVotes + 2));
                    video.isVoted = 1;
                    upvote(position, 2);
                } else {
                    holder.totalVotes.setText(String.valueOf(currentVotes + 1));
                    video.isVoted = 1;
                    upvote(position, 1);
                }
                setVoteButtons(video.isVoted, holder.upVote, holder.downVote);
            } else {
                Toast.makeText(activity, "Please Login first !", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
            }

            }
        });
        holder.downVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Tracker mTracker;
                ApplicationBase application = (ApplicationBase) activity.getApplication();
                mTracker = application.getDefaultTracker();
                mTracker.setScreenName("QueueFragment");
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Downvote")
                        .setAction("click")
                        .setLabel(Utilities.getUserEmail(activity))
                        .build());

                if(currentUser!=null) {
                    int currentVotes = Integer.parseInt(holder.totalVotes.getText().toString());
                    v.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.pop_out));
                    holder.totalVotes.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.pop_out));
                    if (video.isVoted == 1) {
                        holder.totalVotes.setText(String.valueOf(currentVotes - 2));
                        video.isVoted = -1;
                        downvote(position, 2);
                    } else if (video.isVoted == -1) {
                        holder.totalVotes.setText(String.valueOf(currentVotes + 1));
                        video.isVoted = 0;
                        upvote(position, 1);
                    } else {
                        holder.totalVotes.setText(String.valueOf(currentVotes - 1));
                        video.isVoted = -1;
                        downvote(position, 1);
                    }
                    setVoteButtons(video.isVoted, holder.upVote, holder.downVote);
                }
                else {
                    Toast.makeText(activity, "Please Login first !", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                }
            }
        });

        final Runnable mLongPressed = new Runnable() {
            public void run() {

                Tracker mTracker;
                ApplicationBase application = (ApplicationBase) activity.getApplication();
                mTracker = application.getDefaultTracker();
                mTracker.setScreenName("QueueFragment");
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("GIF")
                        .setAction("preview")
                        .setLabel(Utilities.getUserEmail(activity))
                        .build());


                Log.i("", "Long press!");
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(Uri.parse(video.getString(ParseTables.Videos.GIF)))
                        .setAutoPlayAnimations(true)
                        .build();
                ChannelVideoFragment.gifView.setController(controller);
                ChannelVideoFragment.gifView.setVisibility(View.VISIBLE);
                ChannelVideoFragment.progressBar.setVisibility(View.VISIBLE);
            }
        };

        if(nonSynchronous) {
            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChannelVideoFragmentNonSynchronousWithoutExo.playVideo(video.getString(ParseTables.Videos.URL));
                }
            });
        }
        else{
            holder.rootLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if(!nonSynchronous){
                            v.setSelected(true);
                            handler.postDelayed(mLongPressed, 500);
                            Log.d("press", "pressed");
                        }
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.setSelected(false);
                        Log.d("press", "release");
//                    dialog.dismiss();
                        handler.removeCallbacks(mLongPressed);
                        ChannelVideoFragment.gifView.setVisibility(View.INVISIBLE);
                        ChannelVideoFragment.progressBar.setVisibility(View.GONE);
                        return true;

                    } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                        v.setSelected(false);
                        Log.d("press", "release");
//                    dialog.dismiss();
                        handler.removeCallbacks(mLongPressed);
                        ChannelVideoFragment.gifView.setVisibility(View.INVISIBLE);
                        ChannelVideoFragment.progressBar.setVisibility(View.GONE);
                        return true;

                    } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        v.setSelected(false);
                        Log.d("press", "release");
//                    dialog.dismiss();
                        handler.removeCallbacks(mLongPressed);
                        ChannelVideoFragment.gifView.setVisibility(View.INVISIBLE);
                        ChannelVideoFragment.progressBar.setVisibility(View.GONE);
                        return true;
                    } else
                        return false;
                }
            });

        }

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

    private void upvote(int position, int amount) {
        ParseObject video = videos.get(position);
        int currentVotes = video.getInt(ParseTables.Videos.UPVOTE);
        String objectId = video.getObjectId();
        video.put(ParseTables.Videos.UPVOTE, currentVotes + amount);
        video.saveInBackground();
        notifyVotes(objectId);

        //Add to parse relations
        if (currentUser != null) {
            ParseRelation<ParseObject> relation = currentUser.getRelation(ParseTables.Users.UPVOTED);
            relation.add(video);
            relation = currentUser.getRelation(ParseTables.Users.DOWNVOTED);
            relation.remove(video);
            currentUser.saveInBackground();
        } else {
            Log.e(TAG, "Did not add to the relation because the user is null");
        }
    }

    private void downvote(int position, int amount) {
        ParseObject video = videos.get(position);
        int currentVotes = video.getInt(ParseTables.Videos.UPVOTE);
        if (currentVotes > 0) {
            video.put(ParseTables.Videos.UPVOTE, currentVotes - amount);
            video.saveInBackground();
            if (currentUser != null) {
                Log.d(TAG, "Doing shit with PArse");
                ParseRelation<ParseObject> relation = currentUser.getRelation(ParseTables.Users.DOWNVOTED);
                relation.add(video);
                relation = currentUser.getRelation(ParseTables.Users.UPVOTED);
                relation.remove(video);
                currentUser.saveInBackground();
            } else {
                Log.e(TAG, "Did not add to the relation because current user is null");
            }
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

    private void setVoteButtons(int isVoted, ImageButton upVote, ImageButton downVote) {
        switch (isVoted) {
            case 0:
                upVote.setImageResource(R.drawable.ic_expand_less_white_24dp);
                downVote.setImageResource(R.drawable.ic_expand_more_white_24dp);
                break;
            case 1:
                upVote.setImageResource(R.drawable.ic_expand_less_yellow_24dp);
                downVote.setImageResource(R.drawable.ic_expand_more_white_24dp);
                break;
            case -1:
                upVote.setImageResource(R.drawable.ic_expand_less_white_24dp);
                downVote.setImageResource(R.drawable.ic_expand_more_yellow_24dp);
                break;
        }
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

