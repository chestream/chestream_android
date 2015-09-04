package kuchbhilabs.chestream.fragments.sign;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kuchbhilabs.chestream.ApplicationBase;
import kuchbhilabs.chestream.activities.MainActivity;
import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.activities.MainActivity2;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.helpers.Utilities;

/**
 * Created by root on 28/6/15.
 */
public class SignUpFragment extends Fragment {
    Bundle bundle;
    String email= null;
    private EditText usernameEditText;
    private Button signUpButton;
    private SimpleDraweeView userAvatar;

    long startTime = 0;
    public  static String TAG = "SignUpFragment";

    private Activity activity;
    private ProgressDialog mProgressDialog;
    Bundle b;
    String username;
    public SignUpFragment() {} //Required empty constructor

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_sign_up, null);
        activity = getActivity();

        Tracker mTracker;
        ApplicationBase application = (ApplicationBase) activity.getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("SignUpFragment");
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("OnCreate")
                .setAction("opened")
                .setLabel(Utilities.getUserEmail(activity))
                .build());


        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);

        usernameEditText = (EditText) rootView.findViewById(R.id.username_edit_text);

        b = getArguments();
        if (b.getString(ParseTables.Users.USERNAME) != null) {
            usernameEditText.setText(b.getString(ParseTables.Users.USERNAME));
        }

        userAvatar = (SimpleDraweeView) rootView.findViewById(R.id.user_avatar_sign_up);
        userAvatar.setImageURI(Uri.parse(b.getString(ParseTables.Users.AVATAR)));

//        FacebookApi.getFacebookData(new FacebookApi.FbGotDataCallback() {
//            @Override
//            public void gotData(Bundle b) {
//                Uri avatarUri = Uri.parse(b.getString(ParseTables.Users.AVATAR));
//                userAvatar.setImageURI(avatarUri);
//            }
//        });
        signUpButton = (Button) rootView.findViewById(R.id.button_sign_up);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEditText.getText().toString();
                email = b.getString(ParseTables.Users.EMAIL);
                String type = b.getString("type");

                if(type.equals("facebook"))
                {
                    Toast.makeText(getActivity(), "Sign Up", Toast.LENGTH_SHORT).show();
                    Log.d("score", "The getFirst request failed.");
                    bundle = b;
                    b.putString(ParseTables.Users.USERNAME, username);
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", username);
                    query.findInBackground(new FindCallback<ParseUser>() {
                        public void done(List<ParseUser> object, ParseException e) {
                            if (object.isEmpty()) {
                                new PushUserIntoParse().execute(bundle);
                            } else {
                                Toast.makeText(getActivity(), "Please choose another username and try again !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                if(type.equals("google"))
                {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("email_id", email);
                    query.findInBackground(new FindCallback<ParseUser>() {
                        public void done(List<ParseUser> object, ParseException e) {
                            if (object.isEmpty()) {
                                Toast.makeText(getActivity(), "Sign Up", Toast.LENGTH_SHORT).show();
                                Log.d("score", "The getFirst request failed.");
                                bundle = b;
                                b.putString(ParseTables.Users.USERNAME, username);
                                ParseQuery<ParseUser> query = ParseUser.getQuery();
                                query.whereEqualTo("username", username);
                                query.findInBackground(new FindCallback<ParseUser>() {
                                    public void done(List<ParseUser> object, ParseException e) {
                                        if (object.isEmpty()) {
                                            new PushUserIntoParse().execute(bundle);
                                        }
                                        else
                                        {
                                            Toast.makeText(getActivity(), "Please choose another username and try again !",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                    Toast.makeText(getActivity(), "Log In", Toast.LENGTH_SHORT).show();
                                    ParseUser.logInInBackground(object.get(0).getUsername(), getString(R.string.pw), new LogInCallback() {
                                        public void done(ParseUser user, ParseException e) {
                                            if (user != null) {
                                                // Hooray! The user is logged in.
                                                mProgressDialog.dismiss();
                                                Intent intent = new Intent(activity, MainActivity2.class);
                                                activity.startActivity(intent);
                                                activity.finish();
                                            } else {
                                                Log.d("login", e.getMessage() + e);
                                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                // Signup failed. Look at the ParseException to see what happened.
                                            }
                                        }
                                    });
                                }
                                Log.d("score", "Retrieved the object.");
                            }
                    });
                }
                if(type.equals("twitter"))
                {
                    Toast.makeText(getActivity(), "Sign Up", Toast.LENGTH_SHORT).show();
                    Log.d("score", "The getFirst request failed.");
                    bundle = b;
                    b.putString(ParseTables.Users.USERNAME, username);
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.whereEqualTo("username", username);
                    query.findInBackground(new FindCallback<ParseUser>() {
                        public void done(List<ParseUser> object, ParseException e) {
                            if (object.isEmpty()) {
                                new PushUserIntoParse().execute(bundle);
                            } else {
                                Toast.makeText(getActivity(), "Please choose another username and try again !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });

        return rootView;
    }

    private class PushUserIntoParse extends AsyncTask<Bundle, Void, Bundle> {
        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Signing up...");
            mProgressDialog.show();
        }

        @Override
        protected Bundle doInBackground(Bundle... bundles) {
            final Bundle bundle = bundles[0];
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (bundle.getString(ParseTables.Users.NAME) != null) {
                currentUser.put(ParseTables.Users.NAME, bundle.getString(ParseTables.Users.NAME));
            }
            if (bundle.getString(ParseTables.Users.EMAIL) != null) {
//                currentUser.put(ParseTables.Users.EMAIL, bundle.getString(ParseTables.Users.EMAIL));
                currentUser.setEmail(bundle.getString(ParseTables.Users.EMAIL));
            }
            if (bundle.getString(ParseTables.Users.EMAIL) == null) {
//                currentUser.put(ParseTables.Users.EMAIL, bundle.getString(ParseTables.Users.EMAIL));
                currentUser.setEmail(Utilities.getUserEmail(getActivity()));
            }
            if (bundle.getString(ParseTables.Users.DOB) != null) {
                currentUser.put(ParseTables.Users.DOB, bundle.getString(ParseTables.Users.DOB));
            }
            if (bundle.getString(ParseTables.Users.USERNAME) != null) {
                currentUser.setUsername(bundle.getString(ParseTables.Users.USERNAME));
            }
            if (bundle.getString(ParseTables.Users.AVATAR) != null) {
                currentUser.put(ParseTables.Users.AVATAR, bundle.getString(ParseTables.Users.AVATAR));
            }

            currentUser.put(ParseTables.Users.FULLY_REGISTERED, true);

            Tracker mTracker;
            ApplicationBase application = (ApplicationBase) getActivity().getApplication();
            mTracker = application.getDefaultTracker();
            mTracker.setScreenName("SignUpFragment");
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Signup")
                    .setAction("click")
                    .setLabel(ParseTables.Users.USERNAME)
                    .build());


            try {
                if (currentUser.getSessionToken() != null) {
                    currentUser.save();

                    Toast.makeText(getActivity(), "Welcome !",Toast.LENGTH_LONG).show();
                    ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
                    parseInstallation.put("avatar",  bundle.getString(ParseTables.Users.AVATAR));
                    parseInstallation.put("username", bundle.getString(ParseTables.Users.USERNAME));
                    parseInstallation.put("email", bundle.getString(ParseTables.Users.EMAIL));
                    parseInstallation.put("local_email", Utilities.getUserEmail(getActivity()));
                    parseInstallation.put("user", ParseUser.getCurrentUser());
                    parseInstallation.saveInBackground();

                } else {
                    currentUser.setPassword("ChestreamPasswordYo");
                    currentUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null)
                            {
                                Toast.makeText(getActivity(), "Welcome !",Toast.LENGTH_LONG).show();
                                ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
                                parseInstallation.put("avatar",  bundle.getString(ParseTables.Users.AVATAR));
                                parseInstallation.put("username", bundle.getString(ParseTables.Users.USERNAME));
                                parseInstallation.put("email", bundle.getString(ParseTables.Users.EMAIL));
                                parseInstallation.put("local_email", Utilities.getUserEmail(getActivity()));
                                parseInstallation.put("user", ParseUser.getCurrentUser());
                                parseInstallation.saveInBackground();
                            }
                            else{
                                Toast.makeText(getActivity(), "Please choose another username and try again !",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
//                    currentUser.saveInBackground();
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            return bundle;
        }

        @Override
        protected void onPostExecute(Bundle b) {
            mProgressDialog.dismiss();
            Intent intent = new Intent(activity, MainActivity2.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        Map<String, String> dimensions = new HashMap<String, String>();
        int elapsedTime = (int) getElapsedTimeSecs();
        String time = " - ";
        if(elapsedTime<15){
            time = "0-15";
        }
        else if(elapsedTime>=15 && elapsedTime<30){
            time = "15-30";
        }
        else if(elapsedTime>=30 && elapsedTime<60){
            time = "30-60";
        }
        else if(elapsedTime>=60 && elapsedTime<90){
            time = "60-90";
        }
        else if(elapsedTime>=90 && elapsedTime<120){
            time = "90-120";
        }
        else if(elapsedTime>=120 && elapsedTime<150){
            time = "120-150";
        }
        else if(elapsedTime>=150 && elapsedTime<180){
            time = "150-180";
        }
        else if(elapsedTime>=180 && elapsedTime<210){
            time = "180-210";
        }
        else if(elapsedTime>=210 && elapsedTime<240){
            time = "210-240";
        }
        else {
            time = ">240";
        }
        dimensions.put("time", time);
        ParseAnalytics.trackEventInBackground(TAG, dimensions);
    }

    public long getElapsedTimeSecs() {
        long elapsed = 0;
        elapsed = ((System.currentTimeMillis() - startTime) / 1000) % 60;
        return elapsed;
    }

}
