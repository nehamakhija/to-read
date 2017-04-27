package com.example.neham.to_read;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeTouchListener implements View.OnTouchListener{

    private final GestureDetector gestureDetector;
    public static final String TAG="SwipeListener";


    public OnSwipeTouchListener(Context context){
        Log.d(TAG,"Swipe listener init");
        gestureDetector=new GestureDetector(context,new GestureListener());
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
    public void onSwipeLeft(){

    }
    public void onSwipeRight(){

    }

    private class GestureListener extends SimpleOnGestureListener{

        private static final int SWIPE_DISTANCE_THRESHOLD=100;
        private static final int SWIPE_VELOCITY_THRESHOLD=100;

        @Override
        public boolean onDown(MotionEvent e) {
            Log.d(TAG,"Touch down!!!");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG,"Somesort of flinggggggggggggggggg");
            float distX=e2.getX()-e1.getX();
            float distY=e2.getY()-e1.getY();
            if (Math.abs(distX) > Math.abs(distY) && Math.abs(distX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                return true;
            }
            return false;
        }
    }
}
