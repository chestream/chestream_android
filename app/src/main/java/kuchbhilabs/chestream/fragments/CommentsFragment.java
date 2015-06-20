package kuchbhilabs.chestream.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.comments.Comments;
import kuchbhilabs.chestream.comments.CommentsAdapter;

/**
 * Created by naman on 20/06/15.
 */
public class CommentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CommentsAdapter adapter;
    private List<Comments> commentsList=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.fragment_comments,null);

        recyclerView=(RecyclerView) v.findViewById(R.id.comments_recycler_view);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setHasFixedSize(true);

        setUpCommentList();

        return v;
    }

    private void setUpCommentList() {

        if (commentsList == null) {
            commentsList = new ArrayList<Comments>();

        }

        for (int i = 0; i < 9; i++) {
            Comments comments = new Comments();

            comments.setAvatar("url");
            comments.setUsername("ygyg");
            comments.setComment("yfuyufuyggy");

            commentsList.add(comments);
        }

       adapter = new CommentsAdapter(getActivity(), commentsList);
        recyclerView.setAdapter(adapter);

    }
}
