package win.smartown.android.library.tableLayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Smartown on 2017/7/19.
 */
public class FreeScrollView extends FrameLayout {

    private int scaledTouchSlop;
    private GestureDetector gestureDetector;

    public FreeScrollView(Context context) {
        super(context);
        init();
    }

    public FreeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FreeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FreeScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                getChildAt(0).scrollBy((int) distanceX, (int) distanceY);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                int returnX = 0;
//                int returnY = 0;
//                View child = getChildAt(0);
//                if (child.getScrollX() < 0) {
//                    returnX = -child.getScrollX();
//                } else if (child.getScrollX() + getWidth() > child.getMeasuredWidth()) {
//                    returnX = child.getMeasuredWidth() - child.getScrollX() - getWidth();
//                }
//                if (child.getScrollY() < 0) {
//                    returnY = -child.getScrollY();
//                } else if (child.getScrollY() + getHeight() > child.getMeasuredHeight()) {
//                    returnY = child.getMeasuredHeight() - child.getScrollY() - getHeight();
//                }
//                scrollBy(returnX, returnY);
                return false;
            }
        });
    }

    private float startX;
    private float startY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
//        int action = ev.getAction();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                startX = ev.getX();
//                startY = ev.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float deltaX = startX - ev.getX();
//                float deltaY = startY - ev.getY();
//                return Math.abs(deltaX) >= scaledTouchSlop || Math.abs(deltaY) > scaledTouchSlop;
//        }
//
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }


}
