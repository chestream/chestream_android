package kuchbhilabs.chestream.helpers;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import kuchbhilabs.chestream.R;

/**
 * Created by naman on 04/07/15.
 */
public class LoadingProgress extends ImageView
{
    private ValueAnimator.AnimatorUpdateListener bTO;
    private ValueAnimator bTP;
    private BitmapDrawable bTQ;
    private Rect bTR;
    private float bTS;
    private Bitmap mBitmap;
    private Paint mPaint;

    public LoadingProgress(Context paramContext)
    {
        super(paramContext);
        init();
    }

    public LoadingProgress(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
        init();
    }

    public LoadingProgress(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
        super(paramContext, paramAttributeSet, paramInt);
        init();
    }

    private void init()
    {
        this.bTQ = ((BitmapDrawable)getResources().getDrawable(R.drawable.loading_bitmap));
        this.mBitmap = this.bTQ.getBitmap();
        this.mPaint = new Paint(6);
        this.bTR = new Rect(0, 0, 0, 0);
        this.bTO = new atl(this);
        LinearInterpolator localLinearInterpolator = new LinearInterpolator();
        this.bTP = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
        this.bTP.setInterpolator(localLinearInterpolator);
        this.bTP.setRepeatCount(-1);
        this.bTP.setRepeatMode(1);
        this.bTP.setDuration(1700L);
        Log.d("lol","ij here1");
    }

    public void hide()
    {
        clearAnimation();
        this.bTP.removeUpdateListener(this.bTO);
        setVisibility(GONE);
    }

    protected void onDraw(Canvas paramCanvas)
    {
        int j = getWidth();
        int k = this.mBitmap.getWidth();
        int i = (int)-this.bTS;
        while (i < j)
        {
            paramCanvas.drawBitmap(this.mBitmap, i, 0.0F, this.mPaint);
            i += k;
        }
        if (i - j > 0)
        {
            this.bTR.set(i, 0, i - j, this.mBitmap.getHeight());
            paramCanvas.drawBitmap(this.mBitmap, this.bTR, this.bTR, this.mPaint);
        }
    }

    public void show()
    {
        setVisibility(VISIBLE);
        this.bTP.addUpdateListener(this.bTO);
        this.bTP.start();
        Log.d("lol","ij here2");
    }

    class atl implements ValueAnimator.AnimatorUpdateListener
    {
        LoadingProgress loadingProgress;
        atl(LoadingProgress paramatk)
        {
            loadingProgress=paramatk;
        }

        public void onAnimationUpdate(ValueAnimator paramValueAnimator)
        {
             bTP.setCurrentFraction(paramValueAnimator.getAnimatedFraction());
             loadingProgress.setMinimumWidth(bTQ.getIntrinsicWidth());
             loadingProgress.invalidate();
        }
    }
}
