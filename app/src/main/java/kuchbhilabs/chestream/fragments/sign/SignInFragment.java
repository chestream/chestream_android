package kuchbhilabs.chestream.fragments.sign;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import kuchbhilabs.chestream.MainActivity;
import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.externalapi.FacebookApi;
import kuchbhilabs.chestream.externalapi.ParseTables;
import kuchbhilabs.chestream.externalapi.TwitterApi;
import kuchbhilabs.chestream.helpers.Utilities;

/**
 * Created by root on 28/6/15.
 */
public class SignInFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static String token;

    private static final String TAG = "SignInFragment";
    private View rootView;
    private Button fbLogin, twLogin, gpLogin;
    private Button skip;

    private Activity activity;

    private static final int RC_SIGN_IN = 69;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    private ProgressDialog mProgressDialog;

    public SignInFragment() {} //Required empty constructor

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_sign_in, null);
        activity = getActivity();

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);

        fbLogin = (Button) rootView.findViewById(R.id.btn_fb);
        twLogin = (Button) rootView.findViewById(R.id.btn_tw);
        gpLogin = (Button) rootView.findViewById(R.id.btn_gp);
        skip = (Button) rootView.findViewById(R.id.btn_skip);

        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/HelveticaNeue-UltraLight.ttf");
        skip.setTypeface(typeface);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);
                activity.finish();
            }
        });

        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFacebookSignOn();
            }
        });

        twLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTwitterSignOn();
            }
        });
        gpLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doGoogleSignOn();
            }
        });
        return rootView;
    }

//    private void displayInit() {
//
//        fbLogin = (Button) rootView.findViewById(R.id.btn_fb);
//        twLogin = (Button) rootView.findViewById(R.id.btn_tw);
//        gpLogin = (Button) rootView.findViewById(R.id.btn_gp);
//        skip = (Button) rootView.findViewById(R.id.btn_skip);
//
//        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/HelveticaNeue-UltraLight.ttf");
//        skip.setTypeface(typeface);
//        skip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(activity, MainActivity.class);
//                startActivity(intent);
//                activity.finish();
//            }
//        });
//
//        fbLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doFacebookSignOn();
//            }
//        });
//
//        twLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doTwitterSignOn(v);
//            }
//        });
//        gpLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doGoogleSignOn(v);
//            }
//        });
//
//    }

    public void doFacebookSignOn() {
        List<String> permissions = Arrays.asList(
                "public_profile", "email");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(activity, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                Log.d(TAG, "done with login");
                if (err != null) {
                    Log.w(TAG, "pe = " + err.getCode() + err.getMessage());
                    Toast.makeText(activity, err.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (user == null) {
                    Log.w(TAG, "Uh oh. The user cancelled the Facebook login.");
                } else {
                    boolean fullyRegistered = user.getBoolean(ParseTables.Users.FULLY_REGISTERED);
                    if (user.isNew() || !fullyRegistered) {
                        Log.w(TAG, "User signed up and logged in through Facebook!");
                        FacebookApi.getFacebookData(new FacebookApi.FbGotDataCallback() {
                            @Override
                            public void gotData(final Bundle bundle) {
                                showSignUpFragment(bundle);
//                                new FetchUserPhotos(new FetchUserPhotos.PhotosFetcher() {
//                                    @Override
//                                    public String downloadCoverPhoto() {
//                                        return bundle.getString(ParseTables.Users.COVER);
//                                    }
//
//                                    @Override
//                                    public String downloadProfilePhoto() {
//                                        return bundle.getString(ParseTables.Users.IMAGE);
//                                    }
//                                }, getActivity()).start();
                            }
                        });
                        //TODO: download user data and sign up
                    } else {
                        Log.d(TAG, "Welcome back old user");
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                    }
                }
            }
        });
    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            mConnectionResult = result;
            if (mSignInClicked) {
                resolveSignInError();
            }
        }

    }

    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                getActivity().startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    public void showSignUpFragment(Bundle bundle) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        SignUpFragment newFragment = new SignUpFragment();
        newFragment.setArguments(bundle);
        transaction.replace(R.id.sign_in_container, newFragment,"SignUpFragment")
                .addToBackStack("SignIn").commit();
    }

    public void doTwitterSignOn() {
        mProgressDialog.setMessage("Signing in via Twitter");
        mProgressDialog.show();
        Log.w(TAG, "Hi twitter");

        ParseTwitterUtils.logIn(getActivity(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                Log.w(TAG, "twitterr user");
                mProgressDialog.dismiss();
                if (err != null) {
                    Log.w(TAG, "twitterr error"+err+err.getMessage());
                    err.printStackTrace();
                    return;
                }
                if (user == null) {
                    Log.w(TAG, "Uh oh. The user cancelled the Twitter login.");
                } else {
                    boolean fullyRegistered = false;
                    try {
                        fullyRegistered = user.getBoolean(ParseTables.Users.FULLY_REGISTERED);
                    } catch (Exception ignored) {
                    }

                    if (user.isNew() || (!fullyRegistered)) {
                        Log.w(TAG, "User signed up and logged in through Twitter!" + ParseTwitterUtils.getTwitter().getScreenName());
                        TwitterApi.getTwitterData(new TwitterApi.TwitterDataCallback() {
                            @Override
                            public void gotData(Bundle bundle) {
                                showSignUpFragment(bundle);
                            }
                        });
                    } else {
                        Log.d(TAG, "Welcome back old user");
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                    }
                }
            }
        });
    }

    public void doGoogleSignOn() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(final Bundle bundle) {
        mSignInClicked = false;
        Toast.makeText(getActivity(), "Google+ sign-in successful", Toast.LENGTH_LONG).show();
        final Bundle b = new Bundle();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    token = GoogleAuthUtil.getToken(
                            getActivity(),
                            Plus.AccountApi.getAccountName(mGoogleApiClient),
                            "oauth2:" + Scopes.PLUS_LOGIN);
                } catch (IOException | GoogleAuthException transientEx) {
                    // Network or server error, try later
                    Log.e(TAG, transientEx.toString());
                }
                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                Log.d(TAG, "Access token retrieved:" + token);
                final HashMap<String, Object> params = new HashMap<>();
                params.put("code", token);
                params.put("email", Plus.AccountApi.getAccountName(mGoogleApiClient));
                ParseCloud.callFunctionInBackground("accessGoogleUser", params, new FunctionCallback<Object>() {
                    @Override
                    public void done(Object returnObj, ParseException e) {
                        if (e == null) {
                            ParseUser.becomeInBackground(returnObj.toString(), new LogInCallback() {
                                public void done(ParseUser user, ParseException e) {
                                    if (user != null && e == null) {
                                        Log.i(TAG, "Google + user validated");
                                        boolean fullyRegistered = false;
                                        try {
                                            fullyRegistered = user.getBoolean(ParseTables.Users.FULLY_REGISTERED);
                                        } catch (Exception ignored) {
                                        }
                                        if (user.isNew() || !fullyRegistered) {
                                            Log.d(TAG, "We've got a new user");
                                            showSignUpFragment(bundle);
                                            //TODO: download user data and sign up
                                        } else {
                                            Log.d(TAG, "Welcome back old user");
                                            Intent intent = new Intent(activity, MainActivity.class);
                                            activity.startActivity(intent);
                                        }

                                        if (user.isNew() || (!fullyRegistered)) {
                                            try {
                                                if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                                                    final Person currentPerson = Plus.PeopleApi
                                                            .getCurrentPerson(mGoogleApiClient);
                                                    b.putString(ParseTables.Users.NAME, currentPerson.getDisplayName());
                                                    b.putString(ParseTables.Users.EMAIL, Plus.AccountApi.getAccountName(mGoogleApiClient));
                                                    if (currentPerson.getBirthday() != null) {
                                                        String reverseDate = new StringBuffer(currentPerson.getBirthday()).reverse().toString();
                                                        b.putString(ParseTables.Users.DOB, reverseDate);
                                                    }
                                                    showSignUpFragment(bundle);
//                                                    new FetchUserPhotos(new FetchUserPhotos.PhotosFetcher() {
//                                                        @Override
//                                                        public String downloadCoverPhoto() {
//                                                            Person.Cover cover = currentPerson.getCover();
//                                                            if (cover != null) {
//                                                                return cover.getCoverPhoto().getUrl();
//                                                            }
//                                                            return null;
//                                                        }
//
//                                                        @Override
//                                                        public String downloadProfilePhoto() {
//                                                            return currentPerson.getImage().getUrl();
//                                                        }
//                                                    }, getActivity()).start();

                                                }
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }

                                        } else {
                                            Log.d(TAG, "Welcome back old user");
                                            Intent intent = new Intent(activity, MainActivity.class);
                                            activity.startActivity(intent);
                                        }
                                    } else if (e != null) {
                                        e.printStackTrace();
                                        mGoogleApiClient.disconnect();
                                    } else
                                        Log.i(TAG, "The Google token could not be validated");
                                }
                            });
                        } else {
                            e.printStackTrace();
                            mGoogleApiClient.disconnect();
                        }
                    }
                });
            }
        }.execute();
    }


}
