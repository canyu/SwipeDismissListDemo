package com.cam.swipedismiss.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

/**
 * 侧滑删除控件
 * Created by yuCan on 17-3-31.
 */

public class SwipeItem extends FrameLayout {

    private ViewDragHelper mDragHelper;
    private ViewDragHelper.Callback mCallBack;
    private ViewGroup mMainContent, mLeftContent, mRightContent;
    private int mHeight, mWidth, mRange, mAllRange;
    private int mMinFlingVelocity;
    private Status mStatus;
    private OnDragStatusListener mListener;
    private OnSwipeSwipeItem mOnSwipeSwipeItem = null;
    private boolean mAbleSwipe = false;
//    int index = 0;

    public enum Status{
        CLOSE, OPEN, DRAGGING
    }
    public SwipeItem(Context context) {
        this(context, null);
    }

    public SwipeItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDragHelper = ViewDragHelper.create(this, 0, initCallBack());
        ViewConfiguration vc = ViewConfiguration.get(context);
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 8;
    }

    private ViewDragHelper.Callback initCallBack() {
        if(mCallBack != null){
            return mCallBack;
        }
        mCallBack = new ViewDragHelper.Callback() {
            int mLeft = 0, mTop = 0;
            boolean mStart = false;
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return true;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                Log.d("cam", " SwipeItem(clampViewPositionHorizontal):  "+left);
                if(isHorizontalSwipe(Math.abs(left), true)){
                    bindSwipeItem();
                }else{
                    return 0;
                }
                Log.d("cam", " SwipeItem(clampViewPositionHorizontal):  准备移动");
                if(child == mMainContent){
                    left = fixLeft(left);
                }
                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                Log.d("cam", " SwipeItem(clampViewPositionVertical):  "+top);
                if(isHorizontalSwipe(Math.abs(top), false)){
                    bindSwipeItem();
                }
                return super.clampViewPositionVertical(child, top, dy);
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                Log.d("cam", " SwipeItem(onViewPositionChanged):  "+dx+" , "+left+" , ");
                if(changedView == mMainContent){
                    mLeftContent.offsetLeftAndRight(dx);
                    mRightContent.offsetLeftAndRight(dx);
                }else if(changedView == mLeftContent){
                    mMainContent.offsetLeftAndRight(dx);
                    mRightContent.offsetLeftAndRight(dx);
                }else if(changedView == mRightContent){
                    mMainContent.offsetLeftAndRight(dx);
                    mLeftContent.offsetLeftAndRight(dx);
                }
                invalidate();
                dispathDragEvent(Math.abs(left));
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                Log.w("cam", " SwipeItem(onViewReleased):  ");
                mAbleSwipe = false;
                mTop = 0;
                mLeft = 0;
                int left = mMainContent.getLeft();
                boolean toRight = left > 0;
                if(xvel * left > 0 && Math.abs(left) > mWidth / 2.0f ||
                        mMinFlingVelocity < Math.abs(xvel)){
                    open(toRight);
                }else{
                    close();
                }
                if(mOnSwipeSwipeItem != null){
                    mOnSwipeSwipeItem.onSwipeSwipeItem(null);
                }
            }

            private boolean isHorizontalSwipe(int index, boolean horizontal){
                Log.d("cam", " SwipeItem(isHorizontalSwipe):  "+index+" , "+horizontal+" , "+mStatus+" , "+mLeft+" , "+mTop);
                if(isDragging()){
                    return true;
                }
                if(mLeft != 0){
                    if(mTop != 0){
                        return mLeft > mTop;
                    }
                    if(horizontal){
                        if(index > mLeft){
                            mLeft = index;
                        }
                        return true;
                    }
                    mTop = index;
                    return mLeft > mTop;
                }
                if(mTop == 0){
                    if(horizontal){
                        mLeft = index;
                    }else{
                        mTop = index;
                    }
                    return false;
                }
                if(horizontal){
                    mLeft = index;
                    return mLeft > mTop;
                }
                if(index > mTop){
                    mTop = index;
                }
                return false;

            }
        };
        return mCallBack;
    }

    private void bindSwipeItem(){
        if(mOnSwipeSwipeItem != null){
            if(mOnSwipeSwipeItem.onSwipeSwipeItem(SwipeItem.this)){
                Log.d("cam", " SwipeItem(onViewPositionChanged):  可以滑动");
                mAbleSwipe = true;
            }
        }
    }
    private void close() {
        if(mDragHelper.smoothSlideViewTo(mMainContent, 0, 0)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void open(boolean toRight) {
        int finalLeft = toRight ? mAllRange : - mAllRange;
        if(mDragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void dispathDragEvent(int offset) {
        float percent = offset * 1.0f / mAllRange;
        Status lastStatus = mStatus;
        mStatus = updateStatus(offset, percent);
        if(mListener != null){
            mListener.onDragging(percent);
            if(lastStatus != mStatus){
                if(mStatus == Status.CLOSE){
                    mListener.onClose();
                }else if(mStatus == Status.OPEN){
                    mListener.onOpen();
                }
            }
        }
    }

    private Status updateStatus(int offset, float percent) {
        Log.d("cam", " SwipeItem(updateStatus):  "+offset+" , "+percent);
        if(mRange <= offset && offset <= mAllRange){
            setAlpha(Math.max(0f, Math.min(1f, 1f - (offset - mRange) * 1.0f / mWidth)));
//            ViewHelper.setAlpha(this, Math.max(0f, Math.min(1f, 1f - (offset - mRange) * 1.0f / mWidth)));
        }
        if(percent == 0){
            return Status.CLOSE;
        }else if(percent == 1){
            return Status.OPEN;
        }else{
            return Status.DRAGGING;
        }
    }

    private int fixLeft(int left) {
        return /*left;//*/mAbleSwipe ? left : 0;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMainContent = (ViewGroup)getChildAt(0);
        mLeftContent = (ViewGroup)getChildAt(1);
        mRightContent = (ViewGroup)getChildAt(2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = mMainContent.getMeasuredHeight();
        mWidth = mMainContent.getMeasuredWidth();
        mRange = mLeftContent.getMeasuredWidth();
        mAllRange = mWidth + mRange;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        layoutContent();
    }

    private void layoutContent() {
        mMainContent.layout(0,0,mWidth, mHeight);
        mLeftContent.layout(- mRange, 0, 0, mHeight);
        mRightContent.layout(mWidth, 0, mWidth + mRange, mHeight);
        bringChildToFront(mMainContent);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try{
            mDragHelper.processTouchEvent(event);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public void setOnDragStatusListener(OnDragStatusListener mListener) {
        this.mListener = mListener;
    }

    public boolean isDragging() {
        return mStatus == Status.DRAGGING;
    }

    public void setOnSwipeSwipeItem(OnSwipeSwipeItem mOnSwipeSwipeItem) {
        this.mOnSwipeSwipeItem = mOnSwipeSwipeItem;
    }

    public interface OnDragStatusListener{
        void onClose();
        void onOpen();
        void onDragging(float percent);
    }

    public interface OnSwipeSwipeItem{
        boolean onSwipeSwipeItem(SwipeItem swipeItem);
    }
}
