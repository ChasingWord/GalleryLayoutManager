package com.chasing.base.adapter

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.TextWatcher
import android.text.util.Linkify
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.View.*
import android.view.ViewGroup.MarginLayoutParams
import android.widget.*
import android.widget.ImageView.ScaleType
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

/**
 * Created by chasing on 2020/12/29.
 */
class RecyclerViewHelper(var context: Context, var itemView: View, var position: Int) {
    fun <T : View> getView(viewId: Int): T {
        return retrieveView(viewId)
    }

    private fun <T : View> retrieveView(viewId: Int): T {
        @Suppress("UNCHECKED_CAST")
        return itemView.findViewById<View>(viewId) as T
    }

    fun getView(): View {
        return itemView
    }

    // <editor-fold desc="属性设置">
    fun setText(viewId: Int, value: String?): RecyclerViewHelper {
        val view = retrieveView<TextView>(viewId)
        view.text = value ?: ""
        return this
    }

    fun setLines(viewId: Int, lines: Int): RecyclerViewHelper {
        val view = retrieveView<TextView>(viewId)
        view.setLines(lines)
        return this
    }

    fun setMaxLines(viewId: Int, maxLines: Int): RecyclerViewHelper {
        val view = retrieveView<TextView>(viewId)
        view.maxLines = maxLines
        return this
    }

    fun setEnable(viewId: Int, enable: Boolean): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        view.isEnabled = enable
        return this
    }

    fun setText(viewId: Int, resId: Int): RecyclerViewHelper {
        val view = retrieveView<TextView>(viewId)
        view.setText(resId)
        return this
    }

    fun addTextDeleteLine(viewId: Int): RecyclerViewHelper {
        val view = retrieveView<TextView>(viewId)
        view.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
        return this
    }

    fun setHint(viewId: Int, value: String): RecyclerViewHelper {
        val view = retrieveView<EditText>(viewId)
        view.hint = value
        return this
    }

    fun setViewHeight(viewId: Int, viewHeight: Int): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        val lp = view.layoutParams
        lp.height = viewHeight
        view.layoutParams = lp
        return this
    }

    fun setViewMinHeight(viewId: Int, minHeight: Int): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        view.minimumHeight = minHeight
        return this
    }

    fun setViewWidth(viewId: Int, width: Int): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        val layoutParams = view.layoutParams
        layoutParams.width = width
        view.layoutParams = layoutParams
        return this
    }

    fun setViewWidthHeight(viewId: Int, width: Int, height: Int): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        val layoutParams = view.layoutParams
        layoutParams.width = width
        layoutParams.height = height
        view.layoutParams = layoutParams
        return this
    }

    //根据宽高比设置控件高度
    fun setWidthHeightWithRatio(
        viewId: Int,
        width: Int,
        widthRatio: Int,
        heightRatio: Int
    ): RecyclerViewHelper {
        var width = width
        val view = retrieveView<View>(viewId)
        if (width <= 0) width = view.width
        if (width <= 0) return this
        val height = (width * heightRatio / widthRatio.toFloat()).toInt()
        val layoutParams = view.layoutParams
        if (layoutParams != null) {
            layoutParams.height = height
            view.layoutParams = layoutParams
        }
        return this
    }

    fun setImageResource(viewId: Int, imageResId: Int): RecyclerViewHelper {
        val view = retrieveView<ImageView>(viewId)
        view.setImageResource(imageResId)
        return this
    }

    fun setBackgroundColor(viewId: Int, color: Int): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        view.setBackgroundColor(color)
        return this
    }

    fun setBackgroundRes(viewId: Int, backgroundRes: Int): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        view.setBackgroundResource(backgroundRes)
        return this
    }

    fun setBackgroundDrawable(viewId: Int, drawable: Drawable): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        view.background = drawable
        return this
    }

    fun setTextColor(viewId: Int, textColor: Int): RecyclerViewHelper {
        val view = retrieveView<TextView>(viewId)
        view.setTextColor(textColor)
        return this
    }

    fun setTextColorRes(viewId: Int, textColorRes: Int): RecyclerViewHelper {
        val view = retrieveView<TextView>(viewId)
        view.setTextColor(ContextCompat.getColor(context, textColorRes))
        return this
    }

    fun setTextHintColorRes(viewId: Int, textColorRes: Int): RecyclerViewHelper {
        val view = retrieveView<EditText>(viewId)
        view.setHintTextColor(ContextCompat.getColor(context, textColorRes))
        return this
    }

    fun setImageDrawable(viewId: Int, drawable: Drawable): RecyclerViewHelper {
        val view = retrieveView<ImageView>(viewId)
        view.setImageDrawable(drawable)
        return this
    }

    fun setImageBitmap(viewId: Int, bitmap: Bitmap): RecyclerViewHelper {
        val view = retrieveView<ImageView>(viewId)
        view.setImageBitmap(bitmap)
        return this
    }

    fun setImageScaleType(viewId: Int, scaleType: ScaleType): RecyclerViewHelper {
        val view = retrieveView<ImageView>(viewId)
        view.scaleType = scaleType
        return this
    }

    fun setAlpha(viewId: Int, value: Float): RecyclerViewHelper {
        retrieveView<View>(viewId).alpha = value
        return this
    }

    fun setVisible(viewId: Int, visible: Boolean): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        view.visibility = if (visible) VISIBLE else GONE
        return this
    }

    fun setVisibleAndInvisible(viewId: Int, visible: Boolean): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        view.visibility = if (visible) VISIBLE else INVISIBLE
        return this
    }

    fun linkify(viewId: Int): RecyclerViewHelper {
        val view = retrieveView<TextView>(viewId)
        Linkify.addLinks(view, Linkify.ALL)
        return this
    }

    fun setTypeface(viewId: Int, typeface: Typeface): RecyclerViewHelper {
        val view = retrieveView<TextView>(viewId)
        view.typeface = typeface
        view.paintFlags = view.paintFlags or Paint.SUBPIXEL_TEXT_FLAG
        return this
    }

    fun setTypeface(typeface: Typeface, vararg viewIds: Int): RecyclerViewHelper {
        for (viewId in viewIds) {
            val view = retrieveView<TextView>(viewId)
            view.typeface = typeface
            view.paintFlags = view.paintFlags or Paint.SUBPIXEL_TEXT_FLAG
        }
        return this
    }

    fun setProgress(viewId: Int, progress: Int): RecyclerViewHelper {
        val view = retrieveView<ProgressBar>(viewId)
        view.progress = progress
        return this
    }

    fun setProgress(viewId: Int, progress: Int, max: Int): RecyclerViewHelper {
        val view = retrieveView<ProgressBar>(viewId)
        view.max = max
        view.progress = progress
        return this
    }

    fun setProgressMax(viewId: Int, max: Int): RecyclerViewHelper {
        val view = retrieveView<ProgressBar>(viewId)
        view.max = max
        return this
    }

    //注意：不能为0,0则会显示默认的5个
    fun setNumStars(viewId: Int, numStars: Int): RecyclerViewHelper {
        val view = retrieveView<RatingBar>(viewId)
        view.numStars = numStars
        return this
    }

    fun setTag(viewId: Int, tag: Any): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        view.tag = tag
        return this
    }

    fun setTag(viewId: Int, key: Int, tag: Any): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        view.setTag(key, tag)
        return this
    }

    fun setTextGravity(viewId: Int, gravity: Int): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        if (view is TextView) view.gravity = gravity
        return this
    }

    fun setChecked(viewId: Int, checked: Boolean): RecyclerViewHelper {
        val view: Checkable = retrieveView(viewId)
        view.isChecked = checked
        return this
    }

    fun setAdapter(viewId: Int, adapter: Adapter<*>): RecyclerViewHelper {
        val view = retrieveView<RecyclerView>(viewId)
        view.adapter = adapter
        return this
    }

    fun setTvMaxEms(viewId: Int, length: Int): RecyclerViewHelper {
        val view = retrieveView<TextView>(viewId)
        view.maxEms = length
        return this
    }

    fun setMarginTop(viewId: Int, margin: Int): RecyclerViewHelper {
        val v = retrieveView<View>(viewId)
        if (v.layoutParams is MarginLayoutParams) {
            val layoutParams = v.layoutParams as MarginLayoutParams
            layoutParams.topMargin = margin
            v.layoutParams = layoutParams
        } else {
            Log.e(
                "RecyclerHelper",
                "This LayoutParams is not instanceof ViewGroup.MarginLayoutParams"
            )
        }
        return this
    }

    fun setMarginBottom(viewId: Int, margin: Int): RecyclerViewHelper {
        val v = retrieveView<View>(viewId)
        if (v.layoutParams is MarginLayoutParams) {
            val layoutParams = v.layoutParams as MarginLayoutParams
            layoutParams.bottomMargin = margin
            v.layoutParams = layoutParams
        } else {
            Log.e(
                "RecyclerHelper",
                "This LayoutParams is not instanceof ViewGroup.MarginLayoutParams"
            )
        }
        return this
    }

    fun setMarginLeft(viewId: Int, margin: Int): RecyclerViewHelper {
        val v = retrieveView<View>(viewId)
        if (v.layoutParams is MarginLayoutParams) {
            val layoutParams = v.layoutParams as MarginLayoutParams
            layoutParams.leftMargin = margin
            v.layoutParams = layoutParams
        } else {
            Log.e(
                "RecyclerHelper",
                "This LayoutParams is not instanceof ViewGroup.MarginLayoutParams"
            )
        }
        return this
    }

    fun setMarginRight(viewId: Int, margin: Int): RecyclerViewHelper {
        val v = retrieveView<View>(viewId)
        if (v.layoutParams is MarginLayoutParams) {
            val layoutParams = v.layoutParams as MarginLayoutParams
            layoutParams.rightMargin = margin
            v.layoutParams = layoutParams
        } else {
            Log.e(
                "RecyclerHelper",
                "This LayoutParams is not instanceof ViewGroup.MarginLayoutParams"
            )
        }
        return this
    }

    fun setMargin(viewId: Int, left: Int, top: Int, right: Int, bottom: Int): RecyclerViewHelper {
        val v = retrieveView<View>(viewId)
        if (v.layoutParams is MarginLayoutParams) {
            val layoutParams = v.layoutParams as MarginLayoutParams
            layoutParams.leftMargin = left
            layoutParams.topMargin = top
            layoutParams.rightMargin = right
            layoutParams.bottomMargin = bottom
            v.layoutParams = layoutParams
        } else {
            Log.e(
                "RecyclerHelper",
                "This LayoutParams is not instanceof ViewGroup.MarginLayoutParams"
            )
        }
        return this
    }

    fun setPadding(viewId: Int, left: Int, top: Int, right: Int, bottom: Int): RecyclerViewHelper {
        val v = retrieveView<View>(viewId)
        v.setPadding(left, top, right, bottom)
        return this
    }

    fun setPaddingLeft(viewId: Int, paddingLeft: Int): RecyclerViewHelper {
        val v = retrieveView<View>(viewId)
        val paddingTop = v.paddingTop
        val paddingRight = v.paddingRight
        val paddingBottom = v.paddingBottom
        v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        return this
    }

    fun setPaddingTop(viewId: Int, paddingTop: Int): RecyclerViewHelper {
        val v = retrieveView<View>(viewId)
        val paddingLeft = v.paddingLeft
        val paddingRight = v.paddingRight
        val paddingBottom = v.paddingBottom
        v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        return this
    }

    fun setPaddingRight(viewId: Int, paddingRight: Int): RecyclerViewHelper {
        val v = retrieveView<View>(viewId)
        val paddingLeft = v.paddingLeft
        val paddingTop = v.paddingTop
        val paddingBottom = v.paddingBottom
        v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        return this
    }

    fun setPaddingBottom(viewId: Int, paddingBottom: Int): RecyclerViewHelper {
        val v = retrieveView<View>(viewId)
        val paddingLeft = v.paddingLeft
        val paddingTop = v.paddingTop
        val paddingRight = v.paddingRight
        v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        return this
    }

    fun drawableLeft(viewId: Int, drawableId: Int): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        if (view is TextView) try {
            view.setCompoundDrawablesWithIntrinsicBounds(
                if (drawableId > 0) context!!.resources.getDrawable(
                    drawableId
                ) else null, null, null, null
            )
        } catch (e: NotFoundException) {
            e.printStackTrace()
        }
        return this
    }

    //单位为dp
    fun setTextSize(viewId: Int, textSizeDp: Int): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        if (view is TextView) view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSizeDp.toFloat())
        return this
    }

    fun drawable(viewId: Int, left: Int, top: Int, right: Int, bottom: Int): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        if (view is TextView) try {
            view.setCompoundDrawablesWithIntrinsicBounds(
                if (left > 0) context!!.resources.getDrawable(left) else null,
                if (top > 0) context!!.resources.getDrawable(top) else null,
                if (right > 0) context!!.resources.getDrawable(right) else null,
                if (bottom > 0) context!!.resources.getDrawable(bottom) else null
            )
        } catch (e: NotFoundException) {
            e.printStackTrace()
        }
        return this
    }

    fun drawableTop(viewId: Int, drawableId: Int): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        if (view is TextView) try {
            view.setCompoundDrawablesWithIntrinsicBounds(
                null,
                if (drawableId > 0) context!!.resources.getDrawable(drawableId) else null,
                null,
                null
            )
        } catch (e: NotFoundException) {
            e.printStackTrace()
        }
        return this
    }

    fun drawableRight(viewId: Int, drawableId: Int): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        if (view is TextView) try {
            view.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                if (drawableId > 0) context!!.resources.getDrawable(drawableId) else null,
                null
            )
        } catch (e: NotFoundException) {
            e.printStackTrace()
        }
        return this
    }

    fun drawableBottom(viewId: Int, drawableId: Int): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        if (view is TextView) try {
            view.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                null,
                if (drawableId > 0) context!!.resources.getDrawable(drawableId) else null
            )
        } catch (e: NotFoundException) {
            e.printStackTrace()
        }
        return this
    }

    fun setTranslationX(viewId: Int, translationX: Float): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        view.translationX = translationX
        return this
    }
    // </editor-fold>

    // </editor-fold>
    // <editor-fold desc="属性获取">
    fun isVisible(viewId: Int): Boolean {
        val view = retrieveView<View>(viewId)
        return view.visibility == VISIBLE
    }

    fun getX(viewId: Int): Float {
        return retrieveView<View>(viewId).x
    }

    fun getY(viewId: Int): Float {
        return retrieveView<View>(viewId).y
    }

    fun getMarginLeft(viewId: Int): Int {
        val view = retrieveView<View>(viewId)
        val layoutParams = view.layoutParams
        var marginLeft = 0
        if (layoutParams is MarginLayoutParams) marginLeft = layoutParams.leftMargin
        return marginLeft
    }

    fun getMarginRight(viewId: Int): Int {
        val view = retrieveView<View>(viewId)
        val layoutParams = view.layoutParams
        var marginLeft = 0
        if (layoutParams is MarginLayoutParams) marginLeft = layoutParams.rightMargin
        return marginLeft
    }

    fun getLeft(viewId: Int): Int {
        return retrieveView<View>(viewId).left
    }

    fun getTop(viewId: Int): Int {
        return retrieveView<View>(viewId).top
    }

    fun getBottom(viewId: Int): Int {
        return retrieveView<View>(viewId).bottom
    }

    fun getPaddingLeft(viewId: Int): Int {
        val v = retrieveView<View>(viewId)
        return v.paddingLeft
    }

    fun getPaddingRight(viewId: Int): Int {
        val v = retrieveView<View>(viewId)
        return v.paddingRight
    }

    fun getPaddingBottom(viewId: Int): Int {
        val v = retrieveView<View>(viewId)
        return v.paddingBottom
    }

    fun getHeight(viewId: Int): Int {
        val v = retrieveView<View>(viewId)
        var height = v.height
        if (height <= 0) {
            val size = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            v.measure(size, size)
            height = v.measuredHeight
        }
        return height
    }

    fun getWidth(viewId: Int): Int {
        val v = retrieveView<View>(viewId)
        var width = v.width
        if (width <= 0) {
            val size = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            v.measure(size, size)
            width = v.measuredWidth
        }
        return width
    }

    fun getText(viewId: Int): String {
        return (retrieveView<View>(viewId) as TextView).text.toString()
    }

    fun isCheckBoxChecked(viewId: Int): Boolean {
        val view = getView<CheckBox>(viewId)
        return view.isChecked
    }
    // </editor-fold>

    // </editor-fold>
    //只有父布局为RelativeLayout的调用才有用
    fun removeRule(viewId: Int, rule: Int): RecyclerViewHelper {
        val v = getView<View>(viewId)
        val parent = v.parent
        if (parent is RelativeLayout) {
            val layoutParams = v.layoutParams as RelativeLayout.LayoutParams
            layoutParams.removeRule(rule)
            v.layoutParams = layoutParams
        }
        return this
    }

    // <editor-fold desc="监听器类">
    fun setOnClickListener(viewId: Int, listener: OnClickListener): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        view.setOnClickListener(listener)
        return this
    }

    fun setOnCheckedListener(
        viewId: Int,
        listener: CompoundButton.OnCheckedChangeListener
    ): RecyclerViewHelper {
        val view = retrieveView<CheckBox>(viewId)
        view.setOnCheckedChangeListener(listener)
        return this
    }

    fun setOnTouchListener(viewId: Int, listener: OnTouchListener): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        view.setOnTouchListener(listener)
        return this
    }

    fun setOnLongClickListener(viewId: Int, listener: OnLongClickListener): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        view.setOnLongClickListener(listener)
        return this
    }

    fun setOnFocusChangeListener(
        viewId: Int,
        listener: OnFocusChangeListener
    ): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        view.onFocusChangeListener = listener
        return this
    }

    fun addTextChangeListener(viewId: Int, textWatcher: TextWatcher): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        if (view is TextView) {
            view.addTextChangedListener(textWatcher)
        }
        return this
    }

    fun removeTextChangeListener(viewId: Int, textWatcher: TextWatcher): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        if (view is TextView) {
            view.removeTextChangedListener(textWatcher)
        }
        return this
    }

    fun setOnEditorActionListener(
        viewId: Int,
        listener: OnEditorActionListener
    ): RecyclerViewHelper {
        val view = retrieveView<View>(viewId)
        if (view is TextView) {
            view.setOnEditorActionListener(listener)
        }
        return this
    }
    // </editor-fold>
}