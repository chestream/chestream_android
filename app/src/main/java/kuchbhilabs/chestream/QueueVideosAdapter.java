package kuchbhilabs.chestream;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class QueueVideosAdapter extends RecyclerView.Adapter<QueueVideosAdapter.QVHolder> {

    public static class QVHolder extends RecyclerView.ViewHolder {
        ImageView uploaderImage;
        TextView videoTitle;
        TextView location;
        ImageButton upVote;
        ImageButton downVote;
        TextView totalVotes;

        QVHolder(View itemView) {
            super(itemView);

            uploaderImage = (ImageView)itemView.findViewById(R.id.profile_picture);
            videoTitle = (TextView)itemView.findViewById(R.id.video_title);
            location = (TextView)itemView.findViewById(R.id.video_location);
            totalVotes = (TextView)itemView.findViewById(R.id.video_score);
            upVote = (ImageButton)itemView.findViewById(R.id.up_vote);
            downVote = (ImageButton)itemView.findViewById(R.id.down_vote);

        }
    }
    List<QueueVideos> queueVideosList;

    QueueVideosAdapter(List<QueueVideos> queueVideosList){
        this.queueVideosList = queueVideosList;
    }

    @Override
    public QVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_item, parent, false);
        QVHolder qvh = new QVHolder(v);
        return qvh;

    }

    @Override
    public void onBindViewHolder(final QVHolder holder, int position) {
        holder.videoTitle.setText(queueVideosList.get(position).title);
        holder.location.setText(queueVideosList.get(position).location);
        holder.totalVotes.setText(queueVideosList.get(position).numberOfVotes + "");
        final int[] total_votes = {Integer.parseInt(holder.totalVotes.getText().toString())};

        holder.upVote.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int votes = total_votes[0] + 1;
                if (Math.abs(total_votes[0] - votes) == 1) {
                    holder.totalVotes.setText(votes + "");
                    holder.upVote.setBackgroundResource(R.drawable.upvote);
                    holder.downVote.setBackgroundResource(android.R.drawable.arrow_down_float);
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
                    holder.downVote.setBackgroundResource(R.drawable.downvote);
                    holder.upVote.setBackgroundResource(android.R.drawable.arrow_up_float);
                } else {

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return queueVideosList.size();
    }



}

