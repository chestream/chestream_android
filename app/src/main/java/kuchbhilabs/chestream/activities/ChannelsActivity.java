package kuchbhilabs.chestream.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import kuchbhilabs.chestream.R;
import kuchbhilabs.chestream.fragments.channels.ChannelFragment;
import kuchbhilabs.chestream.fragments.channels.ChannelModel;

/**
 * Created by naman on 20/08/15.
 */
public class ChannelsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);

        getSupportFragmentManager().beginTransaction().replace(R.id.container,
                new ChannelFragment().newInstance((ChannelModel) getIntent().getSerializableExtra("channel")))
                .commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
