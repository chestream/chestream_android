package kuchbhilabs.chestream.fragments.queue;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
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
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import kuchbhilabs.chestream.CompressionUploadService;
import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.helpers.AppLocationService;
import kuchbhilabs.chestream.helpers.CircularRevealView;
import kuchbhilabs.chestream.helpers.Helper;

/**
 * Created by naman on 20/06/15.
 */
public class QueueFragment extends Fragment {

    public static SimpleDraweeView gifView;

    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private FloatingActionButton upload;
    private CircularRevealView revealView;
    private View selectedView;
    android.os.Handler handler;
    private String TAG = "QueueFragment";

    String addressString  = "";
    AppLocationService appLocationService;

    Toolbar toolbar;
    SmoothProgressBar progressBar;

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


        toolbar=(Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Stream");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setElevation(5);


        upload = (FloatingActionButton) rootView.findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Image Source");
                builder.setItems(new CharSequence[] {"Gallery", "Camera"},
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                                        Intent chooser = Intent.createChooser(intent, "Choose a Video");
                                        startActivityForResult(chooser, 2);
                                        break;

                                    case 1:

                                        final Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                            final int color = Color.parseColor("#00bcd4");
                                            final Point p = Helper.getLocationInView(revealView, view);

                                            revealView.reveal(p.x, p.y, color, view.getHeight() / 2, 440, null);
                                            selectedView = view;

                                            handler=new Handler();
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
                            }
                        });
                builder.show();
            }
        });
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        loadFromParse();

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 1)
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

                loc.setText(addressString);

                Button dialogButton = (Button) dialog.findViewById(R.id.dialog_update_details);

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String dialogTitle = txt.getText().toString();
                        if (dialogTitle.isEmpty()) {
                            dialogTitle = " - ";
                        }

                        String dialogLocation = loc.getText().toString();
                        if (dialogLocation.isEmpty()) {
                            dialogLocation = " - ";
                        }

                        Uri videoUri = dataGet.getData();
                        Intent serviceIntent = new Intent(getActivity(), CompressionUploadService.class);
                        serviceIntent.putExtra("path", getRealPathFromUri(getActivity(), videoUri));
                        serviceIntent.putExtra("title", dialogTitle);
                        serviceIntent.putExtra("location", dialogLocation);
                        getActivity().startService(serviceIntent);


                        dialog.dismiss();
                    }
                });

                dialog.show();

            }

            if (requestCode == 2)
            {
                Uri videoUri = data.getData();
                Intent serviceIntent = new Intent(getActivity(), CompressionUploadService.class);
                serviceIntent.putExtra("path", getRealPathFromUri(getActivity(), videoUri));
                getActivity().startService(serviceIntent);
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
        ParseQuery<ParseObject> query = new ParseQuery<>(
                "Videos");

        query.orderByDescending(ParseTables.Videos.UPVOTE);
        query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                QueueVideosAdapter queueVideosAdapter = new QueueVideosAdapter(getActivity(), parseObjects);
                progressBar.setVisibility(View.GONE);
                recyclerView.setAdapter(queueVideosAdapter);
                queueVideosAdapter.notifyDataSetChanged();
            }
        });
    }

//    public void showSettingsAlert(String provider) {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//                getActivity());
//
//        alertDialog.setTitle(provider + " SETTINGS");
//
//        alertDialog
//                .setMessage(provider + " is not enabled! Want to go to settings menu?");
//
//        alertDialog.setPositiveButton("Settings",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(
//                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        getActivity().startActivity(intent);
//                    }
//                });
//
//        alertDialog.setNegativeButton("Cancel",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//        alertDialog.show();
//    }
}
