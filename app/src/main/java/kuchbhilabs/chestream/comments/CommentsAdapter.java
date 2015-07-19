package kuchbhilabs.chestream.comments;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;


import java.util.List;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.externalapi.ParseTables;

/**
 * Created by naman on 20/06/15.
 */
public class CommentsAdapter  extends RecyclerView.Adapter<CommentsAdapter.CommentsRowHolder> {

    private List<ParseObject> commentsList;
    private Context mContext;

    private static final String TAG = "CommentsAdapter";

    public CommentsAdapter(Context context, List<ParseObject> commentsList) {
        this.commentsList = commentsList;
        this.mContext = context;
    }

    public void updateDataSet (List<ParseObject> commentsList) {
        this.commentsList = commentsList;
    }

    @Override
    public CommentsRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comments, null);
        CommentsRowHolder ml = new CommentsRowHolder(v);
        return ml;
    }

    @Override
    public void onBindViewHolder(final CommentsRowHolder commentsRowHolder, int i) {
        ParseObject comment = commentsList.get(i);
        comment.getParseUser(ParseTables.Comments.USER).fetchIfNeededInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {

                Uri uri = Uri.parse("https://cdn1.iconfinder.com/data/icons/user-pictures/101/malecostume-512.png");
                try {
                    commentsRowHolder.username.setText(user.get(ParseTables.Users.USERNAME).toString());
                    Log.d("aas", user.getString(ParseTables.Users.USERNAME) + "  " + user.getString(ParseTables.Users.AVATAR));
                    uri = Uri.parse(user.getString(ParseTables.Users.AVATAR));
                } catch (NullPointerException e1) {
                    e1.printStackTrace();
                }
                commentsRowHolder.avatar.setImageURI(uri);
            }
        });
        commentsRowHolder.comment.setText(comment.getString(ParseTables.Comments.TEXT));
        ParseFile image = comment.getParseFile(ParseTables.Comments.IMAGE);
        commentsRowHolder.commentPhoto.setImageURI(Uri.parse(image.getUrl()));
        Log.d(TAG, "URL = " + image.getUrl());
    }

    @Override
    public int getItemCount() {
        return (null != commentsList ? commentsList.size() : 0);
    }

    public class CommentsRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected SimpleDraweeView avatar;
        protected TextView username,comment;
        private SimpleDraweeView commentPhoto;

        public CommentsRowHolder(View view) {
            super(view);
            this.avatar = (SimpleDraweeView) view.findViewById(R.id.profile_picture);
            this.username = (TextView) view.findViewById(R.id.username);
            this.comment = (TextView) view.findViewById(R.id.comment);
            commentPhoto = (SimpleDraweeView) view.findViewById(R.id.comment_photo);

            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }
    }
}

