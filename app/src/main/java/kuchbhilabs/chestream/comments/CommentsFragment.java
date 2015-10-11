package kuchbhilabs.chestream.comments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import kuchbhilabs.chestream.ApplicationBase;
import kuchbhilabs.chestream.activities.LoginActivity;
import kuchbhilabs.chestream.NotificationReceiver;
import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.fragments.channels.ChannelModel;
import kuchbhilabs.chestream.fragments.stream.VideoFragment;
import kuchbhilabs.chestream.helpers.Utilities;

/**
 * Created by naman on 20/06/15.
 */
public class CommentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CommentsAdapter adapter;
    private List<ParseObject> commentsList = new ArrayList<>();
    EditText editText;

    private static String channelId;

    static CommentsAdapter commentsAdapter;
    ImageView sendComment;
    SimpleDraweeView avatar;
    static TextView commentsLoading;
    View addCommentsFooter;
//    FloatingActionButton addCommentFab;
    private static final String TAG = "CommentsFragment";

    private BroadcastReceiver receiver;
    private Activity activity;
    ParseUser pUser;

    public static CommentsFragment newInstance(ChannelModel channel) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putSerializable("channel",channel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.fragment_comments,null);

        Log.d("newAA", "hi4");


        recyclerView=(RecyclerView) v.findViewById(R.id.comments_recycler_view);
        editText=(EditText) v.findViewById(R.id.commentEditText);
        commentsLoading=(TextView) v.findViewById(R.id.commentsLoading);
        addCommentsFooter=v.findViewById(R.id.addCommentFooter);
        avatar=(SimpleDraweeView) v.findViewById(R.id.avatar);
//        addCommentFab=(FloatingActionButton) v.findViewById(R.id.addCommentFab);

        sendComment  =(ImageView) v.findViewById(R.id.send);

         final ChannelModel channel=(ChannelModel) getArguments().getSerializable("channel");
        channelId = channel.id;
        Log.d("channelLog", channelId);

        CommentsFragment.setUpComments(channelId);

        activity = getActivity();

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setHasFixedSize(true);

//        VideoFragment.slidingUpPanelLayout.setScrollView(recyclerView);

        commentsAdapter = new CommentsAdapter(getActivity(), new ArrayList<ParseObject>());
        recyclerView.setAdapter(commentsAdapter);

       pUser = ParseUser.getCurrentUser() ;

        if ((pUser != null)
                && (pUser.isAuthenticated())
                && (pUser.getSessionToken() != null)
                && (pUser.getBoolean(ParseTables.Users.FULLY_REGISTERED))) {
            avatar.setImageURI(Uri.parse(pUser.getString("avatar")));
        } else {
            avatar.setImageURI(Uri.parse("http://www.loanstreet.in/loanstreet-b2c-theme/img/avatar-blank.jpg"));
        }


        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendComment(view);
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //TODO: Add a new comment to the current video

                    sendComment(null);
                }

                return false;
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NotificationReceiver.ACTION_COMMENT);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Comment photo uploaded.", Toast.LENGTH_SHORT).show();
                        setUpComments(channelId);
                    }
                });
            }
        };
        activity.registerReceiver(receiver, intentFilter);
/*
        Button tempButton = (Button) v.findViewById(R.id.button_test);
        tempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Sending broadcast");
                ParsePush push = new ParsePush();
                push.setMessage("The Giants just scored! It's now 2-2 against the Mets.");
                push.sendInBackground();
            }
        });
*/
        return v;
    }

    @Override
    public void onDestroy() {
        activity.unregisterReceiver(receiver);
        super.onDestroy();
    }

    public static void setUpComments(String channelID){

//        ParseObject currentVideoObjectComment = VideoFragment.currentVideo;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Channels");
        query.getInBackground(channelID, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
                    query.whereEqualTo("channel_object", object);
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> list, ParseException e) {
                            Log.d(TAG, "Updating comments dataset");
                            commentsAdapter.updateDataSet(list);
                            commentsAdapter.notifyDataSetChanged();
                            commentsLoading.setVisibility(View.GONE);

                            if (list.size()==0){
                                commentsLoading.setVisibility(View.VISIBLE);
                                commentsLoading.setText("Be the first to comment.");
                            }
                        }
                    });

                    // object will be your game score
                } else {
                    // something went wrong
                }
            }
        });

    }

    public void sendComment(View view){
        Tracker mTracker;
        ApplicationBase application = (ApplicationBase) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("CommentsFragment");
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Comments")
                .setAction("sent")
                .setLabel(Utilities.getUserEmail(activity))
                .build());

        if(view!=null) {
            view.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.pop_out));
        }

        Log.d("commentssss", "1");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Channels");
        query.getInBackground(channelId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Log.d("commentssss", "2");

                    if (object!=null) {

                        if ((pUser != null)
                                && (pUser.isAuthenticated())
                                && (pUser.getSessionToken() != null)
                                ) {
                            Log.d(TAG, pUser.getUsername() + pUser.getSessionToken());


                            List<ParseObject> commentsArrray = (List<ParseObject>) object.get("comments");
                            if (commentsArrray == null) {
                                commentsArrray = new ArrayList<>();
                            }
                            ParseObject postComment = new ParseObject("Comments");
                            postComment.put(ParseTables.Comments.USER, pUser);
                            postComment.put(ParseTables.Comments.TEXT, editText.getText().toString());
//                        postComment.put(ParseTables.Comments.VIDEO, currentVideoObjectComment);
                            postComment.put(ParseTables.Comments.CHANNELS, object);
                            commentsArrray.add(postComment);
                            object.put("comments", commentsArrray);
                            editText.setText("");
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(getActivity(), "Comment Added", Toast.LENGTH_SHORT).show();
                                    setUpComments(channelId);
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Please Login first !", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }
                    }

                    // object will be your game score
                } else {
                    // something went wrong
                }
            }
        });

    }
}
