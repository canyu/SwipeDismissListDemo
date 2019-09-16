package com.cam.swipedismiss.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * 侧滑删除控件
 * Created by yuCan on 17-3-31.
 */

public class SwipeDismissListView extends ListView {

    private static final int ANIMATION_TIME = 200;

    private OnDismissCallback mDismissCallback;
    private SwipeItem mDownView = null;
    private float mDownX, mDownY;
    private int mMinSlop;

    public SwipeDismissListView(Context context) {
        this(context, null);
    }

    public SwipeDismissListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeDismissListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewConfiguration vc = ViewConfiguration.get(context);
        mMinSlop = vc.getScaledTouchSlop();
    }

    public void setOnDismissCallback(OnDismissCallback mDismissCallback) {
        this.mDismissCallback = mDismissCallback;
    }

    public synchronized boolean setDownView(SwipeItem downView) {
        if(downView != null && mDownView != null){
            return false;
        }
        mDownView = downView;
        Log.d("cam", " SwipeDismissListView(setDownView):  绑定成功, "/*+downView.getIndex()*/);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("cam", " SwipeDismissListView(onInterceptTouchEvent):  "+ev.getAction()+" , "+(mDownView == null));
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:{
                Log.e("cam", " SwipeDismissListView(onInterceptTouchEvent): ACTION_DOWN ");
//                int index = pointToPosition((int)ev.getX(), (int)ev.getY() - getFirstVisiblePosition());
//                View downView = getChildAt(index);
//                Log.d("cam", " SwipeDismissListView(onInterceptTouchEvent): ACTION_DOWN "+ev.getX()+" , "+ev.getY()+" , "+index
//                        +" , "+(downView== null));
//                if(downView instanceof SwipeItem){
//                    mDownView = (SwipeItem) downView;
//                    Log.d("cam", " SwipeDismissListView(onInterceptTouchEvent):  setAbleSwipe, "+mDownView.getIndex());
////                    mDownView.setAbleSwipe(true);
//                }
            }
            break;
            case MotionEvent.ACTION_MOVE:{
                Log.d("cam", " SwipeDismissListView(onInterceptTouchEvent): ACTION_HOVER_MOVE 1"+" , "+(mDownView == null));
//                float deltaX = ev.getX() - mDownX;
//                float deltaY = ev.getY() - mDownY;
//                Log.d("cam", " SwipeDismissListView(onInterceptTouchEvent):  "+ev.getX()+" , "+mDownX+" , "+ev.getY()+" , "+mDownY+" , "+mMinSlop);
//                if(Math.abs(deltaX) < mMinSlop && Math.abs(deltaY) > mMinSlop ){
//                    Log.d("cam", " SwipeDismissListView(onInterceptTouchEvent):  垂直滑动");
//                }
//                if(Math.abs(deltaX) > mMinSlop && Math.abs(deltaY) < mMinSlop){
//                    Log.d("cam", " SwipeDismissListView(onInterceptTouchEvent):  水平滑动");
//                }
                if(mDownView != null){
                    Log.d("cam", " SwipeDismissListView(onInterceptTouchEvent):  2 , "+mDownView.isDragging()+" , "/*+mDownView.getIndex()*/);
                    if(mDownView.isDragging()){
                        Log.d("cam", " SwipeDismissListView(onInterceptTouchEvent): ACTION_HOVER_MOVE return false");
                        return false;
                    }
                }
            }
            break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:{
                Log.e("cam", " SwipeDismissListView(onInterceptTouchEvent): ACTION_POINTER_UP ");
                if(mDownView != null){
                    mDownView = null;
                }
            }
            break;
        }
        boolean is = super.onInterceptTouchEvent(ev);
        Log.d("cam", " SwipeDismissListView(onInterceptTouchEvent)结尾:  "+is);
        return is;
    }

    public void performDismiss(final int dismissPosition){
        final View dismissView = getChildAt(dismissPosition + getHeaderViewsCount() - getFirstVisiblePosition());
//        final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
        final int originalHeight = dismissView.getHeight();

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0).setDuration(ANIMATION_TIME);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                int headCount = getHeaderViewsCount();
                if(mDismissCallback != null && headCount <= dismissPosition){
                    mDismissCallback.onDismiss(dismissPosition - headCount);
                }
                dismissView.setAlpha(1f);
                dismissView.setTranslationX(0);
                ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
                lp.height = originalHeight;
                dismissView.setLayoutParams(lp);
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
                lp.height = (Integer) valueAnimator.getAnimatedValue();
                dismissView.setLayoutParams(lp);
            }
        });
        animator.start();

    }

    public interface OnDismissCallback{
        void onDismiss(int dismissPosition);
    }
}
