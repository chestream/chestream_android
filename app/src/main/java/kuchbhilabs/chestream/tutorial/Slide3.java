package kuchbhilabs.chestream.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kuchbhilabs.chestream.R;

/**
 * Created by naman on 15/06/15.
 */
public class Slide3 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.intro_slide3, container, false);
        return v;
    }
}