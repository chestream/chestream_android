package kuchbhilabs.chestream.fragments.stream;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Created by omerjerk on 27/7/15.
 */
public class HackyFrameLayout extends FrameLayout {

    private Context context;

    public HackyFrameLayout(Context context) {
        super(context);
        this.context = context;
    }

    public HackyFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public HackyFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Toast.makeText(context, "Touched!", Toast.LENGTH_SHORT).show();
        return super.onInterceptTouchEvent(ev);
    }
}
