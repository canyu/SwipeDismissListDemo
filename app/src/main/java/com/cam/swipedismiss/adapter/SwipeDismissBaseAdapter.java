package com.cam.swipedismiss.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cam.swipedismiss.widget.SwipeDismissListView;
import com.cam.swipedismiss.widget.SwipeItem;

/**
 * 侧滑消失的adapter
 * Created by yuCan on 17-4-7.
 */

public abstract class SwipeDismissBaseAdapter extends BaseAdapter implements SwipeItem.OnSwipeSwipeItem {

    SwipeDismissListView mLvSwipeDismiss;

    public SwipeDismissBaseAdapter(SwipeDismissListView mLvSwipeDismiss) {
        this.mLvSwipeDismiss = mLvSwipeDismiss;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public abstract BaseMemberHolder getMemberHolder();

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        BaseMemberHolder viewHolder;
        if(view == null){
            viewHolder = getMemberHolder();
            view  = viewHolder.getView();
            view.setTag(viewHolder);
        }else{
            viewHolder = (BaseMemberHolder)view.getTag();
        }
        viewHolder.setViewData(position);
        return view;
    }

    protected class BaseMemberHolder implements SwipeItem.OnDragStatusListener{

        private SwipeItem mSwipeItem;
        private int mPosition = -1;

        protected BaseMemberHolder(SwipeItem view){
            mSwipeItem = view;
            mSwipeItem.setOnDragStatusListener(this);
            mSwipeItem.setOnSwipeSwipeItem(SwipeDismissBaseAdapter.this);
        }

        public View getView() {
            return mSwipeItem;
        }

        public void setViewData(int position) {
            mPosition = position;
        }

        @Override
        public void onClose() {

        }

        @Override
        public void onOpen() {
            mLvSwipeDismiss.performDismiss(mPosition);
        }

        @Override
        public void onDragging(float percent) {

        }
    }

    @Override
    public boolean onSwipeSwipeItem(SwipeItem swipeItem) {
        if(swipeItem != null){
            Log.d("cam", " SwipeDismissActivity(onSwipeSwipeItem):  ");
        }else{
            Log.d("cam", " SwipeDismissActivity(onSwipeSwipeItem):  释放了,设置为空");
        }
        return mLvSwipeDismiss.setDownView(swipeItem);
    }
}
