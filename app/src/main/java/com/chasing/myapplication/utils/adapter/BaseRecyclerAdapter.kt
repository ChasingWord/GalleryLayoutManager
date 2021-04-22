package com.chasing.base.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by chasing on 2020/12/29.
 */
abstract class BaseRecyclerAdapter<T, RecyclerViewHelper> private constructor() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var datas: ArrayList<T?>
    protected lateinit var context: Context
    private lateinit var baseRecyclerMultitemTypeSupport: BaseRecyclerMultitemTypeSupport<T>
    private var layoutId = 0
    private var isMultitemType = false

    private var itemClickListener: ItemClickListener? = null
    private var itemLongClickListener: ItemLongClickListener? = null
    private var onItemLongClickRelease: ItemLongClickReleaseListener? = null

    private var isLongClick = false

    constructor(
        context: Context,
        layoutId: Int,
        datas: List<T?>?
    ) : this() {
        this.context = context
        this.datas = if (datas == null) ArrayList() else ArrayList(datas)
        this.layoutId = layoutId
    }

    constructor(
        context: Context,
        datas: List<T?>?,
        baseRecyclerMultitemTypeSupport: BaseRecyclerMultitemTypeSupport<T>
    ) : this() {
        this.context = context
        this.datas = if (datas == null) ArrayList() else ArrayList(datas)
        this.baseRecyclerMultitemTypeSupport = baseRecyclerMultitemTypeSupport
        isMultitemType = true
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): RecyclerView.ViewHolder {
        Log.e("recycler", "onCreate")
        return if (isMultitemType) {
            val layoutId: Int = baseRecyclerMultitemTypeSupport.getLayoutId(type)
            val view = LayoutInflater.from(context).inflate(layoutId, viewGroup, false)
            object : RecyclerView.ViewHolder(view) {}
        } else {
            val view = LayoutInflater.from(context).inflate(layoutId, viewGroup, false)
            object : RecyclerView.ViewHolder(view) {}
        }
    }

    override fun onBindViewHolder(@NonNull viewHolder: RecyclerView.ViewHolder, position: Int) {
        val interrupt = onBind(viewHolder, position)
        if (interrupt) return
        val t: T? = datas[position]
        val itemViewType = getItemViewType(position)
        val helper: RecyclerViewHelper = getAdapterHelper(position, viewHolder.itemView)
        convert(itemViewType, helper, t)
    }

    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        Log.e("recycler", "onBindViewHolder")
        if (payloads.isEmpty()) onBindViewHolder(viewHolder, position) else {
            val interrupt = onBind(viewHolder, position)
            if (interrupt) return
            val t: T? = datas[position]
            val itemViewType = getItemViewType(position)
            val helper: RecyclerViewHelper = getAdapterHelper(position, viewHolder.itemView)

            // 移除相同的payload，避免重复处理
            val truePayLoads: MutableList<String> = ArrayList()
            val waitDeal: MutableList<Any> = ArrayList()
            for (payload in payloads) {
                if (truePayLoads.contains(payload.toString())) continue
                truePayLoads.add(payload.toString())
                waitDeal.add(payload)
            }
            convertPart(itemViewType, helper, t, waitDeal)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    open fun onBind(viewHolder: RecyclerView.ViewHolder, position: Int): Boolean {
        viewHolder.itemView.setOnClickListener {
            onItemClick(
                viewHolder,
                position
            )
        }
        viewHolder.itemView.setOnLongClickListener {
            isLongClick = true
            onItemLongClick(viewHolder, position)
        }
        viewHolder.itemView.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_CANCEL) {
                if (isLongClick && onItemLongClickRelease != null) {
                    onItemLongClickRelease!!.onItemLongClickRelease(view, position)
                }
                isLongClick = false
            }
            false
        }
        val t: T? = datas[position]
        return isMultitemType && !baseRecyclerMultitemTypeSupport.isConvert(t, position)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun getItemViewType(position: Int): Int {
        val t: T? = datas[position]
        return if (isMultitemType) baseRecyclerMultitemTypeSupport.getItemViewType(
            t,
            position
        ) else super.getItemViewType(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    protected abstract fun getAdapterHelper(position: Int, itemView: View): RecyclerViewHelper

    protected abstract fun convert(viewType: Int, helper: RecyclerViewHelper, t: T?)

    protected open fun convertPart(
        viewType: Int,
        helper: RecyclerViewHelper,
        t: T?,
        payloads: List<Any>
    ) {
    }

    // region 数据操作
    open fun add(elem: T?) {
        add(datas.size, elem)
    }

    open fun add(position: Int, elem: T?) {
        if (elem != null) {
            datas.add(position, elem)
            notifyItemInserted(position)
            if (datas.size > position + 1) { //不刷新，索引下标会出现错误
                notifyItemRangeChanged(position + 1, datas.size - position - 1)
            }
        }
    }

    /**
     * add进行添加数据，并刷新所有数据
     * 后面全部使用insertAll，不进行刷新插入位置之前的的数据
     */
    open fun addAll(elem: List<T?>?) {
        insertAll(datas.size, elem)
    }

    /**
     * add进行添加数据，并刷新所有数据
     * 后面全部使用insertAll，不进行刷新插入位置之前的的数据
     */
    open fun addAll(position: Int, elem: List<T?>?) {
        insertAll(position, elem)
    }

    //insert进行插入，并刷新插入位置之后的数据
    open fun insertAll(elem: List<T?>?) {
        insertAll(datas.size, elem)
    }

    open fun insertAll(position: Int, elem: List<T?>?) {
        if (elem != null && elem.isNotEmpty()) {
            datas.addAll(position, elem)
            notifyItemRangeInserted(position, elem.size)
            if (datas.size > position + elem.size) { //不刷新，索引下标会出现错误
                notifyItemRangeChanged(position + elem.size, datas.size - position - elem.size)
            }
        }
    }

    open fun insertAll(position: Int, elem: List<T?>?, payload: Any?) {
        if (elem != null && elem.isNotEmpty()) {
            datas.addAll(position, elem)
            notifyItemRangeInserted(position, elem.size)
            if (datas.size > position + elem.size) { //不刷新，索引下标会出现错误
                notifyItemRangeChanged(
                    position + elem.size,
                    datas.size - position - elem.size,
                    payload
                )
            }
        }
    }

    open operator fun set(oldElem: T?, newElem: T?) {
        set(datas.indexOf(oldElem), newElem)
    }

    open operator fun set(index: Int, elem: T?) {
        datas[index] = elem
        notifyItemChanged(index)
    }

    open fun remove(elem: T?) {
        if (elem != null) {
            val index = datas.indexOf(elem)
            datas.remove(elem)
            notifyItemRemoved(index)
            if (datas.size - 1 >= index) notifyItemRangeChanged(index, datas.size - index)
        }
    }

    open fun remove(index: Int) {
        if (index < datas.size) {
            datas.removeAt(index)
            notifyItemRemoved(index)
            if (datas.size - 1 >= index) notifyItemRangeChanged(index, datas.size - index)
        }
    }

    open fun removeRange(index: Int, count: Int) {
        var realCount = 0
        for (i in index + count - 1 downTo index) {
            if (i >= datas.size) continue
            datas.removeAt(i)
            realCount++
        }
        notifyItemRangeRemoved(index, realCount)
        if (datas.size - 1 >= index) notifyItemRangeChanged(index, datas.size - index)
    }

    open fun removeAfter(index: Int) {
        if (index >= datas.size) return
        removeRange(index, datas.size - index)
    }

    open fun getItem(position: Int): T? {
        return datas[position]
    }

    open fun getAll(): List<T?>? {
        return datas
    }

    open operator fun contains(elem: T?): Boolean {
        return datas.contains(elem)
    }

    open fun clear() {
        datas.clear()
        notifyDataSetChanged()
    }
    // endregion

    // region 事件回调（点击、长按）
    // endregion
    // region 事件回调（点击、长按）
    /**
     * click
     */
    interface ItemClickListener {
        fun onItemClick(itemView: View, position: Int)
    }

    /**
     * long click
     */
    interface ItemLongClickListener {
        fun onItemLongClick(itemView: View, position: Int): Boolean
    }

    interface ItemLongClickReleaseListener {
        fun onItemLongClickRelease(itemView: View, position: Int)
    }

    open fun onItemClick(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (itemClickListener != null) {
            val view: View = viewHolder.itemView
            itemClickListener!!.onItemClick(view, position)
        }
    }

    open fun onItemLongClick(viewHolder: RecyclerView.ViewHolder, position: Int): Boolean {
        if (itemLongClickListener != null) {
            val view: View = viewHolder.itemView
            return itemLongClickListener!!.onItemLongClick(view, position)
        }
        return false
    }

    open fun setItemClickListener(clickListener: ItemClickListener?) {
        itemClickListener = clickListener
    }

    open fun setItemLongClickListener(longClickListener: ItemLongClickListener?) {
        itemLongClickListener = longClickListener
    }

    open fun setItemLongClickReleaseListener(longClickReleaseListener: ItemLongClickReleaseListener?) {
        onItemLongClickRelease = longClickReleaseListener
    }
    // endregion
}