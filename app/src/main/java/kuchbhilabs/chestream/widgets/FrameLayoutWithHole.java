package kuchbhilabs.chestream.widgets;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

import kuchbhilabs.chestream.R;

public class FrameLayoutWithHole extends FrameLayout {
    private TextPaint mTextPaint;
    private Context mActivity;
    private Paint mEraser;

    Bitmap mEraserBitmap;
    private Canvas mEraserCanvas;
    private Paint mPaint;
    private Paint transparentPaint;
    private View mViewHole;
    private int mRadius;
    private int [] mPos;
    private float mDensity;

    private int viewID=-1;

    private ArrayList<AnimatorSet> mAnimatorSetArrayList;

    public FrameLayoutWithHole(Context context, View view) {
        super(context);
        mActivity = context;
        mViewHole = view;
        init(null, 0);


    }

    public FrameLayoutWithHole(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivity = context;
        init(attrs, 0);
    }

    public FrameLayoutWithHole(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mActivity = context;
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        final TypedArray a = mActivity.obtainStyledAttributes(
                attrs, R.styleable.FrameLayoutWithHole, defStyle, 0);

        if (a!=null) {
            viewID = a.getResourceId(R.styleable.FrameLayoutWithHole_holeView, -1);
            mViewHole = findViewById(viewID);
            a.recycle();
        }

        int [] pos = new int[2];
        if (mViewHole!=null)
        mViewHole.getLocationOnScreen(pos);
        mPos = pos;

        mDensity = mActivity.getResources().getDisplayMetrics().density;
        int padding = (int)(20 * mDensity);

        if (mViewHole.getHeight() > mViewHole.getWidth()) {
            mRadius = mViewHole.getHeight()/2 + padding;
        } else {
            mRadius = mViewHole.getWidth()/2 + padding;
        }

        setWillNotDraw(false);
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        Point size = new Point();
        DisplayMetrics metrics = mActivity.getResources().getDisplayMetrics();
        size.x = metrics.widthPixels;
        size.y = metrics.heightPixels;

        mEraserBitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        mEraserCanvas = new Canvas(mEraserBitmap);

        mPaint = new Paint();
        mPaint.setColor(0xcc000000);
        transparentPaint = new Paint();
        transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        mEraser = new Paint();
        mEraser.setColor(0xFFFFFFFF);
        mEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));


    }

    private boolean mCleanUpLock = false;


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        /* cleanup reference to prevent memory leak */
        mEraserCanvas.setBitmap(null);
        mEraserBitmap = null;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mEraserBitmap.eraseColor(Color.TRANSPARENT);
        mEraserCanvas.drawColor(Color.RED);
        mEraserCanvas.drawCircle(mPos[0] + mViewHole.getWidth() / 2, mPos[1] + mViewHole.getHeight() / 2, mRadius, mEraser);

        canvas.drawBitmap(mEraserBitmap, 0, 0, null);

    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

}