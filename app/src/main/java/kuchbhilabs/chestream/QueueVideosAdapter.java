package kuchbhilabs.chestream;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class QueueVideosAdapter extends RecyclerView.Adapter<QueueVideosAdapter.QVHolder> {


    private static boolean isSpeakButtonLongPressed = false;

    AlertDialog.Builder builder;
    AlertDialog dialog;
    Context context;

    public static class QVHolder extends RecyclerView.ViewHolder {
        ImageView uploaderImage;
        TextView videoTitle;
        TextView location;
        TextView username;
        ImageButton upVote;
        ImageButton downVote;
        TextView totalVotes;
        LinearLayout rootLayout;

        QVHolder(final View itemView) {
            super(itemView);

            uploaderImage = (ImageView)itemView.findViewById(R.id.profile_picture);
            videoTitle = (TextView)itemView.findViewById(R.id.video_title);
            location = (TextView)itemView.findViewById(R.id.video_location);
            username = (TextView)itemView.findViewById(R.id.username);
            totalVotes = (TextView)itemView.findViewById(R.id.video_score);
            upVote = (ImageButton)itemView.findViewById(R.id.up_vote);
            downVote = (ImageButton)itemView.findViewById(R.id.down_vote);
            rootLayout = (LinearLayout)itemView.findViewById(R.id.root_layout);
        }
    }
    List<QueueVideos> queueVideosList;

    public QueueVideosAdapter(Context context2,List<QueueVideos> queueVideosList){
        this.queueVideosList = queueVideosList;
        context=context2;
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
    public void onBindViewHolder(final QVHolder holder, int position) {
        holder.videoTitle.setText(queueVideosList.get(position).title);
        holder.username.setText(queueVideosList.get(position).username);
        holder.location.setText(queueVideosList.get(position).location);

        holder.totalVotes.setText(queueVideosList.get(position).numberOfVotes + "");
//        holder.uploaderImage.
        final int[] total_votes = {Integer.parseInt(holder.totalVotes.getText().toString())};

        holder.upVote.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int votes = total_votes[0] + 1;
                if (Math.abs(total_votes[0] - votes) == 1) {
                    holder.totalVotes.setText(votes + "");
                    holder.upVote.getBackground().setAlpha(165);
                    holder.downVote.getBackground().setAlpha(65);
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
                    holder.downVote.getBackground().setAlpha(165);
                    holder.upVote.getBackground().setAlpha(65);
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


                    dialog.show();
//Overriding the handler immediately after show is probably a better approach than OnShowListener as described below


                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setSelected(false);
                    Log.d("press", "release");
                    dialog.dismiss();
                    return true;
                } else
                    return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return queueVideosList.size();
    }



}

