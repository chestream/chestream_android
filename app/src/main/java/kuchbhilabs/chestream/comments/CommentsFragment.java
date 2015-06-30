package kuchbhilabs.chestream.comments;

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
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import kuchbhilabs.chestream.R;

/**
 * Created by naman on 20/06/15.
 */
public class CommentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CommentsAdapter adapter;
    private List<ParseObject> commentsList = new ArrayList<>();
    EditText editText;
    private TextView commentsCount;
    static CommentsAdapter commentsAdapter;

    private static final String TAG = "CommentsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.fragment_comments,null);

        recyclerView=(RecyclerView) v.findViewById(R.id.comments_recycler_view);
        editText=(EditText) v.findViewById(R.id.commentEditText);
        commentsCount=(TextView) v.findViewById(R.id.commentsCount);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setHasFixedSize(true);

        commentsAdapter = new CommentsAdapter(getActivity(), new ArrayList<ParseObject>());
        recyclerView.setAdapter(commentsAdapter);

        setUpComments();


        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    //TODO: Add a new comment to the current video
                    }

                return false;
            }
        });


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

    private void setUpComments(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                Log.d(TAG, "Updating comments dataset");
                commentsAdapter.updateDataSet(list);
                commentsAdapter.notifyDataSetChanged();
                commentsCount.setText(list.size()+ " Comments");
            }
        });
    }
}
