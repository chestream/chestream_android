package kuchbhilabs.chestream.fragments.sign;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;

import kuchbhilabs.chestream.MainActivity;
import kuchbhilabs.chestream.R;

/**
 * Created by root on 28/6/15.
 */
public class SignUpFragment extends Fragment {

    private EditText usernameEditText;
    private Button signUpButton;

    private Activity activity;

    public SignUpFragment() {} //Required empty constructor

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_sign_up, null);
        activity = getActivity();

        usernameEditText = (EditText) rootView.findViewById(R.id.username_edit_text);
        signUpButton = (Button) rootView.findViewById(R.id.button_sign_up);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                String username = usernameEditText.getText().toString();
                currentUser.setUsername(username);
                currentUser.saveInBackground();
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        return rootView;
    }
}
