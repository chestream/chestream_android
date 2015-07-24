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
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

import kuchbhilabs.chestream.MainActivity;
import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.externalapi.FacebookApi;
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
                                                Intent intent = new Intent(activity, MainActivity.class);
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
            Bundle bundle = bundles[0];
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

            try {
                if (currentUser.getSessionToken() != null) {
                    currentUser.save();
                } else {
                    currentUser.setPassword("ChestreamPasswordYo");
                    currentUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null)
                            {
                                Toast.makeText(getActivity(), "Welcome !",Toast.LENGTH_LONG).show();
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
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }
}
