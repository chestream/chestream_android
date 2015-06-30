package kuchbhilabs.chestream.comments;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import kuchbhilabs.chestream.R;

/**
 * Created by naman on 20/06/15.
 */
public class CommentsAdapter  extends RecyclerView.Adapter<CommentsAdapter.CommentsRowHolder> {

    private List<Comments> commentsList;
    private Context mContext;

    public CommentsAdapter(Context context, List<Comments> commentsList) {
        this.commentsList = commentsList;
        this.mContext = context;
    }

    @Override
    public CommentsRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comments, null);
        CommentsRowHolder ml = new CommentsRowHolder(v);
        return ml;
    }

    @Override
    public void onBindViewHolder(CommentsRowHolder commentsRowHolder, int i) {
        Comments commentItem = commentsList.get(i);

       // commentsRowHolder.avatar.setImageResource(commentItem.getAvatar());
        commentsRowHolder.username.setText(commentItem.getUsername());
        commentsRowHolder.comment.setText(commentItem.getComment());
            Uri uri = Uri.parse(commentItem.getAvatar());
        commentsRowHolder.avatar.setImageURI(uri);


    }

    @Override
    public int getItemCount() {
        return (null != commentsList ? commentsList.size() : 0);
    }

    public class CommentsRowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected SimpleDraweeView avatar;
        protected TextView username,comment;

        public CommentsRowHolder(View view) {
            super(view);
            this.avatar = (SimpleDraweeView) view.findViewById(R.id.profile_picture);
            this.username = (TextView) view.findViewById(R.id.username);
            this.comment = (TextView) view.findViewById(R.id.comment);

            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

        }

    }


}

