package me.android.flickrswipe;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class CustomSwipeDetector implements View.OnTouchListener {

    public static final String TAG = CustomSwipeDetector.class.getSimpleName();
    private int mMinDistance = 100;
    private float mDownX, mDownY, mUpX, mUpY;
    private View mView;
    private onSwipeEvent mSwipeEventListener;

    public enum SwipeTypeEnum {
        RIGHT_TO_LEFT, LEFT_TO_RIGHT, TOP_TO_BOTTOM, BOTTOM_TO_TOP
    }

    public interface onSwipeEvent {
        void SwipeEventDetected(View v, SwipeTypeEnum SwipeType);
    }

    public CustomSwipeDetector(View mView) {
        this.mView = mView;
        mView.setOnTouchListener(this);
    }

    // can use this to customize
    public CustomSwipeDetector setMinDistanceInPixels(int minDistance) {
        this.mMinDistance = minDistance;
        return this;
    }

    public CustomSwipeDetector setOnSwipeListener(onSwipeEvent listener) {
        try {
            mSwipeEventListener = listener;
        } catch (ClassCastException e) {
            Log.e(TAG, "pass correct CustomSwipeDetector.onSwipeEvent Interface", e);
        }
        return null;
    }


    public void onRightToLeftSwipe() {
        if (mSwipeEventListener != null) {
            mSwipeEventListener.SwipeEventDetected(mView, SwipeTypeEnum.RIGHT_TO_LEFT);
        } else {
            Log.e(TAG, "pass correct CustomSwipeDetector.onSwipeEvent Interface");
        }
    }

    public void onLeftToRightSwipe() {
        if (mSwipeEventListener != null) {
            mSwipeEventListener.SwipeEventDetected(mView, SwipeTypeEnum.LEFT_TO_RIGHT);
        } else {
            Log.e(TAG, "pass correct CustomSwipeDetector.onSwipeEvent Interface");
        }
    }

    public void onTopToBottomSwipe() {
        if (mSwipeEventListener != null) {
            mSwipeEventListener.SwipeEventDetected(mView, SwipeTypeEnum.TOP_TO_BOTTOM);
        } else {
            Log.e(TAG, "pass correct CustomSwipeDetector.onSwipeEvent Interface");
        }
    }

    public void onBottomToTopSwipe() {
        if (mSwipeEventListener != null) {
            mSwipeEventListener.SwipeEventDetected(mView, SwipeTypeEnum.BOTTOM_TO_TOP);
        } else {
            Log.e(TAG, "pass correct CustomSwipeDetector.onSwipeEvent Interface");
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mDownX = event.getX();
                mDownY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                mUpX = event.getX();
                mUpY = event.getY();

                float deltaX = mDownX - mUpX;
                float deltaY = mDownY - mUpY;

                //HORIZONTAL SCROLL
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (Math.abs(deltaX) > mMinDistance) {
                        // left or right
                        if (deltaX < 0) {
                            this.onLeftToRightSwipe();
                            return true;
                        }
                        if (deltaX > 0) {
                            this.onRightToLeftSwipe();
                            return true;
                        }
                    } else {
                        //not long enough swipe
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }


}