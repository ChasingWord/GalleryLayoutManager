package com.chasing.base.decoration

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by chasing on 2020/12/30.
 */
abstract class FlexibleDividerDecoration(builder: Builder) : RecyclerView.ItemDecoration() {
    private val DEFAULT_SIZE = 2
    private val ATTRS = intArrayOf(android.R.attr.listDivider)

    protected enum class DividerType {
        DRAWABLE, PAINT, COLOR
    }

    protected var mDividerType: DividerType? = null
    protected var mVisibilityProvider: VisibilityProvider? = null
    protected var mPaintProvider: PaintProvider? = null
    protected var mColorProvider: ColorProvider? = null
    protected var mDrawableProvider: DrawableProvider? = null
    protected var mSizeProvider: SizeProvider? = null
    protected var mShowLastDivider = false
    protected var mPositionInsideItem = false
    private var mPaint: Paint? = null

    init{
        when {
            builder.mPaintProvider != null -> {
                mDividerType = DividerType.PAINT
                mPaintProvider = builder.mPaintProvider
            }
            builder.mColorProvider != null -> {
                mDividerType = DividerType.COLOR
                mColorProvider = builder.mColorProvider
                mPaint = Paint()
                setSizeProvider(builder)
            }
            else -> {
                mDividerType = DividerType.DRAWABLE
                mDrawableProvider = if (builder.mDrawableProvider == null) {
                    val a = builder.mContext.obtainStyledAttributes(ATTRS)
                    val divider = a.getDrawable(0)
                    a.recycle()
                    divider?.let {
                        object : DrawableProvider {
                            override fun drawableProvider(
                                position: Int,
                                parent: RecyclerView?,
                            ): Drawable {
                                return divider
                            }
                        }
                    }
                } else {
                    builder.mDrawableProvider
                }
                mSizeProvider = builder.mSizeProvider
            }
        }
        mVisibilityProvider = builder.mVisibilityProvider
        mShowLastDivider = builder.mShowLastDivider
        mPositionInsideItem = builder.mPositionInsideItem
    }

    private fun setSizeProvider(builder: Builder) {
        mSizeProvider = builder.mSizeProvider
        if (mSizeProvider == null) {
            mSizeProvider = object : SizeProvider {
                override fun dividerSize(position: Int, parent: RecyclerView?): Int {
                    return DEFAULT_SIZE
                }
            }
        }
    }

    override fun onDraw(
        @NonNull c: Canvas,
        parent: RecyclerView,
        @NonNull state: RecyclerView.State,
    ) {
        val adapter = parent.adapter ?: return
        val itemCount = adapter.itemCount
        val lastDividerOffset = getLastDividerOffset(parent)
        val validChildCount = parent.childCount
        var lastChildPosition = -1
        for (i in 0 until validChildCount) {
            val child = parent.getChildAt(i)
            val childPosition = parent.getChildAdapterPosition(child)
            if (childPosition < lastChildPosition) {
                // Avoid remaining divider when animation starts
                continue
            }
            lastChildPosition = childPosition
            if (!mShowLastDivider && childPosition >= itemCount - lastDividerOffset) {
                // Don't draw divider for last line if mShowLastDivider = false
                continue
            }
            if (wasDividerAlreadyDrawn(childPosition, parent)) {
                // No need to draw divider again as it was drawn already by previous column
                continue
            }
            val groupIndex = getGroupIndex(childPosition, parent)
            //如果视图处于刷新状态，会返回NO_POSITION即-1
            if (groupIndex < 0 || mVisibilityProvider!!.shouldHideDivider(groupIndex, parent)) {
                continue
            }
            val bounds = getDividerBound(groupIndex, parent, child)
            when (mDividerType) {
                DividerType.DRAWABLE -> {
                    val drawable = mDrawableProvider!!.drawableProvider(groupIndex, parent)
                    drawable.bounds = bounds
                    drawable.draw(c)
                }
                DividerType.PAINT -> {
                    mPaint = mPaintProvider!!.dividerPaint(groupIndex, parent)
                    c.drawLine(
                        bounds.left.toFloat(),
                        bounds.top.toFloat(),
                        bounds.right.toFloat(),
                        bounds.bottom.toFloat(),
                        mPaint!!
                    )
                }
                DividerType.COLOR -> {
                    mPaint!!.color = mColorProvider!!.dividerColor(groupIndex, parent)
                    mPaint!!.strokeWidth = mSizeProvider!!.dividerSize(groupIndex, parent).toFloat()
                    c.drawLine(
                        bounds.left.toFloat(),
                        bounds.top.toFloat(),
                        bounds.right.toFloat(),
                        bounds.bottom.toFloat(),
                        mPaint!!
                    )
                }
            }
        }
    }

    override fun getItemOffsets(
        @NonNull rect: Rect,
        @NonNull v: View,
        parent: RecyclerView,
        @NonNull state: RecyclerView.State,
    ) {
        val position = parent.getChildAdapterPosition(v)

//        int itemCount = parent.getAdapter().getItemCount();
//        int lastDividerOffset = getLastDividerOffset(parent);
//        if (!mShowLastDivider && position >= itemCount - lastDividerOffset) {
//            // bug:如果是一个一个插入刷新的话，会把每一个都当做是最后一个，最后导致都没有间距
//            // Don't set item offset for last line if mShowLastDivider = false
//            return;
//        }

        //如果视图处于刷新状态，会返回NO_POSITION即-1
        val groupIndex = getGroupIndex(position, parent)
        if (groupIndex < 0 || mVisibilityProvider!!.shouldHideDivider(groupIndex, parent)) {
            return
        }
        setItemOffsets(rect, groupIndex, parent)
    }

    /**
     * Check if recyclerview is reverse layout
     *
     * @param parent RecyclerView
     * @return true if recyclerview is reverse layout
     */
    protected fun isReverseLayout(parent: RecyclerView): Boolean {
        val layoutManager = parent.layoutManager
        return if (layoutManager is LinearLayoutManager) {
            (layoutManager as LinearLayoutManager?)?.reverseLayout ?: false
        } else {
            false
        }
    }

    /**
     * In the case mShowLastDivider = false,
     * Returns offset for how many views we don't have to draw a divider for,
     * for LinearLayoutManager it is as simple as not drawing the last child divider,
     * but for a GridLayoutManager it needs to take the span count for the last items into account
     * until we use the span count configured for the grid.
     *
     * @param parent RecyclerView
     * @return offset for how many views we don't have to draw a divider or 1 if its a
     * LinearLayoutManager
     */
    private fun getLastDividerOffset(parent: RecyclerView): Int {
        if (parent.layoutManager is GridLayoutManager) {
            val layoutManager: GridLayoutManager? = parent.layoutManager as GridLayoutManager?
            val spanSizeLookup: GridLayoutManager.SpanSizeLookup? = layoutManager?.spanSizeLookup
            val spanCount: Int = layoutManager?.spanCount ?: DEFAULT_SIZE
            val itemCount = parent.adapter!!.itemCount
            for (i in itemCount - 1 downTo 0) {
                if (spanSizeLookup != null) {
                    if (spanSizeLookup.getSpanIndex(i, spanCount) == 0) {
                        return itemCount - i
                    }
                }
            }
        }
        return 1
    }

    /**
     * Determines whether divider was already drawn for the row the item is in,
     * effectively only makes sense for a grid
     *
     * @param position current view position to draw divider
     * @param parent   RecyclerView
     * @return true if the divider can be skipped as it is in the same row as the previous one.
     */
    private fun wasDividerAlreadyDrawn(position: Int, parent: RecyclerView): Boolean {
        if (parent.layoutManager is GridLayoutManager) {
            val layoutManager: GridLayoutManager? = parent.layoutManager as GridLayoutManager?
            val spanSizeLookup: GridLayoutManager.SpanSizeLookup? = layoutManager?.spanSizeLookup
            val spanCount: Int = layoutManager?.spanCount ?: DEFAULT_SIZE
            if (spanSizeLookup != null) {
                return spanSizeLookup.getSpanIndex(position, spanCount) > 0
            }
        }
        return false
    }

    /**
     * Returns a group index for GridLayoutManager.
     * for LinearLayoutManager, always returns position.
     *
     * @param position current view position to draw divider
     * @param parent   RecyclerView
     * @return group index of items
     */
    private fun getGroupIndex(position: Int, parent: RecyclerView): Int {
        if (parent.layoutManager is GridLayoutManager) {
            val layoutManager: GridLayoutManager? = parent.layoutManager as GridLayoutManager?
            val spanSizeLookup: GridLayoutManager.SpanSizeLookup? = layoutManager?.spanSizeLookup
            val spanCount: Int = layoutManager?.spanCount ?: DEFAULT_SIZE
            if (spanSizeLookup != null) {
                return spanSizeLookup.getSpanGroupIndex(position, spanCount)
            }
        }
        return position
    }

    protected abstract fun getDividerBound(position: Int, parent: RecyclerView, child: View): Rect

    protected abstract fun setItemOffsets(outRect: Rect, position: Int, parent: RecyclerView)

    /**
     * Interface for controlling divider visibility
     */
    interface VisibilityProvider {
        /**
         * Returns true if divider should be hidden.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return True if the divider at position should be hidden
         */
        fun shouldHideDivider(position: Int, parent: RecyclerView?): Boolean
    }

    /**
     * Interface for controlling paint instance for divider drawing
     */
    interface PaintProvider {
        /**
         * Returns [Paint] for divider
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return Paint instance
         */
        fun dividerPaint(position: Int, parent: RecyclerView?): Paint?
    }

    /**
     * Interface for controlling divider color
     */
    interface ColorProvider {
        /**
         * Returns [android.graphics.Color] value of divider
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return Color value
         */
        fun dividerColor(position: Int, parent: RecyclerView?): Int
    }

    /**
     * Interface for controlling drawable object for divider drawing
     */
    interface DrawableProvider {
        /**
         * Returns drawable instance for divider
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return Drawable instance
         */
        fun drawableProvider(position: Int, parent: RecyclerView?): Drawable
    }

    /**
     * Interface for controlling divider size
     */
    interface SizeProvider {
        /**
         * Returns size value of divider.
         * Height for horizontal divider, width for vertical divider
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return Size of divider
         */
        fun dividerSize(position: Int, parent: RecyclerView?): Int
    }

    @Suppress("UNCHECKED_CAST")
    open class Builder(val mContext: Context) {
        var mResources: Resources = mContext.resources
        var mPaintProvider: PaintProvider? = null
        var mColorProvider: ColorProvider? = null
        var mDrawableProvider: DrawableProvider? = null
        var mSizeProvider: SizeProvider? = null
        var mVisibilityProvider: VisibilityProvider = object : VisibilityProvider {
            override fun shouldHideDivider(position: Int, parent: RecyclerView?): Boolean {
                return false
            }
        }
        var mShowLastDivider = false
        var mPositionInsideItem = false

        fun <H : Builder> color(color: Int): H {
            return colorProvider(object : ColorProvider {
                override fun dividerColor(position: Int, parent: RecyclerView?): Int {
                    return color
                }
            })
        }

        fun <H : Builder> colorResId(@ColorRes colorId: Int): H {
            return color(ContextCompat.getColor(mContext, colorId))
        }

        private fun <H : Builder> colorProvider(provider: ColorProvider?): H {
            mColorProvider = provider
            return this as H
        }

        fun <H : Builder> drawable(@DrawableRes id: Int): H {
            return drawable(ContextCompat.getDrawable(mContext, id))
        }

        fun <H : Builder> drawable(drawable: Drawable?): H {
            return drawableProvider(object : DrawableProvider {
                override fun drawableProvider(position: Int, parent: RecyclerView?): Drawable {
                    return drawable!!
                }
            })
        }

        private fun <H : Builder> drawableProvider(provider: DrawableProvider?): H {
            mDrawableProvider = provider
            return this as H
        }

        fun <H : Builder> size(size: Int): H {
            return sizeProvider(object : SizeProvider {
                override fun dividerSize(position: Int, parent: RecyclerView?): Int {
                    return size
                }
            })
        }

        fun <H : Builder> sizeResId(@DimenRes sizeId: Int): H {
            return size(mResources.getDimensionPixelSize(sizeId))
        }

        private fun <H : Builder> sizeProvider(provider: SizeProvider?): H {
            mSizeProvider = provider
            return this as H
        }

        fun <H : Builder> visibilityProvider(provider: VisibilityProvider): H {
            mVisibilityProvider = provider
            return this as H
        }

        fun <H : Builder> showLastDivider(): H {
            mShowLastDivider = true
            return this as H
        }

        fun <H : Builder> positionInsideItem(positionInsideItem: Boolean): H {
            mPositionInsideItem = positionInsideItem
            return this as H
        }

        protected fun checkBuilderParams() {
            if (mPaintProvider != null) {
                require(mColorProvider == null) { "Use setColor method of Paint class to specify line color. Do not provider ColorProvider if you set PaintProvider." }
                require(mSizeProvider == null) { "Use setStrokeWidth method of Paint class to specify line size. Do not provider SizeProvider if you set PaintProvider." }
            }
        }
    }
}