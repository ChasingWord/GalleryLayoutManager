package com.chasing.myapplication.utils.layout_manager;


import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by chasing on 2021/4/13.
 */
public class ConnectLayoutManagerHelper {
    private GalleryLayoutManager mLayoutManagerFirst;
    private RecyclerView mRcvFirst;
    private GalleryLayoutManager mLayoutManagerSecond;
    private RecyclerView mRcvSecond;

    public void bindRecyclerView(RecyclerView rcvFirst, RecyclerView rcvSecond) {
        if (!(rcvFirst.getLayoutManager() instanceof GalleryLayoutManager)) return;
        if (!(rcvSecond.getLayoutManager() instanceof GalleryLayoutManager)) return;
        mRcvFirst = rcvFirst;
        mRcvSecond = rcvSecond;
        mLayoutManagerFirst = (GalleryLayoutManager) rcvFirst.getLayoutManager();
        mLayoutManagerSecond = (GalleryLayoutManager) rcvSecond.getLayoutManager();
        init();
    }

    private boolean isSnapFirst, isSnapSecond;

    private void init() {
        mLayoutManagerFirst.setOnScrollListener(new GalleryLayoutManager.OnScrollListener() {
            @Override
            public void onScroll(int scroll) {
                if (mLayoutManagerFirst.getScrollItemSpace() == 0 ||
                        mLayoutManagerSecond.getScrollItemSpace() == 0 ||
                        isSnapFirst) return;
                int scrollSecond = scroll * mLayoutManagerSecond.getScrollItemSpace() / mLayoutManagerFirst.getScrollItemSpace();
                mLayoutManagerSecond.scrollHorizontallyBy(scrollSecond, null, null);
            }

            @Override
            public void onScrollStateChange(boolean isSnap, int position) {
                isSnapFirst = isSnap;
                if (isSnapFirst)
                    mLayoutManagerSecond.smoothScrollToPosition(mRcvSecond, null, position);
            }
        });

        mLayoutManagerSecond.setOnScrollListener(new GalleryLayoutManager.OnScrollListener() {
            @Override
            public void onScroll(int scroll) {
                if (mLayoutManagerFirst.getScrollItemSpace() == 0 ||
                        mLayoutManagerSecond.getScrollItemSpace() == 0 ||
                        isSnapSecond) return;
                int scrollSecond = scroll * mLayoutManagerFirst.getScrollItemSpace() / mLayoutManagerSecond.getScrollItemSpace();
                mLayoutManagerFirst.scrollHorizontallyBy(scrollSecond, null, null);
            }

            @Override
            public void onScrollStateChange(boolean isSnap, int position) {
                isSnapSecond = isSnap;
                if (isSnapSecond)
                    mLayoutManagerFirst.smoothScrollToPosition(mRcvFirst, null, position);
            }
        });
    }
}
