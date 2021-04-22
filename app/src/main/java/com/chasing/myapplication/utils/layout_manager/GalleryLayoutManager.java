package com.chasing.myapplication.utils.layout_manager;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chasing on 2021/4/12.
 * RecyclerView的高度需要进行设置，否则可能导致Item的高度会出现偏差
 */
public class GalleryLayoutManager extends RecyclerView.LayoutManager {
    private RecyclerView.Recycler mRecycler;
    private int mItemWidth, mItemHeight;
    private int mInterval;
    private int mStartInterval; //起始点左边的空白间距
    private float mScale = 1; //1-2之间
    private int mScaleSpace;
    private int mCurrentPosition = -1;

    int mMaxScrollX;
    int mScrollX; //负数到0
    List<OnPageChangeListener> mOnPageChangeListeners = new ArrayList<>();
    OnScrollListener mOnScrollListener;

    public void setScale(float scale) {
        if (scale < 1)
            mScale = 1;
        else if (scale > 2)
            mScale = 2;
        else
            mScale = scale;
    }

    public void setInterval(int interval) {
        this.mInterval = interval;
    }

    public void setFirstInterval(int firstInterval) {
        this.mStartInterval = firstInterval;
    }

    public void addOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        if (onPageChangeListener != null)
            mOnPageChangeListeners.add(onPageChangeListener);
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    public int getScrollItemSpace() {
        return mItemWidth + mInterval;
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public boolean isAutoMeasureEnabled() {
        return true;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public int getOffsetToCenter() {
        if (mItemWidth == 0) return 0;
        else {
            if (mCurrentPosition == Math.abs(mScrollX / (mItemWidth + mInterval)))
                return mScrollX % (mItemWidth + mInterval);
            else {
                return mScrollX % (mItemWidth + mInterval) + mItemWidth + mInterval;
            }
        }
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0) {
            //没有Item可布局，就回收全部临时缓存 (参考自带的LinearLayoutManager)
            //这里的没有Item，是指Adapter里面的数据集，
            removeAndRecycleAllViews(recycler);
            return;
        }
        mRecycler = recycler;
        if (mItemWidth == 0 || mItemHeight == 0) {
            View first = recycler.getViewForPosition(0);
            measureChildWithMargins(first, 0, 0);
            mItemWidth = getDecoratedMeasuredWidth(first);
            mItemHeight = getDecoratedMeasuredHeight(first);
            mScaleSpace = (int) ((mScale - 1) * mItemWidth);
            mMaxScrollX = (mItemWidth + mInterval) * (getItemCount() - 1);
        }
        layoutItems(recycler);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state != null && state.getItemCount() == 0) {
            return dx;
        } else {
            if (recycler != null)
                mRecycler = recycler;
            int consumeX;
            if (mScrollX - dx > 0) {
                consumeX = mScrollX;
                mScrollX = 0;
            } else if (mScrollX - dx < -mMaxScrollX) {
                consumeX = mMaxScrollX + mScrollX;
                mScrollX = -mMaxScrollX;
            } else {
                mScrollX -= dx;
                consumeX = dx;
            }
            if (mOnScrollListener != null && recycler != null)
                mOnScrollListener.onScroll(consumeX);
            layoutItems(mRecycler);
            return consumeX;
        }
    }

    private void layoutItems(RecyclerView.Recycler recycler) {
        detachAndScrapAttachedViews(recycler);
        int size = (getWidth() - mScaleSpace) / (mItemWidth + mInterval) + 2;
        int start = Math.abs((mScrollX + mStartInterval) / (mItemWidth + mInterval));
        int scrollX = mScrollX + start * (mItemWidth + mInterval) + mStartInterval;  //负数到mStartInterval
        View view;
        int left = 0, viewHorizontalSpace = 0;
        int realWidth, realHeight;
        float scale;
        int scaleCount = 2;
        for (int i = 0; i < size; i++) {
            if (start + i < getItemCount()) {
                view = recycler.getViewForPosition(start + i);
                scale = 1;
                if (scrollX == 0) {
                    if (i == 0) {
                        scale = mScale;
                    }
                } else if (scaleCount > 0) {
                    if (i == 0 && Math.abs(scrollX) + mStartInterval < mItemWidth + mInterval) {
                        scale = (1 - (Math.abs(scrollX - mStartInterval)) / (float) (mItemWidth + mInterval)) * (mScale - 1) + 1;
                        scaleCount--;
                    } else if (i == 1) {
                        if (scaleCount == 2) {
                            if (Math.abs(scrollX) + mStartInterval >= mItemWidth + mInterval)
                                scale = (1 - (Math.abs(scrollX) + mStartInterval - mItemWidth - mInterval) / (float) (mItemWidth + mInterval)) * (mScale - 1) + 1;
                            else
                                scale = (1 - (Math.abs(scrollX) + mStartInterval) / (float) (mItemWidth + mInterval)) * (mScale - 1) + 1;
                        } else
                            scale = (Math.abs(scrollX - mStartInterval)) / (float) (mItemWidth + mInterval) * (mScale - 1) + 1;
                        scaleCount--;
                    } else if (i == 2) {
                        scale = (Math.abs(scrollX) + mStartInterval - mItemWidth - mInterval) / (float) (mItemWidth + mInterval) * (mScale - 1) + 1;
                        scaleCount--;
                    }
                }

                view.setPivotX(0);
                view.setPivotY(0);
                view.setScaleX(scale);
                view.setScaleY(scale);
                addView(view);
                measureChildWithMargins(view, 0, 0);

                realWidth = (int) (mItemWidth * scale);
                realHeight = (int) (mItemHeight * scale);
                left += viewHorizontalSpace;
                viewHorizontalSpace = realWidth + mInterval;

                layoutDecorated(view, scrollX + left, (int) (mItemHeight * mScale - realHeight),
                        scrollX + left + mItemWidth, (int) (mItemHeight * mScale));

                if (scale < 1)
                    scale = 1;
                else if (scale > mScale)
                    scale = mScale;
                if (scale != 1 && mScale != 1 && (mScale - scale) <= (mScale - 1) * 0.5 ||
                        mItemWidth == getWidth() && (scrollX + left >= 0 && scrollX + left <= getWidth() / 2 ||
                                scrollX + left + mItemWidth > getWidth() / 2 && scrollX + left + mItemWidth <= getWidth())) {
                    int prePosition = mCurrentPosition;
                    mCurrentPosition = start + i;
                    if (prePosition != mCurrentPosition && mOnPageChangeListeners != null && mOnPageChangeListeners.size() > 0)
                        for (OnPageChangeListener pageChangeListener : mOnPageChangeListeners) {
                            pageChangeListener.onPageSelectedWhenScroll(mCurrentPosition);
                        }
                }
            } else
                break;
        }

        List<RecyclerView.ViewHolder> scrapList = recycler.getScrapList();
        //遍历，然后先移除，后回收，其实也就是removeAndRecycleView方法所做的事
        for (int i = 0; i < scrapList.size(); i++) {
            RecyclerView.ViewHolder holder = scrapList.get(i);
            removeView(holder.itemView);
            recycler.recycleView(holder.itemView);
        }
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        recyclerView.smoothScrollBy(position * (mItemWidth + mInterval) + mScrollX, 0);
    }

    public interface OnPageChangeListener {
        void onPageSelected(int position);

        void onPageSelectedWhenScroll(int position);
    }

    public interface OnScrollListener {
        void onScroll(int scroll);

        void onScrollStateChange(boolean isSnap, int position);
    }
}
