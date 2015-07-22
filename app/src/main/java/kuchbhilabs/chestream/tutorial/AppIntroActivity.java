package kuchbhilabs.chestream.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import kuchbhilabs.chestream.LoginActivity;


/**
 * Created by naman on 15/06/15.
 */

public class AppIntroActivity extends IntroActivity {


    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(new Slide1(), getApplicationContext());
        addSlide(new Slide2(), getApplicationContext());
        addSlide(new Slide3(), getApplicationContext());

    }

    private void loadMainActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSkipPressed() {
        prefs = getSharedPreferences("chestream", MODE_PRIVATE);
        prefs.edit().putBoolean("firstrun", false).commit();
        loadMainActivity();
    }

    @Override
    public void onDonePressed() {
        prefs = getSharedPreferences("chestream", MODE_PRIVATE);
        prefs.edit().putBoolean("firstrun", false).commit();
        loadMainActivity();
    }

    public void getStarted(View v){
        loadMainActivity();
    }
}
