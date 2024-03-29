package kuchbhilabs.chestream.fragments.queue;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import kuchbhilabs.chestream.ApplicationBase;
import kuchbhilabs.chestream.CompressionUploadService;
import kuchbhilabs.chestream.activities.LoginActivity;
import kuchbhilabs.chestream.NotificationReceiver;
import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.helpers.AppLocationService;
import kuchbhilabs.chestream.helpers.CircularRevealView;
import kuchbhilabs.chestream.helpers.Helper;
import kuchbhilabs.chestream.helpers.Utilities;
import kuchbhilabs.chestream.parse.ParseVideo;

/**
 * Created by naman on 20/06/15.
 */
public class QueueFragment extends Fragment {

    public static SimpleDraweeView gifView;

    private static QueueVideosAdapter queueVideosAdapter;
    private static SimpleSectionedRecyclerViewAdapter mSectionedAdapter;

    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    public static FloatingActionButton upload;
    private CircularRevealView revealView;
    private View selectedView;
    android.os.Handler handler;
    private String TAG = "QueueFragment";

    String addressString  = "";
    AppLocationService appLocationService;

    Toolbar toolbar;
    public static SmoothProgressBar progressBar;

    private BroadcastReceiver receiver;
    private static Activity activity;

    Tracker mTracker;

    public QueueFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_queue, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        revealView=(CircularRevealView) rootView.findViewById(R.id.reveal);
        progressBar=(SmoothProgressBar) rootView.findViewById(R.id.progress);
        gifView = (SimpleDraweeView) rootView.findViewById(R.id.preview_gif);

        activity = getActivity();

        ApplicationBase application = (ApplicationBase) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("QueueFragment");
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("OnCreate")
                .setAction("opened")
                .setLabel(Utilities.getUserEmail(activity))
                .build());


        toolbar=(Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NotificationReceiver.ACTION_VOTE);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String videoId = intent.getStringExtra(NotificationReceiver.EXTRA_VIDEO_ID);
                ParseQuery<ParseVideo> query = ParseQuery.getQuery(ParseVideo.class);
                query.whereEqualTo("objectId", videoId);
                query.findInBackground(new FindCallback<ParseVideo>() {
                    @Override
                    public void done(List<ParseVideo> list, ParseException e) {
                        ParseVideo updatedVideo = list.get(0);
                        List<ParseVideo> derp = queueVideosAdapter.getDataSet();
                        if (derp.contains(updatedVideo)) {
                            int location = derp.indexOf(updatedVideo);
                            queueVideosAdapter.updateItem(location);
                        } else {
                            Log.e(TAG, "An upvote has been done but the video is not in the list");
                        }
                    }
                });
                Log.d(TAG, "received");
            }
        };
        activity.registerReceiver(receiver, intentFilter);

        upload = (FloatingActionButton) rootView.findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                Tracker mTracker;
                ApplicationBase application = (ApplicationBase) getActivity().getApplication();
                mTracker = application.getDefaultTracker();
                mTracker.setScreenName("QueueFragment");
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Upload")
                        .setAction("click")
                        .setLabel(Utilities.getUserEmail(getActivity()))
                        .build());

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Video Source");
                builder.setItems(new CharSequence[]{"Gallery", "Camera"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ParseUser pUser = ParseUser.getCurrentUser();
                                if ((pUser != null)
                                        && (pUser.isAuthenticated())
//                                            && (pUser.isNew())
                                        && (pUser.getSessionToken() != null)
                /*&& (pUser.getBoolean(ParseTables.Users.FULLY_REGISTERED))*/) {

                                    switch (which) {
                                        case 0:
                                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                                            Intent chooser = Intent.createChooser(intent, "Choose a Video");
                                            startActivityForResult(chooser, 2);
                                            break;
                                        case 1:
                                            final Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                            takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);

                                            if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                                final int color = Color.parseColor("#00bcd4");
                                                final Point p = Helper.getLocationInView(revealView, view);

                                                revealView.reveal(p.x, p.y, color, view.getHeight() / 2, 440, null);
                                                selectedView = view;

                                                handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        startActivityForResult(takeVideoIntent, 1);
                                                        handler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                revealView.hide(p.x, p.y, android.R.color.transparent, 0, 330, null);
                                                            }
                                                        }, 300);

                                                    }
                                                }, 500);
                                            }
                                            break;
                                        default:
                                            break;
                                    }


                                } else {
                                    Toast.makeText(getActivity(), "Please Login first !", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                builder.show();
            }
        });



        recyclerView.setHasFixedSize(true);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //In background because some important shit is being downloading inside the constructor
                queueVideosAdapter = new QueueVideosAdapter(getActivity(), new ArrayList<ParseVideo>(),false);

                List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                        new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();

                sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0, "Currently Playing"));
                sections.add(new SimpleSectionedRecyclerViewAdapter.Section(1, "Up Next in Queue"));

                SimpleSectionedRecyclerViewAdapter.Section[] dummy =
                        new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
                mSectionedAdapter = new
                        SimpleSectionedRecyclerViewAdapter(getActivity(), R.layout.queue_section_header,
                        R.id.section_text, queueVideosAdapter);
                mSectionedAdapter.setSections(sections.toArray(dummy));
            }
        });

        llm = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);

        loadFromParse();

        return rootView;
    }

    @Override
    public void onDestroy() {
        try {
            activity.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 1 || requestCode == 2)
            {
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if(isNetworkEnabled)
                {
                    appLocationService = new AppLocationService(
                            getActivity());

                    Location nwLocation = appLocationService
                            .getLocation(LocationManager.NETWORK_PROVIDER);

                    if (nwLocation != null) {
                        double latitude = nwLocation.getLatitude();
                        double longitude = nwLocation.getLongitude();

                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(getActivity(), Locale.getDefault());

                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            addressString = state+ ", "+ country;

                            Log.d("loc",addressString);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(getActivity(), "Turn on the Location !", Toast.LENGTH_LONG).show();
                    }
                }

                else if (isGPSEnabled){
                    Location gpsLocation = appLocationService
                            .getLocation(LocationManager.GPS_PROVIDER);
                    if (gpsLocation != null) {
                        double latitude = gpsLocation.getLatitude();
                        double longitude = gpsLocation.getLongitude();

                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(getActivity(), Locale.getDefault());

                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            addressString = state+ ", "+ country;

                            Log.d("loc",addressString);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(getActivity(), "Turn on the Location !", Toast.LENGTH_LONG).show();
                    }
            }
                else{
                    Toast.makeText(getActivity(), "Turn on the Location !", Toast.LENGTH_LONG).show();
                }

                final Intent dataGet = data;

                final Dialog dialog = new Dialog(getActivity());

                //tell the Dialog to use the dialog.xml as it's layout description
                dialog.setContentView(R.layout.post_video_dialog);
                dialog.setTitle("Update Details");
                dialog.setCancelable(false);

                final EditText txt = (EditText) dialog.findViewById(R.id.dialog_title);
                final EditText loc = (EditText) dialog.findViewById(R.id.dialog_location);
                final TextView locStatic = (TextView) dialog.findViewById(R.id.location_static);
                final TextView titleStatic = (TextView) dialog.findViewById(R.id.title_static);

                loc.setText(addressString);

                Button dialogButton = (Button) dialog.findViewById(R.id.dialog_update_details);

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        String dialogTitle = txt.getText().toString();
                        String dialogLocation = loc.getText().toString();

                         if (dialogTitle.isEmpty() ) {
                            Toast.makeText(getActivity(), "Please enter the title",Toast.LENGTH_LONG).show();
                            titleStatic.setTextColor(Color.RED);
                        }

                        else if ( dialogLocation.isEmpty()) {
                            Toast.makeText(getActivity(), "Please enter the location",Toast.LENGTH_LONG).show();
                            locStatic.setTextColor(Color.RED);
                        }

                        else
                        {
                            Uri videoUri = dataGet.getData();
                            Intent serviceIntent = new Intent(getActivity(), CompressionUploadService.class);
                            serviceIntent.putExtra("path", getRealPathFromUri(getActivity(), videoUri));
                            serviceIntent.putExtra("title", dialogTitle);
                            serviceIntent.putExtra("location", dialogLocation);
                            getActivity().startService(serviceIntent);

                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();

            }
        }
    }


    private String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public  void loadFromParse() {
        ParseQuery<ParseVideo> query = ParseQuery.getQuery(ParseVideo.class);

        query.orderByDescending(ParseTables.Videos.UPVOTE);
        query.whereEqualTo(ParseTables.Videos.PLAYED, false);
        query.whereEqualTo(ParseTables.Videos.COMPILED, true);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<ParseVideo>() {
            @Override
            public void done(final List<ParseVideo> parseObjects, ParseException e) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        while (queueVideosAdapter == null) {
                            //queueVideosAdapter is instantiated in another thread. Make sure it's not null
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.GONE);
                                queueVideosAdapter.updateDataSet(parseObjects);
                                recyclerView.setAdapter(mSectionedAdapter);
                                queueVideosAdapter.notifyDataSetChanged();
                                mSectionedAdapter.notifyDataSetChanged();
                                bounceUploadButton();
                            }
                        });
                    }
                });
            }
        });
    }

    public static void updateCurrentlyPlaying(){
        queueVideosAdapter.removeItem(0);
        queueVideosAdapter.notifyItemRemoved(0);
    }

    public static void bounceUploadButton(){
        upload.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.pop_out));
    }
}
