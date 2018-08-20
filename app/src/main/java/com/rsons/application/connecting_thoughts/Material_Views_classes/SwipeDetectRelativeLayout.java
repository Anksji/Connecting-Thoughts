package com.rsons.application.connecting_thoughts.Material_Views_classes;

/**
 * Created by ankit on 3/3/2018.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;


public class SwipeDetectRelativeLayout extends RelativeLayout {


    private float x1,x2;
    static final int MIN_DISTANCE=150;
    private onSwipeEventDetected mSwipeDetectedListener;


    public SwipeDetectRelativeLayout(Context context) {
        super(context);
    }

    public SwipeDetectRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeDetectRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch(ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = ev.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = ev.getX();
                float deltaX = x2 - x1;
                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    //swiping right to left
                    if(deltaX<0)
                    {
                        if(mSwipeDetectedListener!=null)
                            mSwipeDetectedListener.swipeEventDetected();
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public interface onSwipeEventDetected
    {
        public void swipeEventDetected();
    }

    public void registerToSwipeEvents(onSwipeEventDetected listener)
    {
        this.mSwipeDetectedListener=listener;
    }
}