package kuchbhilabs.chestream.fragments.sign;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

import kuchbhilabs.chestream.MainActivity;
import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.externalapi.ParseTables;

/**
 * Created by root on 28/6/15.
 */
public class SignInFragment extends Fragment {

    private static final String TAG = "SignInFragment";

    private Button fbLogin;
    private Button skip;

    private Activity activity;

    public SignInFragment() {} //Required empty constructor

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_sign_in, null);
        activity = getActivity();

        fbLogin = (Button) rootView.findViewById(R.id.btn_fb);
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

        return rootView;
    }

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
                        Log.d(TAG, "We've got a new user");
                        showSignUpFragment();
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

    public void showSignUpFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        SignUpFragment newFragment = new SignUpFragment();
        transaction.replace(R.id.sign_in_container, newFragment,"SignUpFragment")
                .addToBackStack("SignIn").commit();
    }
}
