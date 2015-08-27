package kuchbhilabs.chestream.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.fragments.channels.AboutChannelFragment;
import kuchbhilabs.chestream.fragments.channels.AllChannelFragment;
import kuchbhilabs.chestream.fragments.channels.ChannelFragment;
import kuchbhilabs.chestream.fragments.channels.ChannelModel;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivity2,
                new AllChannelFragment())
                .commit();
    }


}
