package kuchbhilabs.chestream.comments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import kuchbhilabs.chestream.LoginActivity;
import kuchbhilabs.chestream.NotificationReceiver;
import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.fragments.stream.VideoFragment;

/**
 * Created by naman on 20/06/15.
 */
public class CommentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CommentsAdapter adapter;
    private List<ParseObject> commentsList = new ArrayList<>();
    EditText editText;

    static CommentsAdapter commentsAdapter;
    ImageView sendComment;
    static TextView commentsLoading;
    View addCommentsFooter;
//    FloatingActionButton addCommentFab;
    private static final String TAG = "CommentsFragment";

    private BroadcastReceiver receiver;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.fragment_comments,null);

        recyclerView=(RecyclerView) v.findViewById(R.id.comments_recycler_view);
        editText=(EditText) v.findViewById(R.id.commentEditText);
        commentsLoading=(TextView) v.findViewById(R.id.commentsLoading);
        addCommentsFooter=v.findViewById(R.id.addCommentFooter);
//        addCommentFab=(FloatingActionButton) v.findViewById(R.id.addCommentFab);

        sendComment  =(ImageView) v.findViewById(R.id.send);

        activity = getActivity();

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setHasFixedSize(true);

        VideoFragment.slidingUpPanelLayout.setScrollView(recyclerView);

        commentsAdapter = new CommentsAdapter(getActivity(), new ArrayList<ParseObject>());
        recyclerView.setAdapter(commentsAdapter);

//        addCommentFab.attachToRecyclerView(recyclerView);

//        int footerHeight = 30;
//
//        QuickReturnRecyclerViewOnScrollListener scrollListener = new QuickReturnRecyclerViewOnScrollListener.Builder(QuickReturnViewType.FOOTER)
//                .footer(addCommentsFooter)
//                .minFooterTranslation(footerHeight)
//                .isSnappable(true)
//                .build();
//        recyclerView.setOnScrollListener(scrollListener);

//        setUpComments();

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser pUser = ParseUser.getCurrentUser();
                if ((pUser != null)
                        && (pUser.isNew())
//                        && (pUser.isAuthenticated())
                        && (pUser.getSessionToken() != null)
                /*&& (pUser.getBoolean(ParseTables.Users.FULLY_REGISTERED))*/) {
                    Log.d(TAG, pUser.getUsername() + pUser.getSessionToken());

                    ParseObject currentVideoObjectComment = VideoFragment.currentVideo ;
                    List<ParseObject> commentsArrray = (List<ParseObject>) currentVideoObjectComment.get("comments");
                    ParseObject postComment = new ParseObject("Comments");
                    postComment.put(ParseTables.Comments.USER, pUser);
                    postComment.put(ParseTables.Comments.TEXT, editText.getText().toString());
                    postComment.put(ParseTables.Comments.VIDEO, currentVideoObjectComment);
                    commentsArrray.add(postComment);
                    currentVideoObjectComment.put("comments", commentsArrray);
                    editText.setText("");
                    currentVideoObjectComment.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Toast.makeText(getActivity(), "Comment Added", Toast.LENGTH_SHORT).show();
                            setUpComments();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getActivity(), "Please Login first !", Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //TODO: Add a new comment to the current video
                    ParseUser pUser = ParseUser.getCurrentUser();
                    if ((pUser != null)
                            && (pUser.isAuthenticated())
//                            && (pUser.isNew())
                            && (pUser.getSessionToken() != null)
                /*&& (pUser.getBoolean(ParseTables.Users.FULLY_REGISTERED))*/) {
                        Log.d(TAG, pUser.getUsername() + pUser.getSessionToken());

                        ParseObject currentVideoObjectComment = VideoFragment.currentVideo;
                        List<ParseObject> commentsArrray = (List<ParseObject>) currentVideoObjectComment.get("comments");
                        ParseObject postComment = new ParseObject("Comments");
                        postComment.put("user", pUser);
                        postComment.put("comment", editText.getText().toString());
                        postComment.put("video_object", currentVideoObjectComment);
                        commentsArrray.add(postComment);
                        currentVideoObjectComment.put("comments", commentsArrray);
                        editText.setText("");
                        currentVideoObjectComment.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Toast.makeText(getActivity(), "Comment Added", Toast.LENGTH_SHORT).show();
                                setUpComments();
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Please Login first !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }

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
                        setUpComments();
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

    public static void setUpComments(){

        ParseObject currentVideoObjectComment = VideoFragment.currentVideo;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
        query.whereEqualTo("video_object", currentVideoObjectComment);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                Log.d(TAG, "Updating comments dataset");
                commentsAdapter.updateDataSet(list);
                commentsAdapter.notifyDataSetChanged();
                commentsLoading.setVisibility(View.GONE);

                VideoFragment.setCommentsCount(list.size() + " Comments");
                if (list.size()==0){
                    commentsLoading.setVisibility(View.VISIBLE);
                    commentsLoading.setText("Be the first to comment.");
                }
            }
        });


    }
}
