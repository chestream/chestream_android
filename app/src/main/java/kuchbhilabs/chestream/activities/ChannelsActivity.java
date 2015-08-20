package kuchbhilabs.chestream.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.fragments.channels.ChannelFragment;

/**
 * Created by naman on 20/08/15.
 */
public class ChannelsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);

        getSupportFragmentManager().beginTransaction().replace(R.id.container,new ChannelFragment());

    }
}
