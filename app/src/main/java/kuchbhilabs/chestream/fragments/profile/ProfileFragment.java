package kuchbhilabs.chestream.fragments.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import kuchbhilabs.chestream.LoginActivity;
import kuchbhilabs.chestream.MainActivity;
import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.helpers.Helper;
import kuchbhilabs.chestream.parse.ParseVideo;

/**
 * Created by naman on 12/07/15.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    public static SimpleDraweeView gifView;

    Toolbar toolbar;
    ImageView profile;
    FrameLayout header;
    RecyclerView recyclerView;
    MyVideosAdapter adapter;
    TextView username ;
    ParseUser pUser;
    LinearLayout emptyLayout;
    Button emptyLayoutButton;
    TextView emptyLayoutText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_profile, null);

        toolbar=(Toolbar) rootView.findViewById(R.id.toolbar);
        profile=(ImageView) rootView.findViewById(R.id.profile_picture);
        header=(FrameLayout) rootView.findViewById(R.id.header);
        emptyLayout=(LinearLayout) rootView.findViewById(R.id.emptyLayout);
        emptyLayoutText=(TextView) rootView.findViewById(R.id.emptyLayoutText);
        recyclerView=(RecyclerView) rootView.findViewById(R.id.recycler_view);
        username=(TextView) rootView.findViewById(R.id.username);
        emptyLayoutButton=(Button) rootView.findViewById(R.id.emptyLayoutButton);
        gifView = (SimpleDraweeView) rootView.findViewById(R.id.preview_gif);

        pUser = ParseUser.getCurrentUser() ;

        if ((pUser != null)
                && (pUser.isAuthenticated())
                && (pUser.getSessionToken() != null)
                && (pUser.getBoolean(ParseTables.Users.FULLY_REGISTERED))) {

            username.setText(pUser.getUsername());

            ImageLoader.getInstance().displayImage(pUser.getString("avatar"), profile,
                    new DisplayImageOptions.Builder().cacheInMemory(true)
                            .cacheOnDisk(true)
                            .resetViewBeforeLoading(true)
                            .displayer(new FadeInBitmapDisplayer(400))
                            .build(),new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            header.setBackground(Helper.createBlurredImageFromBitmap(loadedImage,getActivity(),8));
                        }
                    });
//            profile.setImageURI(Uri.parse(pUser.getString("avatar")));

            ParseQuery<ParseVideo> query = ParseQuery.getQuery(ParseVideo.class);
            query.orderByDescending(ParseTables.Videos.UPVOTE);
            query.whereEqualTo(ParseTables.Videos.COMPILED, true);
            query.whereEqualTo("user", pUser);
            query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.findInBackground(new FindCallback<ParseVideo>() {
                @Override
                public void done(List<ParseVideo> parseObjects, ParseException e) {
                    if(parseObjects.isEmpty())
                    {
                        Log.d(TAG, "Empty");
                        recyclerView.setVisibility(View.GONE);
                        emptyLayout.setVisibility(View.VISIBLE);

                    }
                    else {
                        emptyLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        adapter = new MyVideosAdapter(getActivity(), new ArrayList<ParseVideo>());
                        adapter.updateDataSet(parseObjects);
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                    }
                }
            });
            emptyLayoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.movetoQueueAndUploadVideo();
                }
            });

        }
        else{
            recyclerView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            username.setText("");
            emptyLayoutText.setText("Login to view your Profile and upload videos.");
            emptyLayoutButton.setText("Login");
            emptyLayoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }


        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);


        return rootView;
    }
}
