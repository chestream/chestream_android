package kuchbhilabs.chestream.externalapi;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 27/6/15.
 */
public class FacebookApi {

    private static final String TAG = "FacebookApi";

    public static void getFacebookData(final FbGotDataCallback fgdc) {

        final Bundle b = new Bundle();
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        Log.d(TAG, "" + object.toString());
                        try {
                            String id = object.getString("id");
                            b.putString(ParseTables.Users.AVATAR,"https://graph.facebook.com/" + id + "/picture??width=300&&height=300");
                            b.putString(ParseTables.Users.NAME, object.getString("name"));
                            b.putString(ParseTables.Users.EMAIL, object.getString("email"));

                            fgdc.gotData(b);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,id,cover,email,birthday");
        request.setParameters(parameters);
        request.executeAsync();
        return;
    }

    public interface FbGotDataCallback {
        public void gotData(Bundle b);
    }
}
