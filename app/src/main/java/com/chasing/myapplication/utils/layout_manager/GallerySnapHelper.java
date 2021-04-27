package com.chasing.myapplication.utils.layout_manager;

import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GallerySnapHelper extends RecyclerView.OnFlingListener {

    RecyclerView mRecyclerView;
    GalleryLayoutManager mLayoutManager;
    int mPointDownPosition;

    private boolean snapToCenter = false;

    // Handles the snap on scroll case.
    private final RecyclerView.OnScrollListener mScrollListener =
            new RecyclerView.OnScrollListener() {

                boolean mScrolled = false;

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (recyclerView.getChildCount() == 0) return;
                    final GalleryLayoutManager layoutManager =
                            (GalleryLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                            mPointDownPosition = layoutManager.getCurrentPosition();
                            snapToCenter = false;
                            if (layoutManager.mOnScrollListener != null)
                                layoutManager.mOnScrollListener.onScrollStateChange(false, layoutManager.getCurrentPosition());
                        }

                        if (newState == RecyclerView.SCROLL_STATE_IDLE && mScrolled) {
                            mScrolled = false;
                            if (!snapToCenter) {
                                snapToCenterView(layoutManager);
                            } else {
                                snapToCenter = false;
                            }
                        }
                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    if (recyclerView.getChildCount() == 0) return;
                    if (dx != 0 || dy != 0) {
                        mScrolled = true;
                    }
                }
            };

    @Override
    public boolean onFling(int velocityX, int velocityY) {
        if (mRecyclerView.getChildCount() == 0) return false;
        int width = mRecyclerView.getChildAt(0).getWidth();
        // 判断fling的距离大于一页，则直接控制Rcv滚动一页
        if (velocityX > width) {
            mLayoutManager.smoothScrollToPosition(mRecyclerView, null, mLayoutManager.getCurrentPosition() + 1);
            return true;
        } else if (velocityX < -width) {
            mLayoutManager.smoothScrollToPosition(mRecyclerView, null, mLayoutManager.getCurrentPosition() - 1);
            return true;
        }
        return false;
    }

    public void attachToRecyclerView(RecyclerView recyclerView)
            throws IllegalStateException {
        if (mRecyclerView == recyclerView) {
            return; // nothing to do
        }
        if (mRecyclerView != null) {
            destroyCallbacks();
        }
        mRecyclerView = recyclerView;
        if (mRecyclerView != null) {
            final RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            if (!(layoutManager instanceof GalleryLayoutManager)) return;

            mLayoutManager = (GalleryLayoutManager) layoutManager;
            setupCallbacks();
        }
    }

    void snapToCenterView(GalleryLayoutManager layoutManager) {
        final int delta = layoutManager.getOffsetToCenter();
        if (delta != 0) {
            snapToCenter = true;
            mRecyclerView.smoothScrollBy(delta, 0, new DecelerateInterpolator());
        } else {
            // set it false to make smoothScrollToPosition keep trigger the listener
            snapToCenter = false;
        }

        if (layoutManager.mOnPageChangeListeners != null && layoutManager.mOnPageChangeListeners.size() > 0)
            for (GalleryLayoutManager.OnPageChangeListener onPageChangeListener : layoutManager.mOnPageChangeListeners) {
                onPageChangeListener.onPageSelected(layoutManager.getCurrentPosition());
            }
        if (layoutManager.mOnScrollListener != null)
            layoutManager.mOnScrollListener.onScrollStateChange(true, layoutManager.getCurrentPosition());
    }

    /**
     * Called when an instance of a {@link RecyclerView} is attached.
     */
    void setupCallbacks() throws IllegalStateException {
        if (mRecyclerView.getOnFlingListener() != null) {
            throw new IllegalStateException("An instance of OnFlingListener already set.");
        }
        mRecyclerView.setOnFlingListener(this);
        mRecyclerView.addOnScrollListener(mScrollListener);
    }

    /**
     * Called when the instance of a {@link RecyclerView} is detached.
     */
    void destroyCallbacks() {
        mRecyclerView.setOnFlingListener(null);
        mRecyclerView.removeOnScrollListener(mScrollListener);
    }
}
