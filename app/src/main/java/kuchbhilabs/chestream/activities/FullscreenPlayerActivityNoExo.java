package kuchbhilabs.chestream.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import kuchbhilabs.chestream.R;

public class FullscreenPlayerActivityNoExo extends AppCompatActivity {

    VideoView vidView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_player_activity_no_exo);

        vidView = (VideoView)findViewById(R.id.myVideo);

                vidView.setMediaController(null);

        MediaController mediaController = new MediaController(getApplicationContext());
        mediaController.setAnchorView(vidView);
        mediaController.setMediaPlayer(vidView);
        vidView.setMediaController(mediaController);
//        vidView.setMediaController(null);

        vidView.setVideoURI(Uri.parse(getIntent().getStringExtra("url")));

        vidView.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fullscreen_player_activity_no_exo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
