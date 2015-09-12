package kuchbhilabs.chestream.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.fragments.channels.AboutChannelFragment;
import kuchbhilabs.chestream.fragments.channels.AllChannelAdapter;
import kuchbhilabs.chestream.fragments.channels.AllChannelFragment;
import kuchbhilabs.chestream.fragments.channels.ChannelFragment;
import kuchbhilabs.chestream.fragments.channels.ChannelModel;
import kuchbhilabs.chestream.widgets.SectionedGridRecyclerViewAdapter;

public class MainActivity2 extends AppCompatActivity {

    long startTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        startTime = System.currentTimeMillis();

        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity2,
                new AllChannelFragment())
                .commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        ParseAnalytics.trackEventInBackground("WholeAppUsageTime", dimensions);

        String username = "NA";
        String userid = "NA";
        String time2 = String.valueOf(elapsedTime);
        String activityname = "WholeAppUsageTime";
        String channel = "NA";
        ParseUser parseUser= ParseUser.getCurrentUser();
        if(parseUser!=null){
            username= parseUser.getUsername();
            userid=parseUser.getObjectId();
        }
        String url = "http://104.131.207.33/chestream_raw/analytics/analytics.gif?user_name="+username+"&user_id="+userid+"&channel="+channel+"&time="+time2+"&activity_name="+activityname;
        sendAnalytics(url);
    }

    public long getElapsedTimeSecs() {
        long elapsed = 0;
        elapsed = ((System.currentTimeMillis() - startTime) / 1000) % 60;
        return elapsed;
    }


    public void sendAnalytics(String URL){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("analytics", "Response = " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
}
