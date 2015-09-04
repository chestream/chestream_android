package kuchbhilabs.chestream.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseAnalytics;

import java.util.HashMap;
import java.util.Map;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.fragments.channels.AboutChannelFragment;
import kuchbhilabs.chestream.fragments.channels.AllChannelFragment;
import kuchbhilabs.chestream.fragments.channels.ChannelFragment;
import kuchbhilabs.chestream.fragments.channels.ChannelModel;

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
    }

    public long getElapsedTimeSecs() {
        long elapsed = 0;
        elapsed = ((System.currentTimeMillis() - startTime) / 1000) % 60;
        return elapsed;
    }


}
