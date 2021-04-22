package com.chasing.base.decoration

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by chasing on 2020/12/30.
 */
class SpannedGridItemDecoration(var mContext: Context) :
    RecyclerView.ItemDecoration() {
    private var mDecorationSize = 0

    fun widthResId(@DimenRes resId: Int): SpannedGridItemDecoration {
        mDecorationSize = mContext.resources.getDimensionPixelSize(resId)
        return this
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        @NonNull parent: RecyclerView,
        @NonNull state: RecyclerView.State
    ) {
        outRect.left = mDecorationSize / 2
        outRect.right = mDecorationSize / 2
        outRect.top = mDecorationSize / 2
        outRect.bottom = mDecorationSize / 2
    }
}