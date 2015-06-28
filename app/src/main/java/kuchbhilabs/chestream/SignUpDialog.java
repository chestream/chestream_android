package kuchbhilabs.chestream;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by root on 27/6/15.
 */
public class SignUpDialog extends DialogFragment {

    public interface SignUpEvents {
        public void onUserSignedUp();
    }

    SignUpEvents mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.layout_signup_dialog, null);
        final EditText userNameEditText = (EditText) rootView.findViewById(R.id.username_edit_text);

        builder.setMessage("Sign Up")
                .setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO: upload details to parse
                        ParseUser parseUser = ParseUser.getCurrentUser();
                        parseUser.setUsername(userNameEditText.getText().toString());
                        try {
                            parseUser.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
                /*
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                }); */
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SignUpEvents) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SignUpEvents");
        }
    }
}
