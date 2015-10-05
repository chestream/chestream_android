//package kuchbhilabs.chestream.widgets;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.MediaController;
//
//import java.lang.reflect.Field;
//
//class CustomMediaController extends MediaController
//{
//
//    public CustomMediaController(Context context) {
//        super(context);
//    }
//
//    public CustomMediaController(Context context, AttributeSet attrs) {
//    super(context, attrs);
//    }
//
//    public CustomMediaController(Context context, boolean useFastForward) {
//        super(context, useFastForward);
//    }
//
//    @Override
//    public void show(int timeout) {
//        super.show(timeout);
//        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//        if (currentapiVersion < 18) //android.os.Build.VERSION_CODES.JELLY_BEAN_MR2
//        {
//            try {
//                Field field1 = MediaController.class.getDeclaredField("mAnchor");
//                field1.setAccessible(true);
//                View mAnchor = (View)field1.get(controller);
//
//                Field field2 = MediaController.class.getDeclaredField("mDecor");
//                field2.setAccessible(true);
//                View mDecor = (View)field2.get(controller);
//
//                Field field3 = MediaController.class.getDeclaredField("mDecorLayoutParams");
//                field3.setAccessible(true);
//                WindowManager.LayoutParams mDecorLayoutParams = (WindowManager.LayoutParams)field3.get(controller);
//
//                Field field4 = MediaController.class.getDeclaredField("mWindowManager");
//                field4.setAccessible(true);
//                WindowManager mWindowManager = (WindowManager)field4.get(controller);
//
//                int [] anchorPos = new int[2];
//                mAnchor.getLocationOnScreen(anchorPos);
//
//                // we need to know the size of the controller so we can properly position it
//                // within its space
//                mDecor.measure(MeasureSpec.makeMeasureSpec(mAnchor.getWidth(), MeasureSpec.AT_MOST),
//                                MeasureSpec.makeMeasureSpec(mAnchor.getHeight(), MeasureSpec.AT_MOST));
//
//                WindowManager.LayoutParams p = mDecorLayoutParams;
//                p.width = mAnchor.getWidth();
//                p.x = anchorPos[0] + (mAnchor.getWidth() - p.width) / 2;
//                p.y = anchorPos[1] + mAnchor.getHeight() - mDecor.getMeasuredHeight();
//                mWindowManager.updateViewLayout(mDecor, mDecorLayoutParams);
//
//            } catch (Exception e) {
//                    e.printStackTrace();
//            }
//        }
//    }
//}